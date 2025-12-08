package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class FileUploadController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            HttpServletRequest request) {
        
        Resource resource = fileStorageService.loadImageAsResource(filename);
        
        // Determine content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Could not determine file type
        }
        
        // Fallback to default content type
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

