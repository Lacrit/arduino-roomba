import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class UDPServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    private int PORT;
    private InetAddress BROADCAST_ADDR;
    private Map<String, Consumer<DatagramPacket>> commands;
    private Map<String, RemoteDevice> entries;
    private static int CURR_DEVICE_ID = 0;

    public UDPServer(int port, String brAddr) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
        this.PORT = port;
        this.BROADCAST_ADDR = InetAddress.getByName(brAddr);

        entries = new HashMap<>();

        commands = new HashMap<>();
        commands.put("HELLO", p -> createNewEntry(p));
        commands.put("BYE", p -> deleteEntry(p));
    }

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
                commands.get(received).accept(packet);
            }
//            try {
//                socket.send(packet);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        System.out.println("Terminating server thread...");
        socket.close();
    }

    private void createNewEntry(DatagramPacket packet) {
        String IPaddr = packet.getAddress().toString();
        if (!entries.isEmpty()) {
            if (!entries.containsKey(IPaddr)) {
                CURR_DEVICE_ID += 1;
                entries.put(IPaddr, new RemoteDevice(CURR_DEVICE_ID, IPaddr));
                System.out.println("HELLO: Added new device " + CURR_DEVICE_ID + " at " + IPaddr);
            } else {
                RemoteDevice tmp = entries.get(IPaddr);
                System.out.println("HELLO: Device already exists by ID: " + tmp.getID() + " at " + tmp.getIPAddr());
            }
        }
    }

    private void deleteEntry(DatagramPacket packet) {
        String IPaddr = packet.getAddress().toString();
        if (!entries.isEmpty()) {
            if (entries.containsKey(IPaddr)) {
                RemoteDevice tmp = entries.get(IPaddr);
                entries.remove(IPaddr);
                System.out.println("BYE: Removed device " + tmp.getID() + " at " + IPaddr);
            } else {
                System.out.println("BYE: Device at " + IPaddr + " is not stored on the server");
            }
        }
    }

}
