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

    public void addParameter(String varName, int varType, String className) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        v.className = className;
        parameters.add(v);
    }

    public int typeOf(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) return v.varType;
        }
        for(VariableSymbol v : parameters) {
            if(v.varName == varName) return v.varType;
        }
        return -1;//cannot figure out type
    }

    public String getClassName(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) {
                //System.out.println("CUR:"+v.className+v.varType);
                return v.className;
            }
        }
        for(VariableSymbol v : parameters) {
            if(v.varName == varName) {
                //System.out.println("CUR:"+v.className+v.varType);
                return v.className;
            }
        }
        return "";//cannot figure out type
    }

    public VariableSymbol addLocalVariable(String varName, int varType, String className) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        v.className = className;
        variableSymbols.add(v);
        return v;
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

    public interface lambda2 {
        public boolean func(VariableSymbol v);
    }

    public VariableSymbol findVar(lambda2 l) {
        for(VariableSymbol v : this.parameters) {
            if(l.func(v))
                return v;
        }
        for(VariableSymbol v : this.variableSymbols) {
            if(l.func(v))
                return v;
        }
        return null;
    }



}

