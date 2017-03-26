package simpledb.record;

import java.util.HashMap;
import java.util.Map;

import simpledb.file.Block;
import simpledb.tx.Transaction;

/**
 * Manages a file of records.
 * There are methods for iterating through the records
 * and accessing their contents.
 * @author Edward Sciore
 */
public class RecordFile {
	private TableInfo ti;
	private Transaction tx;
	private String filename;
	private RecordPage rp;
	private int currentblknum;
	private Map<RID,BasicRecordStats> statsRecord=new HashMap<RID,BasicRecordStats>();
	private RID lastReadRecord;
	private RID lastWrittenRecord;

	/**
	 * Constructs an object to manage a file of records.
	 * If the file does not exist, it is created.
	 * @param ti the table metadata
	 * @param tx the transaction
	 */
	public RecordFile(TableInfo ti, Transaction tx) {
		this.ti = ti;
		this.tx = tx;
		filename = ti.fileName();
		if (tx.size(filename) == 0)
			appendBlock();
		moveTo(0);
	}

	/**
	 * Closes the record file.
	 */
	public void close() {
		rp.close();
	}

	/**
	 * Positions the current record so that a call to method next
	 * will wind up at the first record. 
	 */
	public void beforeFirst() {
		moveTo(0);
	}

	/**
	 * Moves to the next record. Returns false if there
	 * is no next record.
	 * @return false if there is no next record.
	 */
	public boolean next() {
		while (true) {
			if (rp.next())
				return true;
			if (atLastBlock())
				return false;
			moveTo(currentblknum + 1);
		}
	}

	/**
	 * Returns the value of the specified field
	 * in the current record.
	 * @param fldname the name of the field
	 * @return the integer value at that field
	 */
	public int getInt(String fldname) {
		updateWrittenRecordStats();
		return rp.getInt(fldname);
	}

	/**
	 * Returns the value of the specified field
	 * in the current record.
	 * @param fldname the name of the field
	 * @return the string value at that field
	 */
	public String getString(String fldname) {
		updateWrittenRecordStats();
		return rp.getString(fldname);
	}

	/**
	 * Sets the value of the specified field 
	 * in the current record.
	 * @param fldname the name of the field
	 * @param val the new value for the field
	 */
	public void setInt(String fldname, int val) {
		rp.setInt(fldname, val);
		updateReadRecordStats();
	}

	/**
	 * Sets the value of the specified field 
	 * in the current record.
	 * @param fldname the name of the field
	 * @param val the new value for the field
	 */
	public void setString(String fldname, String val) {
		rp.setString(fldname, val);
		updateReadRecordStats();
	}

	/**
	 * Deletes the current record.
	 * The client must call next() to move to
	 * the next record.
	 * Calls to methods on a deleted record 
	 * have unspecified behavior.
	 */
	public void delete() {
		rp.delete();
	}

	/**
	 * Inserts a new, blank record somewhere in the file
	 * beginning at the current record.
	 * If the new record does not fit into an existing block,
	 * then a new block is appended to the file.
	 */
	public void insert() {
		while (!rp.insert()) {
			if (atLastBlock()){
				appendBlock();
				updateRID();
			}
			moveTo(currentblknum + 1);
		}
	}

	/**
	 * Positions the current record as indicated by the
	 * specified RID. 
	 * @param rid a record identifier
	 */
	public void moveToRid(RID rid) {
		moveTo(rid.blockNumber());
		rp.moveToId(rid.id());
	}

	/**
	 * Returns the RID of the current record.
	 * @return a record identifier
	 */
	public RID currentRid() {
		int id = rp.currentId();
		return new RID(currentblknum, id);
	}

	private void moveTo(int b) {
		if (rp != null)
			rp.close();
		currentblknum = b;
		Block blk = new Block(filename, currentblknum);
		rp = new RecordPage(blk, ti, tx);
	}

	private boolean atLastBlock() {
		return currentblknum == tx.size(filename) - 1;
	}

	private void appendBlock() {
		RecordFormatter fmtr = new RecordFormatter(ti);
		tx.append(filename, fmtr);
	}
	
	//
	//UPDATE
	//
	
	public Map<RID, BasicRecordStats> getStatsRecord() {
		return statsRecord;
	}

	public void setStatsRecord(Map<RID, BasicRecordStats> statsRecord) {
		this.statsRecord = statsRecord;
	}

	public RID getLastReadRecord() {
		return lastReadRecord;
	}

	public void setLastReadRecord(RID lastReadRecord) {
		this.lastReadRecord = lastReadRecord;
	}

	public RID getLastWrittenRecord() {
		return this.lastWrittenRecord;
	}

	public void setLastWrittenRecord(RID lastWrittenRecord) {
		this.lastWrittenRecord = lastWrittenRecord;
	}
	
	public void resetLastReadRecord(){this.lastReadRecord=null;}
	public void resetLastWrittenRecord(){this.lastWrittenRecord=null;}
	public void resetStatsRecord(){this.statsRecord.clear();}
	
	private void updateRID() {
		getStatsRecord().put(currentRid(), new BasicRecordStats());
		setLastReadRecord(currentRid());
		setLastWrittenRecord(currentRid());
	}
	
	private void updateReadRecordStats() {
		if (currentRid()!=getLastReadRecord()) this.statsRecord.get(currentRid()).incrementReadRecord();
		this.statsRecord.get(currentRid()).incrementReadFieldsRecord();
		setLastReadRecord(currentRid());
	}
	
	private void updateWrittenRecordStats() {
		if (currentRid()!=getLastWrittenRecord()) this.statsRecord.get(currentRid()).incrementWrittenRecord();
		this.statsRecord.get(currentRid()).incrementWrittenFieldsRecord();
		setLastWrittenRecord(currentRid());
	}
	
	public int getAllReadRecord(){
		int allReadRecord=0;
		for (RID rid:getStatsRecord().keySet()) allReadRecord+=getStatsRecord().get(rid).getReadRecord();
		return allReadRecord;
	}
	
	public int getAllWrittenRecord(){
		int allReadRecord=0;
		for (RID rid:getStatsRecord().keySet()) allReadRecord+=getStatsRecord().get(rid).getReadRecord();
		return allReadRecord;
	}
	
	public int getAllReadFieldsRecord(){
		int allReadRecord=0;
		for (RID rid:getStatsRecord().keySet()) allReadRecord+=getStatsRecord().get(rid).getReadRecord();
		return allReadRecord;
	}
	
	public int getAllWrittenFieldsRecord(){
		int allReadRecord=0;
		for (RID rid:getStatsRecord().keySet()) allReadRecord+=getStatsRecord().get(rid).getReadRecord();
		return allReadRecord;
	}
}