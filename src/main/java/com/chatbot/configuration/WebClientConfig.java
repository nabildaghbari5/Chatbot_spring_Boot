package com.chatbot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Indique que c'est une classe de configuration Spring
 * But : Centraliser la configuration des appels à l'API Ollama
 */
@Configuration
public class WebClientConfig {
    @Bean //: Crée un composant réutilisable dans Spring
    public WebClient ollamaWebClient() {
       // Configure un client HTTP pour Ollama avec
        return WebClient.builder()
                .baseUrl("http://localhost:11434") //Adresse locale du serveur Ollama (port 11434)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //En-tête Content-Type: application/json par défaut
                .build();
    }
}