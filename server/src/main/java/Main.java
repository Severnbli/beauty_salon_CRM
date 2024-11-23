import server.ServerMain;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            ServerMain.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
