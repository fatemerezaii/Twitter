package model.accounts;

import java.util.Date;
import java.io.Serializable;


public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private int accID;
    private String fullName;
    private String phoneNumber;
    private String username;
    private Date birthday;
    private String profileCoverPath;
    private String password;
    private String email;
    private Date signUpDate;
    private boolean isAdmin;

    public Account(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin) {
        this.accID = accID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.birthday = birthday;
        this.profileCoverPath = profileCoverPath;
        this.password = password;
        this.email = email;
        this.signUpDate = signUpDate;
        this.isAdmin = isAdmin;
    }

    public Account() {
    }

    public int getAccID() {
        return accID;
    }

    public void setAccID(int accID) {
        this.accID = accID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getProfileCoverPath() {
        return profileCoverPath;
    }

    public void setProfileCoverPath(String profileCoverPath) {
        this.profileCoverPath = profileCoverPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(Date signUpDate) {
        this.signUpDate = signUpDate;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }
}