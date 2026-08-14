package de.thkoeln.ccq.firemanager.document;

import de.thkoeln.ccq.firemanager.document.exception.DocumentNotFoundException;
import de.thkoeln.ccq.firemanager.member.MemberLocationAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MemberLocationAssignmentService memberLocationAssignmentService;
    private final Path storagePath;

    public DocumentService(
            DocumentRepository documentRepository,
            MemberLocationAssignmentService memberLocationAssignmentService
    ) {
        this.documentRepository = documentRepository;
        this.memberLocationAssignmentService = memberLocationAssignmentService;
        this.storagePath = Paths.get("/app/storage/documents");
        try {
            Files.createDirectories(this.storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public Document uploadDocument(MultipartFile file, UUID memberId, UUID uploadedBy) {
        if (!memberLocationAssignmentService.existsByMemberId(memberId)) {
            throw new IllegalArgumentException("Member with id " + memberId + " does not exist");
        }

        String originalName = file.getOriginalFilename();
        String mimeType = file.getContentType();
        Long size = file.getSize();

        if (size > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10 MB");
        }

        if (!isValidMimeType(mimeType)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, PNG, JPG allowed");
        }

        String fileName = UUID.randomUUID() + "_" + originalName;
        Path filePath = this.storagePath.resolve(fileName);

        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }

        Document document = new Document(
                originalName,
                mimeType,
                size,
                filePath.toString(),
                uploadedBy,
                LocalDateTime.now(),
                memberId
        );
        
        return this.documentRepository.save(document);
    }

    public List<Document> getDocumentsByMemberId(UUID memberId) {
        return this.documentRepository.findByMemberId(memberId);
    }

    public Document getDocumentById(UUID documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId must not be null");
        }
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    public void deleteDocumentById(UUID documentId) {
        Document document = getDocumentById(documentId);
        
        try {
            Files.deleteIfExists(Path.of(document.getStoragePath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
        
        this.documentRepository.deleteById(documentId);
    }

    private boolean isValidMimeType(String mimeType) {
        return mimeType != null && (
            mimeType.equals("application/pdf") ||
            mimeType.equals("image/png") ||
            mimeType.equals("image/jpeg")
            );
    }
}