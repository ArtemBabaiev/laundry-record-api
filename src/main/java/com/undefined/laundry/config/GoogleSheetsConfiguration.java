package com.undefined.laundry.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

@Configuration
public class GoogleSheetsConfiguration {

	@Value("${CREDENTIALS_PATH}")
	private String credentialPath;

	@Value("${spring.application.name}")
	private String applicationName;

	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	@Bean
	Sheets sheets() throws IOException, GeneralSecurityException {
		try (FileInputStream in = new FileInputStream(credentialPath)) {
			GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
			NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
					clientSecrets, scopes)
					.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
					.setAccessType("online").build();
			
			LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
			Credential credenital = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
			
			return new Sheets.Builder(httpTransport, jsonFactory, credenital).setApplicationName(applicationName)
					.build();
		}
	}
}
