import broadcasting_server.Server;
import client.Client;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerLauncher {

    public static void main(String[] args) {
	// write your code here

        int portNum = Integer.parseInt(args[0]);
        String brAddr = args[1];

        Server server = null;
        Client client = null;
        try {
            client = new Client(portNum, brAddr);
            server = new Server(portNum, brAddr);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Scanner in = new Scanner(System.in);
        while(in.hasNext()) {
//            System.out.println("Print command");
            String input = in.next();



        }

    }
}
