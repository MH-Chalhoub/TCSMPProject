package tpop;

import java.util.StringTokenizer;

public class TPOPClient {
	public static void main(String[] args) {
		TPOPSession tpop = new TPOPSession("localhost", "Y@BINIOU.com", "password");
		try {
			System.out.println("Connecting to TPOP server...");
			tpop.connectAndAuthenticate();
			System.out.println("Connected to TPOP server.");

			int messageCount = tpop.getMessageCount();
			System.out.println("\nWaiting massages on POP3 server : " + messageCount);

			String[] messages = tpop.getHeaders();
			for (int i = 0; i < messages.length; i++) {
				StringTokenizer messageTokens = new StringTokenizer(messages[i]);
				String messageId = messageTokens.nextToken();
				String messageSize = messageTokens.nextToken();
				String messageBody = tpop.getMessage(messageId);

				System.out.println("\n-------------------- message " + messageId + ", size=" + messageSize + " --------------------");
				System.out.print(messageBody);
				System.out.println("-------------------- end of message " + messageId + " --------------------");
			}
			tpop.quit();
		} catch (Exception e) {
			tpop.close();
			System.out.println("Can not receive e-mail!");
			e.printStackTrace();
		}
	}

}
