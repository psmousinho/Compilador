package syntactic;

import Semantic.SymbolTable;
import Semantic.TypeCheckingTable;
import Semantic.exceptions.DuplicateIdentifierException;
import Semantic.exceptions.InvalidOperandsException;
import Semantic.exceptions.UnidentifiedSymbolException;
import common.Token;
import java.util.ArrayList;
import java.util.LinkedList;
import syntactic.exceptions.EmptyOptionException;
import syntactic.exceptions.MismatchSymbolException;

public class Analyzer {

    private final LinkedList<Token> tokens;
    private final SymbolTable st;
    private final TypeCheckingTable tcp;
    private Token current_symbol;

    /**
     * Construtor que recebe a lista de tokens separadas pelo léxico
     *
     * @param tokens
     */
    public Analyzer(LinkedList<Token> tokens) {
        this.tokens = tokens;
        st = new SymbolTable();
        tcp = new TypeCheckingTable();
    }

    /**
     * Método que relizada a remoção(e retorno) do primeiro tokens da lista.
     */
    private void getNextSym() {
        if (!tokens.isEmpty()) {
            this.current_symbol = tokens.removeFirst();
        } else {
            this.current_symbol = new Token("", "", 0);
        }
    }

    /**
     * Adiciona de novo a cabeça da lista o símbolo atual
     */
    private void returnPrevSym() {
        tokens.addFirst(current_symbol);
    }

    public void analyze() throws MismatchSymbolException, DuplicateIdentifierException, UnidentifiedSymbolException, InvalidOperandsException {
        getNextSym();
        texto();
    }

    /**
     * Método que, de fato, faz a implentação da análise sintática
     *
     * @throws MismatchSymbolException
     */
	 
	
     private void texto()throws MismatchSymbolException{
        paragrafo_texto();
        getNextSym();
        if(current_symbol.getClassification().equals("Ponto Final") | current_symbol.getClassification().equals("Ponto Interrogacao") | current_symbol.getClassification().equals("Ponto Exclamacao")){
            System.out.println();
            System.out.println("***** SYNTATIC DONE *****");
            System.out.println();
        }else{
            getNextSym();
            paragrafo_texto();
        }
    }
    
    private void paragrafo_texto() throws MismatchSymbolException{ 
        try{
            frases();
            getNextSym();
            paragrafo_texto();
        } catch (EmptyOptionException ex){
            returnPrevSym();
        }
    }
     
   
    
    private void frases() throws EmptyOptionException, MismatchSymbolException{
        try{
            oracao();
            getNextSym();
            if(current_symbol.getClassification().equals("Ponto Final") | current_symbol.getClassification().equals("Ponto Exclamacao") | current_symbol.getClassification().equals("Ponto Interrogacao")){
                getNextSym();
                getNextSym();
                frases();
            }else{
                throw new MismatchSymbolException("Esperando pontuação na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }   
        }catch(EmptyOptionException ex){
            returnPrevSym();
        }
    }
    
    private void oracao() throws EmptyOptionException, MismatchSymbolException{
        
        np();
        vp();
        
    }
    
    private void np() throws EmptyOptionException, MismatchSymbolException{
        adjetivopc();
        getNextSym();
        np_linha();
    }
    
    private void np_linha() throws  MismatchSymbolException{
        try{
            np_linha_linha();
            getNextSym();
            if((current_symbol.getClassification().equals("pronome")) || (current_symbol.getClassification().equals("substantivo"))){
                np();
            }
//            else if(current_symbol.getClassification().equals(".") | current_symbol.getClassification().equals("?") | current_symbol.getClassification().equals("!")){
//                frases();
//            }
            else{
                vp();
            }
        }catch(EmptyOptionException ex){
            returnPrevSym();
        }
    }
    
    private void np_linha_linha() throws MismatchSymbolException{
        try{
            if( (current_symbol.getClassification().equals("pronome")) || (current_symbol.getClassification().equals("substantivo")) ){
                getNextSym();  
            }else{
                throw new MismatchSymbolException("Esperando pronome/substantivo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
//            if(current_symbol.getValue().equals("substantivo")){
//                    getNextSym();       
//            }else{
//                throw new MismatchSymbolException("Esperando substantivo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
//            }
          
            adjetivopc();       
        }catch(EmptyOptionException ex){
            returnPrevSym();
        }
    }
    
    private void adjetivopc() throws EmptyOptionException{
        if(current_symbol.getClassification().equals("adjetivo")){
            getNextSym();
            adjetivopc();
        }else{
            returnPrevSym();
        }
        
    }
    
    private void vp() throws MismatchSymbolException, EmptyOptionException{
        adverbiopc();
        getNextSym();
        if(current_symbol.getClassification().equals("verbo")){
            getNextSym();
            loc_verbal();
            getNextSym();
            adverbiopc();
            getNextSym();
            vp_complemento();     
        }else{
            returnPrevSym();
        }
        
    }
    
    
    private void adverbiopc(){
        if(current_symbol.getClassification().equals("advérbio")){
            getNextSym();
        }else{
            returnPrevSym();
        }
    }
    
    private void loc_verbal(){
        if(current_symbol.getClassification().equals("verbo")){
            getNextSym();
        }else{
            returnPrevSym();
        }
    }
    
    private void vp_complemento()throws EmptyOptionException, MismatchSymbolException{
        try{
         
          //  getNextSym();
            oracao();
            getNextSym(); 
        }catch(EmptyOptionException ex){
            returnPrevSym();
        }
    }
    
//    private void citacao_bugada(){
//        if(current_symbol.getClassification().equals("citacao")){
//            getNextSym();
//        }
//     }
	 
	
	/* 
    private void programa() throws MismatchSymbolException, DuplicateIdentifierException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("program")) {
            getNextSym();
            st.addMark();
            tcp.openScope();
            if (current_symbol.getClassification().equals("Identificador")) {
                st.addSymbol(current_symbol);
                getNextSym();
                if (current_symbol.getValue().equals(";")) {
                    getNextSym();
                    declaracoes_variaveis();
                    getNextSym();
                    declaracoes_subprogramas();
                    getNextSym();
                    comando_composto();
                    getNextSym();
                    if (current_symbol.getValue().equals(".")) {
                        System.out.println();
                        System.out.println("****** DONE ******");
                        System.out.println();
                    } else {
                        throw new MismatchSymbolException("Esperando Delimitador \'.\' na linha " + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                    }
                } else {
                    throw new MismatchSymbolException("Esperando ; na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                throw new MismatchSymbolException("Esperando identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando a palavra reservada \"program\" na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void declaracoes_variaveis() throws MismatchSymbolException, DuplicateIdentifierException {
        if (current_symbol.getValue().equals("var")) {
            getNextSym();
            lista_declaracoes_variaveis();
        } else {
            returnPrevSym();
        }
    }

    private void lista_declaracoes_variaveis() throws MismatchSymbolException, DuplicateIdentifierException {
        lista_identificadores();
        getNextSym();
        if (current_symbol.getValue().equals(":")) {
            getNextSym();
            tipo();
            getNextSym();
            if (current_symbol.getValue().equals(";")) {
                getNextSym();
                lista_declaracoes_variaveis2();
            } else {
                throw new MismatchSymbolException("Esperando ; na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando : na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void lista_declaracoes_variaveis2() throws MismatchSymbolException, DuplicateIdentifierException {
        if (current_symbol.getClassification().equals("Identificador")) {
            lista_identificadores();
            getNextSym();
            if (current_symbol.getValue().equals(":")) {
                getNextSym();
                tipo();
                getNextSym();
                if (current_symbol.getValue().equals(";")) {
                    getNextSym();
                    lista_declaracoes_variaveis2();
                } else {
                    throw new MismatchSymbolException("Esperando ; na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                throw new MismatchSymbolException("Esperando : na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            returnPrevSym();
        }
    }

    private ArrayList<String> lista_identificadores() throws MismatchSymbolException, DuplicateIdentifierException {
        ArrayList<String> ins = new ArrayList<>();
        if (current_symbol.getClassification().equals("Identificador")) {
            st.addSymbol(current_symbol);
            tcp.addToBuffer(current_symbol);
            ins.add(current_symbol.getValue());
            getNextSym();
            ins.addAll(lista_identificadores2());
            return ins;
        } else {
            throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private ArrayList<String> lista_identificadores2() throws MismatchSymbolException, DuplicateIdentifierException {
        ArrayList<String> ins = new ArrayList<>();
        if (current_symbol.getValue().equals(",")) {
            getNextSym();   
            if (current_symbol.getClassification().equals("Identificador")) {
                st.addSymbol(current_symbol);
                tcp.addToBuffer(current_symbol);
                ins.add(current_symbol.getValue());
                getNextSym();
                ins.addAll(lista_identificadores2());
            } else {
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            returnPrevSym();
        }
         return ins;
    }

    private String tipo() throws MismatchSymbolException {
        if (!(current_symbol.getValue().equals("integer") //se o símbolo atual não for "integer,real ou boolean", então:
                || current_symbol.getValue().equals("real")
                || current_symbol.getValue().equals("boolean"))) {
            throw new MismatchSymbolException("Esperando Tipo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        } else {
            tcp.defineBufferType(current_symbol.getValue());
        }
        return current_symbol.getValue();
    }

    private void declaracoes_subprogramas() throws MismatchSymbolException, DuplicateIdentifierException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("procedure") || current_symbol.getValue().equals("function")) {
            declaracao_subprograma();
            getNextSym();
            if (current_symbol.getValue().equals(";")) {
                getNextSym();
                declaracoes_subprogramas();
            } else {
                returnPrevSym();
            }
        } else {
            returnPrevSym();
        }
    }

    private void declaracao_subprograma() throws MismatchSymbolException, DuplicateIdentifierException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("procedure")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificador")) {
                st.addSymbol(current_symbol);
                st.addMark();
                tcp.openScope();
                String aux = current_symbol.getValue();
                getNextSym();
                ArrayList<String> args = argumentos();
                tcp.addFunc(aux, args);
                getNextSym();
                if (current_symbol.getValue().equals(";")) {
                    getNextSym();
                    declaracoes_variaveis();
                    getNextSym();
                    declaracoes_subprogramas();
                    getNextSym();
                    comando_composto();
                    st.removeLastScope();
                    tcp.removeScope();
                    tcp.addFunc(aux, args);
                } else {
                    throw new MismatchSymbolException("Esperando ; na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else if (current_symbol.getValue().equals("function")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificador")) {
                st.addSymbol(current_symbol);
                st.addMark();
                Token aux = current_symbol;
                tcp.openScope();
                getNextSym();
                ArrayList<String> args = argumentos();
                getNextSym();
                if (current_symbol.getValue().equals(":")) {
                    getNextSym();
                    String aux2 = tipo();
                    tcp.addToBuffer(aux);    
                    tcp.defineBufferType(aux2);
                    tcp.addFunc(aux.getValue(),args);
                    getNextSym();
                    if (current_symbol.getValue().equals(";")) {
                        getNextSym();
                        declaracoes_variaveis();
                        getNextSym();
                        declaracoes_subprogramas();
                        getNextSym();
                        comando_composto();
                        st.removeLastScope();
                        tcp.removeScope();
                        tcp.addToBuffer(aux);
                        tcp.defineBufferType(aux2);
                        tcp.addFunc(aux.getValue(),args);
                    } else {
                        throw new MismatchSymbolException("Esperando ; na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                    }
                } else {
                    throw new MismatchSymbolException("Esperando : na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando procedure na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private ArrayList<String> argumentos() throws MismatchSymbolException, DuplicateIdentifierException {
        if (current_symbol.getValue().equals("(")) {
            getNextSym();
            ArrayList<String> args = lista_parametros();
            getNextSym();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando \")\"  na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
            return args;
        } else {
            returnPrevSym();
        }
        return null;
    }

    private ArrayList<String> lista_parametros() throws MismatchSymbolException, DuplicateIdentifierException {
        ArrayList<String> ids = lista_identificadores();
        getNextSym();
        if (current_symbol.getValue().equals(":")) {
            getNextSym();
            ids.add(tipo());
            getNextSym();
            ids.addAll(lista_parametros2());
            return ids;
        } else {
            throw new MismatchSymbolException("Esperando : na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
        
    }

    private  ArrayList<String> lista_parametros2() throws MismatchSymbolException, DuplicateIdentifierException {
        if (current_symbol.getValue().equals(";")) {
            getNextSym();
            return lista_parametros();
        } else {
            returnPrevSym();
        }
        return new ArrayList<>();
    }

    private void comando_composto() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("begin")) {
            getNextSym();
            comandos_opcionais();
            getNextSym();
            if (!current_symbol.getValue().equals("end")) { // se o comando não for o end
                throw new MismatchSymbolException("Esperando end na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando begin na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void comandos_opcionais() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        try {
            lista_comandos();
        } catch (EmptyOptionException ex) {
            returnPrevSym();
        }
    }

    private void lista_comandos() throws MismatchSymbolException, EmptyOptionException, UnidentifiedSymbolException, InvalidOperandsException {
        comando();
        getNextSym();
        lista_comandos2();
    }

    private void lista_comandos2() throws MismatchSymbolException, EmptyOptionException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals(";")) {
            getNextSym();
            lista_comandos();
        } else {
            returnPrevSym();
        }
    }

    private void comando() throws MismatchSymbolException, EmptyOptionException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getClassification().equals("Identificador")) {
            ativacao_procedimento();
        } else if (current_symbol.getValue().equals("begin")) {
            comando_composto();
        } else if (current_symbol.getValue().equals("if")) {
            getNextSym();
            expressao();
            getNextSym();
            if (current_symbol.getValue().equals("then")) {
                getNextSym();
                comando();
                getNextSym();
                parte_else();
            } else {
                throw new MismatchSymbolException("Esperando palavra then na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else if (current_symbol.getValue().equals("while")) {
            getNextSym();
            expressao();
            getNextSym();
            if (current_symbol.getValue().equals("do")) {
                getNextSym();
                comando();
            } else {
                throw new MismatchSymbolException("Esperando 'do' na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new EmptyOptionException();
        }
    }

    private void parte_else() throws MismatchSymbolException, EmptyOptionException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("else")) {
            getNextSym();
            comando();
        } else {
            returnPrevSym();
        }
    }

    private void variavel() throws MismatchSymbolException {
        if (!current_symbol.getClassification().equals("Identificador")) {
            throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void ativacao_procedimento() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getClassification().equals("Identificador")) {
            st.containsSymbol(current_symbol);
            Token aux = current_symbol;
            getNextSym();
            ativacao_procedimento_apx(aux);
        } else {
            throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());

        }
    }

    private void ativacao_procedimento_apx(Token aux) throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals("(")) {
            getNextSym();
            lista_expressoes();
            tcp.countArgs(aux.getValue(), current_symbol.getLine());
            getNextSym();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando ) na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());

            }
        } else if (current_symbol.getValue().equals(":=")) {
            tcp.pushId(aux);
            getNextSym();
            expressao();
            tcp.executeOp("Atribuicao", current_symbol.getLine());
        } else {
            returnPrevSym();
        }
    }

    private void lista_expressoes() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        expressao();
        getNextSym();
        lista_expressoes2();
    }

    private void lista_expressoes2() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getValue().equals(",")) {
            getNextSym();
            lista_expressoes();
        } else {
            returnPrevSym();
        }
    }

    private void expressao() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        expressao_simples();
        getNextSym();
        expressao_apx();
    }

    private void expressao_apx() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (op_relacional()) {
            getNextSym();
            expressao_simples();
            tcp.executeOp("Operador Relacional", current_symbol.getLine());
        } else {
            returnPrevSym();
        }
    }

    private void expressao_simples() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (sinal()) {
            getNextSym();
        }
        termo();
        getNextSym();
        expressao_simples2();
    }

    private void expressao_simples2() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (op_aditivo()) {
            getNextSym();
            termo();
            tcp.executeOp("Operador Aditivo", current_symbol.getLine());
            getNextSym();
            expressao_simples2();
        } else {
            returnPrevSym();
        }
    }

    private void termo() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        fator();
        getNextSym();
        termo2();
    }

    private void termo2() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (op_multiplicativo()) {
            getNextSym();
            fator();
            tcp.executeOp("Operador Multiplicativo", current_symbol.getLine());
            getNextSym();
            termo2();
        } else {
            returnPrevSym();
        }
    }

    private void fator() throws MismatchSymbolException, UnidentifiedSymbolException, InvalidOperandsException {
        if (current_symbol.getClassification().equals("Identificador")) {
            st.containsSymbol(current_symbol);
            Token aux = current_symbol;
            tcp.pushId(current_symbol);
            getNextSym();
            if (current_symbol.getValue().equals("(")) {
                getNextSym();
                lista_expressoes();
                tcp.countArgs(aux.getValue(), current_symbol.getLine());
                getNextSym();
                if (!current_symbol.getValue().equals(")")) {
                    throw new MismatchSymbolException("Esperando ) na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                returnPrevSym();
                //tcp.pushId(aux);
            }
        } else if (current_symbol.getClassification().equals("integer")) {
            tcp.pushType("integer");

        } else if (current_symbol.getClassification().equals("real")) {
            tcp.pushType("real");

        } else if (current_symbol.getValue().equals("true") || (current_symbol.getValue().equals("false"))) {
            tcp.pushType("boolean");

        } else if (current_symbol.getValue().equals("(")) {
            getNextSym();
            expressao();
            getNextSym();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando ) na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else if (current_symbol.getValue().equals("not")) {
            getNextSym();
            fator();
        } else {
            throw new MismatchSymbolException("Esperando FATOR na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private boolean sinal() throws MismatchSymbolException {
        return current_symbol.getValue().equals("+") || current_symbol.getValue().equals("-");
    }

    private boolean op_relacional() throws MismatchSymbolException {
        return current_symbol.getClassification().equals("Operador Relacional");
    }

    private boolean op_aditivo() throws MismatchSymbolException {
        return current_symbol.getClassification().equals("Operador Aditivo");
    }

    private boolean op_multiplicativo() {
        return current_symbol.getClassification().equals("Operador Multiplicativo");
    }
	
	*/

}
