import java.io.IOException;
import java.net.*;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress LOCAL_ADDR;
    private int PORT;
    private byte[] buf;
    private InetAddress BROADCAST_ADDR;

    public UDPClient(int port, String brAddr) throws SocketException, UnknownHostException {
        this.BROADCAST_ADDR = InetAddress.getByName(brAddr);
        socket = new DatagramSocket();
        LOCAL_ADDR = InetAddress.getByName("localhost");
        this.PORT = port;
    }

    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, LOCAL_ADDR, 4445);
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
                    = new DatagramPacket(buf, buf.length, BROADCAST_ADDR, PORT);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
