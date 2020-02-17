//
// Generated by JTB 1.3.2
//

import visitor.*;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class VaporVisitor implements Visitor {

   /*
      NOTE: You are only allowed to use the following:
      Basic Arithmetic: Add, Sub, MulS
      Comparison: Eq, Lt, LtS
      Displaying Output: PrintIntS
      MemoryAllocation: HeapAllocZ
      Error: Error
   */

   private final static int ARRAY_TYPE   = 0;
   private final static int BOOLEAN_TYPE = 1;
   private final static int INTEGER_TYPE = 2;
   private final static int CLASS_TYPE   = 3;
   private final static int VOID_TYPE    = 4;

   final ArrayList<ClassSymbol> symbolTable; 
   int classIndex = 0;
   int functionIndex = 0;
   String variableName = null;
   int tempNumber = 0;
   int labelNo = 0;

   String temp() {
    String name = String.format("__t%d", tempNumber);
    tempNumber++;
    return name;
   }

   String label() {
    String l = String.format("__label%d", labelNo);
    labelNo++;
    return l;
   }

   public VaporVisitor(ArrayList<ClassSymbol> symbolTable) {
      this.symbolTable = symbolTable;
   }

   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public void visit(NodeList n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeListOptional n) {
      if ( n.present() )
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
            e.nextElement().accept(this);
   }

   public void visit(NodeOptional n) {
      if ( n.present() )
         n.node.accept(this);
   }

   public void visit(NodeSequence n) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
         e.nextElement().accept(this);
   }

   public void visit(NodeToken n) { }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public void visit(Goal n) {

      for(ClassSymbol c : symbolTable) {
        String className = c.className;
	      System.out.printf("const vmt_%s\n", className);
        for(MethodSymbol m : c.methodSymbols) {
          if(m.methodName == "main") continue;
          System.out.printf("  :%s.%s\n", className, m.methodName);
        }
        System.out.println();
      }

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   public void visit(MainClass n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      System.out.println("func Main()");
      n.f7.accept(this);
      n.f8.accept(this);
      n.f9.accept(this);
      n.f10.accept(this);
      n.f11.accept(this);
      n.f12.accept(this);
      n.f13.accept(this);
      n.f14.accept(this);
      n.f15.accept(this);
      n.f16.accept(this);
      System.out.println("  ret\n");
      n.f17.accept(this);
   }

   /** * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public void visit(TypeDeclaration n) {
      classIndex = classIndex + 1;
      functionIndex = 0;
      n.f0.accept(this);
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public void visit(ClassDeclaration n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public void visit(ClassExtendsDeclaration n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public void visit(VarDeclaration n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }


   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression() * f11 -> ";"
    * f12 -> "}"
    */
   public void visit(MethodDeclaration n) {

      // we entered a function
      
      ClassSymbol c = symbolTable.get(classIndex);
      MethodSymbol m = c.methodSymbols.get(functionIndex);
      String params = "";
      for(VariableSymbol v : m.parameters) {
        params += " " + v.varName;
      }

      System.out.printf("func %s.%s(this%s)\n", c.className, m.methodName, params);
      int yy = 4;
      for(VariableSymbol v : c.variableSymbols) {
        System.out.printf("  %s = [this+%d]\n", v.varName, yy);
        yy += 4;
      }


      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
      n.f8.accept(this);
      n.f9.accept(this);
      n.f10.accept(this);
      String retValue = variableName;

			int num = c.findVarID(retValue);
			if(num!=-1) {
				retValue=temp();
				System.out.printf("  %s = [this+%d]\n", retValue, num);
			}

      n.f11.accept(this);
      n.f12.accept(this);
      System.out.printf("  ret %s\n\n", retValue);
      functionIndex += 1;

      // we exited a function
   }

   /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
   public void visit(FormalParameterList n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public void visit(FormalParameter n) {
      // visited a function parameter
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public void visit(FormalParameterRest n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public void visit(Type n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public void visit(ArrayType n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "boolean"
    */
   public void visit(BooleanType n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "int"
    */
   public void visit(IntegerType n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public void visit(Statement n) {
      n.f0.accept(this);
      allocation = false;
      allocClass = null;
      messageSend = false;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public void visit(Block n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   String attemptConvertToObjectMember(String a, boolean thisExpr) {
      int num = 4;
      ClassSymbol c = symbolTable.get(classIndex);
      if(c.hasVariable(a)) {
        for(VariableSymbol v : c.variableSymbols) {
          if(v.varName != a)
            num += 4;
          else
            break;
        }
        System.out.printf("  %s = [this+%d]\n", a, num);
        //support object has ints x, y, z
        //given x, [this+4]
        if(!thisExpr) {
          String t = temp();
          System.out.printf("  %s = [this+%d]\n", t, num);
          return t;
        } else {
          return String.format("[this+%d]", num);
        }
      }
      return a;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */

   public void visit(AssignmentStatement n) {

      //TODO: this is to be handled in a much more
      // complex fashion, especially when it comes to
      // objects.
      n.f0.accept(this);
      String aaaa = variableName;
      String a = attemptConvertToObjectMember(variableName, true);


      n.f1.accept(this);
      n.f2.accept(this);
      String b = attemptConvertToObjectMember(variableName, false);

      n.f3.accept(this);
      System.out.printf("  %s = %s\n", a, b);

      {
        int num = 4;
        ClassSymbol c = symbolTable.get(classIndex);
        if(c.hasVariable(aaaa)) {
          for(VariableSymbol v : c.variableSymbols) {
            if(v.varName != aaaa)
              num += 4;
            else
              break;
          }
          System.out.printf("  %s = [this+%d]\n", aaaa, num);
        }
      }

      if(allocation) {
        ClassSymbol c = symbolTable.get(classIndex);
        int yy = 4;
        for(VariableSymbol v : c.variableSymbols) {
          if(v.varType == ARRAY_TYPE || v.varType == CLASS_TYPE) {
            System.out.printf("  %s = [this+%d]\n", v.varName, yy);
          }
          yy += 4;
        }
      } else if(messageSend) {
        ClassSymbol c = symbolTable.get(classIndex);
        int yy = 4;
        for(VariableSymbol v : c.variableSymbols) {
          System.out.printf("  %s = [this+%d]\n", v.varName, yy);
          yy += 4;
        }
      }
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public void visit(ArrayAssignmentStatement n) {
      String token = n.f0.f0.tokenImage;
      n.f0.accept(this);
      String a = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      String c = variableName;
      String d = temp();
      String e = temp(); 
      String lab = label();

      System.out.printf("  %s = [%s]\n", d, a);
      System.out.printf("  %s = LtS(%s %s)\n", d, b, d);
      System.out.printf("  if %s goto :%s\n", d, lab);
      System.out.printf("  Error(\"array index out of bounds\")\n");
      System.out.printf("  %s:\n", lab);
      System.out.printf("  %s = MulS(%s 4)\n", e, b);
      System.out.printf("  %s = Add(%s %s)\n", d, a, e);
      System.out.printf("  %s = Add(%s 4)\n", d, d);
      System.out.printf("  [%s] = %s\n", d,c);
      n.f6.accept(this);
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public void visit(IfStatement n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      String elseLabel = label();
      String endifLabel = label();
      String a = variableName;
      System.out.printf("  if0 %s goto :%s\n", a, elseLabel);

      n.f3.accept(this);
      n.f4.accept(this);


      System.out.printf("  goto :%s\n", endifLabel);

      System.out.printf("  %s:\n", elseLabel);

      n.f5.accept(this);
      n.f6.accept(this);

      System.out.printf("  %s:\n", endifLabel);

   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public void visit(WhileStatement n) {
      String beginLoopLabel = label();
      String endLoopLabel = label();

      System.out.printf("  %s:\n", beginLoopLabel);

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);

      String a = variableName;
      System.out.printf("  if0 %s goto :%s\n", a, endLoopLabel);

      n.f3.accept(this);
      n.f4.accept(this);

      System.out.printf("  goto :%s\n", beginLoopLabel);
      
      System.out.printf("  %s:\n", endLoopLabel);
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public void visit(PrintStatement n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      String e = variableName;
      System.out.printf("  PrintIntS(%s)\n", e);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   boolean messageSend = false;

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
   public void visit(Expression n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */

   public void visit(AndExpression n) {
      String andLabel = label();
      String andBool  = temp();

      System.out.printf("  %s = 0\n", andBool);

      n.f0.accept(this);
      String a = variableName;
      System.out.printf("  if0 %s goto :%s\n", a, andLabel);

      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;

      System.out.printf("  if0 %s goto :%s\n", b, andLabel);

      System.out.printf("  %s = 1\n", andBool);

      System.out.printf("  %s:\n", andLabel);

      variableName = andBool;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public void visit(CompareExpression n) {
      n.f0.accept(this);
      String a = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      String c = temp();
      System.out.printf("  %s = LtS(%s %s)\n", c, a, b);
      variableName = c;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public void visit(PlusExpression n) {
      n.f0.accept(this);
      String a = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      String c = temp();
      System.out.printf("  %s = Add(%s %s)\n", c, a, b);
      variableName = c;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public void visit(MinusExpression n) {
      n.f0.accept(this);
      String a = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      String c = temp();
      System.out.printf("  %s = Sub(%s %s)\n", c, a, b);
      variableName = c;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public void visit(TimesExpression n) {
      n.f0.accept(this);
      String a = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      String c = temp();
      System.out.printf("  %s = MulS(%s %s)\n", c, a, b);
      variableName = c;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public void visit(ArrayLookup n) {
      n.f0.accept(this);
      String a = variableName;
      // check that the variable is an array type
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      String t = temp();
      String lab = label();

      System.out.printf("  %s = [%s]\n", t, a);
      System.out.printf("  %s = LtS(%s %s)\n", t, b, t);
      System.out.printf("  if %s goto :%s\n", t, lab);
      System.out.printf("  Error(\"array index out of bounds\")\n");
      System.out.printf("  %s:\n", lab);

      System.out.printf("  %s = MulS(%s 4)\n",t,b);
      String t1 = temp();
      System.out.printf("  %s = Add(%s %s)\n", t1, a, t);
      System.out.printf("  %s = Add(%s %s)\n", t1, t1, 4);
      System.out.printf("  %s = [%s]\n", t1, t1);
      // check that the index is an integer type
      n.f3.accept(this);
      variableName = String.format("%s", t1);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public void visit(ArrayLength n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);

   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public void visit(MessageSend n) {
      // TODO: this needs to be checked in the case of virtual functions.

      n.f0.accept(this);
      String a = temp();
      String object = variableName;
      n.f1.accept(this);
      n.f2.accept(this);
      String funct = variableName;
      ClassSymbol c; MethodSymbol m;

      int num = 0;
      if(object == "this") {
        c = symbolTable.get(classIndex);
        num = c.findMethodID(funct);
        m = c.findMethod(funct);
        allocClass = m.retName;
      } else if(allocClass != null) {
        c = ClassSymbol.find(symbolTable, b -> b.className==allocClass);
        num = c.findMethodID(funct);
        m = c.findMethod(funct);
        allocClass = m.retName;
      } else {
        c = symbolTable.get(classIndex);
        m = c.methodSymbols.get(functionIndex);
        VariableSymbol v = m.findVar(b->b.varName==object);
        if(v == null) {
          v = c.findVar(b->b.varName==object);
        }
        assert(v != null);
        String varName = v.className;
        ClassSymbol varClass = ClassSymbol.find(symbolTable, b->b.className==varName);
        num = varClass.findMethodID(funct);
        m = varClass.findMethod(funct);
        allocClass = m.retName;
      }

      assert(num != -1);
      num *= 4;

      n.f3.accept(this);
			variableName = "";
      n.f4.accept(this);
      String params = variableName.length()>0? object + "," + variableName: object;
      String [] parameters = params.split(",");
      //TODO: this is kinda gibberish...
      params = "";
      for(int i = 0; i < parameters.length; i++) {
        params += attemptConvertToObjectMember(parameters[i], false) + " ";
      }
      n.f5.accept(this);
      String ret = temp();
      System.out.printf("  %s = [%s]\n", a, object);
      System.out.printf("  %s = [%s+%s]\n", a, a, num);
      System.out.printf("  %s = call %s(%s)\n", ret, a, params);
      variableName = ret;

      messageSend = true;
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public void visit(ExpressionList n) {
      n.f0.accept(this);
      String x = variableName;
      variableName = "";
      n.f1.accept(this);
      String y = (variableName);
      variableName = y.length() > 0? x + "," + y : x;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public void visit(ExpressionRest n) {
      n.f0.accept(this);
      String x = (variableName);
      n.f1.accept(this);
      variableName = x + "," + variableName;
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
   public void visit(PrimaryExpression n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n) {
      n.f0.accept(this);
      variableName = n.f0.tokenImage;
   }

   /**
    * f0 -> "true"
    */
   public void visit(TrueLiteral n) {
      n.f0.accept(this);
      variableName = "1";  // 1 -> true
   }

   /**
    * f0 -> "false"
    */
   public void visit(FalseLiteral n) {
      n.f0.accept(this);
      variableName = "0";  // 0 -> false
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Identifier n) {
      n.f0.accept(this);
			variableName = n.f0.tokenImage;
   }

   /**
    * f0 -> "this"
    */
   public void visit(ThisExpression n) {
      n.f0.accept(this);
      variableName = "this";
   }


   boolean allocation = false;

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public void visit(ArrayAllocationExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      //variableName now holds Expression String  
      String t = temp(); //create temp for Expression Value * 4 Bytes
      System.out.printf("  %s = MulS(%s 4)\n", t, variableName);
		  // use MULS to output  multiplication of string value
      String t1 = temp();
      System.out.printf("  %s = Add(%s 4)\n", t, t);
      System.out.printf("  %s = HeapAllocZ(%s)\n", t1, t);
      System.out.printf("  [%s] = %s\n", t1, variableName);     
      //creates temp for the memory allocated array
      n.f4.accept(this);
      variableName = t1;
      allocation = true;
   }

   String allocClass = null;
   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public void visit(AllocationExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      String className = variableName;
      ClassSymbol c = ClassSymbol.find(symbolTable, s->s.className==variableName);
      int size = c.variableSymbols.size() * 4 + 4; // variable sizes*4 + vtable ptr
      String t = temp();
      System.out.printf("  %s = HeapAllocZ(%d)\n", t, size);
      System.out.printf("  [%s] = :vmt_%s\n", t, c.className);
      allocClass = variableName;
      variableName = t;
      n.f2.accept(this);
      n.f3.accept(this);
      allocation = true;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public void visit(NotExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      String a = variableName;
      if(a == "0") {
        variableName = "1";
        return;
      } else if(a == "1") {
        variableName = "0";
        return;
      }

      // if 0==0, var=1, if 1!=0, var=0, performs a flip
      System.out.printf("  %s = Eq(%s 0)\n", a, a);
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public void visit(BracketExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

}

