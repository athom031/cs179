package symboltable;

import java.util.ArrayList;
import java.lang.StringBuilder;

class ClassSymbol {

    public String className;        // the name of the class
    public String extendsClassName; // the class that the current class inherits from
    public ArrayList<VariableSymbol> variableSymbols = new ArrayList<VariableSymbol>();
    public ArrayList<MethodSymbol>   methodSymbols   = new ArrayList<MethodSymbol>();

    public static void printSymbolTable(ArrayList<ClassSymbol> symbolTable) {
        StringBuilder sb = new StringBuilder(512);
        for(ClassSymbol c : symbolTable) {
            sb.append(String.format("class %s {\n", c.className));
            for(VariableSymbol v : c.variableSymbols) {
                sb.append(String.format("    var %s\n", v.varName));
            }
            for(MethodSymbol m : c.methodSymbols) {
                sb.append(String.format("    func %s {\n", m.methodName));
                for(VariableSymbol mVar : m.variableSymbols) {
                    sb.append(String.format("        func_var %s\n", mVar.varName));
                }
                sb.append("    }\n");
            }
            sb.append('}');
        }
        System.out.println(sb.toString());
    }


    @Override
    public String toString() {
        return this.className;
    }

    public void addClassVariable(String varName, int varType) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        variableSymbols.add(v);
    }

    public void addClassMethod(String methodName, int retType) {
        MethodSymbol m = new MethodSymbol();
        m.methodName = methodName;
        m.retType = retType;
        methodSymbols.add(m);
    }

    public boolean hasVariable(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) return true;
        }
        return false;
    }

    public boolean hasMethod(String methodName) {
        for(MethodSymbol m : methodSymbols) {
            if(m.methodName == methodName) return true;
        }
        return false;
    }

    public boolean hasVariable(String varName, int type) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName && v.varType == type) return true;
        }
        return false;
    }

    public boolean hasMethod(String methodName, int type) {
        for(MethodSymbol m : methodSymbols) {
            if(m.methodName == methodName && m.retType == type) return true;
        }
        return false;
    }

}

class VariableSymbol {
    
    public String varName;        // the name of the variable
    public int    varType;        // the type of the variable

    @Override
    public String toString() {
        return this.varName;
    }
}

class MethodSymbol {

    public String methodName;    // the method name
    public int    retType;       // the return value of the function
    public ArrayList<VariableSymbol> variableSymbols = new ArrayList<VariableSymbol>();

    @Override
    public String toString() {
        return this.methodName;
    }

    public void addLocalVariable(String varName, int varType) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        variableSymbols.add(v);
    }

    public boolean hasVariable(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) return true;
        }
        return false;
    }

    public boolean hasVariable(String varName, int type) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName && v.varType == type) return true;
        }
        return false;
    }

}

