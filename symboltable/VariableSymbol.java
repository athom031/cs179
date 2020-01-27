package symboltable;

public class VariableSymbol {
    
    public String varName;        // the name of the variable
    public int    varType;        // the type of the variable
    public String className;      // class type of the variable, e.g. "Object"

    @Override
    public String toString() {
        return this.varName;
    }
}
