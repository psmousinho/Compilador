package syntactic;

import common.Token;
import java.util.LinkedList;
import syntactic.exceptions.MismatchSymbolException;

public class Analyzer {

    private LinkedList<Token> tokens;
    private Token current_symbol;

    public Analyzer(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    private void getNextSym() {
        this.current_symbol = tokens.removeFirst();
    }

    private void returnPrevSym() {
        tokens.addFirst(current_symbol);
    }

    public void analyze() throws MismatchSymbolException {
        getNextSym();
        programa();
    }

    private void programa() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("program")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificador")) {
                getNextSym();
                if (current_symbol.getValue().equals(";")) {
                    getNextSym();
                    declaracoes_variaveis();
                    getNextSym();
                    declaracoes_subprogramas();
                    getNextSym();
                    comando_composto();
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

    private void declaracoes_variaveis() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("var")) {
            getNextSym();
            lista_declaracoes_variaveis();
        } else {
            returnPrevSym();
        }
    }

    private void lista_declaracoes_variaveis() throws MismatchSymbolException {
        lista_identificadores();
        getNextSym();
        if (current_symbol.getValue().equals(":")) {
            getNextSym();
            tipo();
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

    private void lista_declaracoes_variaveis2() throws MismatchSymbolException {
        try {
            lista_identificadores();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
            return;
        }
        if (current_symbol.getValue().equals(":")) {
            getNextSym();
            tipo();
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

    private void lista_identificadores() throws MismatchSymbolException {
        if (current_symbol.getClassification().equals("Identificador")) {
            getNextSym();
            lista_identificadores2();
        } else {
            throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void lista_identificadores2() throws MismatchSymbolException {
        if (current_symbol.getValue().equals(",")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificador")) {
                getNextSym();
                lista_identificadores2();
            } else {
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            returnPrevSym();
        }
    }

    private void tipo() throws MismatchSymbolException {
        if (!(current_symbol.getValue().equals("integer")
                || current_symbol.getValue().equals("real")
                || current_symbol.getValue().equals("boolean"))) {
            throw new MismatchSymbolException("Esperando Tipo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void declaracoes_subprogramas() throws MismatchSymbolException {
        try {
            declaracao_subprograma();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
        }
    }

    private void declaracao_subprograma() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("procedure")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificacao")) {
                getNextSym();
                argumentos();
                getNextSym();
                declaracoes_variaveis();
                getNextSym();
                declaracoes_subprogramas();
                getNextSym();
                comando_composto();
            } else {
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando palavra reservada Procedure na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void argumentos() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("(")) {
            getNextSym();
            lista_parametros();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando \")\"  na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            returnPrevSym();
        }
    }

    private void lista_parametros() throws MismatchSymbolException {
        lista_identificadores();
        if (current_symbol.getValue().equals(":")) {
            getNextSym();
            tipo();
            getNextSym();
            lista_parametros2();
        } else {
            throw new MismatchSymbolException("Esperando : na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void lista_parametros2() throws MismatchSymbolException {
        if (current_symbol.getValue().equals(";")) {
            lista_parametros();
        } else {
            returnPrevSym();
        }
    }

    private void comando_composto() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("begin")) {
            getNextSym();
            comandos_opcionais();
            getNextSym();
            if (!current_symbol.getValue().equals("end")) {
                throw new MismatchSymbolException("Esperando end na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando begin na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void comandos_opcionais() throws MismatchSymbolException {
        try {
            lista_comandos();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
        }
    }

    private void lista_comandos() throws MismatchSymbolException {
        comando();
        getNextSym();
        lista_comandos2();
    }

    private void lista_comandos2() throws MismatchSymbolException {
        if (current_symbol.getValue().equals(";")) {
            lista_comandos();
        } else {
            returnPrevSym();
        }
    }

    /* private void comando() throws MismatchSymbolException {
        if(current_symbol.getValue().equals("while")) {
            
        } else if(current_symbol.getValue().equals("if")) {
            
        } else if(current_symbol.getClassification().equals("Identificador")) {
            getNextSym();
            if()
        }
        else {
            comando_composto();
        }
    }*/
    
    private void parte_else() throws MismatchSymbolException {
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

    private void ativacao_procedimento() throws MismatchSymbolException {
        if (current_symbol.getClassification().equals("Identificador")) {
            getNextSym();
            ativacao_procedimento_apx();
        } else {
            throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());

        }
    }

    private void ativacao_procedimento_apx() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("(")) {
            getNextSym();
            lista_expressoes();
            getNextSym();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando ) na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());

            }
        } else {
            returnPrevSym();
        }
    }

    private void lista_expressoes() throws MismatchSymbolException {
        expressao();
        getNextSym();
        lista_expressoes2();
    }

    private void lista_expressoes2() throws MismatchSymbolException {
        if (current_symbol.getValue().equals(",")) {
            getNextSym();
            lista_expressoes();
        } else {
            returnPrevSym();
        }
    }

    private void expressao() throws MismatchSymbolException {
        expressao_simples();
        getNextSym();
        expressao_apx();
    }

    private void expressao_apx() throws MismatchSymbolException {
        try {
            op_relacional();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
            return;
        }

        expressao_simples();
    }

    private void expressao_simples() throws MismatchSymbolException {
        if (sinal()) {
            getNextSym();
        }
        termo();
        getNextSym();
        expressao_simples2();
    }

    private void expressao_simples2() throws MismatchSymbolException {
        try {
            op_aditivo();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
            return;
        }
        termo();
        getNextSym();
        expressao_simples2();
    }

    private void termo() throws MismatchSymbolException {
        fator();
        getNextSym();
        termo2();
    }

    private void termo2() throws MismatchSymbolException {
        try {
            op_multiplicativo();
        } catch (MismatchSymbolException ex) {
            returnPrevSym();
            return;
        }
        fator();
        getNextSym();
        termo2();
    }

    private void fator() throws MismatchSymbolException {
        if (current_symbol.getClassification().equals("Identificador")) {
            getNextSym();
            if (current_symbol.getValue().equals("(")) {
                lista_expressoes();
                if (!current_symbol.getValue().equals(")")) {
                    throw new MismatchSymbolException("Esperando ) na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
                }
            } else {
                returnPrevSym();
            }
        } else if (current_symbol.getClassification().equals("Numero Inteiro")
                || current_symbol.getClassification().equals("Numero Real")
                || current_symbol.getValue().equals("true")
                || current_symbol.getValue().equals("false")) {

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

    private void op_relacional() throws MismatchSymbolException {
        if (!current_symbol.getClassification().equals("Operador Relacional")) {
            throw new MismatchSymbolException("Esperando Operador Relacional na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void op_aditivo() throws MismatchSymbolException {
        if (!current_symbol.getClassification().equals("Operador Aditivo")) {
            throw new MismatchSymbolException("Esperando Operador Aditivo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void op_multiplicativo() throws MismatchSymbolException {
        if (!current_symbol.getClassification().equals("Operador Multiplicativo")) {
            throw new MismatchSymbolException("Esperando Operador Multiplicativo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

}
