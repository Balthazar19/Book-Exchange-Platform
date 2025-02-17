package com.example.model;

import java.time.LocalDateTime;
import java.util.List;

public class Comment {
    private int id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    private List<Comment> replies;
    private Comment parentComment;
    private Client client;
    private Chat chat;


    public Comment(String title, String body, LocalDateTime timestamp, List<Comment> replies, Comment parentComment, Client client) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.replies = replies;
        this.parentComment = parentComment;
        this.client = client;
    }

    public Comment(String title, String body, LocalDateTime timestamp, Client client, Comment parentComment, Chat chat) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.client = client;
        this.parentComment = parentComment;
        this.chat = chat;
    }

    @Override
    public String toString() {
        return client + ": " + title;
    }
}
