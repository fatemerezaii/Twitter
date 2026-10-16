package controller.user;

import controller.ChatController;
import controller.manager.SessionManager;
import model.accounts.users.NormalUser;
import model.accounts.users.User;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.Date;

public class EntryController {

    private final ChatController chatController;

    public EntryController() {
        chatController = new ChatController();
        chatController.connect("localhost", 8080);
    }


    public String signUp(String fullName,
                         String username,
                         String email,
                         String phoneNumber,
                         String password,
                         String confirmPassword,
                         Date birthday) {


        if (fullName.isBlank()
                || username.isBlank()
                || email.isBlank()
                || phoneNumber.isBlank()
                || password.isBlank()
                || confirmPassword.isBlank()
                || birthday == null) {

            return "Fill all fields!";
        }


        if (!phoneNumber.matches("^09\\d{9}$")) {
            return "Invalid phone number!";
        }


        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            return "Invalid email!";
        }


        if (!password.equals(confirmPassword)) {
            return "Passwords do not match!";
        }


        User user = new NormalUser();

        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setBirthday(birthday);

        user.setCredit(0);
        user.setToken(3000);
        user.setBlocked(false);


        NetworkPacket packet =
                new NetworkPacket(
                        RequestType.REGISTER,
                        user
                );


        NetworkPacket response =
                chatController.request(
                        RequestType.REGISTER,
                        user
                );

        if (response == null) {
            return "Server connection failed!";
        }


        if (response == null) {
            return "No response from server!";
        }


        if (response.getRequestType()
                == RequestType.REGISTER_SUCCESS) {


            User registeredUser =
                    (User) response.getData();


            SessionManager.setCurrentUser(
                    registeredUser
            );

            return null;
        }


        return "Registration failed!";
    }




    public String loginUser(String usernameOrEmail,
                            String password) {


        if (usernameOrEmail.isBlank()
                || password.isBlank()) {

            return "Fill all fields!";
        }


        User loginUser = new NormalUser();

        loginUser.setUsername(usernameOrEmail);
        loginUser.setPassword(password);



        NetworkPacket response =
                chatController.request(
                        RequestType.LOGIN,
                        loginUser
                );

        if (response == null) {
            return "Server connection failed!";
        }



        if (response == null) {
            return "No response from server!";
        }



        if (response.getRequestType()
                == RequestType.LOGIN_SUCCESS) {


            User user =
                    (User) response.getData();


            if (user.isBlocked()) {
                return "Your account is blocked!";
            }


            SessionManager.setCurrentUser(user);

            return null;
        }



        return "Username or password is incorrect!";
    }



    public void disconnect() {
        chatController.disconnect();
    }

}