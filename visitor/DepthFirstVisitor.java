//
// Generated by JTB 1.3.2
//

package visitor;
import syntaxtree.*;
import symboltable.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class DepthFirstVisitor implements Visitor {
   private static int ARRAY_TYPE   = 0;
   private static int BOOLEAN_TYPE = 1;
   private static int INTEGER_TYPE = 2;
   private static int CLASS_TYPE   = 3;
   private static int VOID_TYPE    = 4;

   public ArrayList<ClassSymbol> symbolTable = new ArrayList<ClassSymbol> ();
   ClassSymbol  current = null;
   MethodSymbol curFunc = null;
   
   boolean checkValue = true;
   int returnType = -1;


   ArrayList<String> classMethodCheck = new ArrayList<String>();
   ArrayList<String> newExpr = new ArrayList<String>();



   public boolean check() {
       return checkValue;
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
      secondPass();
      ClassSymbol.printSymbolTable(this.symbolTable);
   }

   public void secondPass() {
      for(int i = 0; i < classMethodCheck.size(); i += 2) {
		String className = classMethodCheck.get(i);
        String classMethod = classMethodCheck.get(i+1);
        if(ClassSymbol.findMethod(symbolTable, className, classMethod) == -1) {
            //System.out.println(className+"."+classMethod);
			//checkValue = false;
		}
      }
   }



   boolean noTypeCheck = false;


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

      ClassSymbol c = new ClassSymbol();
      c.className = n.f1.f0.tokenImage;
      symbolTable.add(c);

      MethodSymbol m = c.addClassMethod("main", VOID_TYPE);
      m.addParameter(n.f11.f0.tokenImage, 5, "String []");

      this.current = c;
      this.curFunc = m;
      
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
      noTypeCheck = true;
      n.f15.accept(this);
      noTypeCheck = false;
      n.f16.accept(this);
      n.f17.accept(this);

      this.current = null;
      this.curFunc = null;
   }

   /** * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public void visit(TypeDeclaration n) {
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
      
      ClassSymbol c = new ClassSymbol();
      c.className = n.f1.f0.tokenImage;
      symbolTable.add(c);
      this.current = c;

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
      ClassSymbol c = new ClassSymbol();
      c.className = n.f1.f0.tokenImage;
      c.addExtendClass(n.f3.f0.tokenImage, symbolTable);
      symbolTable.add(c);
      this.current = c;

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
      VariableSymbol v;
      String className = "";

      if(n.f0.f0.which == CLASS_TYPE) {
        Identifier ident = (Identifier) n.f0.f0.choice;
        className = ident.f0.tokenImage;
      }

      if(this.curFunc == null) {
        //we are currently NOT in a function
        v = this.current.addClassVariable(n.f1.f0.tokenImage, n.f0.f0.which, className);
      } else {
        //we are in a function
        v = this.curFunc.addLocalVariable(n.f1.f0.tokenImage, n.f0.f0.which, className);
      }

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
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public void visit(MethodDeclaration n) {

      assert(this.current != null && this.curFunc == null);
      String methodName   = n.f2.f0.tokenImage;
      int retType         = n.f1.f0.which;
      // we entered a function
      this.curFunc = this.current.addClassMethod(methodName, retType);

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
      noTypeCheck = true;
      n.f8.accept(this);
      n.f9.accept(this);
      n.f10.accept(this);

      if(retType == this.returnType) {
		//checkValue = false;
	  }


      noTypeCheck = false;
      n.f11.accept(this);
      n.f12.accept(this);

      // we exited a function
      this.curFunc = null;
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
      assert(this.curFunc != null && this.current != null);
      int type = n.f0.f0.which;
      String name = n.f1.f0.tokenImage;
      String className = "";
      if(n.f0.f0.which == CLASS_TYPE) {
        Identifier ident = (Identifier) n.f0.f0.choice;
        className = ident.f0.tokenImage;
      }



      this.curFunc.addParameter(name, type, className);

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
      String name = n.f0.f0.tokenImage;
      if(curFunc.hasVariable(name) || current.hasVariable(name))
            ;
      else
            checkValue = false;

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
      /*String name = n.f0.f0.tokenImage;
      if(curFunc.hasVariable(name) || current.hasVariable(name))
            ;
      else
            checkValue = false;*/

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
        checkValue = false;
        break;
      case 3: // - is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 4: // * is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 5: // a[i] is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 6: // a.length  is a integer expression, so it's not okay
        checkValue = false;
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
        checkValue = false;
        break;
      case 3: // - is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 4: // * is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 5: // a[i] is a integer expression, so it's not okay
        checkValue = false;
        break;
      case 6: // a.length  is a integer expression, so it's not okay
        checkValue = false;
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
        checkValue = false;
        break;
      case 1: // < 
        checkValue = false;
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

   void checkForBooleanType(PrimaryExpression p) {
      assert(current != null && curFunc != null);
      int exprType = p.f0.which;
      switch(exprType) {

      case 0: // INTEGER TYPE
        checkValue = false;
        break;
      case 1: // TRUE
        break;
      case 2: // FALSE
        break;
      case 3: { // IDENTIFIER
        Identifier i = (Identifier) p.f0.choice;
        String name = i.f0.tokenImage;
        if(curFunc.hasVariable(name, BOOLEAN_TYPE) || current.hasVariable(name, BOOLEAN_TYPE))
            ;
        else
            checkValue = false;
        break;
      }
      case 4: // THIS EXPRESSION
        checkValue = false;
        break;
      case 5: // ARRAY_ALLOCATION_EXPRESSION
        checkValue = false;
        break;
      case 6: // ALLOCATION EXPRESSION
        checkValue = false;
        break;
      case 7: // NOT EXPRESSION
        break;
      case 8: // BRACKET EXPRESSION
        break;
      default:
        assert(false);//should not happen!
      }
   }


   void checkForIntegerType(PrimaryExpression p) {
      assert(current != null && curFunc != null);
      int exprType = p.f0.which;
      switch(exprType) {

      case 0: // INTEGER LITERAL
        break;
      case 1: // TRUE
        checkValue = false;
        break;
      case 2: // FALSE
        checkValue = false;
        break;
      case 3: { // IDENTIFIER
        Identifier i = (Identifier) p.f0.choice;
        String name = i.f0.tokenImage;
        if(curFunc.hasVariable(name, INTEGER_TYPE) || current.hasVariable(name, INTEGER_TYPE))
            ;
        else
            checkValue = false;
        break;
      }
      case 4: // THIS EXPRESSION
        checkValue = false;
        break;
      case 5: // ARRAY_ALLOCATION_EXPRESSION
        checkValue = false;
        break;
      case 6: // ALLOCATION EXPRESSION
        checkValue = false;
        break;
      case 7: // NOT EXPRESSION
        checkValue = false;
        break;
      case 8: // BRACKET EXPRESSION
        break;
      default:
        assert(false);//should not happen!
      }
   }



   /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */

   public void visit(AndExpression n) {
      // TODO: AND EXPR
      checkForBooleanType(n.f0);
      checkForBooleanType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      returnType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public void visit(CompareExpression n) {
      checkForIntegerType(n.f0);
      checkForIntegerType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      returnType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public void visit(PlusExpression n) {
      checkForIntegerType(n.f0);
      checkForIntegerType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      returnType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public void visit(MinusExpression n) {
      checkForIntegerType(n.f0);
      checkForIntegerType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      returnType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public void visit(TimesExpression n) {
      checkForIntegerType(n.f0);
      checkForIntegerType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      returnType = INTEGER_TYPE;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public void visit(ArrayLookup n) {
      checkForIntegerType(n.f2);
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      returnType = INTEGER_TYPE;
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
      returnType = INTEGER_TYPE;
   }

   boolean checkMethod = false;
   String  checkClassForMethod = null;

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public void visit(MessageSend n) {
      checkClassForMethod = null;
      checkMethod = false;
      n.f0.accept(this);
      n.f1.accept(this);
      checkMethod = true;
      n.f2.accept(this);
      checkMethod = false;
      checkClassForMethod = null;
      n.f3.accept(this);
      n.f4.accept(this);
      n.f5.accept(this);
   }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
   public void visit(ExpressionList n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public void visit(ExpressionRest n) {
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
      returnType = INTEGER_TYPE;
   }

   /**
    * f0 -> "true"
    */
   public void visit(TrueLiteral n) {
      n.f0.accept(this);
      returnType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> "false"
    */
   public void visit(FalseLiteral n) {
      n.f0.accept(this);
      returnType = BOOLEAN_TYPE;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Identifier n) {
      n.f0.accept(this);
      if(!noTypeCheck) {
        return;
      }
      String varName = n.f0.tokenImage;

      int type = current.typeOf(varName);
      if(type != -1) {
        returnType = type;
        checkClassForMethod = current.getClassName(varName);
 //       System.out.println(">>>"+checkClassForMethod+varName);
        return;
      }
      type = curFunc.typeOf(varName);
      if(type != -1) {
        returnType = type;
        checkClassForMethod = curFunc.getClassName(varName);

//        System.out.println("+++"+checkClassForMethod+varName);
        return;
      }

      if(!checkMethod) {
        returnType = -1;
        return;
      }
      
      classMethodCheck.add(checkClassForMethod);
      classMethodCheck.add(varName);
   }

   /**
    * f0 -> "this"
    */
   public void visit(ThisExpression n) {
      n.f0.accept(this);
      checkClassForMethod = current.className;
      returnType = CLASS_TYPE;
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
      returnType = ARRAY_TYPE;
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
	  checkClassForMethod = n.f1.f0.tokenImage;

      returnType = CLASS_TYPE;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public void visit(NotExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
      returnType = BOOLEAN_TYPE;
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
