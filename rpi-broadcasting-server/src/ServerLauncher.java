import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerLauncher {

    public static void main(String[] args) {

        // Arguments
        int argType = Integer.parseInt(args[0]);
        int argReceivePort = Integer.parseInt(args[1]);
        int argSendPort = Integer.parseInt(args[2]);
        String argBrAddr = args[3];
        String argLocalAddr = args[4];

        boolean argIsControlServer = argType == 1;
        // -------------------------------------------------------------------
        UDPServer server = null;

        try {
            if (argIsControlServer) {
                server = new ControlServer(argType, argReceivePort, argBrAddr, argSendPort, argLocalAddr);
            } else {
                server = new SubordinateServer(argType, argReceivePort, argBrAddr, argSendPort, argLocalAddr);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Starting packet-listening thread
        Thread serverThread = new Thread(server);
        serverThread.start();
        // -------------------------------------------------------------------
        if (argIsControlServer) {
            // Control Server is listening for user input to parse it into commands for connected subordinates
            Scanner in = new Scanner(System.in);
            do {
                System.out.println("Print next command. Send 'END' to terminate.");
                String input = in.next();
                ((ControlServer) server).parseUserInput(input);

            } while (in.hasNext());
            in.close();
        } else {
            System.out.println("Connecting to Control Server...");
            // Subordinate server spams SYN until SYN response is sent back
            int ctrl = 3;
            while (!((SubordinateServer) server).isConnected()) {
                server.broadcastCommand(Util.SYN);
                ctrl--;
                //if(ctrl == 0) break;
            }
            System.out.println("Connected to Control Server at IP "
                    + ((SubordinateServer) server).getControl().getIPAddr());
            System.out.println("Listening to further commands...");
        }
    }
}
