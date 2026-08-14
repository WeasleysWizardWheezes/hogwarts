package de.thkoeln.ccq.firemanager.document;

import de.thkoeln.ccq.firemanager.document.dto.DocumentResponse;
import de.thkoeln.ccq.firemanager.document.dto.UploadDocumentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members/{memberId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable UUID memberId,
            @RequestParam("file") MultipartFile file,
            @RequestParam UUID uploadedBy
    ) {
        Document document = documentService.uploadDocument(file, memberId, uploadedBy);
        
        DocumentResponse response = new DocumentResponse(
                document.getId(),
                document.getOriginalName(),
                document.getMimeType(),
                document.getSize(),
                document.getUploadedBy(),
                document.getUploadedAt(),
                "/api/v1/members/" + memberId + "/documents/" + document.getId()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getDocumentsByMemberId(@PathVariable UUID memberId) {
        List<Document> documents = documentService.getDocumentsByMemberId(memberId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable UUID memberId,
            @PathVariable UUID documentId
    ) throws IOException {
        Document document = documentService.getDocumentById(documentId);
        
        Path filePath = Path.of(document.getStoragePath());
        byte[] fileContent = Files.readAllBytes(filePath);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(document.getMimeType()));
        headers.setContentDispositionFormData("attachment", document.getOriginalName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID memberId,
            @PathVariable UUID documentId
    ) {
        documentService.deleteDocumentById(documentId);
        return ResponseEntity.noContent().build();
    }
}