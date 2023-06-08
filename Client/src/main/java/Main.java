import exceptions.InvalidArgsException;
import exceptions.InvalidResponseException;

import java.io.IOException;

/**
 * Program entry point class. Contains main() method.
 *
 * @author Башаримов Евгений Александрович
 */

public class Main {
    public static void main(String[] args) throws IOException, InvalidArgsException, ClassNotFoundException, InvalidResponseException {
        Client client = new Client(args);
        client.start();
    }
}