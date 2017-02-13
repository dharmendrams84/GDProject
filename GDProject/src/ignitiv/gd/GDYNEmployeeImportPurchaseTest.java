package ignitiv.gd;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Class responsible for fetching data from xml file and updating the database
 * column.
 * 
 * @author Manojeet Padhy
 * 
 */
public class GDYNEmployeeImportPurchaseTest extends DefaultHandler {

	/**
	 * The logger to which log messages will be sent.
	 */
	private static final Logger logger = Logger
			.getLogger(GDYNEmployeeImportPurchaseTest.class);

	static Session session = null;

	static String msgContent = null;

	static String dbURL = null;
	static String dbDRIVER = null;
	static String dbUSER = null;
	static String dbPASS = null;

	static String startMask = null;
	static String extension = null;

	static String maskName = null;

	static String user = null;
	static String pass = null;
	static String msgTo = null;
	static String msgFrom = null;
	static String msgCc = null;
	static String userName = null;
	static String subject = null;
	static String description = null;
	static String advertisement = null;

	static InputStream inStream = null;
	static OutputStream outStream = null;

	static String inDir = null;
	static String outDir = null;
	static String errDir = null;
	static String fileName = null;
	static String fileProcessName = null;

	static String currentDateTime = null;
	static String currentMailDateTime = null;

	static Boolean key = Boolean.FALSE;
	static Boolean dbConnectionsSet = Boolean.FALSE;

	static int nosaveList;

	static String insertQuery = null;
	static String updateQuery = null;

	static String tempMsg = null;
	static String dispMsg = null;
	static String dupEmpId = null;

	static String mailHost = null;
	static String mailPort = null;

	static PreparedStatement preParedstatementOne = null;
	static PreparedStatement preparedStatement = null;
	static Connection connection = null;
	public Employee emp;
	private String temp;
	public static ArrayList<Employee> empList = new ArrayList<Employee>();
	public static List<String> selectedFiles = new ArrayList<String>();

	public static List<String> ids = new ArrayList<String>();

	/**
	 * The main method sets things up for parsing
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		initialization();

		// Create a "parser factory" for creating SAX parsers
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser sp = spfac.newSAXParser();

		// Create an instance of this class; it defines all the handler methods
		GDYNEmployeeImportPurchaseTest handler = new GDYNEmployeeImportPurchaseTest();

		// Finally, tell the parser to parse the input and notify the handler
		// When the database connection is successful.

		File filesFolder = new File(inDir);
		FileFilter fileFilter = new WildcardFileFilter(maskName);
		File[] files = filesFolder.listFiles(fileFilter);

		logger.info("Total number of files to be processed with specified masking : "
				+ files.length);

		for (File f : files) {
			logger.info("Incoming folder contains file with name "
					+ f.getName());
		}

		if (dbConnectionsSet) {

			if (files != null && files.length != 0) {
				for (File file : files) {
					try {
						String fileToProcess = file.getName();
						sp.parse(inDir + fileToProcess, handler);
						handler.saveList(fileToProcess);
						processFile(fileToProcess);
					} catch (IOException e) {
						logger.warn(e.getMessage());
						logger.info("Please check for file or location.");
						msgContent = ("Location doesn`t contains file.\n\nPlease check for path.\n\n")
								.concat(e.getMessage());
						mailing();
					}
				}
			} else {
				logger.info("Please check for file or location.");
				msgContent = ("Location doesn`t contains file.\n\nPlease check for path.\n\n");

				mailing();
			}
		} else {
			logger.info("Connection not established");
		}
	}

	/**
	 * The initialization method sets parameter from properties file and then
	 * loads the driver and then prepares connection object. Sets data flag as
	 * success if successfully executed.
	 */
	private static void initialization() {
		PropertyConfigurator.configure("D:\\LoyalityDocuments\\GDTickets\\POS-327\\log4j.properties");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// get current date time with Date()
		Date mailDateTime = new Date();

		currentMailDateTime = dateFormat.format(mailDateTime);
		File file = new File("D:\\LoyalityDocuments\\GDTickets\\POS-327\\nameLoc.properties");
		try (FileReader read = new FileReader(file)) {
			Properties prop = new Properties();
			prop.load(read);

			inDir = prop.getProperty("incoming");
			outDir = prop.getProperty("outgoing");
			errDir = prop.getProperty("error");

			startMask = prop.getProperty("start");
			extension = prop.getProperty("ext");

			maskName = startMask.concat("*").concat(".").concat(extension);

			dbURL = prop.getProperty("url");
			dbDRIVER = prop.getProperty("driver");
			dbUSER = prop.getProperty("user");
			dbPASS = prop.getProperty("pswd");

			user = prop.getProperty("userKey");
			pass = prop.getProperty("userPass");
			msgTo = prop.getProperty("messageTo");
			msgFrom = prop.getProperty("messageFrom");
			msgCc = prop.getProperty("messageCc");
			subject = prop.getProperty("sub");
			description = prop.getProperty("desc");
			advertisement = prop.getProperty("advt");

			mailHost = prop.getProperty("mHost");
			mailPort = prop.getProperty("mPort");

			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.starttls.enable", "true");
			prop.put("mail.smtp.host", mailHost);
			prop.put("mail.smtp.port", mailPort);

			session = Session.getInstance(prop, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			});

			Class.forName(dbDRIVER);
			logger.info("Driver Loaded");
			//dbURL = "jdbc:oracle:thin:@localhost
			connection = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			logger.info("Database Connection Successfull");

			String checkQuery = "select * from ct_eep_empl_master where EMPL_ID = ?";
			// String insertQuery =
			// "insert into ct_eep_empl_master values(?,?,?,?,?,?,?,?,?,?,?)";
			insertQuery = "insert into ct_eep_empl_master values(?,?,?,?,?,?,?,?,?,?,?)";
			updateQuery = "update ct_eep_empl_master set EMPL_ID = ?, SOURCE = ?,EMPL_ID_SRC =?,EMPL_DISC_GROUP_CODE=?,EMPL_NUMBER=?,FIRSTNAME=?,LASTNAME=?,"
					+ "EMPL_STATUS_CODE=?,POSITION_CODE=?,EMAIL=?,HOMESTORENUMBER=? where EMPL_ID = ? ";

			preParedstatementOne = connection.prepareStatement(insertQuery);

			preparedStatement = connection.prepareStatement(checkQuery);

			dbConnectionsSet = Boolean.TRUE;
		} catch (IOException e) {
			logger.info(e.getMessage());
			logger.info("Please check for file or location.");
		} catch (ClassNotFoundException e) {
			logger.info("Driver Loading is failed.");
			logger.warn(e.getMessage());
			msgContent = "Driver Loading is failed. \n\n"
					.concat(e.getMessage());
			mailing();
		} catch (SQLException e) {
			logger.info("Database Connection Failed");
			logger.warn(e.getMessage());
			logger.info("Please check for connection details.");
			msgContent = "Database Connection Failed.\n\nPlease check for connection details.\n\n"
					.concat(e.getMessage());
			mailing();
		}

	}

	/**
	 * This mailing method mails the error message through e-mail
	 */
	private static void mailing() {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(msgFrom));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(msgTo));
			message.setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(msgCc));
			message.setSubject(subject);
			message.setText("Date: " + currentMailDateTime + "\n\n"
					+ "Subject:" + subject + " - Description:" + description
					+ "\n\n" + msgContent + "\n\n\n\n" + advertisement);

			Transport.send(message);

		} catch (MessagingException me) {
			logger.warn("Please Check your mailing credentials.");
			logger.info(me.getMessage());
		}

	}

	/*
	 * When the parser encounters plain text (not XML elements), it calls(this
	 * method, which accumulates them in a string buffer
	 */
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}

	/*
	 * Every time the parser encounters the beginning of a new element, it calls
	 * this method, which resets the string buffer
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("Record")) {
			emp = new Employee();
		}
	}

	/*
	 * When the parser encounters the end of an element, it calls this method
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("Record")) {
			// add it to the list
			empList.add(emp);
		} else if (qName.equalsIgnoreCase("EmployeeNumber")) {
			emp.setEmployeeNumber(temp);
		} else if (qName.equalsIgnoreCase("EmployeeFirstName")) {
			emp.setEmployeeFirstName(temp);
		} else if (qName.equalsIgnoreCase("EmployeeLastName")) {
			emp.setEmployeeLastName(temp);
		} else if (qName.equalsIgnoreCase("BirthDate")) {
			emp.setBirthDate(temp);
		} else if (qName.equalsIgnoreCase("Email")) {
			emp.setEmail(temp);
		} else if (qName.equalsIgnoreCase("Cellphone")) {
			emp.setCellphone(temp);
		} else if (qName.equalsIgnoreCase("EmpStatusName")) {
			emp.setEmpStatusName(temp);
		} else if (qName.equalsIgnoreCase("EmpStatusXRef")) {
			emp.setEmpStatusXRef(temp);
		} else if (qName.equalsIgnoreCase("SupervisorName")) {
			emp.setSupervisorName(temp);
		} else if (qName.equalsIgnoreCase("JobName")) {
			emp.setJobName(temp);
		} else if (qName.equalsIgnoreCase("HomeStoreNumber")) {
			emp.setHomeStoreNumber(temp);
		} else if (qName.equalsIgnoreCase("EmployeeDiscountGroup")) {
			emp.setEmployeeDiscountGroup(temp);
		} else if (qName.equalsIgnoreCase("xRef")) {
			emp.setxRef(temp);
		}
	}

	/**
	 * The checkIfEmployeeExists method checks whether any duplicate entries are
	 * there in the ct_eep_empl_master table.
	 */
	private static Boolean checkIfEmployeeExists(String employeeId)
			throws SQLException {
		boolean empExists = Boolean.FALSE;

		preparedStatement.setString(1, employeeId);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			/*
			 * logger.warn("Employee is already present in the database with ID : "
			 * + employeeId);
			 */
			nosaveList++;
			ids.add(employeeId);
			return Boolean.TRUE;
		}

		return empExists;
	}

	/**
	 * The saveList method saves the values from xml file to the
	 * ct_eep_empl_master table.
	 * 
	 */
	private Boolean saveList(String fileName) throws Exception {

		int savedList = 0;
		int updateList = 0;
		int totalList = 0;

		nosaveList = 0;

		try {
			logger.info("Processing file with name " + fileName);
			logger.info("------------------------------------------");
			for (Employee employee : empList) {
				Boolean isEmployeeExist = checkIfEmployeeExists(employee
						.getxRef());
				/*
				 * String insertQuery =
				 * "insert into ct_eep_empl_master values(?,?,?,?,?,?,?,?,?,?,?)"
				 * ; String updateQuery =
				 * "update ct_eep_empl_master set EMPL_ID = ?, SOURCEIE = ?,EMPL_ID_SRC =?,EMPL_DISC_GROUP_CODE=?,EMPL_NUMBER=?,FIRSTNAME=?,LASTNAME=?,"
				 * +
				 * "EMPL_STATUS_CODE=?,POSITION_CODE=?,EMAIL=?,HOMESTORENUMBER=? where EMPL_ID = ? "
				 * ;
				 */
				if (!isEmployeeExist) {

					preParedstatementOne = connection
							.prepareStatement(insertQuery);

					preParedstatementOne.setString(1, employee.getxRef()
							.toString());
					preParedstatementOne.setString(2, "E");
					preParedstatementOne.setString(3,
							"E:".concat(employee.getxRef().toString()));
					preParedstatementOne.setString(4, employee
							.getEmployeeDiscountGroup().toString());
					preParedstatementOne.setString(5, employee
							.getEmployeeNumber().toString());
					preParedstatementOne.setString(6, employee
							.getEmployeeFirstName().toString());
					preParedstatementOne.setString(7, employee
							.getEmployeeLastName().toString());
					preParedstatementOne.setString(8, employee
							.getEmpStatusXRef().toString());
					preParedstatementOne.setString(9, employee.getJobName()
							.toString());
					preParedstatementOne.setString(10, employee.getEmail()
							.toString());
					preParedstatementOne.setString(11, employee
							.getHomeStoreNumber().toString());

					preParedstatementOne.executeUpdate();

					savedList++;
					logger.info("Employee added with ID :"
							+ employee.getxRef().toString());

				} else {
					preParedstatementOne = connection
							.prepareStatement(updateQuery);

					preParedstatementOne.setString(1, employee.getxRef()
							.toString());

					preParedstatementOne.setString(2, "E");
					preParedstatementOne.setString(3,
							"E:".concat(employee.getxRef().toString()));
					preParedstatementOne.setString(4, employee
							.getEmployeeDiscountGroup().toString());
					preParedstatementOne.setString(5, employee
							.getEmployeeNumber().toString());
					preParedstatementOne.setString(6, employee
							.getEmployeeFirstName().toString());
					preParedstatementOne.setString(7, employee
							.getEmployeeLastName().toString());
					preParedstatementOne.setString(8, employee
							.getEmpStatusXRef().toString());
					preParedstatementOne.setString(9, employee.getJobName()
							.toString());
					preParedstatementOne.setString(10, employee.getEmail()
							.toString());
					preParedstatementOne.setString(11, employee
							.getHomeStoreNumber().toString());

					preParedstatementOne.setString(12, employee.getxRef()
							.toString());

					preParedstatementOne.executeUpdate();

					updateList++;
					logger.info("Employee updated with ID :"
							+ employee.getxRef().toString());
				}
				preParedstatementOne.close();
				/*
				 * logger.info("Employee added with ID :" +
				 * employee.getxRef().toString());
				 */
				// }

			}
			totalList = savedList + updateList;
			logger.info("Total number of Employee added :" + savedList);
			logger.info("Total number of Employee updated :" + updateList);

			if (empList.size() == totalList) {
				key = Boolean.TRUE;

			} else {
				key = Boolean.FALSE;
				msgContent = "All the employee data are not added.";
				mailing();
			}

			if (updateList > 0) {
				msgContent = "Following "
						+ updateList
						+ " Employee ID is/are already present in the database . \n\n"
								.concat(ids.toString()).concat(
										"\n\n And hence updated to the database.\n\nDetails for file : "
												+ fileName);

				mailing();
			}

		} catch (SQLException e) {
			logger.warn(e.getMessage());
			msgContent = e.getMessage();
			mailing();
		}
		empList = new ArrayList<Employee>();
		ids = new ArrayList<String>();
		return key;

	}

	/**
	 * The processFile method moves the xml file from incoming folder to Archive
	 * folder if processed successfully.
	 * 
	 */
	private static void processFile(String fileName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		// get current date time with Date()
		Date date = new Date();

		currentDateTime = dateFormat.format(date);

		try {
			File afile = new File(inDir + fileName);

			if (key) {
				File bfile = new File(outDir.concat(currentDateTime.toString())
						+ fileName);

				inStream = new FileInputStream(afile);
				outStream = new FileOutputStream(bfile);
				logger.info("File processed successfully and moved to "
						+ outDir + " with file name "
						+ currentDateTime.toString() + fileName);

			} else {
				File bfile = new File(errDir.concat(currentDateTime.toString())
						+ fileName);

				inStream = new FileInputStream(afile);
				outStream = new FileOutputStream(bfile);
				logger.info("File processing is unsuccessfull and moved to "
						+ errDir + " with file name "
						+ currentDateTime.toString() + fileName);
			}

			byte[] buffer = new byte[1024];

			int length;

			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// delete the original file
			afile.delete();
		} catch (Exception e) {
			// logger.info(e.getMessage());
		}
	}
}
