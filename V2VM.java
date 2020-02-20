import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
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
        for(String v : function.vars) {
          System.err.println("  vars: "+ v);
        }

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
      VVarRef dest = a.dest;
      VOperand src = a.source;
      System.err.printf("  MOV %s, %s\n", dest, src);
    }

    @Override
    public void visit(VBranch b) throws Exception {
      boolean positive = b.positive;
      VLabelRef<VCodeLabel> target = b.target;
      VOperand value = b.value;
      System.err.printf("  BRCH %s [%s] %s\n", positive, target, value);
    }

    @Override
    public void visit(VBuiltIn b) throws Exception {
      VOperand [] args = b.args;
      VVarRef dest = b.dest;
      VBuiltIn.Op op = b.op;
      switch(op.name) {
      case "Add": {
        System.err.printf("  %s %s, %s, %s\n", op.name, dest, args[0], args[1]);
        break;
      }

      case "Sub": {
        System.err.printf("  %s %s, %s, %s\n", op.name, dest, args[0], args[1]);
        break;
      }

      case "MultS": {
        System.err.printf("  %s %s, %s, %s\n", op.name, dest, args[0], args[1]);
        break;
      }

      case "Eq": {
        break;
      }

      case "Lt": {
        break;
      }

      case "LtS": {
        break;
      }

      case "PrintIntS": {
        System.err.printf("  %s(%s)\n", op.name, args[0]);
        break;
      }

      case "HeapAllocZ": {
        break;
      }

      case "Error": {
        break;
      }

      default: {
        System.err.printf("What the fuck???\n");
        break;
      }

      }
    }

    @Override
    public void visit(VCall c) throws Exception {
      VAddr<VFunction> addr = c.addr;
      VOperand [] args = c.args;
      VVarRef.Local dest = c.dest;
      System.err.printf("  %s = Call %s(%s)\n", dest, addr, args);
    }

    @Override
    public void visit(VGoto g) throws Exception {
      VAddr<VCodeLabel> target = g.target;
      System.err.printf("  Goto: %s\n", target);
    }

    @Override
    public void visit(VMemRead r) throws Exception {
      VVarRef dest = r.dest;
      VMemRef src = r.source;
      System.err.printf(" MEM_READ %s, %s\n", dest, src);
    }

    @Override
    public void visit(VMemWrite w) throws Exception {
      VMemRef dest = w.dest;
      VOperand src = w.source;
      System.err.printf("  MEM_WRITE %s, %s\n", dest, src);
    }

    @Override
    public void visit(VReturn r) throws Exception {
      VOperand value = r.value;
      System.err.printf("  RET %s\n", value==null? "" : value);
    }
  }

}




