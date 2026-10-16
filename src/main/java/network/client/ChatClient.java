package network.client;

import interfaces.INetworkConnection;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ChatClient implements INetworkConnection {

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    @Override
    public boolean connect(String host, int port) {

        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            startReceiverThread();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            connected = false;
            return false;
        }
    }

    private void startReceiverThread() {
        Thread receiver = new Thread(() -> {
            while (connected) {
                try {
                    NetworkPacket packet = (NetworkPacket) input.readObject();
                    PacketRouter.route(packet);
                } catch (Exception e) {
                    connected = false;
                    break;
                }
            }
        });
        receiver.setDaemon(true);
        receiver.start();
    }

    @Override
    public synchronized boolean send(NetworkPacket packet) {
        try {
            output.writeObject(packet);
            output.flush();
            return true;
        } catch (IOException e) {
            connected = false;
            return false;
        }
    }

    @Override
    public NetworkPacket listen() {
        return waitFor(RequestType.RECEIVE_MESSAGE);
    }

    public NetworkPacket waitFor(RequestType type) {
        return PacketRouter.waitFor(type);
    }

    @Override
    public void disconnect() {
        connected = false;
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null) {
                socket.close();
            }

        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }

}