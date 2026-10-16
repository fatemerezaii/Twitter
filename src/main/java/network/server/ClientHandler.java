package network.server;

import network.server.handler.RequestDispatcher;
import network.shared.NetworkPacket;
import repository.MessageRepository;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MessageRepository messageRepository;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean running = true;
    private int userId;
    private final RequestDispatcher dispatcher;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.messageRepository = new MessageRepository();
        this.dispatcher = new RequestDispatcher(this);
    }

    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            ServerLauncher.clients.add(this);
            while (running) {
                NetworkPacket packet = (NetworkPacket) input.readObject();
                if (packet == null) {
                    continue;
                }
                dispatcher.dispatch(packet, output);
            }
        } catch (IOException | ClassNotFoundException e) {
            stopHandler();
        }
    }

    public synchronized void sendPacket(NetworkPacket packet) {
        try {
            output.writeObject(packet);
            output.flush();
        } catch (IOException e) {
            stopHandler();
        }
    }

    public void stopHandler() {
        if (!running) {
            return;
        }
        running = false;
        ServerLauncher.clients.remove(this);
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException ignored) {
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}