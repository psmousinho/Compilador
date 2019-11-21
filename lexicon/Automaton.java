package lexicon;

import common.Token;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import lexicon.exceptions.*;

public class Automaton {

    private int state;
    private int lastSafeState;
    private String token;
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
            put(1, "NF");
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
        this.lastSafeState = 0;
        this.token = "";
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
                } else if (symbol.matches(";|\\.|,|\\(|\\)")) {
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
                break;
            //relacional >
            case 9:
                if (symbol.matches("=")) {
                    state = 10;
                } else {
                    return false;
                }
                break;
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

    public LinkedList<Token> parse(File file) throws UnknownSymbolException, CommentNotClosedException {
        LinkedList<Token> tokenList = new LinkedList<>();
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String wordBuffer = "";

            int c;
            while ((c = br.read()) != -1) {
                Character cha = (char) c;
                boolean acc = transition(cha.toString());

                /*salva caracteres validos e.g caracteres que levam para fora do estado inicial
                e que nao foram lidos durante o estado de comentario*/
                if (state != 0 && state != 1) {
                    wordBuffer += cha;
                }

                //se foi encontrada uma transicao
                if (acc) {
                    lastSafeState = state;
                    token = wordBuffer;
                    br.mark(1000);
                } //se a transicao nao foi encontrada durante o estado inicial
                else if (lastSafeState == 0) {
                    throw new UnknownSymbolException(line);
                } //adicionando o ultimo token valido
                else {
                    String cla = (state == 2 && reserved.containsKey(token)) ? reserved.get(token) : classification.get(lastSafeState);

                    tokenList.add(new Token(token, cla, line));
                    br.reset();
                    wordBuffer = "";
                    state = 0;
                    lastSafeState = 0;
                }
            }

            //se o ultimo estado valido foi o de comentario
            if (lastSafeState == 1) {
                throw new CommentNotClosedException();
            } //se o ultimo estado valido nao foi o inicial. Serve para pegar o ultimo token escrito
            else if (lastSafeState != 0) {
                String cla = (state == 2 && reserved.containsKey(token)) ? reserved.get(token) : classification.get(lastSafeState);
                tokenList.add(new Token(token, cla, line));
            }

            fr.close();
            br.close();
        } catch (IOException ex) {
            System.err.println("Erro durante Parsing");
        }

        return tokenList;
    }

}
