import org.apache.log4j.Logger;
import server.ServerMain;
import services.DBConnection;

import java.io.IOException;

public class Main {
    public static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            loadServer();
        } catch (Exception e) {
            log.error("Unhandled exception: ", e);
        }
        finally {
            DBConnection.closeSessionFactory(); // Will be executed after server stop working
        }
    }

    public static void loadServer() {
        log.info("Trying to load server...");

        try {
            ServerMain.startServer();
        } catch (IOException e) {
            log.error("Failed to start server ", e);
        }
    }
}
