package lexico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Automaton {

    private int state;
    private int line;

    static final HashMap<String, String> reserved = new HashMap<String, String>() {
        {
            put("program", "Palavra Reservada");
            put("var", "Palavra Reservada");
            put("integer", "Palavra Reservada");
            put("real", "Palavra Reservada");
            put("boolean", "Palavra Reservada");
            put("procedure", "Palavra Reservada");
            put("begin", "Palavra Reservada");
            put("end", "Palavra Reservada");
            put("if", "Palavra Reservada");
            put("then", "Palavra Reservada");
            put("else", "Palavra Reservada");
            put("while", "Palavra Reservada");
            put("do", "Palavra Reservada");
            put("not", "Palavra Reservada");
            put("or", "Operador Aditivo");
            put("and", "Operador Multiplicativo");
        }
    };

    static final HashMap<Integer, String> classification = new HashMap<Integer, String>() {
        {
            put(0, "NF");
            put(1, "Comentario");
            put(2, "Identificador");
            put(3, "Numero Inteiro");
            put(4, "Numero Real");
            put(5, "Delimitador");
            put(6, "Delimitador");
            put(7, "Atribuicao");
            put(8, "Operador Relacional");
            put(9, "Operador Relacional");
            put(10, "Operador Relacional");
            put(11, "Operador Aditivo");
            put(12, "Operador Multiplicativo");
        }
    };

    public Automaton() {
        this.state = 0;
        this.line = 1;
    }

    private boolean transition(String symbol) {
        switch (state) {
            //estado inicial
            case 0:
                if (symbol.matches("\\s")) {
                    if (symbol.matches("\\n")) {
                        line++;
                    }
                } else if (symbol.matches("\\{")) {
                    state = 1;
                } else if (symbol.matches("[a-zA-Z]")) {
                    state = 2;
                } else if (symbol.matches("\\d")) {
                    state = 3;
                } else if (symbol.matches(";|\\.|(|)|,")) {
                    state = 5;
                } else if (symbol.matches(":")) {
                    state = 6;
                } else if (symbol.matches("<")) {
                    state = 8;
                } else if (symbol.matches(">")) {
                    state = 9;
                } else if (symbol.matches("=")) {
                    state = 10;
                } else if (symbol.matches("\\+|\\-")) {
                    state = 11;
                } else if (symbol.matches("\\*|\\/")) {
                    state = 12;
                } else {
                    return false;
                }
                break;
            //comentario
            case 1:
                if (symbol.matches("\\}")) {
                    state = 0;
                } else if (symbol.matches("\\n")) {
                    line++;
                }
                break;
            //identificador
            case 2:
                if (symbol.matches("\\W")) {
                    return false;
                }
                break;
            //numero
            case 3:
                if (symbol.matches("\\.")) {
                    state = 4;
                } else if (symbol.matches("\\D")) {
                    return false;
                }
                break;
            //numero real
            case 4:
                if (symbol.matches("\\D")) {
                    return false;
                }
                break;
            //delimitador
            case 5:
                return false;
            //delimitador :
            case 6:
                if (symbol.matches("=")) {
                    state = 7;
                } else {
                    return false;
                }
                break;
            //atribuicao
            case 7:
                return false;
            //relacional <
            case 8:
                if (symbol.matches("=|>")) {
                    state = 10;
                } else {
                    return false;
                }
            //relacional >
            case 9:
                if (symbol.matches("=")) {
                    state = 10;
                } else {
                    return false;
                }
            //realcional =|<>|<=|>=
            case 10:
                return false;
            //aditivo    
            case 11:
                return false;
            //multiplicativo
            case 12:
                return false;
        }
        return true;
    }

    private String process(String str) {
        LinkedList<Integer> stateBuffer = new LinkedList<>(); //guarda o estado
        String wordBuffer = ""; //guarda o token

        for (int i = 0; i < str.length(); i++) {
            Character cha = str.charAt(i);

            if (transition(cha.toString())) {
                stateBuffer.push(state);
                if (state != 0 && state != 1) {
                    wordBuffer += cha;
                }
            } else {
                int count = 0;
                while (classification.get(stateBuffer.peek()).equals("NF")) {
                    stateBuffer.pop();
                    count++;
                    if (count == stateBuffer.size()) {
                        //erro caractere nao indentificado
                    }
                }
                String cla;
                if (state == 2 && reserved.containsKey(wordBuffer.length() - count)) {
                    cla = reserved.get(wordBuffer.length() - count);
                } else {
                    cla = classification.get(stateBuffer.peek());
                }
                System.out.println(wordBuffer.substring(0, wordBuffer.length() - count) + "|" + cla + "|" + line);
                state = 0;
                process(wordBuffer.substring(wordBuffer.length() - count));
                //wordBuffer = "";
            }
        }
        return wordBuffer;
    }

    public void parse(File file) {
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            LinkedList<Integer> stateBuffer = new LinkedList<>(); //guarda o estado
            String wordBuffer = ""; //guarda o token

            int c;

            while ((c = br.read()) != -1) {
                Character cha = (char) c;

                boolean acc = transition(cha.toString());
                stateBuffer.push(state);
                if (state != 0 && state != 1) {
                    wordBuffer += cha;
                }
                if (!acc) {
                    int count = 1;
                    while (classification.get(stateBuffer.peek()).equals("NF")) {
                        stateBuffer.pop();
                        count++;
                        if (0 == stateBuffer.size()) {
                            //erro caractere nao indentificado
                        }
                    }
                    String cla;
                    if (state == 2 && reserved.containsKey(wordBuffer.substring(0, wordBuffer.length() - count))) {
                        cla = reserved.get(wordBuffer.substring(0, wordBuffer.length() - count));
                    } else {
                        cla = classification.get(stateBuffer.peek());
                    }
                    System.out.println(wordBuffer.substring(0, wordBuffer.length() - count) + "|" + cla + "|" + line);
                    state = 0;
                    wordBuffer = process(wordBuffer.substring(wordBuffer.length() - count));
                }

            }
            fr.close();
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] arg) {
        File file = new File("C:\\Users\\Pablo Suria\\Desktop\\teste.txt");
        Automaton a = new Automaton();
        a.parse(file);

    }
}
