package network.server.handler;

import model.accounts.users.User;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.SubscriptionRepository;

public class SubscriptionHandler {

    private final SubscriptionRepository repository = new SubscriptionRepository();

    public NetworkPacket handle(NetworkPacket packet) {
        switch (packet.getRequestType()) {
            case DEDUCT_TOKEN: {
                Object[] data = (Object[]) packet.getData();
                int userId = (Integer) data[0];
                String postText = (String) data[1];
                boolean hasMedia = (Boolean) data[2];
                boolean success = repository.deductToken(userId, postText, hasMedia);
                return new NetworkPacket(RequestType.DEDUCT_TOKEN_RESPONSE, success);
            }

            case BUY_SUBSCRIPTION: {
                Object[] data = (Object[]) packet.getData();
                int userId = (Integer) data[0];
                String type = (String) data[1];
                User user = repository.buySubscription(userId, type);
                return new NetworkPacket(RequestType.BUY_SUBSCRIPTION_RESPONSE, user);
            }

            case UPDATE_TOKEN: {
                int userId = (Integer) packet.getData();
                boolean success = repository.updateToken(userId);
                return new NetworkPacket(RequestType.UPDATE_TOKEN_RESPONSE, success);
            }

            case CHARGE_CREDIT: {
                Object[] data = (Object[]) packet.getData();
                boolean success = repository.chargeCredit((Integer) data[0], (Integer) data[1]);
                return new NetworkPacket(RequestType.CHARGE_CREDIT_RESPONSE, success);
            }

            case CHARGE_TOKEN: {
                Object[] data = (Object[]) packet.getData();
                boolean success = repository.chargeToken((Integer) data[0], (Integer) data[1]);
                return new NetworkPacket(RequestType.CHARGE_TOKEN_RESPONSE, success);
            }

            default:
                return null;
        }
    }
}