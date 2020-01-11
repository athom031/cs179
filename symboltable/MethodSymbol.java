package symboltable;
import java.util.ArrayList;

public class MethodSymbol {

    public String methodName;    // the method name
    public int    retType;       // the return value of the function
    public ArrayList<VariableSymbol> parameters      = new ArrayList<VariableSymbol>();
    public ArrayList<VariableSymbol> variableSymbols = new ArrayList<VariableSymbol>();

    @Override
    public String toString() {
        return this.methodName;
    }

    public void addParameter(String varName, int varType) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        parameters.add(v);
    }

    public void addLocalVariable(String varName, int varType) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        variableSymbols.add(v);
    }

    public boolean hasVariable(String varName) {
        for(VariableSymbol v : parameters) {
            if(v.varName == varName) return true;
        }

        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) return true;
        }
        return false;
    }

    public boolean hasVariable(String varName, int type) {
        for(VariableSymbol v : parameters) {
            if(v.varName == varName && v.varType == type) return true;
        }

        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName && v.varType == type) return true;
        }
        return false;
    }

}

