package site.allawbackend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.allawbackend.chat.service.ChatGptService;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class ChatGptController {
    private final ChatGptService chatGptService;

    @PostMapping("/gen")
    public String chat(@RequestBody String prompt) {
        return chatGptService.chat(prompt);
    }

    @PostMapping("/{billId}/summary")
    public String summary(
    @PathVariable Long billId){
        return chatGptService.summary(billId);
    }

    @PostMapping("/{billId}/pdfChat")
    public String pdfChat(@RequestBody String prompt,
                          @PathVariable Long billId){
        return chatGptService.checkChat(prompt, billId);
    }

    @PostMapping("/agree")
    public String agree(@RequestBody String prompt, String pdf){
        return chatGptService.agree(prompt, pdf);
    }

    @PostMapping("/disagree")
    public String disagree(@RequestBody String prompt, String pdf){
        return chatGptService.disagree(prompt, pdf);
    }
}
