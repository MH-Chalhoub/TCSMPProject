package tcsmp;

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
}
