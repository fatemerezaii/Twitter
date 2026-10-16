package network.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerLauncher {
    public static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8080);
        server.start();
    }
}