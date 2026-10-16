package model.accounts.users.premiumUsers;

import java.util.Date;

public class BlueUser extends PremiumUser {
    public BlueUser(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin, int credit, int token, String biography, boolean blocked, String accSign, Date expirationDate) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, isAdmin, credit, token, biography, blocked, "Blue", expirationDate);
    }

    @Override
    public int calculatePostCost(String postText, boolean hasMedia) {
        int cost = postText.length() / 2;
        if (hasMedia) cost += 5;
        return cost;
    }
}