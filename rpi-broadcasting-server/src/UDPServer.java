import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// Datagram packet: dest IP, dest port
public abstract class UDPServer extends Thread {

    protected static int CURR_DEVICE_ID = 0;
    protected DatagramSocket _SOCKET;
    protected boolean _RUNNING;
    protected byte[] _BUFFER = new byte[256];
    protected Integer _TYPE;

    protected int _RECEIVE_PORT;
    protected int _SEND_PORT;
    protected String _BROADCAST_ADDR;
    protected String _LOCALHOST_ADDR;
    protected Map<String, Consumer<String>> commands;
    protected HashMap<String, Consumer<String>> ackResponses;


    public UDPServer(int type, int receivePort, String brAddr, int sendPort, String localAddr)
            throws SocketException, UnknownHostException {

        _SOCKET = new DatagramSocket(receivePort);
        _SOCKET.setBroadcast(true);
        this._RECEIVE_PORT = receivePort;
        this._SEND_PORT = sendPort;
        this._BROADCAST_ADDR = brAddr;
        this._LOCALHOST_ADDR = localAddr;
        this._TYPE = type;

        commands = new HashMap<>();
        ackResponses = new HashMap<>();

        commands.put(Util.END, p -> terminate(p));
    }

    private void terminate(String p) {
        if (p.substring(Util.CMD1_IND, Util.CMD2_IND).equals(Util.END)) {
            System.out.println("Terminating launcher...");
            System.exit(0);
        }
    }


    // TODO: use codes in the protocol instead of strings for flexibility, i.e. fist byte in the buffer as the protocol code
    public void run() {
        _RUNNING = true;

        while (_RUNNING) {
            DatagramPacket packet
                    = new DatagramPacket(_BUFFER, _BUFFER.length);
            try {
                _SOCKET.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received
                    = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received new packet: " + received);
            String protocolCommand = received.substring(Util.CMD1_IND, Util.CMD2_IND);
            System.out.println("Protocol command: " + protocolCommand);
            if (protocolCommand.equals(Util.END)) {
                _RUNNING = false;
                continue;
            } else {
                if(commands.containsKey(protocolCommand)){
                    commands.get(protocolCommand).accept(received);
                }
            }
        }
        System.out.println("Terminating server thread...");
        _SOCKET.close();
    }

    public void broadcastCommand(String cmd) {
        sendCommand(cmd, _BROADCAST_ADDR);
    }

    // Send command format: sender_type + cmd + sender_ip
    public void sendCommand(String cmd, String destAddr){
        String cmdToSend = this._TYPE.toString().concat(cmd).concat(_LOCALHOST_ADDR);
        _BUFFER = cmdToSend.getBytes();
        System.out.println("Sending packet: [" + cmdToSend + "] (" + cmdToSend.getBytes().length+")" + " to " + destAddr);
        try {
            DatagramPacket packet
                    = new DatagramPacket(_BUFFER, _BUFFER.length, InetAddress.getByName(destAddr), _SEND_PORT);
            _SOCKET.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
