import java.net.SocketException;
import java.net.UnknownHostException;
/*
* The listening server on subordinate Roombas which will be listening to broadcasts for commands that should
* be later sent over (possibly with python or C because of USB driver mismatch problems?) to the Arduino via
* serial interface where the communication with Roomba will be handled
* */
public class SubordinateServer extends UDPServer{

    private String controlIP;
    private boolean connected = false;
    private RemoteDevice control;

    public SubordinateServer(int type, int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        super(type, receivePort, brAddr, sendPort, localAddr);

        commands.put("STR", p -> driveStraight(p)); // example
        commands.put("SYN", p ->shareAddress(p));
        commands.put("ACK", p -> acknowledge(p));
    }

    private void acknowledge(String p) {
        connected = true;
    }

    private void driveStraight(String p) {
        //stub
    }

    private void shareAddress(String p){

            String destIP = p.substring(3);
            System.out.println("SYN: sharing my address with " + destIP);
            sendComand("SYN", destIP);
            controlIP = destIP;
            //sendComand("SYN", BROADCAST_ADDR);

    }

    public boolean isConnected() {
        return connected;
    }
}
