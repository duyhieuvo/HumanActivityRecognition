package Run;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;
import java.lang.Math;

import Utilities.GenerateAlert;
import Utilities.UserData;



public class ActivityRecognition {
	private GenerateAlert alert;
	private UserData data;
	private Timer timer;
	private boolean fall;
	
	public ActivityRecognition(GenerateAlert alert) {
		this.alert=alert;
	}
	
	
	private double thresholdBig() { //retrieve obvious G threshold
		if (data.getSex()) {
			if (data.getAge()>60) return 5;
			else {
				if (data.getWeigth()<50) return 6;
				else if (data.getWeigth()<=60) return 7;
				else return 7.5;
			}
		}
		else {
			if (data.getAge()>60) return 6;
			else {
				if (data.getWeigth()<60) return 8;
				else if (data.getWeigth()<=75) return 10;
				else return 10.5;
			}
		}
	}
	
	private double thresholdSmall() { //retrieve questionable G threshold
		if (data.getSex()) {
			if (data.getAge()>=60) return 2.3;
			else {
				if (data.getWeigth()<50) return 2.6;
				else if (data.getWeigth()<=60) return 3;
				else return 3.3;
			}
		}
		else {
			if (data.getAge()>=60) return 2.9;
			else {
				if (data.getWeigth()<60) return 3.4;
				else if (data.getWeigth()<=75) return 4;
				else return 4.2;
			}
		}
	}
	
	public void stop() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		System.out.println("Recognition is stopped.");
	}
	
	public void runAnalysis(String datafile) throws FileNotFoundException {
		fall=false;
		double thresholdBig = thresholdBig();
		double thresholdSmall = thresholdSmall();
		double[][] sampled = new double[1600][4];
		System.out.println("Running analysis!");
		timer = new Timer();
		System.out.println(timer);
		Scanner scanner = new Scanner(new File(datafile));
		timer.scheduleAtFixedRate(new TimerTask() { //data sampling thread
				String line;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					line=scanner.nextLine();	//read a whole line						
					line=line.replaceAll("\\s",""); //delete all spaces
					String tokens[] = line.split(","); //split into string-type numbers
					tokens[8]=tokens[8].substring(0, tokens[8].length() - 1); //remove ";" of last element
					double[] array = Arrays.stream(tokens).mapToDouble(Double::parseDouble).toArray(); //convert to int
					for (int i=0;i<1599;i++) {  //left shift (modify) processing frame
						sampled[i][0]=sampled[i+1][0];
						sampled[i][1]=sampled[i+1][1];
						sampled[i][2]=sampled[i+1][2];
						sampled[i][3]=sampled[i+1][3];
					}
					sampled[1599][0]=array[0]*2*16/8192; //conversion to G unit
					sampled[1599][1]=array[1]*2*16/8192; //as well as add newly sampled data to frame
					sampled[1599][2]=array[2]*2*16/8192;
					sampled[1599][3]=(Math.sqrt(sampled[1599][0]*sampled[1599][0]
							+sampled[1599][1]*sampled[1599][1]
							+sampled[1599][2]*sampled[1599][2]));
					if (!scanner.hasNext()) { //if end file reached
					//Here with the simulation, the recognition function simply stops
					//With real reading from sensors, a notification will be displayed to user to warn about the empty data buffer.
						System.out.println("End of file.");
						scanner.close();
						stop();
					}
				}
		}, 0, 5);	//start thread immediately at 200Hz
		timer.scheduleAtFixedRate(new TimerTask() { //data processing thread
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				System.out.println("Processing");
				outerloop:
				for (int i=0;i<800;i++) {
					for (int j=i+1;j<i+200;j++) { 
						if (Math.abs(sampled[i][3]-sampled[j][3])>thresholdBig) {
							System.out.println("ALARM!!");fall=true;stop();break outerloop;/*ALARM!!*/}
						else if (Math.abs(sampled[i][3]-sampled[j][3])>thresholdSmall) {
							boolean fell=true;
							for (int k=j+200;k<j+600;k++) {
								if (Math.abs(sampled[k][1])>0.6) {
									fell=false;
									break;
								}
							}
							//if a fall is detected, the analyzer is stopped
							if (fell) {System.out.println("ALARM!!");fall=true;stop();break outerloop;/*ALARM*/}
						}
					}
				}
				if(fall) {
					Fall();
					
				}
			}
		}, 8000, 100);
	}
	

	public void getData(UserData data) {
		this.data = data;
	}
	public UserData returnData() {
		return data;
	}
	public void Fall() {
		alert.alarm(data);//notify the alert observer that a fall has happened.
	}

	
}
