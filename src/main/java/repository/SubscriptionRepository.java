package repository;

import model.accounts.users.User;
import model.accounts.users.premiumUsers.BlueUser;
import model.accounts.users.premiumUsers.GoldUser;
import model.accounts.users.premiumUsers.PremiumUser;

import java.util.Date;

public class SubscriptionRepository {

    private final UserRepository userRepository = new UserRepository();

    public User buySubscription(int userId, String type) {
        User currentUser = userRepository.findById(userId);
        if (currentUser == null){
            return null;}
        String currentSign = currentUser.getAccSign();
        if ("Gold".equals(currentSign) && "Blue".equalsIgnoreCase(type)) {
            return null;
        }
        int cost;
        if ("Blue".equalsIgnoreCase(type)) {
            cost = 9;
        } else if ("Gold".equalsIgnoreCase(type)) {
            cost = 19;
        } else {
            return null;
        }
        if (currentUser.getCredit() < cost) {
            return null;
        }
        currentUser.setCredit(currentUser.getCredit() - cost);
        currentUser.setToken(currentUser.getToken() + 3000);
        Date expiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        User newUser;
        if ("Blue".equalsIgnoreCase(type)) {
            newUser = new BlueUser(currentUser.getAccID(), currentUser.getFullName(), currentUser.getPhoneNumber(), currentUser.getUsername(), currentUser.getBirthday(), currentUser.getProfileCoverPath(), currentUser.getPassword(), currentUser.getEmail(), currentUser.getSignUpDate(), currentUser.isAdmin(), currentUser.getCredit(), currentUser.getToken(), currentUser.getBiography(), currentUser.isBlocked(), "Blue", expiry);
        } else {
            newUser = new GoldUser(currentUser.getAccID(), currentUser.getFullName(), currentUser.getPhoneNumber(), currentUser.getUsername(), currentUser.getBirthday(), currentUser.getProfileCoverPath(), currentUser.getPassword(), currentUser.getEmail(), currentUser.getSignUpDate(), currentUser.isAdmin(), currentUser.getCredit(), currentUser.getToken(), currentUser.getBiography(), currentUser.isBlocked(), "Gold", expiry);
        }
        newUser.setFollowers(currentUser.getFollowers());
        newUser.setFollowings(currentUser.getFollowings());
        newUser.setPosts(currentUser.getPosts());
        newUser.setLikedPosts(currentUser.getLikedPosts());
        newUser.setFavoriteHashtags(currentUser.getFavoriteHashtags());
        if (userRepository.update(newUser)) {
            return userRepository.findById(newUser.getAccID());
        }
        return null;
    }

    public boolean deductToken(int userId, String postText, boolean hasMedia) {
        User user = userRepository.findById(userId);
        if (user == null){
            return false;}

        int cost = user.calculatePostCost(postText, hasMedia);
        if (user.getToken() < cost){
            return false;}
        user.setToken(user.getToken() - cost);
        return userRepository.update(user);
    }

    public boolean updateToken(int userId) {
        User user = userRepository.findById(userId);
        if (user == null){
            return false;}
        int limit;
        if (user.getAccSign() == null || user.getAccSign().isEmpty()) {
            limit = 250;
        } else if ("Blue".equals(user.getAccSign())) {
            limit = 400;
        } else {
            limit = 600;
        }
        if (user.getToken() < limit) {
            user.setToken(limit);
            return userRepository.update(user);
        }
        return true;
    }

    public boolean chargeCredit(int userId, int amount) {
        User user = userRepository.findById(userId);
        if (user == null){
            return false;}
        user.setCredit(user.getCredit() + amount);
        return userRepository.update(user);
    }

    public boolean chargeToken(int userId, int amount) {
        User user = userRepository.findById(userId);
        if (user == null){
            return false;}
        user.setToken(user.getToken() + amount);
        return userRepository.update(user);
    }
}