package lap_english.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class AppConfig {
    @Bean
    public TextToSpeechClient textToSpeechClient() throws IOException {
        // Tải file credentials từ resources
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("lapenglish-texttospeech.json").getInputStream()
        );

        // Tạo TextToSpeechSettings từ credentials
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        // Khởi tạo TextToSpeechClient
        return TextToSpeechClient.create(settings);
    }

}
