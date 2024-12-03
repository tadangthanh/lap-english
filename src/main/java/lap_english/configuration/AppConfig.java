package lap_english.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.io.IOException;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
@EnableCaching
@EnableAsync
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

    @Bean(name = "taskExecutor") // Đặt tên taskExecutor
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Số thread chính
        executor.setMaxPoolSize(10); // Số thread tối đa
        executor.setQueueCapacity(500); // Dung lượng hàng đợi
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        return JsonMapper
//                .builder()
//                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
//    }
}
