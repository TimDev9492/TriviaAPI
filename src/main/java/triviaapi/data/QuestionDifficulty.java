package triviaapi.data;

public enum QuestionDifficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String key;
    QuestionDifficulty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static QuestionDifficulty fromKey(String key) {
        for (QuestionDifficulty difficulty : QuestionDifficulty.values()) {
            if (difficulty.getKey().equals(key)) return difficulty;
        }
        return null;
    }
}
