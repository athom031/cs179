import syntaxtree.Goal;
import visitor.DepthFirstVisitor;

public class Typecheck {

    public static void main(String [] args) {
        try {
            // Initialize the parser, and parse the program
            MiniJavaParser parser = new MiniJavaParser(System.in);
            Goal goal = MiniJavaParser.Goal();
            DepthFirstVisitor depthFirstVisitor = new DepthFirstVisitor();
            depthFirstVisitor.visit(goal);

        } catch(ParseException p) {
            System.out.println("Parse Error");
            p.printStackTrace();


        } catch(Exception e) {
            System.out.println("Other kind of error");
            e.printStackTrace();


        }
    }

}
