package model.accounts.users;

import model.accounts.Account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class User extends Account {
    private int credit;
    private int token;
    private String biography;
    private boolean blocked;
    private String accSign;
    private List<Integer> posts;
    private List<Integer> followers;
    private List<Integer> followings;
    private List<Integer> likedPosts;
    private List<Integer> favoriteHashtags;

    public User(int accID, String fullName, String phoneNumber, String username, Date birthday, String profileCoverPath, String password, String email, Date signUpDate, boolean isAdmin, int credit, int token, String biography, boolean blocked, String accSign) {
        super(accID, fullName, phoneNumber, username, birthday, profileCoverPath, password, email, signUpDate, isAdmin);
        this.token = token;
        this.credit = credit;
        this.biography = biography;
        this.blocked = blocked;
        this.accSign = accSign;
        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.likedPosts = new ArrayList<>();
        this.favoriteHashtags = new ArrayList<>();
    }

    public User() {
        super();
    }

    public abstract int calculatePostCost(String postText, boolean hasMedia);

    public void addFavoriteHashtag(int hashtagId) {
        if (!favoriteHashtags.contains(hashtagId)) {
            favoriteHashtags.add(hashtagId);
        }
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getAccSign() {
        return accSign;
    }

    public void setAccSign(String accSign) {
        this.accSign = accSign;
    }

    public List<Integer> getPosts() {
        return posts;
    }

    public void setPosts(List<Integer> posts) {
        this.posts = posts;
    }

    public List<Integer> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Integer> followers) {
        this.followers = followers;
    }

    public List<Integer> getFollowings() {
        return followings;
    }

    public void setFollowings(List<Integer> followings) {
        this.followings = followings;
    }

    public List<Integer> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Integer> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<Integer> getFavoriteHashtags() {
        return favoriteHashtags;
    }

    public void setFavoriteHashtags(List<Integer> favoriteHashtags) {
        this.favoriteHashtags = favoriteHashtags;
    }
}