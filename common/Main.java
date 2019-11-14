package common;

import java.util.LinkedList;
import java.io.File;
import lexico.Automaton;
import lexico.exceptions.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File(args[0]);
        Automaton a = new Automaton();

        try {
            LinkedList<Token> list = a.parse(file);
            for (Token t : list) {
                System.out.println(t);
            }
        } catch (UnknownSymbolException | CommentNotClosedException ex) {
            ex.printStackTrace();
        }

    }
}
