package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Hashtag implements Serializable {

    private static final long serialVersionUID = 1L;
    private int hashtagID;
    private String title;
    private List<Integer> hashtagPosts;

    public Hashtag(int hashtagID, String title) {
        this.hashtagID = hashtagID;
        this.title = title;
        this.hashtagPosts = new ArrayList<>();
    }

    public int getHashtagID() {
        return hashtagID;
    }

    public void setHashtagID(int hashtagID) {
        this.hashtagID = hashtagID;
    }

    public List<Integer> getHashtagPosts() {
        return hashtagPosts;
    }

    public void setHashtagPosts(List<Integer> hashtagPosts) {
        this.hashtagPosts = hashtagPosts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}