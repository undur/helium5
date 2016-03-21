package is.rebbi.wo.interfaces;

import com.webobjects.foundation.NSTimestamp;

public interface TimeStampedModification {

	public abstract NSTimestamp modificationDate();

	public abstract void setModificationDate( NSTimestamp t );
}