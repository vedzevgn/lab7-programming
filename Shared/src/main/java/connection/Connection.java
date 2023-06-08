package connection;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

public interface Connection {
    void send(Serializable object) throws IOException;
    Serializable receive() throws IOException, ClassNotFoundException;

}
