package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OllamaRequest {
    private String model;
    private String prompt;
    private boolean stream; // Important pour désactiver le streaming
}
