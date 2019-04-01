import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public abstract class UDPServer extends Thread {

    protected DatagramSocket socket;
    protected boolean running;
    protected byte[] buf = new byte[256];

    protected int PORT;
    protected InetAddress BROADCAST_ADDR;
    protected Map<String, Consumer<DatagramPacket>> commands;


    public UDPServer(int port, String brAddr) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
        this.PORT = port;
        this.BROADCAST_ADDR = InetAddress.getByName(brAddr);
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
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            //packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("END")) {
                running = false;
                continue;
            } else {
                if(commands.containsKey(received)){
                    commands.get(received).accept(packet);
                }
            }
            // TODO: send ACK-like packet back
//            try {
//                socket.send(packet);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        System.out.println("Terminating server thread...");
        socket.close();
    }

    public void broadcastCommand(String cmd) {
        buf = cmd.getBytes();
        try {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, BROADCAST_ADDR, PORT);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
