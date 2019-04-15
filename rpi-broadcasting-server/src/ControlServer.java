import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/*
 * The UDP Server thread running on our Raspberry Pi as 'the brain' of the Roomba trio which will be
 * controlled from the command line*/
public class ControlServer extends UDPServer {
    protected Map<String, RemoteDevice> entries;
    protected static int CURR_DEVICE_ID = 0;

    public ControlServer(int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        super(receivePort, brAddr, sendPort, localAddr);
        entries = new HashMap<>();
        commands.put("SYN", p -> createNewEntry(p));
        commands.put("DEL", p -> deleteEntry(p));
    }

    private void createNewEntry(String packet) {
        String IPaddr = packet.substring(3);
        System.out.println("Trying to create a new entry.");
        if (entries != null) {
            if (!entries.containsKey(IPaddr)) {
                CURR_DEVICE_ID += 1;
                entries.put(IPaddr, new RemoteDevice(CURR_DEVICE_ID, IPaddr));
                System.out.println("SYN: Added new device " + CURR_DEVICE_ID + " at " + IPaddr);
                sendComand("ACK", IPaddr);
            } else {
                RemoteDevice tmp = entries.get(IPaddr);
                System.out.println("SYN: Device already exists by ID: " + tmp.getID() + " at " + tmp.getIPAddr());
            }

        }
    }

    private void deleteEntry(String packet) {
        String IPaddr = packet.substring(3, packet.length() - 1);
        if (entries != null && !entries.isEmpty()) {
            if (entries.containsKey(IPaddr)) {
                RemoteDevice tmp = entries.get(IPaddr);
                entries.remove(IPaddr);
                System.out.println("DEL: Removed device " + tmp.getID() + " at " + IPaddr);
            } else {
                System.out.println("DEL: Device at " + IPaddr + " is not stored on the server");
            }
        }
    }
}
