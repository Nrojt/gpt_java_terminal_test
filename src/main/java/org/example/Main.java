package org.example;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private static final OpenAiService SERVICE = new OpenAiService(OPENAI_API_KEY);
    private static final List<ChatMessage> messages = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static String userMessageString = "";

    public static void main(String[] args) {
        //TODO go to a next line before user input

        System.out.println("Hallo, ik ben de chatbot van Bedrijf 42. Ik ben hier om je te helpen met vragen over onze producten en diensten. Stel je vraag en ik zal mijn best doen om je te helpen. Als je wilt stoppen, typ dan 'exit'.");
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "Je bent een virtuele assistent voor Bedrijf 42. Geef formeel op de vragen van de klant, maar hou het kort. Antwoord altijd in het Nederlands. Als de gebruiker 'exit' typt, moet je de sessie stoppen.");
        messages.add(systemMessage);

        while (!userMessageString.equalsIgnoreCase("exit")) {
            userMessageString = scanner.nextLine();

            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessageString);

            messages.add(userMessage);

            printResponse();
        }

        SERVICE.shutdownExecutor();
    }

    private static void printResponse(){
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1) // number of choices to return
                .logitBias(new HashMap<>()) // bias towards certain tokens, idk how to implement this yet
                .build();

        SERVICE.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .subscribe(chatCompletionResponse -> {
                    String response = chatCompletionResponse.getChoices().get(0).getMessage().getContent();
                    if(response != null){
                        System.out.print(response);
                    }
                });
        System.out.println(System.lineSeparator());
    }
}