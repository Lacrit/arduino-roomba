import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerLauncher {

    public static void main(String[] args) {

        // write your code here
//        boolean isControlServer = (Integer.parseInt(args[0]) == 1)? true: false;
        int portNum = Integer.parseInt(args[0]);
        String brAddr = args[1];
        // -------------------------------------------------------------------
        UDPServer server = null;
        UDPClient client = null;
        try {
            client = new UDPClient(portNum, brAddr);
            server = new UDPServer(portNum, brAddr);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Thread serverThread = new Thread(server);
        serverThread.start();
        // -------------------------------------------------------------------
        Scanner in = new Scanner(System.in);
        do{
            System.out.println("Print next command. Send 'END' to terminate.");
            String input = in.next();
            client.broadcastCommand(input);
            if(input.equals("END")) {
                System.out.println("Terminating launcher...");
                break;
            }
        } while (in.hasNext());
        in.close();
    }
}
