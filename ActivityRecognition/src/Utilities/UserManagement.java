package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.jasypt.util.password.StrongPasswordEncryptor;

import GUI.GUI;
import Run.ActivityRecognition;

public class UserManagement {
	
	public UserManagement(ActivityRecognition analyser, GUI frame) {
		this.analyser = analyser;
		this.frame=frame;
		passwordEncryptor = new StrongPasswordEncryptor();
	}

	private Connection conn;
	private StrongPasswordEncryptor passwordEncryptor;
	private ActivityRecognition analyser;
	private GUI frame;

	//Get data to connect to database.
	//For testing, a database is run on localhost
	private Properties getConnectionData() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();     
		encryptor.setPassword("*****");
		
		Properties props = new EncryptableProperties(encryptor); 

        String fileName = "src/Database/db.properties";

        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException ex) {
        	warnUser();
        }

        return props;
    }

	//establish connection to database
	private void establishConnection() {
		Properties props = getConnectionData();
		String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String passwd = props.getProperty("db.passwd");
        try 
        {
        	conn = DriverManager.getConnection(url, user, passwd);
        	System.out.println("Connect to database successfully...");

        }
        catch (SQLException se) 
		{
			se.printStackTrace();
			warnUser();
		}
		catch (Exception a)
		{
			a.printStackTrace();
			warnUser();
		}
	}
	
	
	private void notifyRecognition(UserData data) {
		analyser.getData(data);
	}
	
	//Display warning to user if cannot connect to database
	private void warnUser() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cannot connect to database, please check your network connection or contact support team.");
		frame.showWarnMes(builder);
	}
	
	public boolean Login(String username,String pass) {
		boolean status = false;
		establishConnection();
		UserData user =null;
	    String query = "SELECT `password` FROM `userdata` WHERE `username`= ?";

	    try {
		    PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setString(1, username);
	    	ResultSet rs = preparedStmt.executeQuery();
	    	if(rs.next()) {
	    		String databasePassword = rs.getString("password");
	    		if (passwordEncryptor.checkPassword(pass,databasePassword)) {
	    			query = "SELECT * FROM `userdata` WHERE `username`= ?";
	    			preparedStmt = conn.prepareStatement(query);
	    		    preparedStmt.setString(1, username);
	    		    rs = preparedStmt.executeQuery();
	    		    if(rs.next()) {
	    		    	user = new UserData(rs.getString("name"),rs.getInt("age"),rs.getBoolean("sex"),rs.getString("address"),rs.getString("contact"),rs.getInt("height"),rs.getInt("weight"));
	    		    	notifyRecognition(user);
	    		    	status = true;
	    		    }
	    		}
	    	}
	    	
	    }
	    catch (SQLException se) 
		{
			se.printStackTrace();
			warnUser();
		}
		catch (Exception a) 
		{
			a.printStackTrace();
			warnUser();
		}
	    return status;
	}
	
	public void logOut() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	private boolean checkUsername(String username) {
		boolean usernameExist=false;
		String query = "SELECT `username` FROM `userdata` WHERE `username`= ?";
		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, username);
		    ResultSet rs = preparedStmt.executeQuery();
	    	if(rs.next()) {
	    		usernameExist=true;
	    	}
	    }
		catch (SQLException se) 
		{
			se.printStackTrace();
			warnUser();
		}
		catch (Exception a) //catch block
		{
			a.printStackTrace();
			warnUser();
		}
		return usernameExist;
	}
	public boolean signUp(String username, String password, String name, int age, boolean isFemale, String address, String contact, int height, int weight) {
		boolean status = false;
		establishConnection();
		UserData user = null;
		if(checkUsername(username)) {
			System.out.println("Username already exists!");
			return false;
		}
		else {
		String query = "INSERT INTO `accidentreport`.`userdata`(`username`,`password`,`ts`,`name`,`age`,`sex`,`address`,`contact`,`height`,`weight`) "
				+ "VALUES(?,?,current_timestamp,?,?,?,?,?,?,?)";
		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, passwordEncryptor.encryptPassword(password));
			preparedStmt.setString(3, name);
			preparedStmt.setInt(4, age);
			preparedStmt.setBoolean(5, isFemale);
			preparedStmt.setString(6, address);
			preparedStmt.setString(7, contact);
			preparedStmt.setInt(8, height);
			preparedStmt.setInt(9, weight);
	    	int rs = preparedStmt.executeUpdate();
	    	if(rs!=0) {
	    		user = new UserData(name,age,isFemale,address,contact,height,weight);
	    		notifyRecognition(user);
	    		status = true;
	    	}
		}
		catch (SQLException se) 
		{
			se.printStackTrace();
			warnUser();
		}
		catch (Exception a)
		{
			a.printStackTrace();
			warnUser();
		}
		}
		return status;
	}
	//User needs to re-enter password as verification for updating the data
	public boolean updateData(String username, String password, String name, int age, boolean isFemale, String address, String contact, int height, int weight) {
		boolean status = false;
		UserData user = null;
		String query = "SELECT `password` FROM `userdata` WHERE `username`= ?";
		 try {
			    PreparedStatement preparedStmt = conn.prepareStatement(query);
			    preparedStmt.setString(1, username);
		    	ResultSet rs = preparedStmt.executeQuery();
		    	if(rs.next()&&(passwordEncryptor.checkPassword(password,rs.getString("password")))) {
		    		query = "UPDATE `userdata` SET `ts` = current_timestamp, `name` = ?, `age` = ?, `sex` = ?, `address` = ?, `contact` = ?, `height` = ?, `weight` = ? WHERE `username` = ?";
		    		preparedStmt = conn.prepareStatement(query);
		    		preparedStmt.setString(1, name);
		    		preparedStmt.setInt(2, age);
		    		preparedStmt.setBoolean(3, isFemale);
		    		preparedStmt.setString(4, address);
		    		preparedStmt.setString(5, contact);
		    		preparedStmt.setInt(6, height);
		    		preparedStmt.setInt(7, weight);
		    		preparedStmt.setString(8, username);
		    		int rs1 = preparedStmt.executeUpdate();
		    		if(rs1!=0) {
		    			user = new UserData(name,age,isFemale,address,contact,height,weight);
		    			notifyRecognition(user);
		    			status = true;
		    		}
		    	}
		 }
		 catch (SQLException se) 
		 {
			 	se.printStackTrace();
			 	warnUser();
		 }
		 catch (Exception a) 
		 {
				a.printStackTrace();
				warnUser();
		 }
		 return status;
	}
}
