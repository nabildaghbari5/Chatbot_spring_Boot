package com.chatbot.service;

import com.chatbot.model.OllamaRequest;
import com.chatbot.model.OllamaResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final WebClient ollamaWebClient;

    public String generateResponse(String userMessage) {
        log.info("Génération de réponse via Ollama pour le message : {}", userMessage);
        // Création de l’objet OllamaRequest
        OllamaRequest ollamaRequest = new OllamaRequest("deepseek-r1:7b", userMessage, false);
        try {
            OllamaResponse ollamaResponse = ollamaWebClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ollamaRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            Mono.error(new RuntimeException("Erreur Ollama - Code HTTP " + response.statusCode())))
                    .bodyToMono(OllamaResponse.class)
                    .block(Duration.ofSeconds(45));
           //  Si la réponse est bien reçue et marquée comme terminée (isDone()), on récupère le texte généré
            String rawResponse = (ollamaResponse != null && ollamaResponse.isDone())
                    ? ollamaResponse.getResponse()
                    : "";

            // Nettoyage amélioré avec regex multi-ligne
            String cleanedResponse = rawResponse
                    .replaceAll("(?i)(?s)<think>.*?</think>", "") // Regex améliorée
                    .replaceAll("\\n{2,}", "\n")
                    .replaceAll("[\\s\\u200B]+", " ")
                    .trim();

            if (cleanedResponse.isEmpty()) {
                log.warn("Réponse nettoyée vide pour le message : {}", userMessage);
                return "Je rencontre des difficultés à formuler une réponse adaptée. Pouvez-vous reformuler votre demande ?";
            }

            log.debug("Réponse générée : {}", cleanedResponse);
            return cleanedResponse;

        } catch (RuntimeException e) {
            log.error("Erreur technique lors de l'appel à Ollama - {}", e.getMessage(), e);
            return "Une erreur technique est survenue. Notre équipe en est informée.";
        } catch (Exception e) {
            log.error("Erreur inattendue - {}", e.getMessage(), e);
            return "Désolé, un problème inattendu a interrompu le traitement de votre demande.";
        }
    }
}