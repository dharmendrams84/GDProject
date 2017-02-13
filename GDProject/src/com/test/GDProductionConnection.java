package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class GDProductionConnection {

	public static Connection connection= null;
	static Logger logger = Logger.getLogger(GDProductionConnection.class);
	private static Connection getConnection(String driverClass, String url,String userName, String passWord)
	{
		//Connection connection = null;
		try {
			
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, userName, passWord);
		} catch (Exception e) 
		{
			logger.error("Exception thrown while establishing a JDBC connection" +  e);
		}
		return connection;
	}
	
	
	public static void getEmplPeriodDtls() throws SQLException 
	{
		PreparedStatement pstmt=null;
		ResultSet resultSet=null;
		try {
			 pstmt = connection.prepareStatement("select * from ct_trn_lylt");
			 resultSet = pstmt.executeQuery();
			while (resultSet.next())
			{
				System.out.println(resultSet.getString(1)+" : "+resultSet.getString(2)
						+" : "+resultSet.getString(3)+" : "+resultSet.getString(4));
			}
			
		} catch (Exception e) 
		{
			logger.error("Exception thrown while getting employee period details from ct_eep_period table" +  e);
		}
		
		finally
		{
			//resultSet.close();
			//pstmt.close();
		}

	}
	public static void main(String[] args) {
	
		String url="jdbc:oracle:thin:@//stotst-scan.corp.gdglobal.ca:1521/stotst.corp.gdglobal.ca";
		//String url="jdbc:oracle:thin:@saodah01.corp.gdglobal.ca:1521:stoprd1";
		String userName="RCO_SCHEMA";
		String passWord="Rco_1120#";
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		
		try{
		getConnection(driverClass, url, userName, passWord);
		getEmplPeriodDtls();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

}
