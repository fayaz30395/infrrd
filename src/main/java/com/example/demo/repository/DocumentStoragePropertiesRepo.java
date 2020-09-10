package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.DocStorageProperties;

public interface DocumentStoragePropertiesRepo extends JpaRepository<DocStorageProperties, Integer> {

	@Query("Select a from DocumnentStorageProperties a where user_id = ?1 and document_type = ?2")
	DocStorageProperties checkDocumentByUserId(Integer userId, String docType);

	@Query("Select fileName from DocumnentStorageProperties a where user_id = ?1 and document_type = ?2")
	String getUploadDocumnetPath(Integer userId, String docType);

}