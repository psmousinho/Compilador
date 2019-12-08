
package Semantic;

import Semantic.exceptions.InvalidOperandsException;
import common.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TypeCheckingTable {
    
    private final LinkedList<String> tpt;
    private final ArrayList<HashMap<String,String>> idType;
    private final LinkedList<String> buffer;

    public TypeCheckingTable() {
        tpt = new LinkedList<>();
        idType = new ArrayList<>();
        buffer = new LinkedList<>();
    }
    
    public void addToBuffer(Token t) {
        buffer.add(t.getValue());
    }
    
    public void defineBufferType(String type) {
        int scope = idType.size()-1;
        for(String t : buffer) {
            idType.get(scope).put(t, type);
        }
        buffer.clear();
    }
    
    public void pushType(String type) {
        tpt.push(type);    
    }
    
    public void pushId(Token id) {
        for(int i = idType.size()-1; i >= 0; i--) {
            if(idType.get(i).keySet().contains(id.getValue())) {
                tpt.push(idType.get(i).get(id.getValue()));
                break;
            }
        }
    }
    
    public void openScope() {
        idType.add(new HashMap<>());
    }
    
    public void removeScope() {
        idType.remove(idType.size()-1);
    }
    
    public void executeOp(String op,int line) throws InvalidOperandsException {
        String t2 = tpt.pop();
        String t1 = tpt.pop();
        switch(op) {
            case "Operador Aditivo":
            case "Operador Multiplicativo":
                if(t1.equals("integer") && t2.equals("integer")){
                    tpt.push("integer");
                }
                else if(t1.equals("real") && t2.equals("integer")){
                    tpt.push("real");
                }
                else if(t1.equals("integer") && t2.equals("real")){
                    tpt.push("real");
                }
                else if(t1.equals("real") && t2.equals("real")){
                    tpt.push("real");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op+ " : " + t1 + " " + t2 + " na linha " + line);
                }
                
                break;
            case "Operador Relacional":
             
                if((t1.equals("integer") || t1.equals("real")) && (t2.equals("integer") || t2.equals("real")) ) {
                    tpt.push("boolean");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op+ " : " + t1 + " " + t2 + " na linha " + line);
                }
                break;
            case "Operador Logico":
                if(t1.equals(t2) && t1.equals("Boolean")) {
                    tpt.push("boolean");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op+ " : " + t1 + " " + t2 + " na linha " + line);
                }
                break;
            case "Atribuicao":
                if(!t1.equals(t2)) {
                    if(!(t1.equals("real") && t2.equals("integer"))) {
                        throw new InvalidOperandsException("Tipos invalidos para " + op+ " : " + t1 + " " + t2 + " na linha " + line);
                    }
                }
                break;
        }
    }
    
}
