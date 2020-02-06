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
public class PassVisitor implements Visitor {
   private final static int ARRAY_TYPE   = 0;
   private final static int BOOLEAN_TYPE = 1;
   private final static int INTEGER_TYPE = 2;
   private final static int CLASS_TYPE   = 3;
   private final static int VOID_TYPE    = 4;

   final ArrayList<ClassSymbol> symbolTable; 
   int classIndex = 0;
   int functionIndex = 0;
   boolean checkValue = true;

   public boolean check() {
      return checkValue;
   }

   public PassVisitor(ArrayList<ClassSymbol> symbolTable) {
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
      //this.current = c;
      //this.curFunc = m;
      
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
      n.f11.accept(this);
      n.f12.accept(this);
      n.f13.accept(this);
      n.f14.accept(this);
      n.f15.accept(this);
      n.f16.accept(this);
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

   int expressionType = -1;

   public void checkReturnType(Type t, Expression e, String name) {
        int s = t.f0.which;
        
        if(s != expressionType)
            checkValue = false;
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
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public void visit(MethodDeclaration n) {

      // we entered a function

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
      visitingStatement = true;
      n.f10.accept(this);
      visitingStatement = false;
      checkReturnType(n.f1, n.f10, n.f2.f0.tokenImage);
      n.f11.accept(this);
      n.f12.accept(this);
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

   boolean visitingStatement = false;

   public void visit(Statement n) {
      visitingStatement = true;
      n.f0.accept(this);
      visitingStatement = false;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public void visit(Block n) {
      //TODO: put code here...
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public void visit(AssignmentStatement n) {

      //TODO: put code here...

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
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

      //TODO: put code here...
      String token = n.f0.f0.tokenImage;


      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
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

      //TODO: figure out whether the expression "f2" is a boolean expr
      int exprType = n.f2.f0.which;
      switch(exprType) {
      case 0: // && is a boolean expression, so it's okay
        break;
      case 1: // < is a boolean expression, so it's okay
        break;
      case 2: // + is a integer expression, so it's not okay
        break;
      case 3: // - is a integer expression, so it's not okay
        break;
      case 4: // * is a integer expression, so it's not okay
        break;
      case 5: // a[i] is a integer expression, so it's not okay
        break;
      case 6: // a.length  is a integer expression, so it's not okay
        break;
      case 7: // a.methodCall(b, c, d)
        // TODO: check the method call for type.
        
        break;
      case 8: // primary expression
        break;
      default:
        break;
      }

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public void visit(WhileStatement n) {

     //TODO: figure out whether the expression "f2" is a boolean expr

      int exprType = n.f2.f0.which;
      switch(exprType) {
      case 0: // && is a boolean expression, so it's okay
        break;
      case 1: // < is a boolean expression, so it's okay
        break;
      case 2: // + is a integer expression, so it's not okay
        break;
      case 3: // - is a integer expression, so it's not okay
        break;
      case 4: // * is a integer expression, so it's not okay
        break;
      case 5: // a[i] is a integer expression, so it's not okay
        break;
      case 6: // a.length  is a integer expression, so it's not okay
        break;
      case 7: // a.methodCall(b, c, d)
        // TODO: check the method call for type.
        
        break;
      case 8: // primary expression
        break;
      default:
        break;
      }

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public void visit(PrintStatement n) {

      //TODO: print statement can ONLY print integers
      int exprType = n.f2.f0.which;
      switch(exprType) {
      case 0: // && 
        break;
      case 1: // < 
        break;
      case 2: // +  is integer
        break;
      case 3: // -  is integer
        break;
      case 4: // *  is integer
        break;
      case 5: // a[i]  is integer
        break;
      case 6: // a.length is integer
        break;
      case 7: // a.methodCall(b, c, d)
        // TODO: check the method call for type.
        
        break;
      case 8: // primary expression
        break;
      default:
        break;
      }

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

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
      // TODO: AND EXPR
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      expressionType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public void visit(CompareExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      expressionType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public void visit(PlusExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public void visit(MinusExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public void visit(TimesExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public void visit(ArrayLookup n) {
      n.f0.accept(this);
      // check that the variable is an array type
      if(expressionType != ARRAY_TYPE)
        checkValue = false;
      n.f1.accept(this);
      n.f2.accept(this);
      // check that the index is an integer type
      if(expressionType != INTEGER_TYPE)
        checkValue = false;
      n.f3.accept(this);

      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public void visit(ArrayLength n) {
      n.f0.accept(this);
      if(expressionType != ARRAY_TYPE)
        checkValue = false;
      n.f1.accept(this);
      n.f2.accept(this);

      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */

   boolean identifyClassType = false;
   String classMessageSend = null;

   // TODO: not robust for long term, but good enough for now.
   int [] numParam = new int[10];
   int head = -1;



   public void visit(MessageSend n) {
      head = head + 1;  // "push" the stack
      assert(head < numParam.length);
      numParam[head]=0;

      identifyClassType = false;
      classMessageSend = null;
      identifyClassType = true;
      n.f0.accept(this);
      n.f1.accept(this);
      identifyClassType = false;

      String functName = n.f2.f0.tokenImage;
      MethodSymbol ret = ClassSymbol.findMethod(symbolTable, classMessageSend, functName);
      
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);

      if(ret == null) {
        checkValue = false;
        expressionType = -1;
      } else {
        int expected = ret.parameters.size();
        if(numParam[head] != expected) {
            checkValue = false;
        }
        expressionType = ret.retType;
      }

      head = head - 1;  // "pop" the stack
      identifyClassType = false;

   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public void visit(ExpressionList n) {
      numParam[head]++;


      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public void visit(ExpressionRest n) {
      numParam[head]++;


      n.f0.accept(this);
      n.f1.accept(this);
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
      expressionType = INTEGER_TYPE;
   }

   /**
    * f0 -> "true"
    */
   public void visit(TrueLiteral n) {
      n.f0.accept(this);
      expressionType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> "false"
    */
   public void visit(FalseLiteral n) {
      n.f0.accept(this);
      expressionType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Identifier n) {
      // TODO: add the return type of IDENTIFIER
      n.f0.accept(this);

      if(visitingStatement) { //TODO: eventually optimize this, but for now, keep it separate
        String token = n.f0.tokenImage;
        ClassSymbol c = symbolTable.get(classIndex);
        MethodSymbol m = c.methodSymbols.get(functionIndex);
        if(ClassSymbol.isGlobal(symbolTable, token))
            expressionType = CLASS_TYPE; // TODO: This is a hack...
        else if(m.findVar(v->v.varName == token) != null) {
            VariableSymbol vv = m.findVar(v->v.varName == token);
            expressionType = vv.varType;
        } else if(c.findVar(v->v.varName == token) != null) {
            VariableSymbol vv = c.findVar(v->v.varName==token);
            expressionType = vv.varType;
        } else {
            //value doesn't exist.
            checkValue = false;
        }
      }



      if(!identifyClassType)
        return;

      String token = n.f0.tokenImage;

      //locate the class in the global scope.
      ClassSymbol c = symbolTable.get(classIndex);
      VariableSymbol variable = c.findVar(v -> v.varName == token);
      if(variable != null) {
        classMessageSend = variable.className;
        return;
      }

      //locate among the local variables
      variable = c.methodSymbols.get(functionIndex).findVar(v -> v.varName == token);
      if(variable != null) {
        classMessageSend = variable.className;
        return;
      }

      if(c.methodSymbols.get(functionIndex).methodName != "main")
        checkValue = false;
   }

   /**
    * f0 -> "this"
    */
   public void visit(ThisExpression n) {
      n.f0.accept(this);
      expressionType = CLASS_TYPE;
      if(!identifyClassType) return;
      classMessageSend = symbolTable.get(classIndex).className;
   }

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
      n.f4.accept(this);
      
      expressionType = ARRAY_TYPE;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public void visit(AllocationExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);

      //allocating new Object(), the ident must be a user-defined object
      String ident = n.f1.f0.tokenImage;
      ClassSymbol symbol = ClassSymbol.find(symbolTable, c->c.className == ident);
      if(symbol == null)
        checkValue = false;

      //we need to cover cases such as: new A().run()
      if(identifyClassType)
        classMessageSend = ident;

      expressionType = CLASS_TYPE;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public void visit(NotExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);

      expressionType = BOOLEAN_TYPE;
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
