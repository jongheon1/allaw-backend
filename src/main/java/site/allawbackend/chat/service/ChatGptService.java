package site.allawbackend.chat.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.allawbackend.chat.dto.ChatRequestDto;
import site.allawbackend.chat.dto.ChatResponseDto;
import site.allawbackend.chat.dto.SummaryRequestDto;
import site.allawbackend.common.exception.BillNotFoundException;
import site.allawbackend.entity.Bill;
import site.allawbackend.repository.BillRepository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptService {
    private final RestTemplate restTemplate;

    private final BillRepository billRepository;
    private final GptService gptService;

    @Value("${openai.sum-system-prompt}")
    private String SumSystemPrompt;

    @Value("${openai.chat-system-prompt}")
    private String ChatSystemPrompt;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.chat-model}")
    private String chatModel;

    @Value("${openai.sum-model}")
    private String sumModel;

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.agree-system-prompt}")
    private String AgreeSystemPrompt;

    @Value("${openai.disagree-system-prompt}")
    private String DisagreeSystemPrompt;

    @Value("${openai.agree-model}")
    private String AgreeModel;

    @Value("${openai.disagree-model}")
    private String DisagreeModel;



    public String chat(String prompt) {
        // create a request
        ChatRequestDto request = new ChatRequestDto(model, prompt, ChatSystemPrompt);

        // call the API
        ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }

    public String summary(Long billId){
        try {
            Bill bill = getBill(billId);
            if (bill.getSummary() != null) {
                return bill.getSummary();
            }

            String link = bill.getFileLink();
            String extractText = PdfUtil.extractTextFromPdf(link);

            String summary = gptService.summarize(sumModel, extractText, SumSystemPrompt);

            bill.saveSummary(summary);
            billRepository.save(bill);

            return summary;
        } catch (IOException e) {
            return "Failed to process the document: " + e.getMessage();
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

    public String checkChat(String prompt, Long billId) {
        Bill bill = getBill(billId);

        String link = bill.getFileLink();

        try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream());){
            PDDocument document = PDDocument.load(in);
            PDFTextStripper stripper = new PDFTextStripper();
            String extractText = stripper.getText(document);

            String txt = prompt + extractText +"External references include a '(출처:" + bill.getTitle() +") citation.";

            ChatRequestDto request = new ChatRequestDto(chatModel, txt, ChatSystemPrompt);

            // call the API
            ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                return "No response";
            }

            // return the first response
            return response.getChoices().get(0).getMessage().getContent();
        }
        catch (IOException ignored) {
            return "No response";
        }
    }

    public String agree(String prompt, String pdf) {
        String system = AgreeSystemPrompt + pdf;
        // create a request
        ChatRequestDto request = new ChatRequestDto(AgreeModel, prompt, system);

        // call the API
        ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }
    public String disagree(String prompt, String pdf) {
        String system = DisagreeSystemPrompt + pdf;
        // create a request
        ChatRequestDto request = new ChatRequestDto(DisagreeModel, prompt, system);

        // call the API
        ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }

    private Bill getBill(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException(billId));
    }

}
