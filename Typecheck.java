import syntaxtree.*;
import visitor.*;

public class Typecheck {

    public static void main(String [] args) {
        try {
            // Initialize the parser, and parse the program
            MiniJavaParser parser = new MiniJavaParser(System.in);
            Goal goal = MiniJavaParser.Goal();
            DepthFirstVisitor visitor = new DepthFirstVisitor();
            visitor.visit(goal);

        } catch(ParseException p) {
            System.out.println("Parse Error");
            p.printStackTrace();


        } catch(Exception e) {
            System.out.println("Other kind of error");
            e.printStackTrace();


        }
    }

}
