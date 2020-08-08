package tpop;

import java.util.StringTokenizer;

public class POP3Client {
	public static void main(String[] args) {
		POP3Session pop3 = new POP3Session("pop.mycompany.com", "username", "password");
		try {
			System.out.println("Connecting to POP3 server...");
			pop3.connectAndAuthenticate();
			System.out.println("Connected to POP3 server.");

			int messageCount = pop3.getMessageCount();
			System.out.println("\nWaiting massages on POP3 server : " + messageCount);

			String[] messages = pop3.getHeaders();
			for (int i = 0; i < messages.length; i++) {
				StringTokenizer messageTokens = new StringTokenizer(messages[i]);
				String messageId = messageTokens.nextToken();
				String messageSize = messageTokens.nextToken();
				String messageBody = pop3.getMessage(messageId);

				System.out.println("\n-------------------- messsage " + messageId + ", size=" + messageSize
						+ " --------------------");
				System.out.print(messageBody);
				System.out.println("-------------------- end of message " + messageId + " --------------------");
			}
		} catch (Exception e) {
			pop3.close();
			System.out.println("Can not receive e-mail!");
			e.printStackTrace();
		}
	}

}
