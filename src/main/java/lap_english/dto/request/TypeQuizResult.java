package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeQuizResult {
    @JsonProperty("quizzVocabulary")
    quizzVocabulary,
    @JsonProperty("quizzSentence")
    quizzSentence,
    @JsonProperty("quizGrammar")
    quizGrammar,
    @JsonProperty("quizzCustom")
    quizzCustom

}
