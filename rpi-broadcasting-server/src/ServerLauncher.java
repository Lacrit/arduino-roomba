import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerLauncher {

    public static void main(String[] args) {

        // write your code here
        int type = Integer.parseInt(args[0]);
        boolean isControlServer = (type== 1)? true: false;
        int receivePort = Integer.parseInt(args[1]);
        int sendPort = Integer.parseInt(args[2]);
        String brAddr = args[3];
        String localAddr = args[4];
        // -------------------------------------------------------------------
        UDPServer server = null;
        //UDPClient client = null;
        try {
            //client = new UDPClient(portNum, brAddr);
            if (isControlServer) {
                server = new ControlServer(type, receivePort, brAddr, sendPort, localAddr);
            }
            else {
                server = new SubordinateServer(type, receivePort, brAddr, sendPort, localAddr);
                SubordinateServer sub = (SubordinateServer) server;

                while (server != null) {
                    server.broadcastCommand("SYN");
                    if (sub.isConnected()) break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Thread serverThread = new Thread(server);
        serverThread.start();
        // -------------------------------------------------------------------
        if (isControlServer) {
            Scanner in = new Scanner(System.in);
            do{
                System.out.println("Print next command. Send 'END' to terminate.");
                String input = in.next();
                server.broadcastCommand(input);
                //client.broadcastCommand(input);
                if(input.equals("END")) {
                    System.out.println("Terminating launcher...");
                    break;
                }
            } while (in.hasNext());
            in.close();
        } else {

        }
    }
}
