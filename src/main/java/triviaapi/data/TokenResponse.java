package triviaapi.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("response_code")
        int responseCode,
        @JsonProperty("response_message")
        String message,
        @JsonProperty("token")
        String token
) implements IResponseData {
}
