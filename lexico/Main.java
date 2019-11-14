package lexico;

import java.util.LinkedList;
import java.io.File;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("C:\\Users\\luyza\\Desktop\\Compilador\\lexico\\teste");
        Automaton a = new Automaton();
        LinkedList<Token> list = a.parse(file);
        for(Token t : list) {
            System.out.println(t);
        }
     
    }    
}
