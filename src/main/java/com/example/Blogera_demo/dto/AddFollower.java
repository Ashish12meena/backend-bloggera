package com.example.Blogera_demo.dto;

import lombok.Data;

@Data
public class AddFollower {
    private String loggedUserId;
    private String followedUserId;
}
