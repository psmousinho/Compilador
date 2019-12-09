package Semantic;

import Semantic.exceptions.InvalidOperandsException;
import common.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TypeCheckingTable {

    private final LinkedList<String> tpt;
    private final ArrayList<HashMap<String, String>> idType;
    private final ArrayList<HashMap<String, ArrayList<String>>> funcArgs;
    private final LinkedList<String> buffer;

    public TypeCheckingTable() {
        tpt = new LinkedList<>();
        idType = new ArrayList<>();
        buffer = new LinkedList<>();
        funcArgs = new ArrayList<>();
    }

    public void addToBuffer(Token t) {
        buffer.add(t.getValue());
    }

    public void defineBufferType(String type) {
        int scope = idType.size() - 1;
        for (String t : buffer) {
            idType.get(scope).put(t, type);
        }
        buffer.clear();
    }

    public void pushType(String type) {
        tpt.push(type);
    }

    public void pushId(Token id) {
        for (int i = idType.size() - 1; i >= 0; i--) {
            if (idType.get(i).keySet().contains(id.getValue())) {
                tpt.push(idType.get(i).get(id.getValue()));
                break;
            }
        }
    }

    public void openScope() {
        idType.add(new HashMap<>());
        funcArgs.add(new HashMap<>());
    }

    public void removeScope() {
        idType.remove(idType.size() - 1);
        funcArgs.remove(funcArgs.size() - 1);
    }

    public void executeOp(String op, int line) throws InvalidOperandsException {
        String t2 = tpt.pop();
        String t1 = tpt.pop();
        switch (op) {
            case "Operador Aditivo":
            case "Operador Multiplicativo":
                if (t1.equals("integer") && t2.equals("integer")) {
                    tpt.push("integer");
                } else if (t1.equals("real") && t2.equals("integer")) {
                    tpt.push("real");
                } else if (t1.equals("integer") && t2.equals("real")) {
                    tpt.push("real");
                } else if (t1.equals("real") && t2.equals("real")) {
                    tpt.push("real");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op + " : " + t1 + " " + t2 + " na linha " + line);
                }

                break;
            case "Operador Relacional":

                if ((t1.equals("integer") || t1.equals("real")) && (t2.equals("integer") || t2.equals("real"))) {
                    tpt.push("boolean");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op + " : " + t1 + " " + t2 + " na linha " + line);
                }
                break;
            case "Operador Logico":
                if (t1.equals(t2) && t1.equals("Boolean")) {
                    tpt.push("boolean");
                } else {
                    throw new InvalidOperandsException("Tipos invalidos para " + op + " : " + t1 + " " + t2 + " na linha " + line);
                }
                break;
            case "Atribuicao":
                if (!t1.equals(t2)) {
                    if (!(t1.equals("real") && t2.equals("integer"))) {
                        throw new InvalidOperandsException("Tipos invalidos para " + op + " : " + t1 + " " + t2 + " na linha " + line);
                    }
                }
                break;
        }
    }

    public void addFunc(String func, ArrayList args) {
        funcArgs.get(funcArgs.size() - 1).put(func, args);
    }

    public void countArgs(String func, int line) throws InvalidOperandsException {
        ArrayList<String> args = null;
        for (int i = funcArgs.size() - 1; i >= 0; i--) {
            if (funcArgs.get(i).keySet().contains(func)) {
                args = funcArgs.get(i).get(func);
                break;
            }
        }

        if (args == null) {
            return;
        }

        int count = 0;
        String tipo = "";
        for (int i = args.size() - 1; i >= -1; i--) {
            if (i == -1 || args.get(i).equals("integer") || args.get(i).equals("real") || args.get(i).equals("boolean")) {
                for (int j = 0; j < count; j++) {
                    String topTipo = tpt.pop();
                    if (!topTipo.equals(tipo)) {
                        throw new InvalidOperandsException("Operadandos errados para chamada de funca: Esperado " + tipo + " recebeu " + topTipo);
                    }
                }
                count = 0;
                tipo = (i == -1)? "":args.get(i);
            } else {
                count++;
            }
        }
    }

}
