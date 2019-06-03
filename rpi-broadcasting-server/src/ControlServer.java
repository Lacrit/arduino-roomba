import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * The UDP Server thread _RUNNING on our Raspberry Pi as 'the brain' of the Roomba trio which will be
 * controlled from the command line*/
public class ControlServer extends UDPServer {
    protected Map<String, RemoteDevice> entriesByIP;
    protected Map<Integer, ArrayList<RemoteDevice>> entriesByType;

    public ControlServer(int type, int receivePort, String brAddr, int sendPort, String localAddr) throws SocketException, UnknownHostException {
        super(type, receivePort, brAddr, sendPort, localAddr);
        entriesByIP = new HashMap<>();
        entriesByType = new HashMap<>();
        commands.put(Util.SYN, p -> createNewEntry(p));
        commands.put(Util.DEL, p -> deleteEntry(p));

    }

    private void createNewEntry(String packet) {
        String IPaddr = packet.substring(Util.IP_AFTER_CMD1_IND);
        int type = Integer.parseInt(packet.substring(Util.TYPE_IND, Util.CMD1_IND));
        System.out.printf("Trying to create a new entry for type %s\n", type);

        if (entriesByIP != null) {
            if (!entriesByIP.containsKey(IPaddr)) {
                CURR_DEVICE_ID += 1;
                entriesByIP.put(IPaddr, new RemoteDevice(CURR_DEVICE_ID, IPaddr, type));
                System.out.println(Util.SYN + ": Added new device " + CURR_DEVICE_ID + " at " + IPaddr);
                // Automatically send own address
                sendCommand(Util.ACK + Util.SYN, IPaddr);

                if (entriesByType != null) {
                    if (!entriesByType.containsKey(type)) entriesByType.put(type, new ArrayList<>());
                    entriesByType.get(type).add(new RemoteDevice(CURR_DEVICE_ID, IPaddr, type));
                    System.out.println(Util.SYN + ": Attached device " + CURR_DEVICE_ID + " to type " + type);
                }

            } else {
                RemoteDevice tmp = entriesByIP.get(IPaddr);
                System.out.println(Util.SYN + ": Device already exists by ID: " + tmp.getID() + " at " + tmp.getIPAddr());
            }
        }

    }

    public void sendCommandByType(String cmd) {
        int type = Integer.parseInt(cmd.substring(Util.TYPE_IND, Util.CMD1_IND));
        sendCommandByType(cmd.substring(Util.CMD1_IND), type);
    }

    public void sendCommandByType(String cmd, int type) {
        if (type == 0) broadcastCommand(cmd);
        if (entriesByType.containsKey(type))
            for (RemoteDevice d : entriesByType.get(type))
                sendCommand(cmd, d.getIPAddr());
        else System.out.println(type + " is not a valid type or no devices of this type exist");
    }

    public void parseUserInput(String inputString) {
        int targetDeviceType = Integer.parseInt(inputString.substring(Util.TYPE_IND, Util.CMD1_IND));
        if (targetDeviceType == this._TYPE) {
            System.out.println("Calling local command.");
            String cmd = inputString.substring(Util.CMD1_IND, Util.IP_AFTER_CMD1_IND);
            if (commands.containsKey(cmd)) {
                commands.get(cmd).accept(inputString);
            }
            else {
                System.out.println("Command " + cmd + " not recognized. Please try again.");
            }
        } else {
            System.out.println("Calling a remote command on the target Subordinate Server.");
            sendCommandByType(inputString);
        }

    }

    private void deleteEntry(String packet) {
        String IPaddr = packet.substring(Util.IP_AFTER_CMD1_IND);
        if (entriesByIP != null && !entriesByIP.isEmpty()) {
            if (entriesByIP.containsKey(IPaddr)) {
                RemoteDevice tmp = entriesByIP.get(IPaddr);
                entriesByIP.remove(IPaddr);
                System.out.println(Util.DEL + ": Removed device " + tmp.getID() + " at " + IPaddr);
            } else {
                System.out.println(Util.DEL + ": Device at " + IPaddr + " is not stored on the server");
            }
        }
    }
}
