package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeQuizResult {
    @JsonProperty("TypeQuizz.quizzVocabulary")
    quizzVocabulary, // tu vung
    @JsonProperty("TypeQuizz.quizzSentence")
    quizzSentence, // cau
    @JsonProperty("TypeQuizz.quizGrammar")
    quizGrammar, //grammar
    @JsonProperty("TypeQuizz.quizzCustom")
    quizzCustom //

}
