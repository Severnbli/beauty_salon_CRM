import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.fail;

public class ServerTest {
    @Test
    public void checkPortAvailability() {
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(Dotenv.load().get("SERVER_PORT")))) {
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            fail();
        }
    }
}
