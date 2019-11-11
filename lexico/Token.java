package lexico;

public class Token {

    private final String value;
    private final String classification;
    private final int line;

    public Token(String value, String classification, int line) {
        this.value = value;
        this.classification = classification;
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public String getClassification() {
        return classification;
    }

    public int getLine() {
        return line;
    }

    public String toString() {

        return value + " | " + classification + " | " + line;

    }
}

