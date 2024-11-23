import server.ServerMain;

import java.io.IOException;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        loadProperties();
        loadServer();
    }

    public static void loadServer() {
        try {
            ServerMain.startServer();
        } catch (IOException e) {
            e.printStackTrace();
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
