package com.kite.automation.google.sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

@Component
public class DriveService {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE_READONLY);
    private static final String CREDENTIALS = System.getenv("GCP_CREDENTIALS");
    private static final String SS_ID = System.getenv("GCP_SS_ID");

    @SneakyThrows
    public static Drive getService()
    {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredential.fromStream(new ByteArrayInputStream(CREDENTIALS.getBytes()))
                .createScoped(SCOPES))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

//    public static void main(String[] args) {
//        ThreadExecutor threadExecutor = new ThreadExecutor();
//        IndexDataLoader indexDataLoader = new IndexDataLoader();
//        indexDataLoader.setThreadExecutor(threadExecutor);
//        indexDataLoader.loadNfoData();
//    }

}
