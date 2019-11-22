package syntactic;

import common.Token;
import java.util.LinkedList;
import syntactic.exceptions.EmptyOptionException;
import syntactic.exceptions.MismatchSymbolException;

public class Analyzer {

    private final LinkedList<Token> tokens;
    private Token current_symbol;

    /**
     * Construtor que recebe a lista de tokens separadas pelo léxico
     *
     * @param tokens
     */
    public Analyzer(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Método que relizada a remoção(e retorno) do primeiro tokens da lista.
     */
    private void getNextSym() {
        if(!tokens.isEmpty()) {
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

    public void analyze() throws MismatchSymbolException {
        getNextSym();
        programa();
    }

    /**
     * Método que, de fato, faz a implentação da análise sintática
     *
     * @throws MismatchSymbolException
     */
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
                    getNextSym();
                    if (current_symbol.getValue().equals(".")) {
                        System.out.println("******Sintatic done******");
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

    private void lista_declaracoes_variaveis2() throws MismatchSymbolException {
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
        if (!(current_symbol.getValue().equals("integer") //se o símbolo atual não for "integer,real ou boolean", então:
                || current_symbol.getValue().equals("real")
                || current_symbol.getValue().equals("boolean"))) {
            throw new MismatchSymbolException("Esperando Tipo na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void declaracoes_subprogramas() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("procedure")) {
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

    private void declaracao_subprograma() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("procedure")) {
            getNextSym();
            if (current_symbol.getClassification().equals("Identificador")) {
                getNextSym();
                argumentos();
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
                throw new MismatchSymbolException("Esperando Identificador na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando procedure na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    private void argumentos() throws MismatchSymbolException {
        if (current_symbol.getValue().equals("(")) {
            getNextSym();
            lista_parametros();
            getNextSym();
            if (!current_symbol.getValue().equals(")")) {
                throw new MismatchSymbolException("Esperando \")\"  na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            returnPrevSym();
        }
    }

    private void lista_parametros() throws MismatchSymbolException {
        lista_identificadores();
        getNextSym();
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
            getNextSym();
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
            if (!current_symbol.getValue().equals("end")) { // se o comando não for o end
                throw new MismatchSymbolException("Esperando end na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
            }
        } else {
            throw new MismatchSymbolException("Esperando begin na linha" + current_symbol.getLine() + " antes de " + current_symbol.getValue());
        }
    }

    //concertar
    private void comandos_opcionais() throws MismatchSymbolException {
        try {
            lista_comandos();
        } catch (EmptyOptionException ex) {
            returnPrevSym();
        }
    }

    private void lista_comandos() throws MismatchSymbolException, EmptyOptionException {
        comando();
        getNextSym();
        lista_comandos2();
    }

    private void lista_comandos2() throws MismatchSymbolException, EmptyOptionException {
        if (current_symbol.getValue().equals(";")) {
            getNextSym();
            lista_comandos();
        } else {
            returnPrevSym();
        }
    }

    private void comando() throws MismatchSymbolException, EmptyOptionException {
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

    private void parte_else() throws MismatchSymbolException, EmptyOptionException {
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
        } else if (current_symbol.getValue().equals(":=")) {
            getNextSym();
            expressao();
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
        if (op_relacional()) {
            getNextSym();
            expressao_simples();
        } else {
            returnPrevSym();
        }
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
        if (op_aditivo()) {
            getNextSym();
            termo();
            getNextSym();
            expressao_simples2();
        } else {
            returnPrevSym();
        }
    }

    private void termo() throws MismatchSymbolException {
        fator();
        getNextSym();
        termo2();
    }

    private void termo2() throws MismatchSymbolException {
        if (op_multiplicativo()) {
            getNextSym();
            fator();
            getNextSym();
            termo2();
        } else {
            returnPrevSym();
        }
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

    private boolean op_relacional() throws MismatchSymbolException {
        return current_symbol.getClassification().equals("Operador Relacional");
    }

    private boolean op_aditivo() throws MismatchSymbolException {
        return current_symbol.getClassification().equals("Operador Aditivo");
    }

    private boolean op_multiplicativo() {
        return current_symbol.getClassification().equals("Operador Multiplicativo");
    }

}
