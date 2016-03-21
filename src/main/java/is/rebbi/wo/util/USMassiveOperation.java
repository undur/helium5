package is.rebbi.wo.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.foundation.ERXArrayUtilities;
import is.rebbi.core.util.Stopwatch;
import is.rebbi.core.util.StringUtilities;

/**
 * This class is for handling operations that need to work with massive amounts of data.
 */

public class USMassiveOperation {

	private final Logger logger = LoggerFactory.getLogger( USMassiveOperation.class );
	private Collection<String> _messagePasser;

	private EOEditingContext _editingContext;
	private Class<? extends EOEnterpriseObject> _entityClass;
	private EOQualifier _qualifier;

	private Long _startTime;
	private Long _endTime;
	private Long _averageTimePerRun;
	private final Integer _batchSize = 1000;
	private int _rowCurrentlyBeingProcessed;

	public USMassiveOperation() {}

	public USMassiveOperation( Collection<String> passer ) {
		_messagePasser = passer;
	}

	public long startTime() {
		return _startTime;
	}

	/**
	 * The time this run ended.
	 */
	public Long endTime() {
		return _endTime;
	}

	/**
	 * Time elapsed since task started.
	 */
	public Long elapsedTime() {
		return System.currentTimeMillis() - startTime();
	}

	/**
	 * Average time spent processing each object, since start.
	 */
	public Long averageTimePerRun() {
		return _averageTimePerRun;
	}

	/**
	 * Estimates the remaining time of this task.
	 */
	public Long estimatedRemainingTime() {
		return (totalNumberOfRowsToProcess() - rowCurrentlyBeingProcessed()) * averageTimePerRun();
	}

	private int rowCurrentlyBeingProcessed() {
		return _rowCurrentlyBeingProcessed;
	}

	public int totalNumberOfRowsToProcess() {
		return ERXEOControlUtilities.objectCountWithQualifier( editingContext(), entityName(), qualifier() );
	}

	private String entityName() {
		return entityClass().getSimpleName();
	}

	public void setEditingContext( EOEditingContext _editingContext ) {
		this._editingContext = _editingContext;
	}

	public EOEditingContext editingContext() {
		return _editingContext;
	}

	public void setEntityClass( Class<? extends EOEnterpriseObject> _entityClass ) {
		this._entityClass = _entityClass;
	}

	public Class<? extends EOEnterpriseObject> entityClass() {
		return _entityClass;
	}

	public void setQualifier( EOQualifier _qualifier ) {
		this._qualifier = _qualifier;
	}

	public EOQualifier qualifier() {
		return _qualifier;
	}

	/**
	 * @param entityClass The class of the EO Entity to apply your action to.
	 * @param batchSize The number of objects to work with at a time. Defaults to 200 if no batch size is specified.
	 * @param handler Handler that is applied to each object.
	 * @param qualifier Qualifier , if you only want to apply this to a subset of the objects.
	 *
	 * FIXME: In reality, we might need to save  the child editing context's changes to the parent editing context on each batch.
	 */
	public void start( EOEditingContext ec, Class<?> entityClass, EOQualifier qualifier, Integer batchSize, Operation handler ) {
		log( StringUtilities.stringWithFormat( "Invoking mass operation on class {}.", entityClass ) );

		EOEditingContext.setInstancesRetainRegisteredObjects( false );
		Stopwatch taskTimer = new Stopwatch();

		if( batchSize == null ) {
			batchSize = 200;
		}

		EOEditingContext nestedEC = leanEditingContext( ec );

		String entityName = entityClass.getSimpleName();
		EOEntity entity = EOModelGroup.defaultGroup().entityNamed( entityName );

		Integer totalNumberOfRows = ERXEOControlUtilities.objectCountWithQualifier( nestedEC, entityName, qualifier );

		EOFetchSpecification fs = new EOFetchSpecification( entityName, null, null );
		fs.setQualifier( qualifier );
		fs.setFetchesRawRows( true );
		fs.setRawRowKeyPaths( entity.primaryKeyAttributeNames() );

		log( StringUtilities.stringWithFormat( "Fetching Raw rows for entity {}. Will apply action to {} objects.", entityName, totalNumberOfRows ) );

		NSArray<NSDictionary> allRawRows = nestedEC.objectsWithFetchSpecification( fs );
		NSArray<NSArray<NSDictionary>> rawRowBatches = ERXArrayUtilities.batchedArrayWithSize( allRawRows, batchSize );
		int batchCount = rawRowBatches.count();

		log( StringUtilities.stringWithFormat( "Fetch and batching complete, created {} batches of {} objects each.", batchCount, batchSize ) );

		int batchNumber = 0;

		for( NSArray<NSDictionary> batch : rawRowBatches ) {
			batchNumber++;

			Stopwatch batchTimer = new Stopwatch();

			nestedEC = leanEditingContext( ec );

			NSArray<EOEnterpriseObject> eoBatch = convertRawRowsToEOs( nestedEC, entityName, batch );

			for( EOEnterpriseObject eo : eoBatch ) {
				handler.handleObject( eo );
			}

			// Only logging below here
			long elapsedTaskTime = taskTimer.elapsed();
			long avgBatchTime = elapsedTaskTime / batchNumber;
			long currentBatchTime = batchTimer.elapsed();
			long estRemainingTime = avgBatchTime * (batchCount - batchNumber);
			Object[] info = new Object[] { batchNumber, batchCount, currentBatchTime, avgBatchTime, elapsedTaskTime, estRemainingTime };
			log( StringUtilities.stringWithFormat( "Processed batch {} of {} in {} ms (avg {} ms). Elapsed time {} ms, est. remaining time {} ms.", info ) );
		}

		log( "finished, total time was: " + taskTimer.elapsed() );
	}

	/**
	 * @return Ar array of EOs corresponding to the raw rows.
	 */
	private static NSArray<EOEnterpriseObject> convertRawRowsToEOs( EOEditingContext ec, String entityName, NSArray<NSDictionary> rawRows ) {
		NSMutableArray<EOQualifier> allQualifier = new NSMutableArray<>();

		for( NSDictionary pk : rawRows ) {
			EOQualifier q = EOQualifier.qualifierToMatchAllValues( pk );
			allQualifier.addObject( q );
		}

		EOQualifier q = new EOOrQualifier( allQualifier );
		EOFetchSpecification fs = new EOFetchSpecification( entityName, q, null );
		NSArray<EOEnterpriseObject> eos = ec.objectsWithFetchSpecification( fs );
		return eos;
	}

	private void log( String msg ) {
		logger.info( msg );
		if( _messagePasser != null ) {
			_messagePasser.add( msg );
		}
	}

	/**
	 * Creates an editing context with as little overhead as possible.
	 */
	private EOEditingContext leanEditingContext( EOEditingContext parent ) {
		EOEditingContext nestedEC = ERXEC.newEditingContext( parent );
		nestedEC.setUndoManager( null );
		EOEditingContext.setInstancesRetainRegisteredObjects( false );
		return nestedEC;
	}

	/**
	 * The interface that must be implemented by object handlers.
	 */
	public static interface Operation {

		public void handleObject( Object object );
	}
}