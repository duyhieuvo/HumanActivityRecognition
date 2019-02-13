package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import Utilities.*;
import Run.ActivityRecognition;

public class GUI extends JFrame implements MessageBox{

	private static final long serialVersionUID = 1L;
	
	//GUI components
	private			JPanel contentPane;
	private 		JButton btnStart, btnStop, btnSignup, btnLogout, btnEdit, btnAbort, btnSendEmail;
	private static	JButton btnLogin;
	
	private 		JLabel 	lblPatients, lblPassword, lblUser, lblConfPass, lblAge, lblGender, lblAddress, lblPatientName, 
						lblSignUpRequest, lblRelatives, lblEmail, lblNotes, lblEditRequest, lblName, lblHeight, lblWeight;
	private			JTextField 	txtUser, txtAge, txtGender, txtAddress, txtEmail, txtName, txtHeight, txtWeight;
	
	private			JPasswordField txtPass, txtConfPass;
	private 		JCheckBox checkboxSignUp, checkboxEdit;
	

	//Message box String
	private 		StringBuilder infoMessage = new StringBuilder("");
	private 		StringBuilder warnMessage = new StringBuilder("");
	 
	//read value from JText
	static 			String username, password, confPassword, name, addr, ageString, weightString, heightString, gender, email;

	static			int age, weight, height;
	
	static 	boolean sex = false; //false is male, true is female
	private static 	ArrayList<String> patientData = new ArrayList<String>();	//List of patient input data
	private static 	ArrayList<String> patientEdit = new ArrayList<String>();	//List of patient edit data
	private 		List<Object> patientField = new ArrayList <>();				//List of patient input field (JTextField z.B txtUser, ...)
	private 		List<Object> signUpField = new ArrayList<> ();				//List of signup field (all patient input fields except name and pass)
	private 		List<Object> signUpLabel = new ArrayList<> ();				//List of labels of signup fields
	private static	List<Object> patientOutput = new ArrayList<Object>();				//Write patient data out
	
//	private static	DataGUI patGUI; 
//	private			List<Double> scores = new ArrayList<>();
	
	//boolean login, signup
	static boolean login = false, signup = false, logout = false, edit = false;
	static boolean editrequest = false;
	
	 
	//number of fields (JTextField) will be checked
	private int fieldCheck; 
	
	private 		CheckInteger checkInteger = new CheckInteger(patientData, patientEdit, warnMessage);
	private			CheckGender checkGender = new CheckGender(patientData, patientEdit, warnMessage);
	private			CheckEmail checkEmail = new CheckEmail(patientData, patientEdit, warnMessage);

	
	//User management
	private String currentUser;
	private UserManagement manager;
	private GenerateAlert alert;
	private ActivityRecognition recognition;
	
	private mySwingWorker worker;
	private boolean running =true;
	
	private int abortCounter = 0;
	
	private void Initialize() {
		alert = new GenerateAlert(this);
		recognition = new ActivityRecognition(alert);
		manager = UserManagement.getInstance(recognition);
		
	}
	
//--------------------------------------------------BUTTON METHOD----------------------------------------------------------------
	private void Login() {
		//add patient input fields to ArrayList
		addPatField(patientField);
		//add patient input values to ArrayList 
		addPatInput(patientData);
		
//		only check the first 2 inputs: username + password
		fieldCheck = 2;
        
        if (checkInput(patientData)) {
        	if(manager.Login(patientData.get(0), patientData.get(1))) {
        		currentUser = patientData.get(0);
        		
        		addPatientName(currentUser);
        		infoMessage.append("Login successfully");
    			showInfoMes(infoMessage);
    			
    			btnEdit.setVisible(true);
    			btnLogin.setEnabled(false);
    			btnLogin.setVisible(false);
    			btnLogout.setEnabled(true);
    			btnLogout.setVisible(true);
    			btnSignup.setEnabled(false);
    			btnStart.setEnabled(true);
    			btnStart.setVisible(true);
    			btnStop.setVisible(true);
    			btnSendEmail.setEnabled(true);
    			btnSendEmail.setVisible(true);
    			
    			checkboxSignUp.setEnabled(false);
    			checkboxEdit.setVisible(true);
    			checkboxEdit.setEnabled(true);;
    			
    			lblEditRequest.setVisible(true);
    			lblPatientName.setVisible(true);
    			lblPassword.setVisible(false);
    			lblUser.setVisible(false);
    			
    			txtUser.setVisible(false);
    			txtName.setVisible(false);
    			txtPass.setVisible(false);
    			
    			//disable all fields
    			setEnabled(patientField, false);
    			
    			login = true;
        	}
        	else {
        		warnMessage.append("Username or password is not correct \n");
        		showWarnMes(warnMessage);
        		removeInput(patientData);
        	}
		}
		else {
			showWarnMes(warnMessage);
			removeInput(patientData);
		}
		
		
		clearText(patientField);
//		removeField(listPatientInput);
	}
	
	private void Logout() {
		//if stop button is not pressed then log out
		if (!btnStop.isEnabled()) {
			if(!checkboxEdit.isSelected()) {
				btnEdit.setEnabled(false);
				btnEdit.setVisible(false);
				btnLogout.setEnabled(false);
				btnLogout.setVisible(false);
				btnLogin.setEnabled(true);
				btnLogin.setVisible(true);
				btnSendEmail.setEnabled(false);
				btnSendEmail.setVisible(false);
				
				
				
				btnStart.setEnabled(false);
				btnStart.setVisible(false);
				btnStop.setEnabled(false);
				btnStop.setVisible(false);
				
				checkboxSignUp.setEnabled(true);
				checkboxSignUp.setSelected(false);
				checkboxEdit.setEnabled(false);
				checkboxEdit.setVisible(false);
				
				txtUser.setEnabled(true);
				txtUser.setVisible(true);
				txtPass.setEnabled(true);
				txtPass.setVisible(true);
				
				lblUser.setVisible(true);
				lblPassword.setVisible(true);
				lblEditRequest.setVisible(false);
				lblPatientName.setVisible(false);
				
				setVisible(signUpField, false);
				
				infoMessage.append("Logout successfully");
				showInfoMes(infoMessage);
				
//				login = false;
//				signup = false;
				
				//remove all the processing data
				removeInput(patientData);
				removeInput(patientEdit);
				manager.logOut();
			}
			else {
				warnMessage.append("Please uncheck the Edit checkbox!!!\n");
				showWarnMes(warnMessage);
			}	
		}
		else {
			warnMessage.append("Please press the Stop button!!!\n");
			showWarnMes(warnMessage);
		}		
	}
	
	private void Signup() {
		//add patient input fields to ArrayList
		addPatField(patientField);
		//add patient input values to ArrayList
		addPatInput(patientData);
		
		fieldCheck = patientData.size();
		
		//check all inputs are filled
		if (checkInput(patientData)){ 
			//check if age are integers; gender is M or F
//			if (checkAge(patientData, 3) && checkGender(patientData, 4) && checkEmail(patientData, 6)) {
			if (checkInteger.check(patientData, 4) && checkInteger.check(patientData, 5) && checkInteger.check(patientData, 6) 
					&& checkGender.check(patientData, 7) && checkEmail.check(patientData, 9)){
				//check if passwords are matched
				if (patientData.get(1).equals(patientData.get(2)) && !(patientData.get(1) == null | patientData.get(1).trim().isEmpty())) {
					//check if username is existed
					if(patientData.get(7).equals("F")) {
	    				sex = true;
	    			}
	    			else if (patientData.get(7).equals("M")){
	    				sex = false;
	    			}
					if (manager.signUp(patientData.get(0), patientData.get(1),patientData.get(3),Integer.parseInt(patientData.get(4)),sex,patientData.get(8),patientData.get(9),Integer.parseInt(patientData.get(5)),Integer.parseInt(patientData.get(6)))) {	
						currentUser = patientData.get(0);
						
						infoMessage.append("Signup successfully");
						showInfoMes(infoMessage);
						signup = true;
						addPatientName(currentUser);
						setEnabled(patientField, false);
						setVisible(signUpField, false);
						setVisible(signUpLabel, false);
						
						btnLogin.setEnabled(false);
		    			btnLogin.setVisible(false);
		    			btnLogout.setEnabled(true);
		    			btnLogout.setVisible(true);
		    			btnSignup.setEnabled(false);
		    			btnStart.setEnabled(true);
		    			btnStart.setVisible(true);
		    			btnEdit.setVisible(true);
		    			
		    			checkboxSignUp.setEnabled(false);
		    			checkboxSignUp.setSelected(false);
		    			checkboxEdit.setVisible(true);
		    			checkboxEdit.setEnabled(true);
		    			
		    			lblEditRequest.setVisible(true);
		    			lblPatientName.setVisible(true);
		    			lblPassword.setVisible(false);
		    			lblUser.setVisible(false);
		    			
		    			txtUser.setVisible(false);
		    			txtPass.setVisible(false);
		    			
		    			
					}
					else {
						warnMessage.append("This username is existed \n");
						showWarnMes(warnMessage);
						removeInput(patientData);
					}
				}
				else {
					warnMessage.append("Password does not match the confirm password \n");
					showWarnMes(warnMessage);
					removeInput(patientData);
				}
			}
			else {
				showWarnMes(warnMessage); 
				removeInput(patientData);
			}
		}
		else {
			showWarnMes(warnMessage); //show missing fields
			removeInput(patientData);
		}
		
		//set all text input to blank
		clearText(patientField);
		//remove components in these two lists
//		removeInput(patientData);
//		removeField(patientField);
		
//		signup = false;
	}
	
	private void Edit() {
		addPatEdit(patientEdit);
		editrequest = true;
		
		fieldCheck = patientEdit.size();
		
		//check all inputs are filled
		if (checkInput(patientEdit)){ 
				//check if age, phone are integers; gender is M or F
//				if (checkAge(patientEdit, 1) && checkGender(patientEdit, 2) && checkEmail(patientEdit, 4)) {
			if (checkInteger.check(patientEdit, 2) && checkInteger.check(patientEdit, 3) && checkInteger.check(patientEdit, 4) 
					&& checkGender.check(patientEdit, 5) && checkEmail.check(patientEdit, 7)) {
				if(patientData.get(5).equals("F")) {
    				sex = true;
    			}
    			else if (patientData.get(5).equals("M")){
    				sex = false;
    			}
				//check if password are matched with the existed user
				if (manager.updateData(currentUser, patientEdit.get(0),patientEdit.get(1),Integer.parseInt(patientEdit.get(2)), sex, patientEdit.get(6), patientEdit.get(7),Integer.parseInt(patientEdit.get(3)),Integer.parseInt(patientEdit.get(4)))){
					infoMessage.append("Edit successfully");
					showInfoMes(infoMessage);
					signup = true;
					
					setEnabled(patientField, false);
					setVisible(signUpField, false);
					setVisible(signUpLabel, false);
					
					btnLogin.setEnabled(false);
	    			btnLogin.setVisible(false);
	    			btnLogout.setEnabled(true);
	    			btnLogout.setVisible(true);
	    			btnSignup.setEnabled(false);
	    			btnStart.setEnabled(true);
	    			btnStart.setVisible(true);
	    			btnEdit.setEnabled(false);
	    			
	    			checkboxSignUp.setEnabled(false);
	    			checkboxEdit.setSelected(false);
	    			
	    			lblEditRequest.setVisible(true);	
	    			
	    			
	
	    			removeAllInputData();
	    		
				}
				else {
					warnMessage.append("Password does not match");
					showWarnMes(warnMessage); 
				}
			}
			else {
				
				showWarnMes(warnMessage);//show which integer fields are not appropriate
			}
			
		}
		else {
			showWarnMes(warnMessage); //show missing fields
		}
		
		//set all text input to blank
		clearText(patientField);
		editrequest = false;
		//remove components in these two lists
		removeInput(patientEdit);
	}

	private void Start() {
		if(!checkboxEdit.isSelected()) {
			//button states
			btnEdit.setEnabled(false);
			btnLogout.setEnabled(false);
			btnStart.setEnabled(false);
			btnStart.setVisible(false);
			btnStop.setEnabled(true);
			btnStop.setVisible(true);
			checkboxEdit.setSelected(false);
			checkboxEdit.setEnabled(false);
			
			(worker = new mySwingWorker()).execute();
		
//			long elapsedTime = 0;
//			long startTime = System.currentTimeMillis();
//			while(true) {
//				elapsedTime = (System.currentTimeMillis() - startTime)/1000;
//				if(elapsedTime>5){
//					break;
//				}
//			}
//			recognition.Fall();
			//method
//			patGUI = new DataGUI();
//			patGUI.createAndShowGui(); 
//			addDataGUI();
		}
		else {
			warnMessage.append("Please uncheck the Edit button!!!\n");
			showWarnMes(warnMessage);
		}
		
	}
	
	private void Stop() {
		btnEdit.setEnabled(true);
		btnLogout.setEnabled(true);
		btnStart.setEnabled(true);
		btnStart.setVisible(true);
		btnStop.setEnabled(false);
		btnStop.setVisible(false);
		btnEdit.setEnabled(false);
		checkboxEdit.setEnabled(true);

		recognition.stop();
//		worker.cancel(true);
//		worker=null;
	
//		removeDataGUI();
	}
	
	//checkbox method enable Signup request
	private void checkBoxSignUp() {
		if (checkboxSignUp.isSelected()) {
//			addSignUpField(signUpField);
//			addSignUpLabel(signUpLabel);
			
			setVisible(signUpField, true);
			setEnabled(signUpField, true);
			setVisible(signUpLabel, true);
			
			btnSignup.setEnabled(true);
			btnLogin.setEnabled(false);
		}
		else {
			setVisible(signUpField, false);
			setEnabled(signUpField, false);
			setVisible(signUpLabel, false);
			
			btnSignup.setEnabled(false);
			btnLogin.setEnabled(true);
		}
	}
	
	//checkbox method enable Edit request
	private void checkBoxEdit() {
		if (checkboxEdit.isSelected()) {
//			addSignUpField(signUpField);
//			addSignUpLabel(signUpLabel);
			
			setVisible(signUpField, true);
			setEnabled(signUpField, true);
			setVisible(signUpLabel, true);
			

			
			btnEdit.setEnabled(true);
			btnStart.setEnabled(false);
			btnSendEmail.setEnabled(false);
			lblPassword.setVisible(true);
			txtPass.setVisible(true);
			
			infoMessage.append("Please add the confirmed password at field 3 \n"
					+ "and the other information in the other fields.");
			showInfoMes(infoMessage);
//			edit = true;
		}
		else {
			setEnabled(signUpField, false);
			setVisible(signUpField, false);
			setVisible(signUpLabel, false);
			lblPassword.setVisible(false);
			txtPass.setVisible(false);
			
			btnEdit.setEnabled(false);
			btnStart.setEnabled(true);
			btnSendEmail.setEnabled(true);
//			edit = false;
		}
	}
	
	private void addPatientName(String Patientname) {
		lblPatientName.setText(Patientname);
	}
	
//	-------------------------------------------------------DISPLAY METHOD--------------------------------------------------------------------
	
	//clear all the text inputs in JTextField
	private void clearText(List<Object> list) {
		for(int i=0; i<list.size(); i++) {
			((JTextComponent) list.get(i)).setText(null);
		}
	}
	
	//visualize input fields (JTextField, JLabel, ...)
	private void setVisible(List<Object> listText, boolean allow) {
		for (int i = 0; i<listText.size(); i++) {
			((Component) listText.get(i)).setVisible(allow);
		}
	}
	
	//enable/disable fields (JTextField, JLabel, ...)
	private void setEnabled(List<Object> listText, boolean allow) {
		for (int i = 0; i<listText.size(); i++) {
			((Component) listText.get(i)).setEnabled(allow);
		}
	}
	
	
//	---------------------------------------------------ADD AND REMOVE DATA-------------------------------------------------------------------
	
	//add Patient data inputs (JTextField.getText() )
	@SuppressWarnings("deprecation")
	private void addPatInput(ArrayList<String> patientData) {
		this.patientData = patientData;
		
		username = txtUser.getText();
		password = txtPass.getText();
		confPassword = txtConfPass.getText();
		name = txtName.getText();
		ageString = txtAge.getText();
		heightString = txtHeight.getText();
		weightString = txtWeight.getText();
		gender = txtGender.getText();
		addr = txtAddress.getText();
		email = txtEmail.getText();
		
		patientData.add(username);
		patientData.add(password);
		patientData.add(confPassword);
		patientData.add(name);
		patientData.add(ageString);
		patientData.add(heightString);
		patientData.add(weightString);
		patientData.add(gender);
		patientData.add(addr);
		patientData.add(email);
	}
	
	

	//add Patient edited data inputs (String) (JTextField.getText() )
	@SuppressWarnings("deprecation")
	private void addPatEdit(ArrayList<String> patientEdit) {
		this.patientEdit = patientEdit;
		
		confPassword = txtConfPass.getText();
		name = txtName.getText();
		ageString = txtAge.getText();
		gender = txtGender.getText();
		heightString = txtHeight.getText();
		weightString = txtWeight.getText();
		addr = txtAddress.getText();
		email = txtEmail.getText();
		
		patientEdit.add(confPassword);
		patientEdit.add(name);
		patientEdit.add(ageString);
		patientEdit.add(heightString);
		patientEdit.add(weightString);
		patientEdit.add(gender);
		patientEdit.add(addr);
		patientEdit.add(email);
	}
	
	private static void addPatOutput(List<Object> patientOutput) {
//		this.patientOutput = patientOutput;
		age = Integer.parseInt(ageString);
		weight = Integer.parseInt(weightString);
		height = Integer.parseInt(heightString);
		
		patientOutput.add(username);
		patientOutput.add(password);
		patientOutput.add(confPassword);
		patientOutput.add(name);
		patientOutput.add(age);
		patientOutput.add(height);
		patientOutput.add(weight);
		patientOutput.add(sex);
		patientOutput.add(addr);
		patientOutput.add(email);
	}
	
	//add Patient fields (JTextField). This will be use for setVisible method. Patient fields get from 1->7
	private void addPatField(List<Object> patientField) {
		this.patientField = patientField;
		patientField.add(txtUser);
		patientField.add(txtPass);
		patientField.add(txtConfPass);
		patientField.add(txtName);
		patientField.add(txtAge);
		patientField.add(txtHeight);
		patientField.add(txtWeight);
		patientField.add(txtGender);
		patientField.add(txtAddress);
		patientField.add(txtEmail);
	}
	
	//add Signup fields (JTextField). It is similiar as Patient fields except username and password). Signup fields get from 3->7
	private void addSignUpField(List<Object> signUpField) {
		this.signUpField = signUpField;
		signUpField.add(txtConfPass);
		signUpField.add(txtName);
		signUpField.add(txtAge);
		signUpField.add(txtHeight);
		signUpField.add(txtWeight);
		signUpField.add(txtGender);
		signUpField.add(txtAddress);
		signUpField.add(txtEmail);
	}
	
	//add Signup labels (JLabel). Use method setVisible to visualize these labels or not.
	private void addSignUpLabel(List<Object> signUpLabel) {
		this.signUpLabel = signUpLabel;
		signUpLabel.add(lblConfPass);
		signUpLabel.add(lblName);
		signUpLabel.add(lblAge);
		signUpLabel.add(lblHeight);
		signUpLabel.add(lblWeight);
		signUpLabel.add(lblGender);
		signUpLabel.add(lblAddress);
		signUpLabel.add(lblEmail);
		signUpLabel.add(lblRelatives);
		signUpLabel.add(lblNotes);
	}
	
	//remove all components of a list. Use when successful logout or failed login, signup
	private void removeInput(ArrayList<String> list) {
		list.clear();
	}
	
//	-------------------------------------------------------INPUT CHECK----------------------------------------------------------------------
	
	//check whether all inputs of a list are filled. Spaces and blanks will be notified.
	private boolean checkInput(ArrayList<String> patientData) {
		this.patientData = patientData;
		int error = 0;

		//check if the field is blank or contains multiple spaces
		//if Edit option is chosen, the first field is field 3, therefore it is (i+3)
		//Meanwhile, signup option starts at field 1, therefore it is (i+1)
		if (editrequest) {
			for(int i=0; i<fieldCheck; i++) {
				if(patientData.get(i) == null | patientData.get(i).trim().isEmpty()) { 
					warnMessage.append("Missing input at field " + (i+3) + "\n");
					error++;
				}
			}
		}
		else {
			for(int i=0; i<fieldCheck; i++) {
				if(patientData.get(i) == null | patientData.get(i).trim().isEmpty()) { 
					warnMessage.append("Missing input at field " + (i+1) + "\n");
					error++;
				}
			}
		}
		
		if (error==0) {
			return true;
		}
		else return false;
	}	
	
	/*//check whether email is valid. Email is based on RFC 822.
	private boolean checkEmail(ArrayList<String> patientData, int i) {
		this.patientData = patientData;
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                "[a-zA-Z0-9_+&*-]+)*@" + 
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                "A-Z]{2,7}$"; 
		Pattern pat = Pattern.compile(emailRegex); 

		if (!pat.matcher(patientData.get(i)).matches()) {
        	warnMessage.append("Email: Invalid format");
        	return false;
        }
        else
        	return true;
	}
	
	//check whether age is integer using parseInt() method.
	private boolean checkAge(ArrayList<String> patientData, int i) {
		this.patientData = patientData;
		//check if the field is blank or contains multiple spaces
		if(!(patientData.get(i) == null | patientData.get(i).trim().isEmpty())) {
			try{ 
	            Integer.parseInt(patientData.get(i)); 
	        }  
	        catch (NumberFormatException e)  
	        { 
	            warnMessage.append("Age: Invalid integer number \n");
	            return false;
	        } 
		}
		return true;
	}
	
	//check whether gender is only F or M.
	private boolean checkGender(ArrayList<String> patientData, int i) {
		this.patientData = patientData;
		//check if the field is blank or contains multiple spaces
		if(!(patientData.get(i) == null | patientData.get(i).trim().isEmpty())) {
			if (!(patientData.get(i).equals("M") | patientData.get(i).equals("F"))){
				warnMessage.append("Gender: Please only write \"M\" or \"F\" ");
				return false;
			}
			else if (patientData.get(i).equals("F")) {
				sex = true;
			}
		}
		return true;
	}*/
	

	
//----------------------------------------------------MESSAGE BOX-----------------------------------------------------------------
	
	//show Information message box
	public void showInfoMes(StringBuilder infoMessage)
    {
		this.infoMessage = infoMessage;
        JOptionPane.showMessageDialog(null, infoMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
        clearMes(infoMessage);
    }
	
	//show Warning message box
	public void showWarnMes(StringBuilder warnMessage) {
		this.warnMessage = warnMessage;
		JOptionPane.showMessageDialog(null, warnMessage, "Warning", JOptionPane.WARNING_MESSAGE);
		clearMes(warnMessage);
	}
	
	public void showScheduledWarn(String message,int milli) {
		JOptionPane jop = new JOptionPane();
		jop.setMessageType(JOptionPane.PLAIN_MESSAGE);
		jop.setMessage(message);
		JDialog dialog = jop.createDialog(null, "Failed attempt");
		dialog.addComponentListener(new ComponentListener() {
			 public void componentHidden(ComponentEvent e)
			 {
			    System.out.println("dialog hidden");
			 }

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//dialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
		// Set a 2 second timer
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(milli);
				} catch (Exception e) {
				}
				dialog.dispose();
			}

		}).start();

		dialog.setVisible(true);
	}
	
	
	
	//clear Message box
	public void clearMes(StringBuilder message) {
		message.setLength(0);
	}		
		
//-----------------------------------------------------------FRAME--------------------------------------------------------------------
	
	//main GUI of the program
	public GUI() {
		Initialize();
		

		
		//GUI 
		setTitle("Human Activity Recognition");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 670);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//buttons
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Edit();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnEdit.setBounds(297, 249, 89, 23);
		contentPane.add(btnEdit);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login();
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnLogin.setBounds(297, 135, 89, 23);
		contentPane.add(btnLogin);
		
		btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logout();
			}
		});
		btnLogout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnLogout.setBounds(297, 135, 89, 23);
		contentPane.add(btnLogout);
		
		btnSignup = new JButton("Signup");
		btnSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Signup();
			}
		});
		btnSignup.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSignup.setBounds(297, 198, 89, 23);
		contentPane.add(btnSignup);
		
		btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					Start();
			}
		});
		btnStart.setBackground(Color.GREEN);
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnStart.setBounds(297, 375, 89, 38);
		contentPane.add(btnStart);
		btnStart.setEnabled(false);
		
		btnStop = new JButton("STOP");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Stop();
			}
		});
		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnStop.setBackground(Color.RED);
		btnStop.setBounds(297, 375, 89, 38);
		contentPane.add(btnStop);
		
		btnAbort = new JButton("Abort");
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abortCounter+=1;
				if(abortCounter==1) {
					btnAbort.setBounds(247, 425, 190, 23);
					btnAbort.setText("Are you really ok?");
				}else if(abortCounter==2) {
					abortCounter=0;
					btnAbort.setBounds(297, 425, 89, 23);
					btnAbort.setText("Abort");
					alert.notFall();
					(worker = new mySwingWorker()).execute();
					offAbortButton();
				}
			}
		});
		
		btnAbort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnAbort.setBounds(297, 425, 89, 23);
		contentPane.add(btnAbort);
		
		btnSendEmail = new JButton("Send Email");
		btnSendEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//add some code here
				Thread t = new Thread() {
					 public void run() {
						 alert.sendEmail(recognition.returnData(),false);
					 }
					
				};
				t.start();
			}
		});
		btnSendEmail.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSendEmail.setBounds(285, 524, 110, 23);
		contentPane.add(btnSendEmail);
		
		//checkbox
		
		checkboxEdit = new JCheckBox("Yes");
		checkboxEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBoxEdit();
			}
		});
		checkboxEdit.setBounds(433, 227, 97, 19);
		contentPane.add(checkboxEdit);
		
		checkboxSignUp = new JCheckBox("Yes");
		checkboxSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkBoxSignUp();
			}
		});
		checkboxSignUp.setBounds(431, 169, 76, 23);
		contentPane.add(checkboxSignUp);
		
		//labels
		
		lblAddress = new JLabel("9. Address");
		lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAddress.setBounds(37, 420, 150, 14);
		contentPane.add(lblAddress);
		
		lblAge = new JLabel("5. Age");
		lblAge.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAge.setBounds(37, 231, 150, 19);
		contentPane.add(lblAge);
		
		lblConfPass = new JLabel("3. Confirm Password");
		lblConfPass.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblConfPass.setBounds(37, 140, 150, 14);
		contentPane.add(lblConfPass);
		
		lblEditRequest = new JLabel("Do you want to edit data?");
		lblEditRequest.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEditRequest.setBounds(257, 225, 170, 23);
		contentPane.add(lblEditRequest);
		
		lblEmail = new JLabel("10. Email");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEmail.setBounds(37, 501, 124, 14);
		contentPane.add(lblEmail);
		
		lblGender = new JLabel("8. Gender M/F");
		lblGender.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGender.setBounds(37, 375, 150, 14);
		contentPane.add(lblGender);
		
		lblHeight = new JLabel("6. Height (cm)");
		lblHeight.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblHeight.setBounds(37, 278, 150, 19);
		contentPane.add(lblHeight);
		
		lblWeight = new JLabel("7. Weight (kg)");
		lblWeight.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblWeight.setBounds(37, 327, 150, 19);
		contentPane.add(lblWeight);
		
		lblNotes = new JLabel("<html>NOTES:\r\n<br/>5. 6. 7. Please only add integer\r\n<br/>"
				+ "8. Please only add letter \"M\" or \"F\"\r\n <br/>       "
				+ "10. Please write email in correct form: xxx@xx.xxx\r\n</html>");
		lblNotes.setBounds(37, 549, 315, 70);
		contentPane.add(lblNotes);
		
		lblPatients = new JLabel("PATIENTS");
		lblPatients.setForeground(Color.RED);
		lblPatients.setHorizontalAlignment(SwingConstants.CENTER);
		lblPatients.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 18));
		lblPatients.setBounds(77, 11, 89, 19);
		contentPane.add(lblPatients);
		
		lblPatientName = new JLabel("");
		lblPatientName.setForeground(new Color(153, 102, 204));
		lblPatientName.setHorizontalAlignment(SwingConstants.CENTER);
		lblPatientName.setFont(new Font("Georgia", Font.PLAIN, 18));
		lblPatientName.setBounds(37, 41, 180, 23);
		contentPane.add(lblPatientName);
		
		lblPassword = new JLabel("2. Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassword.setBounds(37, 91, 150, 14);
		contentPane.add(lblPassword);
		
		lblRelatives = new JLabel("RELATIVES");
		lblRelatives.setForeground(Color.BLUE);
		lblRelatives.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 18));
		lblRelatives.setBounds(77, 470, 100, 20);
		contentPane.add(lblRelatives);
		
		lblSignUpRequest = new JLabel("Do you want to signup?");
		lblSignUpRequest.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSignUpRequest.setBounds(257, 169, 180, 23);
		contentPane.add(lblSignUpRequest);
		
		lblUser = new JLabel("1. Username");
		lblUser.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUser.setHorizontalAlignment(SwingConstants.LEFT);
		lblUser.setBounds(37, 41, 150, 14);
		contentPane.add(lblUser);
		
		lblName = new JLabel("4. Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblName.setBounds(37, 188, 76, 14);
		contentPane.add(lblName);
		
		//input text fields
		
		txtAge = new JTextField();
		txtAge.setBounds(37, 252, 150, 20);
		txtAge.setColumns(10);
		contentPane.add(txtAge);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(37, 440, 150, 19);
		txtAddress.setColumns(10);
		contentPane.add(txtAddress);
		
		txtConfPass = new JPasswordField();
		txtConfPass.setBounds(37, 158, 150, 19);
		contentPane.add(txtConfPass);
		
		txtEmail = new JTextField();
		txtEmail.setBounds(37, 518, 150, 20);
		txtEmail.setColumns(10);
		contentPane.add(txtEmail);
		
		txtGender = new JTextField();
		txtGender.setBounds(37, 395, 150, 19);
		txtGender.setColumns(10);
		contentPane.add(txtGender);
		
		txtPass = new JPasswordField(); 
		txtPass.setBounds(37, 109, 150, 20);
		contentPane.add(txtPass);
		
		txtUser = new JTextField();
		txtUser.setBounds(37, 60, 150, 20);
		txtUser.setColumns(10);		
		contentPane.add(txtUser);
		
		txtName = new JTextField();
		txtName.setBounds(37, 207, 150, 19);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		txtHeight = new JTextField();
		txtHeight.setBounds(37, 301, 150, 20);
		contentPane.add(txtHeight);
		txtHeight.setColumns(10);
		
		txtWeight = new JTextField();
		txtWeight.setBounds(37, 350, 150, 20);
		contentPane.add(txtWeight);
		txtWeight.setColumns(10);
		
		//initial state of GUI, define which components are visible and enabled.
		initialState();
		addSignUpLabel(signUpLabel);
		addSignUpField(signUpField);
	}
	
	//initial state of GUI, define which components are visible and enabled.
	private void initialState() {
		txtAddress.setEnabled(false);
		txtAddress.setVisible(false);
		txtAge.setEnabled(false);
		txtAge.setVisible(false);
		txtConfPass.setEnabled(false);
		txtConfPass.setVisible(false);
		txtEmail.setEnabled(false);
		txtEmail.setVisible(false);
		txtGender.setEnabled(false);
		txtGender.setVisible(false);
		txtName.setEnabled(false);
		txtName.setVisible(false);
		txtWeight.setEnabled(false);
		txtWeight.setVisible(false);
		txtHeight.setEnabled(false);
		txtHeight.setVisible(false);
		
		lblAddress.setVisible(false);
		lblAge.setVisible(false);
		lblConfPass.setVisible(false);
		lblEditRequest.setVisible(false);
		lblEmail.setVisible(false);
		lblGender.setVisible(false);
		lblRelatives.setVisible(false);
		lblPatientName.setVisible(false);
		lblNotes.setVisible(false);
		lblName.setVisible(false);
		lblWeight.setVisible(false);
		lblHeight.setVisible(false);
		//-----------------------------------------------------------------------------------------------------------------------
		
		btnStop.setEnabled(false);
		btnStop.setVisible(false);
		btnSignup.setEnabled(false);
		btnAbort.setVisible(false);
		btnEdit.setVisible(false);
		btnEdit.setEnabled(false);
		btnStart.setVisible(false);
		btnStop.setVisible(false);
		btnSendEmail.setVisible(false);
		btnSendEmail.setEnabled(false);
		
		checkboxEdit.setVisible(false);
	}
	
//	-----------------------------------------------------LOGIN SHORTCUT--------------------------------------------------------------
	//initiate Login method when pressing ENTER key
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            Login();
        }
    }
	
	/*//add patient Data and draw GUI
	private void addDataGUI() {
		List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40; 
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add((double) random.nextDouble() * maxScore);
//            scores.add((double) i);
        }
//      GUI dimension
        patGUI = new DataGUI(scores);
        patGUI.setPreferredSize(new Dimension(525, 400));
        DataPanel.add(patGUI);
	}
	
	//remove GUI
	private void removeDataGUI() {
		DataPanel.remove(patGUI);
		DataPanel.setBackground(null);
	}*/

//	-------------------------------------------------GETTERS AND SETTERS--------------------------------------------------------------
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		GUI.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		GUI.password = password;
	}

	public static String getConfPassword() {
		return confPassword;
	}

	public static void setConfPassword(String confPassword) {
		GUI.confPassword = confPassword;
	}

	public static String getAddr() {
		return addr;
	}

	public static void setAddr(String addr) {
		GUI.addr = addr;
	}

	public static String getGender() {
		return gender;
	}

	public static void setGender(String gender) {
		GUI.gender = gender;
	}

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		GUI.email = email;
	}
	
	public static boolean isSex() {
		return sex;
	}

	public static void setSex(boolean sex) {
		GUI.sex = sex;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		GUI.name = name;
	}

	public static String getAgeString() {
		return ageString;
	}

	public static void setAgeString(String ageString) {
		GUI.ageString = ageString;
	}

	public static String getWeightString() {
		return weightString;
	}

	public static void setWeightString(String weightString) {
		GUI.weightString = weightString;
	}

	public static String getHeightString() {
		return heightString;
	}

	public static void setHeightString(String heightString) {
		GUI.heightString = heightString;
	}

	public static int getAge() {
		return age;
	}

	public static void setAge(int age) {
		GUI.age = age;
	}

	public static int getWeight() {
		return weight;
	}

	public static void setWeight(int weight) {
		GUI.weight = weight;
	}

	public static void setHeight(int height) {
		GUI.height = height;
	}

	public static List<Object> getAllInputData() {
		addPatOutput(patientOutput);
		return GUI.patientOutput;
	}
	
	public static void removeAllInputData() {
		patientOutput.clear();
	}
	
	public void onAbortButton() {
		//btnAbort on
		btnAbort.setEnabled(true);
		btnAbort.setVisible(true);
		
		//other buttons off
		btnLogout.setEnabled(false);
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);
		btnSendEmail.setEnabled(false);
		//disable all input fields
		setEnabled(patientField, false);
	
	}
	
	public void offAbortButton() {
		//btnAbort off
		btnAbort.setEnabled(false);
		btnAbort.setVisible(false);
		btnSendEmail.setEnabled(true);
		//other buttons on
		btnLogout.setEnabled(true);
		btnStart.setEnabled(true);
		btnStop.setEnabled(true);
		
		//enable all input fields (if needed)
		setEnabled(patientField, true);
	}
	
	//Launch the GUI
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
//					frame.getContentPane().add(patGUI);
					
//					set a default button that will automatically listen to a designed key. In this case, the ENTER key
					frame.getRootPane().setDefaultButton(btnLogin); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private class mySwingWorker extends SwingWorker<Void,Integer>{

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			System.out.println("Is started!");
			recognition.runAnalysis("D:/DOCUMENT/Master/SCS/Project/data/F01_SA01_R01.txt");
			return null;
		}
		
	}
}
 