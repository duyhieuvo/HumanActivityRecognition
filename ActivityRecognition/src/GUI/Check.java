package GUI;

import java.util.ArrayList;

public abstract class Check {
	
	public ArrayList<String> patientData, patientEdit;
	public StringBuilder warnMessage;
	int i;
	
	public Check() {
		
	}
	
	public Check(ArrayList<String> patientData, ArrayList<String> patientEdit, StringBuilder warnMessage) {
		this.patientData = patientData;
		this.patientEdit = patientEdit;
		this.warnMessage = warnMessage;
	}
	
	public abstract boolean check(ArrayList<String> patientData, int i);
}
