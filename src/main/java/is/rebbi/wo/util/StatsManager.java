package is.rebbi.wo.util;

import com.webobjects.foundation.NSTimestamp;

public class StatsManager {

	/**
	 * Time at which the application was started.
	 */
	private NSTimestamp _startupTime;

	private static StatsManager _instance;

	public static void register() {
		instance().setStartupTime( new NSTimestamp() );
	}

	public static StatsManager instance() {
		if( _instance == null ) {
			_instance = new StatsManager();
		}

		return _instance;
	}

	/**
	 * @return Time at which the application was started.
	 */
	private void setStartupTime( NSTimestamp value ) {
		_startupTime = value;
	}

	/**
	 * @return Time at which the application was started.
	 */
	public NSTimestamp startupTime() {
		return _startupTime;
	}
}