
package Semantic;

import Semantic.exceptions.DuplicateIdentifierException;
import Semantic.exceptions.UnidentifiedSymbolException;
import common.Token;
import java.util.LinkedList;

public class SymbolTable {
    LinkedList<Token> st;

    public SymbolTable() {
        st = new LinkedList<>();
    }
    
    public void addMark() {
        st.add(new Token("$","MARK",0));
    }
    
    private boolean scopeContainsSymbol(Token t) {
        Token in;
        int i = st.size()-1;
        do {
            
            in = st.get(i--);
            if(in.getValue().equals(t.getValue()))
                return true;
            
        } while(!in.getValue().equals("$"));
        
        return false;
    }
    
    public void addSymbol(Token t)throws DuplicateIdentifierException{
        if(!scopeContainsSymbol(t)) {
            st.add(t);
        } else {
            throw new DuplicateIdentifierException("Identificador duplicado: " + t.getValue() + " na linha: " + t.getLine());
        }
        
    }
    
    public void containsSymbol(Token t) throws UnidentifiedSymbolException{
        for(Token token : st) {
            if(token.getValue().equals(t.getValue()))
                return;
        }
        throw new UnidentifiedSymbolException("Simbolo nao identificado: " + t.getValue() +" na Linha " + t.getLine());
    }
    
    public void removeLastScope(){
        Token in;
        do {
            in = st.removeLast();
        } while(!in.getValue().equals("$"));
    }
    
}
