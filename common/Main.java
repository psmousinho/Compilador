package common;

import java.io.File;
import java.util.LinkedList;
import lexicon.Automaton;
import syntactic.Analyzer;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            File file = new File("C:\\Users\\Pablo Suria\\Documents\\Java\\NetBeans\\compilador\\src\\test files\\41");

            Automaton a = new Automaton();
            LinkedList<Token> list = a.parse(file);
            for (Token t : list) {
                System.out.println(t);
            }

            Analyzer sintatic = new Analyzer(list);
            sintatic.analyze();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
