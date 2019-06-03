import java.net.SocketException;
import java.net.UnknownHostException;

/*
* The listening server on subordinate Roombas which will be listening to broadcasts for commands that should
* be later sent over (possibly with python or C because of USB driver mismatch problems?) to the Arduino via
* serial interface where the communication with Roomba will be handled
* */
public class SubordinateServer extends UDPServer{

    private boolean connected = false;

    private RemoteDevice control;

    public SubordinateServer(int type, int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        super(type, receivePort, brAddr, sendPort, localAddr);

        //commands.put(Util.SYN, p ->shareAddress(p));
        commands.put(Util.ACK, p -> acknowledge(p));

        ackResponses.put(Util.SYN, p -> synACKResponse(p));

    }

    private void acknowledge(String p) {
        String cmd = p.substring(Util.CMD2_IND,Util.IP_AFTER_CMD2_IND);
        if(ackResponses.containsKey(cmd)) {
            System.out.println("Received " + Util.ACK + " of " + cmd);
            ackResponses.get(cmd).accept(p);
        }
        else {
            System.out.println("Received " + Util.ACK +" of unknown command");
        }
    }

    private void synACKResponse(String p) {
        if(!connected) {
            if(control == null) {
                control = new RemoteDevice(CURR_DEVICE_ID++, p.substring(Util.IP_AFTER_CMD1_IND),
                        Integer.parseInt(p.substring(Util.TYPE_IND,Util.CMD1_IND)));
                connected = true;
                System.out.println("Stored control server at IP " + control.getIPAddr());
            }
            System.out.println("Control server is already stored.");
        }
        System.out.println("This subordinate server is already connected.");
    }

    public RemoteDevice getControl() {
        return control;
    }

//    private void shareAddress(String p){
//            String destIP = p.substring(Util.IP_AFTER_CMD1_IND);
//            System.out.println(Util.SYN + ": sharing my address with " + destIP);
//            sendCommand(Util.SYN, destIP);
//            controlIP = destIP;
//
//
//    }


    public boolean isConnected() {
        return connected;
    }
}
