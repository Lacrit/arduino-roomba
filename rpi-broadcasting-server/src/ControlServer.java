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

    public ControlServer(int port, String brAddr) throws SocketException, UnknownHostException {
        super(port, brAddr);
        entries = new HashMap<>();
        commands.put("HELLO", p -> createNewEntry(p));
        commands.put("BYE", p -> deleteEntry(p));
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
