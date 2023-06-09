import exceptions.InvalidArgsException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Program entry point class. Contains main() method.
 *
 * @author Башаримов Евгений Александрович
 */

public class Main {

    public static void main(String[] args) throws IOException, InvalidArgsException, ClassNotFoundException, SQLException, ExecutionException, InterruptedException {
        Server server = new Server(args);
        server.start();
    }
}