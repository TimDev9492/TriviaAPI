package triviaapi.data;

public enum QuestionType {
    MULTIPLE_CHOICE("multiple"),
    TRUE_FALSE("boolean");

    private final String key;
    QuestionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static QuestionType fromKey(String key) {
        for (QuestionType type : QuestionType.values()) {
            if (type.getKey().equals(key)) return type;
        }
        return null;
    }
}

