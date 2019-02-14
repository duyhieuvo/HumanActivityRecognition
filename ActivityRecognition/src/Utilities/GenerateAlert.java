package Utilities;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



import GUI.GUI;


public class GenerateAlert {
	private final Properties properties = new Properties();  
	private Session session;  
	private GUI frame;
	
	//Boolean variable
	private boolean fall = false;

	
	private volatile boolean sendingEmail;

	
	
	public GenerateAlert(GUI frame) {
		this.frame = frame;
	
	}


	private void initEmail() {  
		//ssl
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");

		session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {
            	protected PasswordAuthentication getPasswordAuthentication() {
            		//for the demo using gmail server, a mail address is hard-coded into the source code
            		//In real application, all sending email request will be directed to a server and done there.
            		return new PasswordAuthentication("********@gmail.com","******");
            	}
        	});
	}  
	
	
	//Send email to emergency service and relative of user
	public void sendEmail(UserData data,boolean fallTriggered){  
		initEmail();  
		try {
			String sex;
			if(data.getSex()) {
				sex = "Female";
			}
			else {
				sex = "Male";
			}
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("******@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(data.getContact()+","+"******@gmail.com"));
			message.setSubject("Alert Message");
			if(fallTriggered) {
				message.setText("Hello," +
		                "\n\n A fall has been detected. \n \n"+
		        		"Name: " + data.getName() + "\n \n"+
		                "Age: " + data.getAge() + "\n \n" +
		                "Sex: " + sex + "\n \n" +
		                "Height: " + data.getHeight() + " cm" +"\n \n" +
		                "Weight: " + data.getWeigth() + " kg" +"\n \n" +
		        		"Address: " + data.getAddress());
			}
			else {
				message.setText("Hello," +
		                "\n\n A user is not feeling well. \n \n"+
		        		"Name: " + data.getName() + "\n \n"+
		                "Age: " + data.getAge() + "\n \n" +
		                "Sex: " + sex + "\n \n" +
		                "Height: " + data.getHeight() + " cm" +"\n \n" +
		                "Weight: " + data.getWeigth() + " kg" +"\n \n" +
		        		"Address: " + data.getAddress());
			}
			
			Transport t = session.getTransport("smtp");
			t.connect("smtp.gmail.com", "*******@gmail.com", "*******");
			t.sendMessage(message, message.getAllRecipients());
			System.out.println("Done");

		} catch (MessagingException e) {
			if(fallTriggered) {//if the email cannot be sent, send warning to user and re-try until success
				frame.showScheduledWarn("Cannot send the email, will retry in 30 seconds.",5000);
				long elapsedTime = 0;
				long startTime = System.currentTimeMillis();
				while(sendingEmail) {
					elapsedTime = (System.currentTimeMillis() - startTime)/1000;
					if (elapsedTime>=5) {
						break;
					}
				}
				if(sendingEmail) {
					sendEmail(data,true);
				}
			}
			else {
				StringBuilder builder = new StringBuilder();
				builder.append("Cannot send the email, please check your connection and try again.");
				frame.showWarnMes(builder);
			}
			
			
		}   
	}  
	

	//Function for user to stop the alarm when pressing 'Abort' button
	public void notFall() {
		fall = false;
		sendingEmail= false;
	}
	
	
	private void addButton() {
		frame.onAbortButton();
	}

	
	public void alarm(UserData data) {
		sendingEmail=true;
		boolean songStarted = false;
		fall = true;
		addButton();
		boolean emailSent = false;
		long elapsedTime = 0;
		long startTime = System.currentTimeMillis();
		while(true) {
			if(!songStarted) {
				SoundAlarm.playSong();
				songStarted = true;
			}
			if (!emailSent) {
				elapsedTime = (System.currentTimeMillis() - startTime)/1000;
			}
			if (((elapsedTime >= 10)) && (!emailSent)) {
				emailSent=true;
				Thread t = new Thread() {
					 public void run() {
						 sendEmail(data,true);
					 }
					
				};
				t.start();
			}
			if(!fall) {	
				System.out.println("Abort is pressed.");
				break;
			}
		}
		SoundAlarm.stopSong();
	}
}
