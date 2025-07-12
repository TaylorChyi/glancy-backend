package com.glancy.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatGptResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String content;
    }
}
