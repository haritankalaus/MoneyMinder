package com.jay.accountmanager;

public class Test123 {

	//Aplha numeric string 
	
	public static void main(String[] args) {
		String str = "abc123!@";
		
		int sum = 0;
		for(char ch : str.toCharArray()) {
			
			if(ch >= '1' && ch <= '9') {
				sum = sum + Integer.parseInt(ch+"");
			}
			
			
			
		}
		System.out.println("sum>> " + sum);
		
	}
	
}
