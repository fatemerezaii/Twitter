package controller;

import controller.manager.SessionManager;
import model.Hashtag;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;

public class HashtaggingController {

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    private String normalizeTag(String tagName) {
        if (tagName == null) {
            return null;
        }

        tagName = tagName.trim();

        if (tagName.startsWith("#")) {
            tagName = tagName.substring(1);
        }

        return tagName.trim().toLowerCase();
    }

    public Hashtag findHashtagByTitle(String tagName) {
        String normalized = normalizeTag(tagName);

        if (normalized == null || normalized.isEmpty()) {
            return null;
        }

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.SEARCH_HASHTAGS, normalized));

        if (response != null && response.getRequestType() == RequestType.SEARCH_HASHTAGS_RESPONSE) {
            return (Hashtag) response.getData();
        }

        return null;
    }

    public List<Integer> extractHashtags(String text, int postId) {

        List<Integer> hashtagIds = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return hashtagIds;
        }

        String[] words = text.split("\\s+");

        for (String word : words) {

            if (!word.startsWith("#") || word.length() <= 1) {
                continue;
            }

            String tag = normalizeTag(word);

            if (tag == null || !tag.matches("[a-zA-Z0-9_]+")) {
                continue;
            }

            Hashtag hashtag = findHashtagByTitle(tag);

            if (hashtag != null && !hashtagIds.contains(hashtag.getHashtagID())) {
                hashtagIds.add(hashtag.getHashtagID());
            }
        }

        return hashtagIds;
    }

    @Override
    public String toString() {
        return "HashtaggingController";
    }
}