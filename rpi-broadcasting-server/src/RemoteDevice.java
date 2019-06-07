import java.util.Objects;

public class RemoteDevice {

    private final int _ID;
    private String _IPAddr;
    private Integer _TYPE;

    private static int ALL_TYPES = 0;
    private static int CONTROL = 1;
    private static int CAMERA = 2;
    private static int NO_CAMERA = 3;

    public RemoteDevice(int id, String _IPAddr, int _TYPE) {
        _ID = id;
        //this.IPAddr = InetAddress.getByName(IPAddr);
        this._IPAddr = _IPAddr;
        this._TYPE = _TYPE;

    }

    @Override
    public String toString() {
        return "RemoteDevice{" +
                "ID = " + _ID +
                ", IP address = '" + _IPAddr + '\'' +
                ", Type = " + _TYPE +
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
        return _ID;
    }

    public String getIPAddr() {
        return _IPAddr;
    }
}
