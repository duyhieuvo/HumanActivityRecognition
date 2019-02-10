package GUI;


import java.util.ArrayList;
import java.util.regex.Pattern;

public class CheckEmail extends Check{

	public CheckEmail(ArrayList<String> patientData, ArrayList<String> patientEdit, StringBuilder warnMessage) {
		this.patientData = patientData;
		this.patientEdit = patientEdit;
		this.warnMessage = warnMessage;
	}
	
	@Override
	public boolean check(ArrayList<String> patientData, int i) {
		// TODO Auto-generated method stub
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

}
