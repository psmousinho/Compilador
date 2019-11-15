
package common;

import lexical.exceptions.UnknownSymbolException;
import lexical.exceptions.CommentNotClosedException;
import java.util.LinkedList;
import java.io.File;
import lexical.Automaton;
import syntactic.Analyzer;
import syntactic.exceptions.MismatchSymbolException;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

        try {
            File file = new File(args[0]);
            
            Automaton a = new Automaton();
            LinkedList<Token> list = a.parse(file);
            for (Token t : list) {
                System.out.println(t);
            }
            
            Analyzer sintatic = new Analyzer(list);
            sintatic.analyze();
            
            System.out.println("Sintatic done");
        } catch (UnknownSymbolException | CommentNotClosedException | MismatchSymbolException ex) {
            ex.printStackTrace();
        }

    }
}
