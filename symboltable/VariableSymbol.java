package symboltable;

public class VariableSymbol {
    
    public String varName;        // the name of the variable
    public int    varType;        // the type of the variable

    @Override
    public String toString() {
        return this.varName;
    }
}
