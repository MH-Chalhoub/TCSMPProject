package tcsmp;

public class TCSMPClient
{
    public static void main(String[] args)
    {
    	TCSMPSession tcsmp = new TCSMPSession(
           "localhost",
           "X@POUET.com",
           "Y@BINIOU.com",
           "Some subject",
           "... Message text ...",
           true);

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
