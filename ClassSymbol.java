import java.util.ArrayList;
import java.lang.StringBuilder;

public class ClassSymbol {

    private static final int BOOLEAN_TYPE = 1;
    private static final int INTEGER_TYPE = 2;
    private static final int CLASS_TYPE   = 3;
    private static final int VOID_TYPE    = 4;


    public String className;        // the name of the class
    public String extendsClassName; // the class that the current class inherits from
    public ArrayList<VariableSymbol> variableSymbols = new ArrayList<VariableSymbol>();
    public ArrayList<MethodSymbol>   methodSymbols   = new ArrayList<MethodSymbol>();

    public static boolean isGlobal(ArrayList<ClassSymbol> symbolTable, String token) {
 		for(ClassSymbol c : symbolTable) {
			if(c.className == token) return true;
			for(MethodSymbol m : c.methodSymbols) {
				if(m.methodName == token) return true;
			}
		}
		  return false;
	  }

    public static void printSymbolTable(ArrayList<ClassSymbol> symbolTable) {
        StringBuilder sb = new StringBuilder(512);
        for(ClassSymbol c : symbolTable) {
            sb.append(String.format("class %s : %s {\n", c.className, c.extendsClassName));
            for(VariableSymbol v : c.variableSymbols) {
                sb.append(String.format("    %s %s\n", ClassSymbol.type(v), v.varName));
            }
            for(MethodSymbol m : c.methodSymbols) {
                sb.append(String.format("    func %s -> %s {\n", m.methodName, ClassSymbol.type(m.retType, m.retName)));

                for(VariableSymbol param : m.parameters) {
                    sb.append(String.format("        %s %s [parameter]\n", ClassSymbol.type(param), param.varName));
                }


                for(VariableSymbol mVar : m.variableSymbols) {
                    sb.append(String.format("        %s %s\n", ClassSymbol.type(mVar), mVar.varName));
                }
                sb.append("    }\n");
            }
            sb.append("};\n");
        }
        System.out.println(sb.toString());
    }

    public void addExtendClass(String extendsClassName, ArrayList<ClassSymbol> symbolTable) {
        this.extendsClassName = extendsClassName;
        ClassSymbol ccc = new ClassSymbol();//dummy
        for(ClassSymbol c : symbolTable) {
            if(c.className == extendsClassName) {
                ccc = c;
                break;
            }
        }
        for(VariableSymbol v : ccc.variableSymbols) {
            this.variableSymbols.add(v);
        }
        for(MethodSymbol m : ccc.methodSymbols) {
            this.methodSymbols.add(m);
        }
        
    }

    public MethodSymbol findMethod(String methodName) {
      for(MethodSymbol m : methodSymbols) {
        if(m.methodName==methodName) return m;
      }
      return null;
    }

    public static int findType(String name, ArrayList<ClassSymbol> symbolTable, int classIndex, int method) {
        ClassSymbol c = symbolTable.get(classIndex);
        MethodSymbol m = c.methodSymbols.get(method);


        return 0;
    }

    public static MethodSymbol findMethod(ArrayList<ClassSymbol> symbolTable, String className, String methodName) {
        for(ClassSymbol c : symbolTable) {
            if(c.className == className) {
                for(MethodSymbol m : c.methodSymbols) {
                    if(m.methodName == methodName) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    public int findMethodID(String methodName) {
      for(int i = 0; i < methodSymbols.size(); i++) {
        MethodSymbol m = methodSymbols.get(i);
        if(m.methodName == methodName) return i;
      }
      return -1;
    }

    public int typeOf(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) return v.varType;
        }
        return -1;//cannot figure out type
    }



    public String getClassName(String varName) {
        for(VariableSymbol v : variableSymbols) {
            if(v.varName == varName) {
                return v.className;
            }
        }
        return null;
    }

    static String type(VariableSymbol v) {
       int a = v.varType;
       switch(a) {
       case BOOLEAN_TYPE: return "boolean";
       case INTEGER_TYPE: return "integer";
       case CLASS_TYPE:   return v.className;
       case VOID_TYPE:    return "void";
       default: return "unknown";
       }
    }

    static String type(int a, String s) {
       switch(a) {
       case BOOLEAN_TYPE: return "boolean";
       case INTEGER_TYPE: return "integer";
       case CLASS_TYPE:   return s;
       case VOID_TYPE:    return "void";
       default: return "unknown";
       }
    }

    static String type(int a) {
       switch(a) {
       case BOOLEAN_TYPE: return "boolean";
       case INTEGER_TYPE: return "integer";
       case CLASS_TYPE:   return "other";
       case VOID_TYPE:    return "void";
       default: return "unknown";
       }
    }

    @Override
    public String toString() {
        return this.className;
    }

    public VariableSymbol addClassVariable(String varName, int varType, String className) {
        VariableSymbol v = new VariableSymbol();
        v.varName = varName;
        v.varType = varType;
        v.className = className;
        variableSymbols.add(v);
        return v;
    }

    public MethodSymbol addClassMethod(String methodName, int retType, String retName) {
        MethodSymbol m = new MethodSymbol();
        m.methodName = methodName;
        m.retType = retType;
        m.retName = retName;
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

    public interface lambda {
        public boolean func(ClassSymbol a);
    }

    public static ClassSymbol find(ArrayList<ClassSymbol> symbolTable, lambda l) {
        for(ClassSymbol c : symbolTable) {
            if(l.func(c))
                return c;
        }
        return null;
    }

    public interface lambda2 {
        public boolean func(VariableSymbol v);
    }

    public VariableSymbol findVar(lambda2 l) {
        for(VariableSymbol v : this.variableSymbols) {
            if(l.func(v))
                return v;
        }
        return null;
    }

    public int findVarID(String s) {
			int ret = 4;
			for(VariableSymbol v : this.variableSymbols) {
				if(v.varName == s) {
					return ret;
				}
				ret += 4;
			}
			return -1;//cannot find.
		}

}