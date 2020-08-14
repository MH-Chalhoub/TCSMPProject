package tcsmp;

import java.util.Scanner;

public class TCSMPClient
{
    public static void main(String[] args)
    {
    	TCSMPClientSession tcsmp = new TCSMPClientSession(
           "localhost",
           1999,
           "Y@BINIOU.com",
           "X@POUET.com",
           "Some subject",
           "... Message text ...");

		Scanner scanner = new Scanner(System.in);

    	System.out.println("Enter 1 to send a message and 2 to retrive msg from the server");
		int todo = scanner.nextInt();
		
		//1 to send a message and 2 to retrive msg from the server
		if(todo == 1) {
	        try {
	        	System.out.println("Sending e-mail...");
	        	tcsmp.sendMessage();
	        	System.out.println("E-mail sent.");
	        } catch (Exception e) {
	        	tcsmp.close();
	        	System.out.println("Can not send e-mail!");
	        	e.printStackTrace();
	        }
		}
		else {
			
		}
		
    }
}
