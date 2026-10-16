package network.client;

import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PacketRouter {

    private static final Map<RequestType, LinkedBlockingQueue<NetworkPacket>> queues = new ConcurrentHashMap<>();

    private PacketRouter() {
    }

    public static void route(NetworkPacket packet) {
        queues.computeIfAbsent(packet.getRequestType(), k -> new LinkedBlockingQueue<>()).offer(packet);
    }

    public static NetworkPacket waitFor(RequestType type) {
        try {
            return queues.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}