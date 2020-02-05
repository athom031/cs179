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
    String name = String.format("____t%d", tempNumber);
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
      System.out.println("  ret");
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
      n.f10.accept(this);
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

      n.f0.accept(this);
      String a = variableName;
      //System.err
      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;
      n.f3.accept(this);
      String instr = String.format("  %s = %s", a, b);
      System.out.println(instr);
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
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      String elseLabel = label();
      String endifLabel = label();
      String a = variableName;
      String instr = String.format("  if0 %s goto :%s", a, elseLabel);
      System.out.println(instr);

      n.f3.accept(this);
      n.f4.accept(this);


      instr = String.format("  goto :%s", endifLabel);
      System.out.println(instr);

      instr = String.format("  %s:", elseLabel);
      System.out.println(instr);

      n.f5.accept(this);
      n.f6.accept(this);

      instr = String.format("  %s:", endifLabel);
      System.out.println(instr);

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

      String instr = String.format("  %s:", beginLoopLabel);
      System.out.println(instr);

      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);

      String a = variableName;
      instr = String.format("  if0 %s goto :%s", a, endLoopLabel);
      System.out.println(instr);

      n.f3.accept(this);
      n.f4.accept(this);

      instr = String.format("  goto :%s", beginLoopLabel);
      System.out.println(instr);
      
      instr = String.format("  %s:", endLoopLabel);
      System.out.println(instr);
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
      String instr = String.format("  PrintIntS(%s)", e);
      System.out.println(instr);
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
      // TODO: AND EXPR needs to be fixed later!!!! --VAPOR OUTPUT
      String andLabel = label();
      String andBool  = temp();

      String instr = String.format("  %s = 0", andBool);
      System.out.println(instr);

      n.f0.accept(this);
      String a = variableName;
      instr = String.format("  if0 %s goto :%s", a, andLabel);
      System.out.println(instr);

      n.f1.accept(this);
      n.f2.accept(this);
      String b = variableName;

      instr = String.format("  if0 %s goto :%s", b, andLabel);
      System.out.println(instr);

      instr = String.format("  %s = 1", andBool);
      System.out.println(instr);

      instr = String.format("  %s:", andLabel);
      System.out.println(instr);

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
      String instr = String.format("  %s = LtS(%s %s)", c, a, b);
      System.out.println(instr);
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
      String instr = String.format("  %s = Add(%s %s)", c, a, b);
      System.out.println(instr);
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
      String instr = String.format("  %s = Sub(%s %s)", c, a, b);
      System.out.println(instr);
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
      String instr = String.format("  %s = MulS(%s %s)", c, a, b);
      System.out.println(instr);
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
      // check that the variable is an array type
      n.f1.accept(this);
      n.f2.accept(this);
      // check that the index is an integer type
      n.f3.accept(this);

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
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
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
      //System.err.println(n.f0.tokenImage);
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
      // TODO: add the return type of IDENTIFIER
      n.f0.accept(this);
      variableName = n.f0.tokenImage;
   }

   /**
    * f0 -> "this"
    */
   public void visit(ThisExpression n) {
      n.f0.accept(this);
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
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public void visit(NotExpression n) {
      n.f0.accept(this);
      n.f1.accept(this);
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

