
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






public class GetReportDetails {

	static Logger logger = Logger.getLogger(GetReportDetails.class);
	static int periodId = 0;

	static String emplName = "";

	static int emplNumber = 0;

	static String discountGroupCode = "";

	static String periodName = "";

	static String discountEntlments = "";

	static String retailLimit = "";

	static String currentPeriodPurchases = "";

	static int entlId = 0;

	static int emplGroupId = 0;
	
	static int countVwSummary = 0;

	static String maxSpendEntl = "";
	
	static String discGroupCode = "";
	
	static String discount_division="";

	
	static String remainingSpendString = "" ;
	static String discountPercent = "";
	static String getEepEmailDtls = "select * from ct_trn_lylt1";
	//static String getEepEmailDtls = "select * from ct_eep_emp_eml where  MSGTO is not null";
	static String deleteEepEmailDtls = "delete from ct_eep_emp_eml where EMPLNUMBER = ?";
	//static String getEepItemDtls = "select * from ct_eep_item where empl_number = ? and rownum <2  order by ts_crt_rcrd desc;";
	static String getEepItemDtls = "select * from CT_VW_EEP_ENT_SUM where empl_number = ?";
	static String getDiscDivQuery ="select * from ct_eep_entitlement where  EMPL_GROUP_ID = ?";
	static String getPeriodDtlsQuery = "SELECT * FROM ct_eep_period WHERE sysdate BETWEEN period_start_date AND period_end_date+1";

	static String getPeriodIdQuery = "SELECT period_id FROM ct_eep_period WHERE TO_DATE(sysdate, ''DD-MON-YY'') >=  "
			+ "to_date(period_start_date,''DD-MON-YY'') "
			+ "AND TO_DATE(sysdate, ''DD-MON-YY'') <=to_date(period_end_date,''DD-MON-YY'')";

	static String merchandiseGroupIdQuery = "select id_mrhrc_gp , de_itm from as_itm where id_itm = ?";

	static String divisionQuery = "select divdesc from ct_merch_hierarchy where id_mrhrc_gp = ?";
	
	static String getDivisionQuery =
	"select a.discount_division  from ct_eep_entitlement a , ct_eep_item b where a.EMPL_GROUP_ID= b.EMPL_GROUP_ID and a.ENTITLEMENT_ID= b.ENTITLEMENT_ID and b.empl_number = ? and b.period_id= ? and b.id_itm = ?";

	static String getEmplMstrDtlsQuery = "select * from ct_eep_empl_master where empl_number = ?";
	static Connection connection = null;

	//static String getEntlIdQuery = "select entitlement_id from ct_eep_item where empl_number = ? and rownum <2";
	static String getEntlIdQuery = "select * from ct_vw_eep_summary where empl_number = ? and period_id= ?";
	static String getEntlDtlsQuery = "select * from ct_vw_eep_summary where empl_number = ? and period_id= ?";
	
	static String getRemainingSpendQuery = "select * from CT_VW_EEP_ENT_SUM where empl_number = ?";
	
	
	static String getEmplGrpDescQuery = "select EMPL_GROUP_DESCR from ct_eep_group where EMPL_GROUP_ID = ?";

	static String getTransactionDtls = "select * from ct_eep_item where empl_number = ? and period_id = ?";

	static String getItemSizeQuery = "select ed_sz from as_itm_stk where id_itm = ?";

	static List<String> purchaseDateList = new ArrayList<String>();
	
	
	static List<String> divisionList = new ArrayList<String>();
	static List<String> itemIdList = new ArrayList<String>();
	static List<String> itemsDescriptionList = new ArrayList<String>();
	static List<String> itemsSizeList = new ArrayList<String>();
	static List<String> retailPriceList = new ArrayList<String>();
	static List<String> itemSizeList = new ArrayList<String>();
	static List<String> discountPriceList = new ArrayList<String>();
	static List<String> itemDescList = new ArrayList<String>();
	
	static List<String>  maxSpendEntlList = new ArrayList<String>();
	static  List<String> discountPercentList = new ArrayList<String>();
	static  List<String> discountDivList = new ArrayList<String>();
	static  List<String> discountDivNewList = new ArrayList<String>();
	static  List<String> entlDescList = new ArrayList<String>();
	
	

	static BigDecimal totalRetailsPriceBD = new BigDecimal(0);
	static BigDecimal totalNetPriceDB = new BigDecimal(0);

	static BigDecimal totalRetailsPriceBDWithDisc = new BigDecimal(0);
	static BigDecimal totalNetPriceDBWithDisc = new BigDecimal(0);
	
	static BigDecimal totalRetailsPriceBDWithoutDisc = new BigDecimal(0);
	static BigDecimal totalNetPriceDBWithoutDisc = new BigDecimal(0);
	
	static BigDecimal maxSpendAmtBD = new BigDecimal(0);
	
	static BigDecimal remainingSpendAmtBD = new BigDecimal(0);
	
	static String remainingSpendAmt = "";
	
	static String totalSpendStr = "";
	
	

	static String driverClass = "";
	static String url = "";
	static String userName = "";
	static String passWord = "";

	static Boolean mailSendStatus = Boolean.TRUE;
	
	static Map<Integer,String> msgToMap = new HashMap<Integer,String>();
	static Map<Integer,String> msgCcMap = new HashMap<Integer,String>();
	
	
	static Map<String,String> remSpendMap = new HashMap<String,String>();
	
	static Map<String,String> currentperiodPurchaseMap = new  HashMap<String,String>();
	
	static Map<String,String> newRemSpendMap = new HashMap<String,String>();
	static Map<String,String> newCurrentperiodPurchaseMap = new HashMap<String,String>();
	static Map<String,String> newDiscountDivMap = new HashMap<String,String>();
	
	
	static String user = null;
	static String pass = null;
	
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

	static List<String> getMerchandiseGroupId(String idItem)
	{
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				list.add(resultSet.getString("id_mrhrc_gp"));
				if(resultSet.getString("de_itm")!=null && !"".equalsIgnoreCase(resultSet.getString("de_itm"))){
				list.add(resultSet.getString("de_itm"));
				}else{
					list.add("No Item Desc");	
				}
			}
			preparedStatement.close();
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	static String geMerchandiseGrpId(String idItem) {
		String merchandiseGroupId = "";
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(merchandiseGroupIdQuery);
			preparedStatement.setString(1, idItem);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				merchandiseGroupId = resultSet.getString("id_mrhrc_gp");
			}
			preparedStatement.close();
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merchandiseGroupId;
	}

	/*static String geItemDivision(String merchandiseGrpId) {
		String divisionName = "";
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(divisionQuery);
			preparedStatement.setString(1, merchandiseGrpId);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				divisionName = resultSet.getString("divdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return divisionName;
	}*/
	
	
	static String geItemDivision(int emplNumber, int periodId, String itemId) {
		String divisionName = "";
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(getDivisionQuery);
			preparedStatement.setInt(1,emplNumber);
			preparedStatement.setInt(2,periodId);
			preparedStatement.setString(3,itemId);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				divisionName = resultSet.getString(1);
			}
			preparedStatement.close();
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return divisionName;
	}

	static String getTransaDetails(int emplId, Integer periodId) {
		String transactionDetails = "Store Id\t WorkStattion Id\tBusiness Date\ttrans Id\tLN_ITM\tEMPL Id\tPeriod\tItem Id<br><br>";
		try {

			String queryString = "select * from ct_eep_item where empl_number  = ? and  period_id = ?";

			PreparedStatement preparedStatement = connection
					.prepareStatement(queryString);
			preparedStatement.setString(1, "" + emplId + "");
			preparedStatement.setInt(2, periodId);

			ResultSet resultSet = preparedStatement.executeQuery();
			
			
			while (resultSet.next()) {

				String ID_ITM = resultSet.getString("ID_ITM");

				List<String> list = getMerchandiseGroupId(ID_ITM);
				String merchandiseGrpId = "";
				String itemDesc ="";
				if(list!=null&&list.size()!=0){
				 merchandiseGrpId = list.get(0);
				 itemDesc = list.get(1);
				}else{
					merchandiseGrpId="no Merchandise";
					itemDesc = "No ItemDesc";
				}
				//String divisionName = geItemDivision(merchandiseGrpId);
			 String divisionName = geItemDivision(emplNumber,periodId,ID_ITM);
				purchaseDateList.add(resultSet.getString("DC_DY_BSN"));
				itemIdList.add(resultSet.getString("ID_ITM"));
				retailPriceList.add(resultSet.getString("MO_EXTN_LN_ITM_RTN"));
				discountPriceList
						.add(resultSet.getString("MO_EXTN_DSC_LN_ITM"));
				if (divisionName != null && !"".equalsIgnoreCase(divisionName)) {
					divisionList.add(divisionName);
				} else {
					divisionName = "";
					divisionList.add(divisionName);
				}
				itemDescList.add(itemDesc);

			}
			preparedStatement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactionDetails;
	}

	public static void getEepDtls() {

		try {

			PreparedStatement preparedStatement = connection
					.prepareStatement(getEepItemDtls);
		      preparedStatement.setInt(1, emplNumber);			      
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				//emplGroupId = resultSet.getInt("EMPL_GROUP_ID");
				maxSpendEntlList.add(resultSet.getString("MAX_SPEND_ENTITLED"));
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
				maxSpendAmtBD = new BigDecimal(Double.parseDouble(maxSpendEntl))
						.setScale(2, RoundingMode.HALF_UP);
				/*discountPercent = resultSet.getString("DISCOUNT_PERCENT");
				discountPercentList.add( resultSet.getString("DISCOUNT_PERCENT"));*/
				entlDescList.add(resultSet.getString("ENTITLEMENT_DESCR"));
				
			}
			preparedStatement.close();
			resultSet.close();
		} catch (Exception e) {
			logger.error("Exception thrown while getting employee group details from ct_eep_item table" +  e);
		}

	}

	public static void getCtEepEmailDtls() {
		ResultSet resultSet = null;
		try {

			PreparedStatement preparedStatement = connection
					.prepareStatement(getEepEmailDtls);
			preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				msgToMap.put(resultSet.getInt(1) , resultSet.getString(2));
				msgCcMap.put(resultSet.getInt(1) , resultSet.getString(3));
			}
			
			preparedStatement.close();
			resultSet.close();
		} catch (Exception e)
		{
			logger.error("Exception thrown while getting employee email details from ct_eep_emp_eml table" +  e);
		}

	}
	
	
	public static void deleteCtEepEmailDtls(int emplNumber) {

		try {

			PreparedStatement preparedStatement = connection
					.prepareStatement(deleteEepEmailDtls);
			preparedStatement.setInt(1, emplNumber);
			preparedStatement.execute();
			preparedStatement.close();
			
		} catch (Exception e)
		{
			logger.error("Exception thrown while deleting employee email details from ct_eep_emp_eml table" +  e);
		}

	}

	public static void getEmplMstrDtls() {
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEmplMstrDtlsQuery);
			pstmt.setInt(1, emplNumber);
			
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				emplName = resultSet.getString("firstname") + " "
						+ resultSet.getString("lastname");
			}
			pstmt.close();
			resultSet.close();
		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee name from ct_eep_empl_master table" +  e);
		}

	}

	public static void getEmplPeriodDtls() {
		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getPeriodDtlsQuery);

			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				periodId = resultSet.getInt("PERIOD_ID");
				periodName = resultSet.getString("PERIOD_NAME");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee period details from ct_eep_period table" +  e);
		}

	}

	public static void getDiscountDivision() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getDiscDivQuery);
			pstmt.setInt(1, emplGroupId);			
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				discount_division = resultSet.getString("DISCOUNT_DIVISION");
				if(discount_division!=null&&!"".equalsIgnoreCase(discount_division)){
					if(Integer.parseInt(discount_division)==new Integer(50))
					{
						discountDivList.add("DYN"+"  ");
						discountDivNewList.add(discount_division);
					}else{
					discountDivList.add("GRG"+"  ");
					discountDivNewList.add(discount_division);
					}
				}else{
					discountDivList.add("Store  ");
					discountDivNewList.add(discount_division);
				}
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee's entitlement ID from ct_eep_item table" +  e);
		}

	}

	public static void getEntitlementDtls() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getEntlDtlsQuery);
			pstmt.setInt(1, emplNumber);
			pstmt.setInt(2, periodId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				/*maxSpendEntlList.add(resultSet.getString("MAX_SPEND_ENTITLED"));
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
				maxSpendAmtBD = new BigDecimal(Double.parseDouble(maxSpendEntl))
						.setScale(2, RoundingMode.HALF_UP);*/
				emplGroupId = resultSet.getInt("EMPL_GROUP_ID");
				discountPercent = resultSet.getString("DISCOUNT_PERCENT");
				discountPercentList.add( resultSet.getString("DISCOUNT_PERCENT"));
				
				
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee entitlement details from ct_eep_entitlement table" +  e);
		}

	}

	public static void getRemainingSpendAmt() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getRemainingSpendQuery);
			
			pstmt.setInt(1, emplNumber);
			//pstmt.setInt(2, periodId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				countVwSummary= countVwSummary+1;
				maxSpendEntlList.add(resultSet.getString("MAX_SPEND_ENTITLED"));
				maxSpendEntl = resultSet.getString("MAX_SPEND_ENTITLED");
				maxSpendAmtBD = new BigDecimal(Double.parseDouble(maxSpendEntl))
						.setScale(2, RoundingMode.HALF_UP);
				/*discountPercent = resultSet.getString("DISCOUNT_PERCENT");
				discountPercentList.add( resultSet.getString("DISCOUNT_PERCENT"));*/
				entlDescList.add(resultSet.getString("ENTITLEMENT_DESCR"));
				remainingSpendAmt = resultSet.getString("remaining_spend");
				String currentPurchaseStr = resultSet.getString("TOTAL_SPEND");
				/*if(discountDivNewList!=null&&discountDivNewList.size()!=0)
				{
				for(int i=0;i<discountDivNewList.size();i++)
				{
					if(discountDivNewList.get(i)!=null&&!"".equalsIgnoreCase(discountDivNewList.get(i))){
						if(discountDivNewList.get(i).equalsIgnoreCase("50")){
						currentperiodPurchaseMap.put("DYN"+"  ", currentPurchaseStr);
						remSpendMap.put("DYN"+"  ", remainingSpendAmt);
						}
						else if(discountDivNewList.get(i).equalsIgnoreCase("70")){
							remSpendMap.put("GRG"+"  ", remainingSpendAmt);
							currentperiodPurchaseMap.put("GRG"+"  ", currentPurchaseStr);
							}
				}
				}
				}*/
				
				
				
				if(discount_division!=null&&discount_division.length()!=0){
					if(discount_division!=null&&!"".equalsIgnoreCase(discount_division)){
						if(Integer.parseInt(discount_division)==new Integer(50)){
						currentperiodPurchaseMap.put("DYN"+"  ", currentPurchaseStr);
						remSpendMap.put("DYN"+"  ", remainingSpendAmt);
						}else if(Integer.parseInt(discount_division)==new Integer(70)){
						remSpendMap.put("GRG"+"  ", remainingSpendAmt);
						currentperiodPurchaseMap.put("GRG"+"  ", currentPurchaseStr);
						}
					}	
				}
				else{
								
					remSpendMap.put("Store  ", remainingSpendAmt);
					currentperiodPurchaseMap.put("Store  ", currentPurchaseStr);
				}
				
				
			}
			
			resultSet.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee entitlement details from ct_eep_entitlement table" +  e);
		}

	}

	public static void getNewRemainingSpendAmt() {

		try {
			PreparedStatement pstmt = connection.prepareStatement(getRemainingSpendQuery);
			
			pstmt.setInt(1, emplNumber);
			
			ResultSet resultSet = pstmt.executeQuery();
			BigDecimal remSpendBD= new BigDecimal(0);
			remSpendBD= remSpendBD.setScale(2);
			BigDecimal currentperiodPurchaseBD= new BigDecimal(0);
			currentperiodPurchaseBD= currentperiodPurchaseBD.setScale(2);
			Integer count = 0;
			while (resultSet.next()) {
				if(countVwSummary==2){/*
					if(resultSet.getString("ENTITLEMENT_DESCR").contains("DYN")){
					newDiscountDivMap.put("DYN"+"  ", resultSet.getString("ENTITLEMENT_DESCR"));	
					String totalSpend =	 resultSet.getString("TOTAL_SPEND");
					currentperiodPurchaseBD = new BigDecimal(totalSpend);
					currentperiodPurchaseBD = currentperiodPurchaseBD.setScale(2);
					totalSpend = currentperiodPurchaseBD.toString();
					
					
					if(totalSpend!=null&&totalSpend.length()!=0){
						
						newCurrentperiodPurchaseMap.put("DYN"+"  ", totalSpend);
					 }else{
						 newCurrentperiodPurchaseMap.put("DYN"+"  ", "0.00");
					 }
					String remainingSpend =	 resultSet.getString("REMAINING_SPEND");
					
					remSpendBD= new BigDecimal(remainingSpend);
					remSpendBD = remSpendBD.setScale(2);
					remainingSpend = remSpendBD.toString();
					if(remainingSpend!=null&&remainingSpend.length()!=0){
						newRemSpendMap.put("DYN"+"  ", remainingSpend);
					 }else{
						 newRemSpendMap.put("DYN"+"  ", "0.00");
					 }
					}else{
						newDiscountDivMap.put("GRG"+"  ", resultSet.getString("ENTITLEMENT_DESCR"));
						String totalSpend =	 resultSet.getString("TOTAL_SPEND");
						currentperiodPurchaseBD = new BigDecimal(totalSpend);
						currentperiodPurchaseBD = currentperiodPurchaseBD.setScale(2);
						totalSpend = currentperiodPurchaseBD.toString();
						
						
						if(totalSpend!=null&&totalSpend.length()!=0){
							newCurrentperiodPurchaseMap.put("GRG"+"  ", totalSpend);
						 }else{
							 newCurrentperiodPurchaseMap.put("GRG"+"  ", "0.00");
						 }
						
						String remainingSpend =	 resultSet.getString("REMAINING_SPEND");
						remSpendBD= new BigDecimal(remainingSpend);
						remSpendBD = remSpendBD.setScale(2);
						remainingSpend = remSpendBD.toString();
						if(remainingSpend!=null&&remainingSpend.length()!=0){
							newRemSpendMap.put("GRG"+"  ", remainingSpend);
						 }else{
							 newRemSpendMap.put("GRG"+"  ", "0.00");
						 }
					}
				*/
					count = count+1;
					newDiscountDivMap.put(count.toString(), resultSet.getString("ENTITLEMENT_DESCR"));	
					String remainingSpend =	 resultSet.getString("REMAINING_SPEND");
					remSpendBD= new BigDecimal(remainingSpend);
					remSpendBD = remSpendBD.setScale(2);
					remainingSpend = remSpendBD.toString();
					if(remainingSpend!=null&&remainingSpend.length()!=0){
						
						newRemSpendMap.put(count.toString(),remainingSpend );
					 }else{
						 newRemSpendMap.put(count.toString(), "0.00");
					 }
					
					String totalSpend =	 resultSet.getString("TOTAL_SPEND");
					currentperiodPurchaseBD = new BigDecimal(totalSpend);
					currentperiodPurchaseBD = currentperiodPurchaseBD.setScale(2);
					totalSpend = currentperiodPurchaseBD.toString();
					
					if(totalSpend!=null&&totalSpend.length()!=0){
						newCurrentperiodPurchaseMap.put(count.toString(), totalSpend);
					 }else{
						 newCurrentperiodPurchaseMap.put(count.toString(), "0.00");
					 }
					

					
					
					
				}else{
					
					count = count+1;
					
					newDiscountDivMap.put(count.toString(), resultSet.getString("ENTITLEMENT_DESCR"));
					String totalSpend =	 resultSet.getString("TOTAL_SPEND");
					currentperiodPurchaseBD = new BigDecimal(totalSpend);
					currentperiodPurchaseBD = currentperiodPurchaseBD.setScale(2);
					totalSpend = currentperiodPurchaseBD.toString();
					if(totalSpend!=null&&totalSpend.length()!=0){
						//newCurrentperiodPurchaseMap.put("Store  ", totalSpend);
						newCurrentperiodPurchaseMap.put(count.toString(), totalSpend);
					 }else{
						 newCurrentperiodPurchaseMap.put(count.toString(), "0.00");
					 }
					String remainingSpend =	 resultSet.getString("REMAINING_SPEND");
					remSpendBD= new BigDecimal(remainingSpend);
					remSpendBD = remSpendBD.setScale(2);
					remainingSpend = remSpendBD.toString();
					if(remainingSpend!=null&&remainingSpend.length()!=0){
						newRemSpendMap.put(count.toString(), remainingSpend);
					 }else{
						 newRemSpendMap.put(count.toString(), "0.00");
					 }
				}
			}
			resultSet.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee entitlement details from ct_eep_entitlement table" +  e);
		}

	}

	
	
	
	public static String getItemSize(String itemId) {

		String itemSize = "";

		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getItemSizeQuery);
			pstmt.setString(1, itemId);

			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				itemSize = resultSet.getString("ed_sz");
			}

		} catch (SQLException e) 
		{
			logger.error("Exception thrown while getting item's size from as_itm_stk table" +  e);
		}
		return itemSize;
	}

	public static void getEmplGroupDtls() {

		try {
			PreparedStatement pstmt = connection
					.prepareStatement(getEmplGrpDescQuery);
			
			pstmt.setInt(1, emplGroupId);
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) 
			{
				discGroupCode = resultSet.getString("EMPL_GROUP_DESCR");
			}

		} catch (SQLException e) {
			logger.error("Exception thrown while getting employee's discount group description from ct_eep_group table" +  e);
		}

	}

	public static void configureHTMLContent(StringBuilder htmlMailContent) {

		htmlMailContent.append("<html><title></title>");
		htmlMailContent
				.append("<body style='font-size:12px;font-family:Trebuchet MS;'>");
		htmlMailContent
				.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'>");
		htmlMailContent
				.append("<tr border='1' ><th>Nom de l’employé <br/>Employee Name</th><th>Numéro de l’employé<br/>Employee Number</th><th>Groupe de rabais<br/>Discount Group</th><th>Période de rapport<br/>Reporting Period</th></tr>");

		htmlMailContent.append("<tr border='1' ><td>" + emplName + "</td><td>"
				+ emplNumber + "</td>" + "<td>"+discGroupCode+"</td> " + "<td>" + periodName
				+ "</td></tr></table><br/><br/><br/><br/><br/><br/>");
		
		
		
		htmlMailContent 
				.append("                                                                                            ");
		htmlMailContent
				.append("<table width='600px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'>");
		htmlMailContent
				.append("<tr border='1' ><th>Admissibilité au rabais<br/>Discount Entitlement</th><th>Limite (au détail)<br/>Limit (Retail)"
						+ "</th><th>Achats de la période en cours<br/>Current Period Purchases</th><th>Limite restante<br/>Remaining Spend</th></tr>");
		Integer count= 0;
		for(int i=0;i<maxSpendEntlList.size();i++){
			count = count+1;
			 remainingSpendString =  remSpendMap.get(discountDivList.get(i));
			 currentPeriodPurchases = currentperiodPurchaseMap.get(discountDivList.get(i));
			 if(remainingSpendString==null||remainingSpendString.length()==0){
				 remainingSpendString ="0";
			 }
			 
			 if(currentPeriodPurchases==null||currentPeriodPurchases.length()==0){
				 currentPeriodPurchases ="0";
			 }
			String maxSpendEntl = maxSpendEntlList.get(i);
			BigDecimal maxSpendEntlBD = BigDecimal.ZERO;
			if(maxSpendEntl!=null&&maxSpendEntl.length()!=0){
				maxSpendEntlBD = new BigDecimal(maxSpendEntl);
			}
			maxSpendEntlBD= maxSpendEntlBD.setScale(2);
			System.out.println("1111  "+newRemSpendMap.get(new Integer(count).toString())+" : "+newCurrentperiodPurchaseMap.get(new Integer(count).toString()));
			htmlMailContent.append("<tr border='1' ><td>" 
					
					//+discountDivList.get(i)+"%"
					//+newDiscountDivMap.get(new Integer(count).toString())
					+newDiscountDivMap.get(new Integer(count).toString())
					+ "</td><td>" + "$" +maxSpendEntlBD.toString()
					+ "</td><td>$" 
				//	+ new BigDecimal(currentPeriodPurchases).setScale(2) 
					+newCurrentperiodPurchaseMap.get(new Integer(count).toString())
					+ "</td><td>$"
					+newRemSpendMap.get(new Integer(count).toString())
					//+ new BigDecimal(remainingSpendString).setScale(2)
					+ "</td></tr>");
			newCurrentperiodPurchaseMap.get(new Integer(count).toString());
			htmlMailContent.append("");
		}
		
		

		htmlMailContent.append("</table><br/><br/><br/><br/><br/><br/>");

		htmlMailContent
				.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'>");
		htmlMailContent
				.append("<tr border='1'><th width='200px'>TOTAL des achats d'employé avec le rabais d'employé<br/>TOTAL Employee Purchases With Employee Discount applied</th>"
						+ "<th></th><th></th><th colspan=3></th><th></th></tr>");
		htmlMailContent
				.append("<tr border='1' ><th>Date de l’achat<br/>Purchase Date</th><th><br/>Article<br/>Item</th><th>Description</th>"
						+ "<th>Grandeur<br/>Size</th><th>Prix au détail<br/>Retail Price</th><th>% de rabais<br/>Discount %</th><th>Prix net<br/>Net Price</th></tr>");
		for (int i = 0; i < purchaseDateList.size(); i++) {

			BigDecimal retailPriceBD = new BigDecimal(retailPriceList.get(i));
			BigDecimal netPriceBD = new BigDecimal(discountPriceList.get(i));
			retailPriceBD.setScale(2,RoundingMode.HALF_UP);
			netPriceBD.setScale(2,RoundingMode.HALF_UP);
			BigDecimal discPercent = BigDecimal.ZERO;
			discPercent.setScale(2, RoundingMode.HALF_UP);
			discPercent = netPriceBD.divide(retailPriceBD,2, RoundingMode.HALF_UP);
			discPercent = discPercent.multiply(new BigDecimal(100));
			//discPercent = (netPriceBD.divide(retailPriceBD)).multiply(new BigDecimal(100));
			discPercent = new BigDecimal(100).subtract(discPercent);
			
			/*System.out.println("retailPriceBD " + retailPriceBD
					+ "  netPriceBD " + netPriceBD + " : " + discPercent);*/
			if(discPercent.compareTo(new BigDecimal(0))>0){
			totalNetPriceDBWithDisc = totalNetPriceDBWithDisc.add(netPriceBD);
			totalRetailsPriceBDWithDisc = totalRetailsPriceBDWithDisc.add(retailPriceBD);
			totalNetPriceDBWithDisc.setScale(2);
			totalRetailsPriceBDWithDisc.setScale(2);
			String itemId = itemIdList.get(i);
			String retailPriceStr = retailPriceList.get(i);
			String netPriceStr = discountPriceList.get(i);
			
			htmlMailContent.append("<tr border='1' ><td>"
					+ purchaseDateList.get(i) + "</td>"
					 + "<td>" + itemIdList.get(i)
					+ "</td><td>" + itemDescList.get(i) + "</td>" + "<td>"
					+ getItemSize(itemId) + "</td><td>$"
					+ new BigDecimal(retailPriceStr).setScale(2) + "</td><td>" + discPercent + "%"
					+ "</td><td>$" + new BigDecimal(netPriceStr).setScale(2) + "</td></tr>");
			}
		}

		htmlMailContent
				.append("<tr border='1' ><th>Totals</th><th></th><th></th>"
						+ "<th></th><th>$"
						+ totalRetailsPriceBDWithDisc
						+ "</th><th></th><th>$"
						+ totalNetPriceDBWithDisc
						+ "</th></tr>");
		htmlMailContent.append("</table><br/><br/><br/><br/><br/><br/>");
		
		

		htmlMailContent
				.append("<table width='800px' align='left' border='1' cellpadding='0' cellspacing='0' style='border-top:5px solid white;'>");
		htmlMailContent
				.append("<tr border='1'><th width='200px'>TOTAL des achats d'employé SANS le rabais d'employé<br/>TOTAL Employee Purchases WITH NO  Employee Discount</th>"
						+ "<th></th><th></th><th colspan=3></th><th></th></tr>");
		htmlMailContent
				.append("<tr border='1' ><th>Date de l’achat<br/>Purchase Date</th><th>Article<br/>Item</th><th>Description</th>"
						+ "<th>Grandeur<br/>Size</th><th>Prix au détail<br/>Retail Price</th><th>% de rabais<br/>Discount %</th><th>Prix net<br/>Net Price</th></tr>");
		for (int i = 0; i < purchaseDateList.size(); i++) {

			BigDecimal retailPriceBD = new BigDecimal(retailPriceList.get(i));
			BigDecimal netPriceBD = new BigDecimal(discountPriceList.get(i));
			retailPriceBD.setScale(2,RoundingMode.HALF_UP);
			netPriceBD.setScale(2,RoundingMode.HALF_UP);
			BigDecimal discPercent = BigDecimal.ZERO;
			discPercent.setScale(2, RoundingMode.HALF_UP);
			discPercent = netPriceBD.divide(retailPriceBD,2, RoundingMode.HALF_UP);
			discPercent = discPercent.multiply(new BigDecimal(100));
			//discPercent = (netPriceBD.divide(retailPriceBD)).multiply(new BigDecimal(100));
			discPercent = new BigDecimal(100).subtract(discPercent);
			
			/*System.out.println("retailPriceBD " + retailPriceBD
					+ "  netPriceBD " + netPriceBD + " : " + discPercent);*/
			if(discPercent.compareTo(new BigDecimal(0))==0){
				totalNetPriceDBWithoutDisc = totalNetPriceDBWithoutDisc.add(netPriceBD);
				totalRetailsPriceBDWithoutDisc = totalRetailsPriceBDWithoutDisc.add(retailPriceBD);
			String itemId = itemIdList.get(i);
			String retailPriceStr = retailPriceList.get(i);
			String netPriceStr = discountPriceList.get(i);
			htmlMailContent.append("<tr border='1' ><td>"
					+ purchaseDateList.get(i) + "</td>"
					 + "<td>" + itemIdList.get(i)
					+ "</td><td>" + itemDescList.get(i) + "</td>" + "<td>"
					+ getItemSize(itemId) + "</td><td>$"
					+ new BigDecimal(retailPriceStr).setScale(2)+"</td><td>" + discPercent + "%"
					+ "</td><td>$" + new BigDecimal(netPriceStr).setScale(2)+ "</td></tr>");
			}
		}
		totalRetailsPriceBDWithoutDisc.setScale(2);
		totalNetPriceDBWithoutDisc.setScale(2);
		htmlMailContent
		.append("<tr border='1' ><th>Totals</th><th></th><th></th>"
				+ "<th></th><th>$"
				+ totalRetailsPriceBDWithoutDisc.setScale(2)
				+ "</th><th></th><th>$"
				+ totalNetPriceDBWithoutDisc.setScale(2)
				+ "</th></tr>");
		htmlMailContent.append("</table>\r\n\r\n\r\n");
		htmlMailContent.append("<table width='800px' height='50px'>");
		htmlMailContent
		.append("<tr></tr>");
		htmlMailContent.append("</table>");
		/*
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent.append("<tr border='0'><th><th></tr>");
		htmlMailContent.append("<tr border='0' ><th></th></tr></table>");
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent.append("<tr border='0'><th><th></tr>");
		htmlMailContent.append("<tr border='0' ><th></th></tr></table>");
		htmlMailContent.append("<table width='300px' align='left' ");
		htmlMailContent
				.append("<tr border='0' ><th  align='left'>Yours Sincerely<th></tr>");
		htmlMailContent
				.append("<tr border='0' ><th align='left'>Groupe Dynamite</th></tr></table>");
		
		htmlMailContent
		.append("<table width='800px' align='left' border='0' cellpadding='0' cellspacing='0'");

		htmlMailContent.append("</table>");*/
		/*htmlMailContent.append("<br/>");
		htmlMailContent.append("<br/>");
		htmlMailContent.append("<br/>");*/
		

	}

	public static void sendMail(String mailContent,String messageTo,String messageCc) {
		try {
			
			String msgFrom = null;			
			String subject = null;
			String mailHost = null;
			String mailPort = null;
			Session session = null;
		
			// get current date time with Date()
			
			FileReader reader = new FileReader("parameters.properties");
			Properties properties = new Properties();
			properties.load(reader);
			user = properties.getProperty("userKey");
			pass = properties.getProperty("userPass");			
			msgFrom = properties.getProperty("messageFrom");			
			subject = properties.getProperty("sub");			
			

			mailHost = properties.getProperty("mHost");
			mailPort = properties.getProperty("mPort");

			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", mailHost);
			properties.put("mail.smtp.port", mailPort);

		session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});
//Added by Monica to send email through SMTP server in HTML format		   
		    
			Message message = new MimeMessage(session);
			 MimeBodyPart wrap = new MimeBodyPart();
			    
	           MimeMultipart cover = new MimeMultipart("alternative");
	           MimeBodyPart html = new MimeBodyPart();
	           cover.addBodyPart(html);
	           
	           wrap.setContent(cover);
	    
	           MimeMultipart content = new MimeMultipart("related");
	           message.setContent(content);
	           content.addBodyPart(wrap);
	           
			    message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			    message.addHeader("format", "flowed");
			    message.addHeader("Content-Transfer-Encoding", "8bit");
			
			message.setFrom(new InternetAddress(msgFrom));
			if(messageTo!=null)
			{
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(messageTo));
			}
			if(messageCc!=null)
			{
			message.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(messageCc));
			}
			message.setSubject(subject);				   
			message.setContent(mailContent, "text/html; charset=utf-8");
		    html.setContent(mailContent.getBytes(), "text/html");
			/*message.setText("Date: " + currentMailDateTime + "\n\n"
					+ "Subject:" + subject + "\n\n" + html);*/
			       
	          
			message.saveChanges();
		    
			Transport.send(message);
		
		//	System.out.println("response.getMessage() "+response.getMessage()+ " : response.getStatus() "+response.getStatus());
		} catch (Exception e) {
			logger.error("Exception thrown while sending an email to employee" +  e);
		}

	}

	private static void writeToLog(int emplNumber) {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");
		logger.debug("EEPurchase details sent for employee " + emplNumber);
	}
	
	private static void writeDeleteToLog(int emplNumber) {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("log4j.properties");
		logger.debug("EEPurchase details deleted for employee " + emplNumber);
	}
	
    private static void calculateTotalPrice(){
    	for (String s : retailPriceList) {
			totalRetailsPriceBD = totalRetailsPriceBD.add(
					new BigDecimal(s))
					.setScale(2, RoundingMode.HALF_UP);
		}
		for (String s : discountPriceList) {
			totalNetPriceDB = totalNetPriceDB.add(new BigDecimal(s))
					.setScale(2, RoundingMode.HALF_UP);
		}
    }
	
    public static void initialize(){
		purchaseDateList = new ArrayList<String>();
		divisionList = new ArrayList<String>();
		itemIdList = new ArrayList<String>();
		itemsDescriptionList = new ArrayList<String>();
		itemsSizeList = new ArrayList<String>();
		retailPriceList = new ArrayList<String>();
		itemSizeList = new ArrayList<String>();
		discountPriceList = new ArrayList<String>();
		itemDescList = new ArrayList<String>();
		maxSpendEntlList = new ArrayList<String>();
		discountPercentList = new ArrayList<String>();
		discountDivList = new ArrayList<String>();
		entlDescList = new ArrayList<String>();
		discountDivNewList= new ArrayList<String>();
		
		
		
		
		totalRetailsPriceBD = new BigDecimal(0);
		totalRetailsPriceBD.setScale(2);
		totalNetPriceDB = new BigDecimal(0);
		totalNetPriceDB.setScale(2);
		
		totalRetailsPriceBDWithDisc = new BigDecimal(0);
		totalRetailsPriceBDWithDisc.setScale(2);
		totalNetPriceDBWithDisc = new BigDecimal(0);
		totalNetPriceDBWithDisc.setScale(2);
		totalRetailsPriceBDWithoutDisc = new BigDecimal(0);
		totalRetailsPriceBDWithoutDisc.setScale(2);
		totalNetPriceDBWithoutDisc = new BigDecimal(0);
		totalNetPriceDBWithoutDisc.setScale(2);

		maxSpendAmtBD = new BigDecimal(0);
		maxSpendAmtBD.setScale(2);
		remainingSpendAmt = "" ;
		remainingSpendAmtBD = new BigDecimal(0);
		remainingSpendAmtBD.setScale(2);
		mailSendStatus = Boolean.TRUE;    		
		periodId = 0;
    	 emplName = "";
		emplNumber = 0;
		discountGroupCode = "";
		periodName = "";
		discountEntlments = "";
		discount_division= "";

		retailLimit = "";

		 remainingSpendString = "";
		currentPeriodPurchases = "";
		entlId = 0;
		emplGroupId = 0;
		maxSpendEntl = "";
		discGroupCode = "";
		discountPercent = "";
		
		remSpendMap = new HashMap<String,String>();
		 currentperiodPurchaseMap=  new HashMap<String,String>();
		 
		 newRemSpendMap = new HashMap<String,String>();
		 newCurrentperiodPurchaseMap=  new HashMap<String,String>();
		 countVwSummary = 0;
		 newDiscountDivMap = new HashMap<String,String>();
    }
    
	public static void main(String[] args) throws SQLException {

		try  {
			FileReader reader = new FileReader("parameters.properties");
			Properties properties = new Properties();
			properties.load(reader);

			driverClass = properties.getProperty("driverClass");
			url = properties.getProperty("url");
			userName = properties.getProperty("userName");
			passWord = properties.getProperty("passWord");
			connection = getConnection(driverClass, url, userName, passWord);

			
		} catch (IOException e)
		{
			logger.error("Exception thrown while reading the properties from file" +  e);
		}
		

		if (connection != null) 
		{
			try {
				getCtEepEmailDtls();
				/*for(Map.Entry<Integer,String> me :msgToMap.entrySet())
				{
				
				emplNumber = me.getKey();*/
				emplNumber = 34002;
				
				getEmplPeriodDtls();
				getEntitlementDtls();
				//getEepDtls();
				getEmplMstrDtls();				
				//getEntitlementId();
				//getEntitlementDtls();
				getDiscountDivision();
				getEmplGroupDtls();
				getRemainingSpendAmt();
				getNewRemainingSpendAmt();
				getTransaDetails(emplNumber, periodId);
				calculateTotalPrice();
				StringBuilder htmlMailContent = new StringBuilder();
				configureHTMLContent(htmlMailContent);
				
				String mailContent = htmlMailContent.toString();
				//sendMail(mailContent,me.getValue(),msgCcMap.get(me.getKey()));
				sendMail(mailContent,"mambati@dynamite.ca","dharmensm@gmail.com");
				writeToLog(emplNumber);
				deleteCtEepEmailDtls(emplNumber);
				writeDeleteToLog(emplNumber);
				initialize();
				//}
			} 
			catch (Exception e) 
			{
				logger.error("Exception thrown while getting purchase details of employee" +  e);
			}
			
			finally
			{
				connection.close();
			}
		} 
		else 
		{
			logger.error("Database connection could not be obtained" );
		}
		
		
	}

}
