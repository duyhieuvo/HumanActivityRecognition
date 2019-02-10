package Utilities;


public class UserData {
	private String name;
	private int age;
	private boolean sex;
	private String address;
	private String contact;
	private int height;
	private int weight;
	

	public UserData(String name, int age, boolean sex, String address,  String contact, int height, int weight) {
		this.name = name;
		this.age = age; 
		this.sex = sex;
		this.address = address;
		this.contact = contact;
		this.height = height;
		this.weight = weight;
	}
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
	
	public boolean getSex() {
		return sex;
	}
	
	public String getAddress() {
		return address;
	}
	public String getContact() {
		return contact;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWeigth() {
		return weight;
	}
}