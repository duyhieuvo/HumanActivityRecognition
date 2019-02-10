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


import Run.ActivityRecognition;

public class UserManagement {
	private volatile static UserManagement uniqueInstance;
	private UserManagement(ActivityRecognition analyser) {
		this.analyser = analyser;
		passwordEncryptor = new StrongPasswordEncryptor();
	}
	public static UserManagement getInstance(ActivityRecognition analyser) {
		if (uniqueInstance == null) {
			synchronized (UserManagement.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new UserManagement(analyser);
				}
			}
		}
			return uniqueInstance;
		}
	
	private Connection conn;
	private StrongPasswordEncryptor passwordEncryptor;
	private ActivityRecognition analyser;

	private static Properties getConnectionData() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();     
		encryptor.setPassword("Gatsby");
		
		Properties props = new EncryptableProperties(encryptor); 

        String fileName = "src/Database/db.properties";

        try (FileInputStream in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException ex) {
        	
        }

        return props;
    }

	private void establishConnection() {
		Properties props = getConnectionData();
		String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String passwd = props.getProperty("db.passwd");
        try  //try block
        {
        	conn = DriverManager.getConnection(url, user, passwd);
        	System.out.println("Connect to database successfully...");

        }
        catch (SQLException se) 
		{
			//handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception a) //catch block
		{
			a.printStackTrace();
		}
	}
	private void notifyObserver(UserData data) {
		analyser.getData(data);
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
	    		    	notifyObserver(user);
	    		    	status = true;
	    		    }
	    		}
	    	}
	    	
	    }
	    catch (SQLException se) 
		{
			//handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception a) //catch block
		{
			a.printStackTrace();
		}
	    return status;
	}
	
	public void logOut() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			//handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception a) //catch block
		{
			a.printStackTrace();
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
	    		notifyObserver(user);
	    		status = true;
	    	}
		}
		catch (SQLException se) 
		{
			//handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception a) //catch block
		{
			a.printStackTrace();
		}
		}
		return status;
	}
	//User needs to re-enter password as verification for updating the data
	public boolean updateData(String username, String password, String name, int age, boolean isFemale, String address, String contact, int height, int weight) {
		boolean status = false;
		//establishConnection();
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
		    			notifyObserver(user);
		    			status = true;
		    		}
		    	}
		 }
		 catch (SQLException se) 
		 {
				//handle errors for JDBC
			 	se.printStackTrace();
		 }
		 catch (Exception a) //catch block
		 {
				a.printStackTrace();
		 }
		 return status;
	}
}
