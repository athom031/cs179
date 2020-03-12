import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.*;
import java.io.*;

public class VM2M extends CommandLineLauncher.TextOutput {

  public static void main(String [] args) {
    CommandLineLauncher.run(new VM2M(), args);
  }

  @Override
  public void run(PrintWriter out, InputStream in, PrintWriter err, String [] args) throws Exit {
    try {
      VaporProgram program = VM2M.parseVapor(in, System.err);
      Visitor visitor = new Visitor();

      // setup
      System.out.println(".data");

      // virtual function table
      for(VDataSegment dataSegment : program.dataSegments) {
        System.out.printf("%s:\n", dataSegment.ident);
        for(VOperand.Static value : dataSegment.values) {
          String ident = value.toString();
          System.out.printf("  %s\n", ident);
        }
      }


      System.out.println(".text");
      System.out.println("  jal Main");
      System.out.println("  li $v0 10");
      System.out.println("  syscall");

      // go through each function.
      for(VFunction function : program.functions) {
        System.out.printf("%s:\n", function.ident);

        // TODO: eventually make the stack correct.
        // but for now, just get it semi-functional
        System.out.println("  sw $fp -8($sp)");
        System.out.println("  move $fp $sp");
        System.out.println("  subu $sp $sp 8");
        System.out.println("  sw $ra -4($fp)");

        for(VInstr instruction : function.body) {
          instruction.accept(visitor);
        }

        System.out.println("  lw $ra -4($fp)");
        System.out.println("  lw $fp -8($fp)");
        System.out.println("  addu $sp $sp 8");
        System.out.println("  jr $ra");
      }

      System.out.println("_print:");
      System.out.println("  li $v0 1");
      System.out.println("  syscall");
      System.out.println("  la $a0 _newline");
      System.out.println("  li $v0 4");
      System.out.println("  syscall");
      System.out.println("  jr $ra");
      System.out.println("_error:");
      System.out.println("  li $v0 4");
      System.out.println("  syscall");
      System.out.println("_heapAlloc:");
      System.out.println("  li $v0 9");
      System.out.println("  syscall");
      System.out.println("  jr $ra");
      System.out.println(".data");
      System.out.println(".align 0");
      System.out.println("  _newline: .asciiz \"\\n\"");
      System.out.println("  _str0: .asciiz \"null pointer\\n\"");
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
    Op [] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS, Op.PrintIntS, Op.HeapAllocZ, Op.Error};
    boolean allowLocals = false;
    String [] registers = {
      "v0", "v1", 
      "a0", "a1", "a2", "a3",
      "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", 
      "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7"
    };
    boolean allowStack = true;
    try {
      return VaporParser.run(new InputStreamReader(in), 1, 1, java.util.Arrays.asList(ops), allowLocals, registers, allowStack);
    } catch (ProblemException ex) {
      err.println(ex.getMessage());
      return null;
    }
  }

  class Visitor extends VInstr.Visitor<Exception> {

    @Override
    public void visit(VAssign vassign) throws Exception {
      VVarRef dest = vassign.dest;
      VOperand src = vassign.source;

    }

    @Override
    public void visit(VBranch vbranch) throws Exception {
      boolean positive = vbranch.positive;
      VLabelRef<VCodeLabel> target = vbranch.target;
      VOperand value = vbranch.value;
    }

    @Override
    public void visit(VBuiltIn vbuiltin) throws Exception {
      VOperand [] args = vbuiltin.args;
      VVarRef dest = vbuiltin.dest;
      VBuiltIn.Op op = vbuiltin.op;
    }

    @Override
    public void visit(VCall vcall) throws Exception {
      VAddr<VFunction> addr = vcall.addr;
      VOperand [] args = vcall.args;
      VVarRef.Local dest = vcall.dest;
    }

    @Override
    public void visit(VGoto vgoto) throws Exception {
      VAddr<VCodeLabel> target = vgoto.target;
    }

    @Override
    public void visit(VMemRead vmemread) throws Exception {
      VVarRef dest = vmemread.dest;
      VMemRef src = vmemread.source;
    }

    @Override
    public void visit(VMemWrite vmemwrite) throws Exception {
      VMemRef dest = vmemwrite.dest;
      VOperand src = vmemwrite.source;
    }

    @Override
    public void visit(VReturn vreturn) throws Exception {
      VOperand value = vreturn.value;
    }

  }

}
