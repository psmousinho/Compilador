package lexico;

import java.util.LinkedList;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexico.exceptions.UnknownSymbolException;
import lexico.exceptions.commentNotClosedException;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Pablo Suria\\Documents\\Workspace\\compilador\\Arquivos de Teste\\teste2");
        Automaton a = new Automaton();

        try {
            
            LinkedList<Token> list = a.parse(file);
            for (Token t : list) {
                System.out.println(t);
            }
 
        } catch (UnknownSymbolException ex) {
            ex.printStackTrace();
        } catch (commentNotClosedException ex) {
            ex.printStackTrace();
        }

    }
}
