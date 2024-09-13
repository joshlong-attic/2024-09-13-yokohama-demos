package com.example.service.adoptions;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// 
// Pooch (dog) Palace
// 
@Configuration
@RegisterReflectionForBinding(DogAdoptionSuggestion.class)
class Assistant {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder,
                          DogRepository repository,
                          VectorStore vectorStore) {


        if (false)
            repository.findAll().forEach(dog -> {
                var dogument = new Document(
                        "id: %s, name: %s, description: %s".formatted(
                                dog.id(), dog.name(), dog.description()));
                vectorStore.add(List.of(dogument));
            });


        var system = """
                You are an AI powered assistant to help people adopt a dog\s
                from the adoption agency named Pooch Palace with locations in\s
                Seoul, Las Vegas, Yokohama, Tokyo, Krakow, Singapore, Paris, San Francisco,\s
                and London. If you don't know about the dogs housed at our particular\s
                stores, then return a disappointed response suggesting we don't\s
                have any dogs available.
                """;
        return builder
                // RETRIEVAL AUGMENTED GENERATION (RAG)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .defaultSystem(system)
                .build();
    }

    @Bean
    ApplicationRunner demo(ChatClient ai) {
        return args -> {
            var reply = ai
                    .prompt()
                    .user("do you have any neurotic dogs?")
                    .call()
                    .entity(DogAdoptionSuggestion.class);
            System.out.println(reply);

        };
    }

}

record DogAdoptionSuggestion(int id, String name, String description) {
}