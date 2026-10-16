package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sendTime;
    private MessageStatus status;

    public ChatMessage() {
    }

    public ChatMessage(int senderId, int receiverId, String content, LocalDateTime sendTime, MessageStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sendTime = sendTime;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}