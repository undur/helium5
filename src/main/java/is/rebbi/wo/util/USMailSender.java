package is.rebbi.wo.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.FilenameUtils;

import is.rebbi.core.util.ListUtilities;
import is.rebbi.core.util.StringUtilities;

/**
 * For sending email.
 */

public class USMailSender {

	private USMailSender() {}

	private static void _sendMessage( String fromName, String fromEmailAddress, String replyToEmailAddress, List<String> to, List<String> cc, List<String> bcc, String subject, MimeMultipart mp ) {
		try {
			Properties props = new Properties();
			props.put( "mail.smtp.host", SWSettings.smtpHost() );

			if( StringUtilities.hasValue( SWSettings.smtpPort() ) ) {
				props.put( "mail.smtp.port", SWSettings.smtpPort() );
			}

			Authenticator auth = null;

			if( StringUtilities.hasValue( SWSettings.smtpUsername() ) ) {
				props.put( "mail.smtp.auth", "true" );
				auth = new SMTPAuthenticator( SWSettings.smtpUsername(), SWSettings.smtpPassword() );
			}

			Session session = Session.getInstance( props, auth );

			MimeMessage msg = new MimeMessage( session );
			msg.setSentDate( new Date() );

			if( fromEmailAddress != null ) {
				msg.setFrom( new InternetAddress( fromEmailAddress, fromName ) );
			}

			if( replyToEmailAddress != null ) {
				msg.setReplyTo( new InternetAddress[] { new InternetAddress( replyToEmailAddress, fromName ) } );
			}

			if( to != null ) {
				String s = String.join( ", ", to );
				s = MimeUtility.encodeText( s, "UTF-8", "B" );
				msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse( s, false ) );
			}

			if( cc != null ) {
				String s = String.join( ", ", cc );
				s = MimeUtility.encodeText( s, "UTF-8", "B" );
				msg.setRecipients( Message.RecipientType.CC, InternetAddress.parse( s, false ) );
			}

			if( bcc != null ) {
				String s = String.join( ", ", bcc );
				s = MimeUtility.encodeText( s, "UTF-8", "B" );
				msg.setRecipients( Message.RecipientType.BCC, InternetAddress.parse( s, false ) );
			}

			if( subject != null ) {
				msg.setSubject( subject, "utf-8" );
			}

			msg.setContent( mp );
			Transport.send( msg );
		}
		catch( MessagingException | UnsupportedEncodingException e ) {
			throw new RuntimeException( "Error sending e-mail", e );
		}
	}

	public static void composeEmail( String fromEmailAddress, List<String> to, List<String> cc, List<String> bcc, String subject, String text, String html ) {

		try {
			MimeMultipart mp = new MimeMultipart( "alternative" );

			if( StringUtilities.hasValue( text ) ) {
				MimeBodyPart part = new MimeBodyPart();
				part.setText( text, "utf-8" );
				mp.addBodyPart( part );
			}

			if( StringUtilities.hasValue( html ) ) {
				MimeBodyPart part = new MimeBodyPart();
				part.setContent( html, "text/html; charset=\"utf-8\"" );
				mp.addBodyPart( part );
			}

			_sendMessage( null, fromEmailAddress, null, to, cc, bcc, subject, mp );
		}
		catch( MessagingException e ) {
			throw new RuntimeException( "Error sending e-mail", e );
		}
	}

	public static void composeEmail( String fromEmailAddress, List<String> to, List<String> cc, List<String> bcc, String subject, String text, String html, List<String> attachmentFilePaths ) {
		composeEmail( null, fromEmailAddress, null, to, cc, bcc, subject, text, html, attachmentFilePaths );
	}

	public static void composeEmail( String fromName, String fromEmailAddress, String replyToEmailAddress, List<String> to, List<String> cc, List<String> bcc, String subject, String text, String html, List<String> attachmentFilePaths ) {

		try {
			MimeMultipart mp = new MimeMultipart( "alternative" );

			if( !StringUtilities.hasValue( text ) ) {
				text = "";
			}

			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setText( text, "utf-8" );
			mp.addBodyPart( mbp );

			if( StringUtilities.hasValue( html ) ) {
				MimeBodyPart part = new MimeBodyPart();
				part.setContent( html, "text/html; charset=\"utf-8\"" );
				mp.addBodyPart( part );
			}

			if( !ListUtilities.hasObjects( attachmentFilePaths ) ) {
				_sendMessage( fromName, fromEmailAddress, replyToEmailAddress, to, cc, bcc, subject, mp );
			}
			else {
				MimeMultipart mixed = new MimeMultipart( "mixed" );
				MimeBodyPart wrap = new MimeBodyPart();
				wrap.setContent( mp );
				mixed.addBodyPart( wrap );

				for( String attachmentFilePath : attachmentFilePaths ) {
					if( StringUtilities.hasValue( attachmentFilePath ) ) {
						MimeBodyPart part = new MimeBodyPart();
						part.setDataHandler( new DataHandler( new FileDataSource( attachmentFilePath ) ) );
						part.setFileName( FilenameUtils.getName( attachmentFilePath ) );
						mixed.addBodyPart( part );
					}
				}

				_sendMessage( fromName, fromEmailAddress, replyToEmailAddress, to, cc, bcc, subject, mixed );
			}
		}
		catch( MessagingException e ) {
			throw new RuntimeException( "Failed to send email", e );
		}
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {

		private String username;
		private String password;

		public SMTPAuthenticator( String username, String password ) {
			this.username = username;
			this.password = password;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication( username, password );
		}
	}
}