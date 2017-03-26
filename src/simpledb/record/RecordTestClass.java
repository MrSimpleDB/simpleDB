package simpledb.record;


import org.junit.Test;

import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class RecordTestClass {

	@Test
	public void test() {
		//database's initialization
		SimpleDB.init("provadb");
		System.out.println("database server ready");
		
		Transaction tx = new Transaction();
		Schema sch = new Schema();
		sch.addIntField("number");
		sch.addStringField("code", 3);
		TableInfo ti=new TableInfo("prova", sch);
		RecordFile rf= new RecordFile(ti, tx);
		
		//insert 10000 records
		for (int i=0;i<100;i++){
			insert(rf);
		}
		
		String event="Insert 10000 records";
		printAllRecordStats(event,rf);
		
		//read all records
		rf.beforeFirst();
		while(rf.next())read(rf);
		
		event="Read all records";
		printAllRecordStats(event,rf);
		
		//delete 50% of records
		delete(rf);
	}

	private int randomNumberGenerator(){
		return (int) (Math.random()*1000);
	}

	private String randomStringGenerator(){
		return 	"asd";
	} 

	private void insert(RecordFile rf){
		rf.insert();	
		int num=randomNumberGenerator();
		String cod=randomStringGenerator();
		rf.setInt("number", num);
		rf.setString("code", cod);
	}

	private void delete(RecordFile rf){}

	private void read(RecordFile rf){
		rf.getInt("number");
		rf.getString("code");
	}
	
	private void printAllRecordStats(String event,RecordFile rf){
		System.out.println(event+": "+rf.getAllReadRecord()+" readRecord "+rf.getAllWrittenRecord()+" writtenRecord "+
	rf.getAllReadFieldsRecord()+" readFieldsRecord "+rf.getAllWrittenFieldsRecord()+" writtenFieldsRecord");
	}
}
