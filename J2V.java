import syntaxtree.*;

public class J2V {

  public static void main(String [] args) {

    try {
      // Initialize the parser, and parse the program
      MiniJavaParser parser = new MiniJavaParser(System.in);
      Goal goal = MiniJavaParser.Goal();
      DepthFirstVisitor visitor = new DepthFirstVisitor();
      PassVisitor passVisitor   = new PassVisitor(visitor.symbolTable);
      VaporVisitor vaporVisitor = new VaporVisitor(visitor.symbolTable);



      visitor.visit(goal);     // construct the symboltable
      passVisitor.visit(goal);
      if(visitor.check() && passVisitor.check()) {
        System.err.println("Program type checked successfully");
        vaporVisitor.visit(goal);

      } else {
        System.err.println("Type error");
        System.exit(1);
      }
    } catch(ParseException p) {
      System.out.println("Parse Error");
      p.printStackTrace();
    } catch(Exception e) {
      System.out.println("Other kind of error");
      e.printStackTrace();
    }
  }

}
