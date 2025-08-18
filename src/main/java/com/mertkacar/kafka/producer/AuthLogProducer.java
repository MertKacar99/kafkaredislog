package com.mertkacar.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertkacar.dtos.events.AuthEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper(); // JSON

    public void sendLoginEvent(String username,String mail ,boolean success ) {
        AuthEvent evt = AuthEvent.builder()
                .type("LOGIN")
                .username(username)
                .mail(mail)
                .success(success)
                .timestamp(System.currentTimeMillis())
                .build();
        send(evt);
    }

    public void sendRegisterEvent(String username,String mail) {
        AuthEvent evt = AuthEvent.builder()
                .type("REGISTER")
                .username(username)
                .mail(mail)
                .success(true)
                .timestamp(System.currentTimeMillis())
                .build();
        send(evt);
    }

    private void send(AuthEvent event) {
        try {
            String payload = mapper.writeValueAsString(event);

            kafkaTemplate.send("auth-events", event.getUsername(), payload);
            System.out.println("Kafka'ya gönderildi: " + payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AuthEvent serileştirilemedi", e);
        }
    }
}
