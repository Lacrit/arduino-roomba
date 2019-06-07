import java.net.SocketException;
import java.net.UnknownHostException;

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
                control = new RemoteDevice(CURR_DEVICE_ID++, p.substring(Util.IP_AFTER_CMD2_IND),
                        Integer.parseInt(p.substring(Util.TYPE_IND,Util.CMD1_IND)));
                connected = true;
                System.out.println("Stored control server at IP " + control.getIPAddr());
            }
            System.out.println("Control server is successfully stored.");
        }
        System.out.println("This subordinate server is already connected.");
    }

    public RemoteDevice getControl() {
        return control;
    }


    public synchronized boolean isConnected() {
        return connected;
    }
}
