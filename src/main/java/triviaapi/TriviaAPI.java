package triviaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import triviaapi.data.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TriviaAPI {
    private static final String TOKEN_REQUEST_URI = "https://opentdb.com/api_token.php?command=request";
    private static final String CATEGORY_LOOKUP_URI = "https://opentdb.com/api_category.php";
    private static final String QUESTION_ENDPOINT_URI = "https://opentdb.com/api.php?amount=2&category=21&difficulty=hard&type=boolean";

    private final String sessionToken;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    private final Map<String, Integer> categories;

    public TriviaAPI() throws IOException {
        this.httpClient = HttpClients.createDefault();
        this.mapper = new ObjectMapper();

        this.sessionToken = this.createSessionToken();
        this.categories = this.retrieveCategories();
    }

    private void validateState() {
        if (this.httpClient == null)
            throw new IllegalStateException("HttpClient not initialized!");
        if (this.mapper == null)
            throw new IllegalStateException("ObjectMapper not initialized!");
    }

    private void validateResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 200) return;
        throw new IllegalStateException(String.format("Server responded with code %d: %s",
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase()));
    }

    private Map<String, Integer> retrieveCategories() throws IOException {
        Map<String, Integer> availableCategories = new HashMap<>();
        HttpGet categoryLookupRequest = new HttpGet(CATEGORY_LOOKUP_URI);
        this.validateState();
        HttpResponse categoryLookupResponse = this.httpClient.execute(categoryLookupRequest);
        this.validateResponse(categoryLookupResponse);

        CategoryLookupResponse responseData = this.mapper.readValue(categoryLookupResponse.getEntity().getContent(), CategoryLookupResponse.class);

        for (TriviaCategory category : responseData.triviaCategories()) {
            availableCategories.put(category.name(), category.id());
        }

        return availableCategories;
    }

    private String createSessionToken() throws IOException {
        if (this.sessionToken != null)
            throw new IllegalStateException("Session token already exists!");
        HttpGet tokenRequest = new HttpGet(TOKEN_REQUEST_URI);
        this.validateState();

        HttpResponse tokenResponse = this.httpClient.execute(tokenRequest);
        this.validateResponse(tokenResponse);

        TokenResponse data = this.mapper.readValue(tokenResponse.getEntity().getContent(), TokenResponse.class);

        if (data.responseCode() != 0)
            throw new IllegalStateException("Failed to create trivia API session!");

        return data.token();
    }

    public Set<String> getAvailableCategories() {
        return this.categories.keySet();
    }

    /**
     * Retrieve trivia questions from the API.
     * @param amount The amount of questions to retrieve
     * @param category The category from which questions are queried. Use `TriviaAPI#getAvailableCategories`
     *                 to get a list of available categories or use `null` to query from any category.
     * @param difficulty The question's difficulty level
     * @param type The question type
     *
     * @return A list of `TriviaQuestionData` objects matching the provided criteria
     */
    public List<TriviaQuestionData> getTriviaQuestions(int amount, String category, QuestionDifficulty difficulty, QuestionType type) throws URISyntaxException, IOException {
        if (amount < 0) throw new IllegalArgumentException("`amount` must be positive!");

        Integer categoryId = null;
        if (category != null) {
            categoryId = categories.get(category);
            if (categoryId == null)
                throw new IllegalArgumentException(String.format("Category `%s` doesn't exist!", category));
        }

        URIBuilder builder = new URIBuilder(QUESTION_ENDPOINT_URI)
                .addParameter("encode", "url3986")
                .addParameter("token", this.sessionToken)
                .addParameter("amount", String.valueOf(amount))
                .addParameter("type", type.getKey())
                .addParameter("difficulty", difficulty.getKey());
        if (categoryId != null) builder.addParameter("category", categoryId.toString());
        HttpGet questionsRequest = new HttpGet(builder.build());

        HttpResponse questionsResponse = this.httpClient.execute(questionsRequest);
        this.validateResponse(questionsResponse);

        TriviaQuestionResponse responseData = this.mapper.readValue(questionsResponse.getEntity().getContent(), TriviaQuestionResponse.class);
        return responseData.triviaQuestions();
    }
}
