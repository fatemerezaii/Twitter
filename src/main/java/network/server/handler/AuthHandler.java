package network.server.handler;

import model.accounts.users.User;
import network.server.ClientHandler;
import network.server.ServerLauncher;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.UserRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class AuthHandler {

    private final UserRepository userRepository = new UserRepository();
    public void handle(NetworkPacket packet, ObjectOutputStream output, ClientHandler clientHandler) {
        switch (packet.getRequestType()) {
            case LOGIN:
                login(packet, output, clientHandler);
                break;

            case REGISTER:
                register(packet, output, clientHandler);
                break;

            case LOGOUT:
                logout(packet);
                break;

            default:
                break;
        }
    }

    private void login(NetworkPacket packet, ObjectOutputStream output, ClientHandler clientHandler) {
        User loginUser = (User) packet.getData();
        User result = userRepository.login(loginUser.getUsername(), loginUser.getPassword());
        try {
            if(result != null){
                clientHandler.setUserId(result.getAccID());
                clientHandler.setUserId(result.getAccID());
                output.writeObject(new NetworkPacket(RequestType.LOGIN_SUCCESS, result));
            }else{
                output.writeObject(new NetworkPacket(RequestType.LOGIN_FAILED, "Wrong username or password"));
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register(NetworkPacket packet, ObjectOutputStream output, ClientHandler clientHandler){
        User user = (User) packet.getData();
        try {
            if (userRepository.existsUsername(user.getUsername())) {
                output.writeObject(new NetworkPacket(RequestType.REGISTER_FAILED, "Username already exists"));
                output.flush();
                return;
            }
            if (userRepository.existsEmail(user.getEmail())) {
                output.writeObject(new NetworkPacket(RequestType.REGISTER_FAILED, "Email already exists"));
                output.flush();
                return;
            }
            boolean success = userRepository.add(user);
            if (success) {
                User registeredUser =userRepository.login(user.getUsername(), user.getPassword());
                clientHandler.setUserId(registeredUser.getAccID());
                output.writeObject(new NetworkPacket(RequestType.REGISTER_SUCCESS, registeredUser));
            } else {
                output.writeObject(new NetworkPacket(RequestType.REGISTER_FAILED, "Registration failed"));
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout(NetworkPacket packet) {

    }

}