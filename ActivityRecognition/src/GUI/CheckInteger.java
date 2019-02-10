package GUI;

import java.util.ArrayList;

public class CheckInteger extends Check{
	
	public CheckInteger(ArrayList<String> patientData, ArrayList<String> patientEdit, StringBuilder warnMessage) {
		this.patientData = patientData;
		this.patientEdit = patientEdit;
		this.warnMessage = warnMessage;
	}
	
	public boolean check(ArrayList<String> patientData, int i) {
		// TODO Auto-generated method stub
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
}
