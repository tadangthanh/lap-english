package lap_english.service.impl;

import com.google.cloud.texttospeech.v1.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextToSpeechService {


    private final TextToSpeechClient textToSpeechClient;

    public byte[] synthesizeText(String text, String languageCode, SsmlVoiceGender gender) throws Exception {
        // Cấu hình đầu vào văn bản
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        // Cấu hình giọng nói
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(languageCode) // Ngôn ngữ: US, UK, etc.
                .setSsmlGender(gender)         // Giới tính: Nam hoặc Nữ
                .setName(getVoiceName(languageCode, gender)) // Lấy giọng cụ thể
                .build();

        // Cấu hình âm thanh
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3) // Định dạng âm thanh
                .build();

        // Gửi yêu cầu đến API
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        // Trả về dữ liệu âm thanh dưới dạng byte array
        return response.getAudioContent().toByteArray();
    }
    private String getVoiceName(String languageCode, SsmlVoiceGender gender) {
        switch (languageCode) {
            case "en-US": // Tiếng Anh Mỹ
                return gender == SsmlVoiceGender.MALE ? "en-US-Standard-B" : "en-US-Standard-A";
            case "en-GB": // Tiếng Anh Anh
                return gender == SsmlVoiceGender.MALE ? "en-GB-Standard-B" : "en-GB-Standard-A";
            default: // Mặc định là tiếng Anh Mỹ (Male)
                return "en-US-Standard-B";
        }
    }


}
