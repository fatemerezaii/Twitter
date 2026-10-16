package model;

import model.files.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private int postID;
    private String text;
    private File media;
    private int posterID;
    private Date postDate;
    private List<Integer> hashtags;
    private List<Integer> likers;
    private int parentPostID;
    private List<Integer> repliedPosts;
    private int views;
    private int likes;
    private boolean isLocked;
    private boolean isDeleted;
    private boolean isEdited;

    public Post(int postID, int posterID, String text, Date postDate) {
        this.postID = postID;
        this.posterID = posterID;
        this.text = text;
        this.postDate = postDate;
        this.hashtags = new ArrayList<>();
        this.likers = new ArrayList<>();
        this.repliedPosts = new ArrayList<>();
        this.isDeleted = false;
        this.isLocked = false;
        this.isEdited = false;
        this.likes = 0;
        this.views = 0;
        this.parentPostID = 0;
        this.media = null;
    }


    public int getPostID() { return postID; }
    public void setPostID(int postID) { this.postID = postID; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public File getMedia() { return media; }
    public void setMedia(File media) { this.media = media; }
    public int getPosterID() { return posterID; }
    public void setPosterID(int posterID) { this.posterID = posterID; }
    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }
    public List<Integer> getHashtags() { return hashtags; }
    public void setHashtags(List<Integer> hashtags) { this.hashtags = hashtags; }
    public List<Integer> getLikers() { return likers; }
    public void setLikers(List<Integer> likers) { this.likers = likers; }
    public int getParentPostID() { return parentPostID; }
    public void setParentPostID(int parentPostID) { this.parentPostID = parentPostID; }
    public List<Integer> getRepliedPosts() { return repliedPosts; }
    public void setRepliedPosts(List<Integer> repliedPosts) { this.repliedPosts = repliedPosts; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public boolean isEdited() { return isEdited; }
    public void setEdited(boolean edited) { isEdited = edited; }
}