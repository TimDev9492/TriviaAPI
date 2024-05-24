package triviaapi.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CategoryLookupResponse(
        @JsonProperty("trivia_categories")
        List<TriviaCategory> triviaCategories
) {
}
