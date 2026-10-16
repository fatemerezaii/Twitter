package model.accounts.users.premiumUsers;

import java.util.Date;

public class GoldUser extends PremiumUser {
    public GoldUser(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin, int credit, int token, String biography, boolean blocked, String accSign, Date expirationDate) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, isAdmin, credit, token, biography, blocked, "Gold", expirationDate);
    }

    @Override
    public int calculatePostCost(String postText, boolean hasMedia) {
        return 5;
    }
}