package common;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.value);
        hash = 11 * hash + Objects.hashCode(this.classification);
        hash = 11 * hash + this.line;
        return hash;
    }
    
    public boolean equals(Token other) {
        if(   this.value.equals(other.value)
           && this.classification.equals(other.classification)
           && this.line == other.line ) {
            return true;
        }
        return false;
    }

}
