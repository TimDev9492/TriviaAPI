package triviaapi.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record TriviaQuestionData(
        @JsonProperty("type")
        @JsonDeserialize(using = QuestionTypeDeserializer.class)
        QuestionType questionType,
        @JsonProperty("difficulty")
        @JsonDeserialize(using = QuestionDifficultyDeserializer.class)
        QuestionDifficulty difficulty,
        @JsonProperty("category")
        String category,
        @JsonProperty("question")
        @JsonDeserialize(using = URLEncodedStringDeserializer.class)
        String question,
        @JsonProperty("correct_answer")
        @JsonDeserialize(using = URLEncodedStringDeserializer.class)
        String correctAnswer,
        @JsonProperty("incorrect_answers")
        @JsonDeserialize(contentUsing = URLEncodedStringDeserializer.class)
        List<String> incorrectAnswers
) {
    public List<String> choices() {
        List<String> choices = new ArrayList<>(this.incorrectAnswers);
        choices.add(correctAnswer);
        return choices;
    }
}

class URLEncodedStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return URLDecoder.decode(jsonParser.getText(), StandardCharsets.UTF_8);
    }
}

class QuestionDifficultyDeserializer extends JsonDeserializer<QuestionDifficulty> {
    @Override
    public QuestionDifficulty deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        return QuestionDifficulty.fromKey(jsonParser.getText());
    }
}

class QuestionTypeDeserializer extends JsonDeserializer<QuestionType> {
    @Override
    public QuestionType deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        return QuestionType.fromKey(jsonParser.getText());
    }
}
