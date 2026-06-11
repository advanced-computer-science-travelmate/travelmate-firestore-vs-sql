package com.travelmate.travelmate_api;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@SpringBootApplication
public class TravelMateCloudApplication {
	
	@Value("${travelmate.firestore.project-id}")
	private String projectId;
	
	@Value("${travelmate.firestore.database-id}")
	private String databaseId;
	
	@Value("${travelmate.firestore.credentials-path}")
	private String credentialsPath;
	
	public static void main(String[] args) {
		SpringApplication.run(TravelMateCloudApplication.class, args);
	}
	
	@Bean
	public Firestore firestore() throws IOException {
		return FirestoreOptions.getDefaultInstance().toBuilder()
				.setProjectId(projectId)
				.setDatabaseId(databaseId)
				.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
				.build()
				.getService();
	}
}
