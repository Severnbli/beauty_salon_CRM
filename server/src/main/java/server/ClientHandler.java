package server;

import lombok.*;

import java.net.Socket;

@NoArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class ClientHandler implements Runnable {
    @NonNull
    private Socket socket;

    @Override
    public void run() {

    }
}
