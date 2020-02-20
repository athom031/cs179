import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.*;
import java.io.*;

class V2VM extends CommandLineLauncher.TextOutput {


  public static void main(String[] args) {
    CommandLineLauncher.run(new V2VM(), args);

  }

  public void run(PrintWriter out, InputStream in, PrintWriter err, String [] args) throws Exit {
    try {
      VaporProgram p = V2VM.parseVapor(in, System.err);
      InstructionVisitor visitor = new InstructionVisitor();
      for(VFunction function : p.functions) {
        System.err.println(function.ident);
        for(VInstr instr : function.body) {
          instr.accept(visitor);
        }
      }
    } catch(Exception e) {
      System.err.println(e);
    }
  }

  public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
    Op [] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS, Op.PrintIntS, Op.HeapAllocZ, Op.Error};
    boolean allowLocals = true;
    String [] registers = null;
    boolean allowStack = false;

    try {
      return VaporParser.run(new InputStreamReader(in), 
                             1, 
                             1,
                             java.util.Arrays.asList(ops), 
                             allowLocals, 
                             registers, 
                             allowStack);

    } catch(ProblemException ex) {
      err.println(ex.getMessage());
      return null;
    }
  }

  // TODO: I don't know if Exception is the correct exception to use
  class InstructionVisitor extends VInstr.Visitor<Exception> {

    @Override
    public void visit(VAssign a) throws Exception {
      System.err.println("VAssign");
    }

    @Override
    public void visit(VBranch b) throws Exception {
      System.err.println("VBranch");
    }

    @Override
    public void visit(VBuiltIn b) throws Exception {
      System.err.println("VBuiltIn");
    }

    @Override
    public void visit(VCall c) throws Exception {
      System.err.println("VCall");
    }

    @Override
    public void visit(VGoto g) throws Exception {
      System.err.println("VGoto");
    }

    @Override
    public void visit(VMemRead r) throws Exception {
      System.err.println("VMemRead");
    }

    @Override
    public void visit(VMemWrite w) throws Exception {
      System.err.println("VMemWrite");
    }

    @Override
    public void visit(VReturn r) throws Exception {
      System.err.println("VReturn");
    }
  }

}




