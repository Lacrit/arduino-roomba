import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * The UDP Server thread running on our Raspberry Pi as 'the brain' of the Roomba trio which will be
 * controlled from the command line*/
public class ControlServer extends UDPServer {
    protected Map<String, RemoteDevice> entriesByIP;
    protected Map<Integer, ArrayList<RemoteDevice>> entriesByType;
    protected static int CURR_DEVICE_ID = 0;

    public ControlServer(int type, int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        super(type, receivePort, brAddr, sendPort, localAddr);
        entriesByIP = new HashMap<>();
        entriesByType = new HashMap<>();
        commands.put("SYN", p -> createNewEntry(p));
        commands.put("DEL", p -> deleteEntry(p));
    }

    private void createNewEntry(String packet) {
        String IPaddr = packet.substring(3);
        int type = Integer.parseInt(packet.substring(3, 4));
        System.out.println("Trying to create a new entry.");

        if (entriesByIP != null) {
            if (!entriesByIP.containsKey(IPaddr)) {
                CURR_DEVICE_ID += 1;
                entriesByIP.put(IPaddr, new RemoteDevice(CURR_DEVICE_ID, IPaddr, type));
                System.out.println("SYN: Added new device " + CURR_DEVICE_ID + " at " + IPaddr);
                sendComand("ACK", IPaddr);

                if (entriesByType != null) {
                    if (!entriesByType.containsKey(type)) entriesByType.put(type, new ArrayList<>());
                    entriesByType.get(type).add(new RemoteDevice(CURR_DEVICE_ID, IPaddr, type));
                    System.out.println("SYN: Attached device " + CURR_DEVICE_ID + " to type " + type);
                }

            } else {
                RemoteDevice tmp = entriesByIP.get(IPaddr);
                System.out.println("SYN: Device already exists by ID: " + tmp.getID() + " at " + tmp.getIPAddr());
            }
        }
    }

    private void sendCommandByType(String cmd, int type){
        if (entriesByType.containsKey(type))
            for (RemoteDevice d : entriesByType.get(type))
                sendComand(cmd, d.getIPAddr());
        else System.out.println(type + " is not a valid type or no devices of this type exist");
    }

    private void deleteEntry(String packet) {
        String IPaddr = packet.substring(3, packet.length() - 1);
        if (entriesByIP != null && !entriesByIP.isEmpty()) {
            if (entriesByIP.containsKey(IPaddr)) {
                RemoteDevice tmp = entriesByIP.get(IPaddr);
                entriesByIP.remove(IPaddr);
                System.out.println("DEL: Removed device " + tmp.getID() + " at " + IPaddr);
            } else {
                System.out.println("DEL: Device at " + IPaddr + " is not stored on the server");
            }
        }
    }
}
