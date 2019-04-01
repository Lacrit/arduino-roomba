package client;

import java.io.IOException;
import java.net.*;

public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buf;
    private String brAddr;

    public Client(int port, String brAddr) throws SocketException, UnknownHostException {
        this.brAddr = brAddr;
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
        this.port = port;
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

    public void broadcastCommand(String cmd) {
        buf = cmd.getBytes();
        try {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, InetAddress.getByName(brAddr), port);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
