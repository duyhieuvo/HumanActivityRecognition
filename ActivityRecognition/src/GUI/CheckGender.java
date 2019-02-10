package GUI;

import java.util.ArrayList;

public class CheckGender extends Check{

	public CheckGender(ArrayList<String> patientData, ArrayList<String> patientEdit, StringBuilder warnMessage) {
		this.patientData = patientData;
		this.patientEdit = patientEdit;
		this.warnMessage = warnMessage;
	}
	
	@Override
	public boolean check(ArrayList<String> patientData, int i) {
		// TODO Auto-generated method stub
		this.patientData = patientData;
		//check if the field is blank or contains multiple spaces
		if(!(patientData.get(i) == null | patientData.get(i).trim().isEmpty())) {
			if (!(patientData.get(i).equals("M") | patientData.get(i).equals("F"))){
				warnMessage.append("Gender: Please only write \"M\" or \"F\" ");
				return false;
			}
		}
		return true;
	}
}

