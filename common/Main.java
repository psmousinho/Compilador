
package common;

import java.io.File;
import java.util.LinkedList;
import lexico.Automaton;
import lexico.exceptions.CommentNotClosedException;
import lexico.exceptions.UnknownSymbolException;
import syntactic.Analyzer;
import syntactic.exceptions.MismatchSymbolException;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

        try {
            File file = new File("C:\\Users\\Pablo Suria\\Documents\\Java\\NetBeans\\compilador\\src\\test files\\test0");
            
            Automaton a = new Automaton();
            LinkedList<Token> list = a.parse(file);
            for (Token t : list) {
                System.out.println(t);
            }
            
            Analyzer sintatic = new Analyzer(list);
            sintatic.analyze();
            
        } catch (UnknownSymbolException | CommentNotClosedException | MismatchSymbolException ex) {
            ex.printStackTrace();
        }

    }
}
