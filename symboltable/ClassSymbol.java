package symboltable;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class ClassSymbol {

    private static final int BOOLEAN_TYPE = 1;
    private static final int INTEGER_TYPE = 2;
    private static final int CLASS_TYPE   = 3;


    public String className;        // the name of the class
    public String extendsClassName; // the class that the current class inherits from
    public ArrayList<VariableSymbol> variableSymbols = new ArrayList<VariableSymbol>();
    public ArrayList<MethodSymbol>   methodSymbols   = new ArrayList<MethodSymbol>();

    public static void printSymbolTable(ArrayList<ClassSymbol> symbolTable) {
        StringBuilder sb = new StringBuilder(512);
        for(ClassSymbol c : symbolTable) {
            sb.append(String.format("class %s : %s {\n", c.className, c.extendsClassName));
            for(VariableSymbol v : c.variableSymbols) {
                sb.append(String.format("    %s %s\n", ClassSymbol.type(v.varType), v.varName));
            }
            for(MethodSymbol m : c.methodSymbols) {
                sb.append(String.format("    func %s -> %s {\n", m.methodName, ClassSymbol.type(m.retType)));

                for(VariableSymbol param : m.parameters) {
                    sb.append(String.format("        %s %s [parameter]\n", ClassSymbol.type(param.varType), param.varName));
                }


                for(VariableSymbol mVar : m.variableSymbols) {
                    sb.append(String.format("        %s %s\n", ClassSymbol.type(mVar.varType), mVar.varName));
                }
                sb.append("    }\n");
            }
            sb.append("};\n");
        }
        System.out.println(sb.toString());
    }

    static String type(int a) {
       switch(a) {
       case BOOLEAN_TYPE: return "boolean";
       case INTEGER_TYPE: return "integer";
       case CLASS_TYPE:   return "other";
       default: return "unknown";
       }
    }

    @Override
    public String toString() {
        return this.className;
    }

    public VariableSymbol addClassVariable(String varName, int varType) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        variableSymbols.add(v);
        return v;
    }

    public MethodSymbol addClassMethod(String methodName, int retType) {
        MethodSymbol m = new MethodSymbol();
        m.methodName = methodName;
        m.retType = retType;
        methodSymbols.add(m);
        return m;
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
