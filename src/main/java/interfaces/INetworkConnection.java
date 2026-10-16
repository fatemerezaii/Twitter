package interfaces;

import network.shared.NetworkPacket;

public interface INetworkConnection {

    boolean connect(String host, int port);

    boolean send(NetworkPacket packet);

    NetworkPacket listen();

    void disconnect();

    boolean isConnected();

}