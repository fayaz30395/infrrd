package com.example.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.response.DeleteFileResponse;
import com.example.demo.response.UploadFileResponse;
import com.example.demo.service.DocumentStorageService;

@RestController
public class DocumentController {
	@Autowired
	private DocumentStorageService documneStorageService;

	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("userId") Integer UserId, @RequestParam("docType") String docType) {
		String fileName = documneStorageService.storeFile(file, UserId, docType);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();
		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	@PostMapping("/deleteFile")
	public DeleteFileResponse deleteFile(@RequestParam("file") MultipartFile file,
			@RequestParam("userId") Integer UserId, @RequestParam("docType") String docType) {
		String fileName = documneStorageService.removeFile(file, UserId, docType);
		return new DeleteFileResponse(fileName, file.getContentType(), file.getSize());
	}

	@GetMapping("/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam("userId") Integer userId,
			@RequestParam("docType") String docType, HttpServletRequest request) {
		String fileName = documneStorageService.getDocumentName(userId, docType);
		Resource resource = null;
		if (fileName != null && !fileName.isEmpty()) {
			try {
				resource = documneStorageService.loadFileAsResource(fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Try to determine file's content type
			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
				// logger.info("Could not determine file type.");
			}
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}