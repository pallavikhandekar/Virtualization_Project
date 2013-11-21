package org.cmpe283.finalproject.analyser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.cmpe283.finalproject.domain.TestDomain;
import org.cmpe283.finalproject.domain.AnalyzedData;

import com.mysql.jdbc.Statement;

public class AggregateDataFromMongo implements ActionListener{

	//static final String WRITE_ANAYSIS_DATA = "INSERT INTO test(name, lastname, age) VALUES (?, ?, ?)";
	static final String WRITE_ANAYSIS_DATA = "INSERT INTO aggregation_data(ipaddress,host,cpuInformation,memoryInformation" +
			"storageInformation,networkInformation,cpuSpeed,runTimeInformation,timstamp)" +
			"VALUES (?, ?, ?,?,?,?,?,?,?)";
	
	Connection conn = null;
	ArrayList<AnalyzedData> datalist = null;
	
	public AggregateDataFromMongo(Connection conn, ArrayList<AnalyzedData> data){
		this.conn = conn;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			//TODO: POPULATE DATA FROM MONGO
			
			Statement stmt = (Statement) conn.createStatement();
			stmt.executeUpdate("Truncate aggregation_data");
			writeAnaLysisData(datalist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeAnaLysisData( ArrayList<AnalyzedData>  datalist) throws Exception {

	    PreparedStatement pstmt = conn.prepareStatement(WRITE_ANAYSIS_DATA, Statement.RETURN_GENERATED_KEYS);
	
	    // set input parameters
	    for(AnalyzedData data: datalist)
	    {
		    pstmt.setString(1,data.getMyIpAddress());
		    pstmt.setString(2,data.getMyHost());
		    pstmt.setInt(3, data.getCpuInformation());
		    pstmt.setInt(3, data.getMemoryInformation());
		    pstmt.setInt(3, data.getStorageInformation());
		    pstmt.setInt(3, data.getNetworkInformation());
		    pstmt.setInt(3, data.getCpuSpeed());
		    pstmt.setInt(3, data.getRunTimeInfo());
		    pstmt.setDate(3,(Date) data.getTimestamp());
		    pstmt.addBatch();
	    }
	    int success[] = pstmt.executeBatch();

	    // get the generated key for the id
	   if(success.length==-3)
	   {
		   System.out.println("Statement executed incorrectly");
	   }
	    pstmt.close();
	   
	  }

	
	
	
}
