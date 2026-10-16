package model.accounts.users.premiumUsers;

import model.accounts.users.User;

import java.util.Date;

public abstract class PremiumUser extends User {
    private Date expirationDate;

    public PremiumUser(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin, int credit, int token, String biography, boolean blocked, String accSign, Date expirationDate) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, isAdmin, credit, token, biography, blocked, accSign);
        this.expirationDate = expirationDate;
    }

    @Override
    public abstract int calculatePostCost(String postText, boolean hasMedia);

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}