package simpledb.record;

public class BasicRecordStats {
	private int readRecord;
	private int writtenRecord;
	private int readFieldsRecord;
	private int writtenFieldsRecord;
	
	public int getReadRecord() {
		return readRecord;
	}
	
	public void setReadRecord(int readRecord) {
		this.readRecord = readRecord;
	}
	
	public int getWrittenRecord() {
		return writtenRecord;
	}
	
	public void setWrittenRecord(int writtenRecord) {
		this.writtenRecord = writtenRecord;
	}
	
	public int getReadFieldsRecord() {
		return readFieldsRecord;
	}
	
	public void setReadFieldsRecord(int readFieldsRecord) {
		this.readFieldsRecord = readFieldsRecord;
	}
	
	public int getWrittenFieldsRecord() {
		return writtenFieldsRecord;
	}
	
	public void setWrittenFieldsRecord(int writtenFieldsRecord) {
		this.writtenFieldsRecord = writtenFieldsRecord;
	}
	
	public void incrementReadRecord(){this.readRecord+=1;}
	
	public void incrementWrittenRecord(){this.writtenRecord+=1;}
	
	public void incrementReadFieldsRecord(){this.readFieldsRecord+=1;}
	
	public void incrementWrittenFieldsRecord(){this.writtenFieldsRecord+=1;}
	
}
