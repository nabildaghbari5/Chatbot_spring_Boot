package com.chatbot.model;

import lombok.Data;

@Data
public class OllamaResponse {
    private String response;
    private boolean done; // Vérifier que la réponse est complète
}