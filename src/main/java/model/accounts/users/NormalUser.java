package model.accounts.users;

import java.util.Date;

public class NormalUser extends User {
    public NormalUser() {
        super();
    }

    public NormalUser(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin, int credit, int token, String biography, boolean blocked, String accSign) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, isAdmin, credit, token, biography, blocked, "");
    }

    @Override
    public int calculatePostCost(String postText, boolean hasMedia) {
        int cost = postText.length();
        if (hasMedia) cost += 10;
        return cost;
    }
}