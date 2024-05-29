package site.allawbackend.chat.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class PdfUtil {
    public static String extractTextFromPdf(String fileLink) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileLink).openStream())) {
            PDDocument document = PDDocument.load(in);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

}
