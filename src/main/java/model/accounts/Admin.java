package model.accounts;

import java.util.Date;

public class Admin extends Account {
    public Admin(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, true);
    }
}