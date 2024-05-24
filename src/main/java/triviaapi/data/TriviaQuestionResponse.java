package triviaapi.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TriviaQuestionResponse(
        @JsonProperty("response_code")
        int responseCode,
        @JsonProperty("results")
        List<TriviaQuestionData> triviaQuestions
) implements IResponseData {
}
