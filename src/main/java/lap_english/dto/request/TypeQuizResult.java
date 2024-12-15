package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeQuizResult {
    @JsonProperty("quizzVocabulary")
    QUIZ_VOCABULARY,
    @JsonProperty("quizzSentence")
    QUIZ_SENTENCE,
    @JsonProperty("quizGrammar")
    QUIZ_GRAMMAR,
    @JsonProperty("quizzCustom")
    QUIZ_CUSTOM

}
