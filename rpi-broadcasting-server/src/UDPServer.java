import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// Datagram packet: dest IP, dest port
public abstract class UDPServer extends Thread {

    protected DatagramSocket socket;
    protected boolean running;
    protected byte[] buf = new byte[256];
    protected Integer type;

    protected int RECEIVE_PORT;
    protected int SEND_PORT;
    protected String BROADCAST_ADDR;
    protected String LOCALHOST_ADDR;
    protected Map<String, Consumer<String>> commands;


    public UDPServer(int type, int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(receivePort);
        socket.setBroadcast(true);
        this.RECEIVE_PORT = receivePort;
        this.SEND_PORT = sendPort;
//        this.BROADCAST_ADDR = InetAddress.getByName(brAddr);
//        this.LOCALHOST_ADDR = InetAddress.getByName(localAddr);
        this.BROADCAST_ADDR = brAddr;
        this.LOCALHOST_ADDR = localAddr;
        this.type = type;
        commands = new HashMap<>();

    }
// TODO: use codes in the protocol instead of strings for flexibility, i.e. fist byte in the buffer as the protocol code
    public void run() {
        running = true;

        while (running) {
            // receiving & parsing incoming packet
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received
                    = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received new packet: " + received);
            String protocolCommand = received.substring(0,3);
            System.out.println("Protocol command: " + protocolCommand);
            if (protocolCommand.equals("END")) {
                running = false;
                continue;
            } else {
                if(commands.containsKey(protocolCommand)){
                    commands.get(protocolCommand).accept(received);
                }
            }
        }
        System.out.println("Terminating server thread...");
        socket.close();
    }

    public void broadcastCommand(String cmd) {
        sendComand(cmd, BROADCAST_ADDR);
    }

    public void sendComand(String cmd, String destAddr){
        String type = cmd.concat(this.type.toString());
        String typeIPcommand = type.concat(LOCALHOST_ADDR);
        buf = typeIPcommand.getBytes();
        System.out.println("Sending packet: " + typeIPcommand + "(" + typeIPcommand.getBytes().length+")" + " to " + destAddr);
        try {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, InetAddress.getByName(destAddr), SEND_PORT);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
