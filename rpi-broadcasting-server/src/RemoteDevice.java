import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class RemoteDevice {

    private final int ID;
    private InetAddress IPAddr;

    public RemoteDevice(int id, String IPAddr) {
        ID = id;
        try {
            this.IPAddr = InetAddress.getByName(IPAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "RemoteDevice{" +
                "ID=" + ID +
                ", IPAddr='" + IPAddr.toString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteDevice)) return false;
        RemoteDevice that = (RemoteDevice) o;
        return getID() == that.getID() &&
                Objects.equals(getIPAddr(), that.getIPAddr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getIPAddr());
    }

    public int getID() {
        return ID;
    }

    public InetAddress getIPAddr() {
        return IPAddr;
    }
}
