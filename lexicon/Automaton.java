package lexicon;

import common.Token;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexicon.exceptions.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Automaton {

    private int state;
    private int lastSafeState;
    private String token;
    private int line;

    static final HashMap<Integer, String> classification = new HashMap<Integer, String>() {
        {
            put(0, "NF");
            put(2, "Ponto Final");
            put(3, "Ponto Exclamacao");
            put(4, "Ponto Interrogacao");
            put(5, "Virgula");
        }
    };
    
    static final HashMap<String, String> dicAux = new HashMap<String, String>() {
        {
            put("os", "artigo");
            put("disse", "verbo");
            put("dos", "contração");
            put("do", "contração");
            put("que", "conjução");
            put("testes", "substantivo");
            put("foi", "verbo");
            put("o", "artigo");
            put("são", "verbo");
            
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
                   state = 0;
                } else if(symbol.matches("\\w|ç|ã|õ")){
                    state = 1;
                }else if (symbol.equals(".")) {
                    line++;
                    state = 2;
                } else if (symbol.equals("!")) {
                    line++;
                    state = 3;
                } else if (symbol.equals("?")) {
                    line++;
                    state = 4;
                } else if (symbol.equals(",")) {
                    state = 5;
                } else {
                    return false;
                }
                break;
            //lendo palavra
            case 1:
                if (symbol.matches("\\W") && !symbol.matches("ç|ã|õ")) {
                    return false;
                }
                break;
            //ponto final
            case 2:
                return false;
            //ponto exclamacao
            case 3:
                return false;
            //ponto interrogacao
            case 4:
                return false;
            //virgula
            case 5:
                return false;
        }
        return true;
    }

    private String getDictionary(String word) {
        word = word.replace("ç", "c");
        word = word.replace("ã", "a");
        word = word.replace("õ", "o");
        word = word.toLowerCase();
        
        if(dicAux.containsKey(word)) {
            return dicAux.get(word);      
        }
        
        try { 
            Document doc = Jsoup.connect("https://www.dicio.com.br/" + word).get();
            Elements cl = doc.getElementsByClass("cl");
            
            if(!cl.isEmpty()) {
                return cl.first().text().stripLeading().split("\\s")[0];
            } else {
                cl = doc.getElementsByClass("significado");
                if(!cl.isEmpty()){
                    if(cl.first().text().contains("verbo")) {
                        return "verbo";
                    } else if(cl.first().text().contains("plural")) {
                        String aux = cl.first().text().split("\\s")[5];
                        return getDictionary(aux.substring(0,aux.length()-1));
                    } else if(cl.first().text().contains("feminino")) {
                        String aux = cl.first().text().split("\\s")[5];
                        return getDictionary(aux.substring(0,aux.length()-1));
                    }
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "null";
    }
    
    /*private void stemização(LinkedList<Token> list) {
        try { 
            
            for(Token tk : list){
                Document doc = Jsoup.connect("https://www.dicio.com.br/" + tk.getValue()).get();
                Elements cl = doc.getElementsByClass("cl");

                if(cl.isEmpty()) {
                   cl = doc.getElementsByClass("significado");
                   if(!cl.isEmpty()){
                        if(cl.first().text().contains("verbo")) {
                            String aux = cl.first().text().split("\\s")[4];
                            aux = aux.substring(0,aux.length()-1);
                            tk.setValue(aux);  
                        } else if(cl.first().text().contains("plural")) {
                            String aux = cl.first().text().split("\\s")[5];
                            aux = aux.substring(0,aux.length()-1);
                            tk.setValue(aux);
                        } else if(cl.first().text().contains("feminino")) {
                            String aux = cl.first().text().split("\\s")[5];
                            aux = aux.substring(0,aux.length()-1);
                            tk.setValue(aux);
                        }
                    }
                } 
            }  
        } catch (IOException ex) {
            Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
        }

    }*/
    
    private LinkedList<Token> removeStopwprds(LinkedList<Token> list) {
        LinkedList<Token> newList = new LinkedList<>();
        for(Token tk : list) {
            String cla = tk.getClassification();
            if(cla.equals("artigo") || cla.equals("conjução") || cla.equals("preposição") || cla.equals("contração") || cla.equals("Virgula"))
                continue;
            
            newList.add(tk);
        }
        return newList;
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
                if (state != 0) {
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
                    String cla = (state == 1) ? getDictionary(token) : classification.get(lastSafeState);

                    tokenList.add(new Token(token, cla, line));
                    br.reset();
                    wordBuffer = "";
                    state = 0;
                    lastSafeState = 0;
                }
            }

            //se o ultimo estado valido nao foi o inicial. Serve para pegar o ultimo token escrito
            if (lastSafeState != 0) {
                String cla = (state == 1) ? getDictionary(token) : classification.get(lastSafeState);
                tokenList.add(new Token(token, cla, line));
            }

            fr.close();
            br.close();
        } catch (IOException ex) {
            System.err.println("Erro durante Parsing");
        }
        
        //stemização(tokenList);
        return removeStopwprds(tokenList);
    }
    
    
}
