package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeQuizResult {
    @JsonProperty("quizzVocabulary")
    quizzVocabulary, // tu vung
    @JsonProperty("quizzSentence")
    quizzSentence, // cau
    @JsonProperty("quizGrammar")
    quizGrammar, //grammar
    @JsonProperty("quizzCustom")
    quizzCustom //

}
