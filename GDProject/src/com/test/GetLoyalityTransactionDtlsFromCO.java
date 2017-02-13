package com.test;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



public class GetLoyalityTransactionDtlsFromCO {
		 
	/*static String str = "select t1.ID_STR_RT as ID_STR_RT, t1.ID_WS as ID_WS, t1.AI_TRN AS AI_TRN, t1.DC_DY_BSN AS DC_DY_BSN,"+
			"t1.TY_TRN AS TY_TRN, t1.TS_TRN_END AS TS_TRN_END ,t1.CO_TRN_CNY as CO_TRN_CNY from "+ 
			"(SELECT DISTINCT TR_TRN.ID_STR_RT AS ID_STR_RT, TR_TRN.ID_WS AS ID_WS, TR_TRN.AI_TRN AS AI_TRN, TR_TRN.DC_DY_BSN AS DC_DY_BSN " +
			", TR_TRN.TY_TRN AS TY_TRN, TR_TRN.TS_TRN_END AS TS_TRN_END, TR_RTL.CD_CNY_ISO AS CO_TRN_CNY "+  
			" FROM TR_TRN   join ST_STR_HRY on   TR_TRN.ID_STR_RT = ST_STR_HRY.ID_STR_RT "+ 
			" LEFT OUTER JOIN TR_RTL on TR_RTL.ID_STR_RT = TR_TRN.ID_STR_RT AND TR_RTL.ID_WS = TR_TRN.ID_WS AND TR_RTL.AI_TRN = TR_TRN.AI_TRN AND TR_RTL.DC_DY_BSN = "+ 
	"TR_TRN.DC_DY_BSN WHERE 1=1 AND  TY_TRN <> '45'  ORDER BY ID_STR_RT ,ID_WS ,DC_DY_BSN DESC , AI_TRN DESC) t1 INNER JOIN "+ 

	 "(select id_str_rt AS ID_STR_RT ,id_ws AS id_ws,dc_dy_bsn AS dc_dy_bsn ,ai_trn AS AI_TRN from ct_trn_lylt where lylt_id = '6666666666' )  t2"+ 
			 
	" on t1.ID_STR_RT= t2.ID_STR_RT and t1.id_ws= t2.id_ws and t1.dc_dy_bsn= t2.dc_dy_bsn and t1.ai_trn= t2.ai_trn";
	*/
	static String str ="SELECT A.ID_STR_RT ID_STR_RT ,A.ID_WS AS ID_WS, A.AI_TRN AS AI_TRN, A.DC_DY_BSN AS DC_DY_BSN, A.TY_TRN AS TY_TRN, A.TS_TRN_END  AS TS_TRN_END, B.CD_CNY_ISO AS CO_TRN_CNY  FROM(SELECT DISTINCT A.ID_STR_RT AS ID_STR_RT, A.ID_WS AS ID_WS, A.AI_TRN AS AI_TRN, A.DC_DY_BSN AS DC_DY_BSN, A.TY_TRN AS TY_TRN, A.TS_TRN_END AS TS_TRN_END FROM TR_TRN  A, ct_trn_lylt B where A.ID_STR_RT=B.ID_STR_RT  AND  A.ID_WS=B.ID_WS AND  A.AI_TRN=B.AI_TRN AND  A.DC_DY_BSN=B.DC_DY_BSN AND  A.ID_STR_RT=B.ID_STR_RT AND  B.LYLT_ID= ?) A INNER JOIN (SELECT * FROM TR_RTL ) B ON A.ID_STR_RT=B.ID_STR_RT AND  A.ID_WS=B.ID_WS AND  A.AI_TRN=B.AI_TRN AND  A.DC_DY_BSN=B.DC_DY_BSN AND  A.ID_STR_RT=B.ID_STR_RT";
	
	private static Connection getConnection(String driverClass, String url,
			String userName, String passWord) {
		Connection connection = null;
		try {
			
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, userName, passWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
    
	public static void main(String[] args) {

		try  {
			 String driverClass = "oracle.jdbc.driver.OracleDriver";
			 String url = "jdbc:oracle:thin:@localhost:1521/orcl";
			 String userName = "pos_co_datasource_user";
			 String passWord = "pos_co_datasource_user";
			 Connection connection = getConnection(driverClass, url, userName, passWord);
			 if(connection==null){
				 System.out.println("connection not established");
			 }else{
				 System.out.println("connection  established");
			 }
			 
			 PreparedStatement pstmt = connection.prepareStatement(str) ;
			 pstmt.setInt(1, new Integer("6666666666"));
			 ResultSet rs = pstmt.executeQuery();
			 while (rs.next()) {
				System.out.println("details from result set " +rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3) +" "+rs.getString(4) +" "+rs.getString(5));
				
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		

	
	}

}
