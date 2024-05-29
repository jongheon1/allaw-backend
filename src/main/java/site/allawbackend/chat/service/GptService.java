package site.allawbackend.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.allawbackend.chat.dto.ChatRequestDto;
import site.allawbackend.chat.dto.ChatResponseDto;

@Service
@RequiredArgsConstructor

public class GptService {

    private final RestTemplate restTemplate;

    @Value("${openai.api-url}")
    private String apiUrl;

    public String chat(String model, String prompt, String systemPrompt) {
        ChatRequestDto request = new ChatRequestDto(model, prompt, systemPrompt);
        return callGptApi(request);
    }

    public String summarize(String model, String text, String systemPrompt) {
        ChatRequestDto request = new ChatRequestDto(model, text, systemPrompt);
        return callGptApi(request);
    }

    private String callGptApi(ChatRequestDto request) {
        ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        return response.getChoices().get(0).getMessage().getContent();
    }
}

