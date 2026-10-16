package controller;

import controller.manager.SessionManager;
import exceptions.InsufficientCreditException;
import exceptions.InsufficientTokenException;
import exceptions.InvalidSubscriptionException;
import exceptions.UserNotFoundException;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

public class SubscriptionsController {

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    public String buySubscription(String type) throws UserNotFoundException, InvalidSubscriptionException, InsufficientCreditException {

        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        if (!type.equalsIgnoreCase("Blue") && !type.equalsIgnoreCase("Gold")) {
            throw new InvalidSubscriptionException();
        }

        int price = type.equalsIgnoreCase("Blue") ? 500 : 1000;

        if (currentUser.getCredit() < price) {
            throw new InsufficientCreditException();
        }

        Object[] data = {currentUser.getAccID(), type};
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.BUY_SUBSCRIPTION, data));

        if (response != null && response.getRequestType() == RequestType.BUY_SUBSCRIPTION_RESPONSE) {
            User updatedUser = (User) response.getData();

            if (updatedUser != null) {
                SessionManager.setCurrentUser(updatedUser);
                return "Succeed";
            }
        }
        return "Failed";
    }

    public void chargeCredit(int amount) throws UserNotFoundException {

        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        Object[] data = {currentUser.getAccID(), amount};
        NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.CHARGE_CREDIT, data));
        currentUser.setCredit(currentUser.getCredit() + amount);
    }

    public void chargeToken(int amount) throws UserNotFoundException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        Object[] data = {currentUser.getAccID(), amount};
        NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.CHARGE_TOKEN, data));
        currentUser.setToken(currentUser.getToken() + amount);
    }
}