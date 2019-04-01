import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
/*
* The listening server on subordinate Roombas which will be listening to broadcasts for commands that should
* be later sent over (possibly with python or C because of USB driver mismatch problems?) to the Arduino via
* serial interface where the communication with Roomba will be handled
* */
public class SubordinateServer extends UDPServer{
    public SubordinateServer(int port, String brAddr) throws SocketException, UnknownHostException {
        super(port, brAddr);

        commands.put("DRIVE STR", p -> driveStraight(p)); // example
    }

    private void driveStraight(DatagramPacket p) {
        //stub
    }
}
