import org.apache.log4j.Logger;
import server.ServerMain;

import java.io.IOException;
import java.util.logging.LogManager;

public class Main {
    public static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        loadProperties();
        loadServer();
    }

    public static void loadServer() {
        log.info("Trying to load server...");

        try {
            ServerMain.startServer();
        } catch (IOException e) {
            log.error("Failed to start server ", e);
        }
    }

    public static void loadProperties() {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }
}
