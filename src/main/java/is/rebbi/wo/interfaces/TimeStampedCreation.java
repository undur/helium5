package is.rebbi.wo.interfaces;

import com.webobjects.foundation.NSTimestamp;

public interface TimeStampedCreation {

	public abstract NSTimestamp creationDate();

	public abstract void setCreationDate( NSTimestamp t );
}