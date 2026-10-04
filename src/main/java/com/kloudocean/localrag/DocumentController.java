package com.kloudocean.localrag;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentController {

    private final VectorStore vectorStore;

    public DocumentController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/api/documents")
    public UploadResult upload(@RequestParam("file") MultipartFile file) {
        // 1. Read: one Document per page, with its file name and page number
        List<Document> pages = new PagePdfDocumentReader(file.getResource()).get();

        // 2. Split: small chunks make search more accurate
        List<Document> chunks = TokenTextSplitter.builder().withChunkSize(300).build().apply(pages);

        // 3. Store: embed every chunk and save it in pgvector
        vectorStore.add(chunks);

        return new UploadResult(file.getOriginalFilename(), pages.size(), chunks.size());
    }

    public record UploadResult(String file, int pages, int chunks) {
    }
}