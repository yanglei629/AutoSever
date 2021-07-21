import org.apache.http.impl.client.HttpClientBuilder;

import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IPTest {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            System.out.println(ip);

        }
    }
}
