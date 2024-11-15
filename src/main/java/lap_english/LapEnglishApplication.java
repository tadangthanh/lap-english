package lap_english;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LapEnglishApplication {

	public static void main(String[] args) {
		// Load .env variables into environment
		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("AZURE_STORAGE_CONNECTION_STRING", dotenv.get("AZURE_STORAGE_CONNECTION_STRING"));
		SpringApplication.run(LapEnglishApplication.class, args);
	}

}
