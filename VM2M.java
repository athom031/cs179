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
        System.out.printf("\n%s:\n", dataSegment.ident);
        for(VOperand.Static value : dataSegment.values) {

          String ident = value.toString().substring(1);
          System.out.printf("  %s\n", ident);
        }
      }


      System.out.println("\n.text\n");
      System.out.println("  jal Main");
      System.out.println("  li $v0 10");
      System.out.println("  syscall\n");

      // go through each function.
      for(VFunction function : program.functions) {
        System.out.printf("%s:\n", function.ident);
        int local = Integer.max(function.stack.local, function.stack.out);
        visitor.local = local;

        // TODO: eventually make the stack correct.
        // but for now, just get it semi-functional
        System.out.println("  sw $fp -8($sp)");
        System.out.println("  move $fp $sp");
        System.out.printf("  subu $sp $sp %d\n", local*4+8);
        System.out.println("  sw $ra -4($fp)");

        int labelIndex = 0;
        for(VInstr instruction : function.body) {
          // print out label if there's a label
          if(function.labels != null && labelIndex < function.labels.length) {
            VCodeLabel label = function.labels[labelIndex];
            while(label.sourcePos.line <= instruction.sourcePos.line) {
              String labelName = label.ident;
              System.out.printf("%s:\n", labelName);
              labelIndex++;
              if(labelIndex < function.labels.length) {
                label = function.labels[labelIndex];
              } else {
                break;
              }
            }
          }

          // print out instruction
          instruction.accept(visitor);
        }

      }

      System.out.println("_print:");
      System.out.println("  li $v0 1   # syscall: print integer");
      System.out.println("  syscall");
      System.out.println("  la $a0 _newline");
      System.out.println("  li $v0 4   # syscall: print string");
      System.out.println("  syscall");
      System.out.println("  jr $ra\n");
      System.out.println("_error:");
      System.out.println("  li $v0 4   # syscall: print string");
      System.out.println("  syscall");
      System.out.println("  li $v0 10  # syscall: exit");
      System.out.println("  syscall\n");

      System.out.println("_heapAlloc:");
      System.out.println("  li $v0 9   # syscall: sbrk");
      System.out.println("  syscall");
      System.out.println("  jr $ra\n");

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

    int local;

    @Override
    public void visit(VAssign vassign) throws Exception {
      VVarRef dest = vassign.dest;
      VOperand src = vassign.source;
      String destString = dest.toString();
      if(src instanceof VLitInt) {
        VLitInt number = (VLitInt) src;
        int value = number.value;
        System.out.printf("  li %s %d\n", destString, value);
      } else if(src instanceof VOperand.Static) {
        String srcString = src.toString().substring(1);
        System.out.printf("  la %s %s\n", destString, srcString);
      } else {
        String srcString = src.toString();
        System.out.printf("  move %s %s\n", destString, srcString);
      }
    }

    @Override
    public void visit(VBranch vbranch) throws Exception {
      boolean positive = vbranch.positive;
      VLabelRef<VCodeLabel> target = vbranch.target;
      VOperand value = vbranch.value;
      String valName = value.toString();
      String labelName = target.toString().substring(1);
      if(positive) {
        System.out.printf("  bnez %s %s\n", valName, labelName);
      } else {
        System.out.printf("  beqz %s %s\n", valName, labelName);
      }
    }

    @Override
    public void visit(VBuiltIn vbuiltin) throws Exception {
      VOperand [] args = vbuiltin.args;
      VVarRef dest = vbuiltin.dest;
      VBuiltIn.Op op = vbuiltin.op;

      switch(op.name) {
      case "Add": {
        String regName1 = args[0].toString();
        if(args[1] instanceof VLitInt) {
          String litNum = args[1].toString();
          String sumReg = dest.toString();
          System.out.printf("  addu %s %s %s\n", sumReg, regName1, litNum);
        } else {
          String regName2 = args[1].toString();
          String sumReg = dest.toString();
          System.out.printf("  addu %s %s %s\n", sumReg, regName1, regName2);
        }
        break;
      }

      case "Sub": {
        if(args[0] instanceof VLitInt && !(args[1] instanceof VLitInt)) {
          String regName1 = args[0].toString();
          String regName2 = args[1].toString();
          String diff = dest.toString();
          System.out.printf("  li $t9 %s\n", regName1);
          System.out.printf("  subu %s $t9 %s\n", diff, regName2);
        } else if(args[0] instanceof VLitInt && args[1] instanceof VLitInt) {
          VLitInt intA = (VLitInt) args[0];
          VLitInt intB = (VLitInt) args[1];
          int a = intA.value;
          int b = intB.value;
          int sub = a-b;
          String diff = dest.toString();
          System.out.printf("  li %s %d\n", diff, sub);
        } else {
          String regName1 = args[0].toString();
          String regName2 = args[1].toString();
          String diff = dest.toString();
          System.out.printf("  subu %s %s %s\n", diff, regName1, regName2);
        }
        break;
      }

      case "MulS": {
        if(args[0] instanceof VLitInt && !(args[1] instanceof VLitInt)) {
          String regName1 = args[0].toString();
          String regName2 = args[1].toString();
          String product = dest.toString();
          System.out.printf("  mul %s %s %s\n", product, regName2, regName1);
        } else if(args[0] instanceof VLitInt && args[1] instanceof VLitInt) {
          VLitInt intA = (VLitInt) args[0];
          VLitInt intB = (VLitInt) args[1];
          int a = intB.value;
          int b = intA.value;
          int mult = a*b;
          String product = dest.toString();
          System.out.printf("  li %s %d\n", product, mult);
        } else {
          String regName1 = args[0].toString();
          String regName2 = args[1].toString();
          String product = dest.toString();
          System.out.printf("  mul %s %s %s\n", product, regName1, regName2);
        }
        break;
      }

      case "Eq": {
        String regName1 = args[0].toString();
        String regName2 = args[1].toString();
        String output   = dest.toString();
        System.out.printf("  xor %s %s %s\n", output, regName1, regName2);
        System.out.printf("  xor %s %s 1\n",  output, output);
        break;
      }

      case "Lt": {
        String regName1 = args[0].toString();
        String regName2 = args[1].toString();
        String output   = dest.toString();
        if(args[1] instanceof VLitInt && !(args[0] instanceof VLitInt)) {
          System.out.printf("  sltu %s %s %s\n", output, regName1, regName2);
        } else if(args[0] instanceof VLitInt) {
          VLitInt intA = (VLitInt) args[0];
          int value = intA.value;
          System.out.printf("  li $t9 %d\n", value);
          System.out.printf("  sltu %s $t9 %s\n", output, regName2);
        } else {
          System.out.printf("  sltu %s %s %s\n", output, regName1, regName2);
        }
        break;
      }

      case "LtS": {
        String regName1 = args[0].toString();
        String regName2 = args[1].toString();
        String output   = dest.toString();

        if(args[1] instanceof VLitInt) {
          System.out.printf("  slti %s %s %s\n", output, regName1, regName2);
        } else {
          System.out.printf("  slt %s %s %s\n", output, regName1, regName2);
        }
        break;
      }

      case "PrintIntS": {
        String a = args[0].toString();
        if(args[0] instanceof VLitInt) {
          System.out.printf("  li $a0 %s\n", a);
        } else {
          System.out.printf("  move $a0 %s\n", a);
        }

        System.out.println("  jal _print");
        break;
      }

      case "HeapAllocZ": {
        String a = args[0].toString();
        String destString = dest.toString();
        if(args[0] instanceof VLitInt) {
          System.out.printf("  li $a0 %s\n", a);
        } else {
          System.out.printf("  move $a0 %s\n", a);
        }

        System.out.println("  jal _heapAlloc");
        System.out.printf("  move %s $v0\n", destString);
        break;
      }

      case "Error": {
        System.out.println("  la $a0 _str0");
        System.out.println("  j _error");
        break;
      }

      default: {
        assert(false);
        break;
      }

      }
    }

    @Override
    public void visit(VCall vcall) throws Exception {
      VAddr<VFunction> addr = vcall.addr;
      VOperand [] args = vcall.args;
      VVarRef.Local dest = vcall.dest;
      int min = args.length<4? args.length : 4;
      if(addr instanceof VAddr.Label) {
        String addrName = addr.toString().substring(1);
        System.out.printf("  jal %s\n", addrName);
      } else if(addr instanceof VAddr.Var) {
        String addrName = addr.toString();
        System.out.printf("  jalr %s\n", addrName);
      } else {
        assert(false);
      }
      if(dest != null) {
        String retString = dest.toString();
        System.out.printf("  move %s $v0\n", retString);
      }
    }

    @Override
    public void visit(VGoto vgoto) throws Exception {
      VAddr<VCodeLabel> target = vgoto.target;
      String labelName = target.toString().substring(1);
      System.out.printf("  j %s\n", labelName);
    }

    @Override
    public void visit(VMemRead vmemread) throws Exception {
      VVarRef dest = vmemread.dest;
      VMemRef src = vmemread.source;
      if(src instanceof VMemRef.Global) {
        VMemRef.Global g = (VMemRef.Global) src;
        String srcString = g.base.toString();
        String destString = dest.toString();
        int offset = g.byteOffset;
        System.out.printf("  lw %s %d(%s)\n", destString, offset, srcString);
      } else if(src instanceof VMemRef.Stack) {
        VMemRef.Stack s = (VMemRef.Stack) src;
        VMemRef.Stack.Region region = s.region;
        String srcString = src.toString();
        String destString = dest.toString();
        int index = s.index * 4;
        switch(region) {
        case In:
          //System.err.println("VMEMREAD IN");
          //System.out.println("VMEMREAD IN");
          System.out.printf("  lw %s %d($fp)\n", destString, index);
          break;
        case Out:
          System.err.println("VMEMREAD OUT");
          //System.out.println("VMEMREAD OUT");
          break;
        case Local:
          System.out.printf("  lw %s %d($sp)\n", destString, index);
          break;
        }
      } else {
        assert(false);
      }
    }

    @Override
    public void visit(VMemWrite vmemwrite) throws Exception {
      VMemRef dest = vmemwrite.dest;
      VOperand src = vmemwrite.source;
      if(dest instanceof VMemRef.Global) {
        VMemRef.Global g = (VMemRef.Global) dest;
        String destString = g.base.toString();
        if(src instanceof VLitInt) {
          int offset = g.byteOffset;// * 4;
          String srcString = src.toString();
          System.out.printf("  li $t9 %s\n", srcString);
          System.out.printf("  sw $t9 %d(%s)\n", offset, destString);
        } else if(src instanceof VVarRef) {
          int offset = g.byteOffset;
          String srcString = src.toString();
          System.out.printf("  sw %s %d(%s)\n", srcString, offset, destString);
        } else if(src instanceof VOperand.Static) {
          int offset = g.byteOffset * 4;
          String srcString = src.toString().substring(1);
          System.out.printf("  la $t9 %s\n", srcString);
          System.out.printf("  sw $t9 %d(%s)\n", offset, destString);
        } else {
          int offset = g.byteOffset * 4;
          String srcString = src.toString();//.substring(1);
          System.out.printf("  la $t9 %s\n", srcString);
          System.out.printf("  sw %s %d(%s)\n", srcString, offset, destString);
        }

      } else if(dest instanceof VMemRef.Stack) {
        VMemRef.Stack d = (VMemRef.Stack) dest;
        VMemRef.Stack.Region region = d.region;
        String srcString = src.toString();
        String destString = dest.toString();
        int index = d.index * 4;
        switch(region) {
        case In:
          System.err.println("VMEMWRITE IN");
          System.out.println("VMEMWRITE IN");
          
          break;
        case Out:
          //System.err.println("VMEMWRITE OUT");
          if(src instanceof VLitInt) {
            System.out.printf("  li $t9 %s\n", srcString);
            System.out.printf("  sw $t9 %d($sp)\n", index);
          } else {
            System.out.printf("  sw %s %d($sp)\n", srcString, index);
          }
          break;
        case Local:
          System.out.printf("  sw %s %d($sp)\n", srcString, index);
          break;
        }
      } else {
        assert(false);
      }
    }

    @Override
    public void visit(VReturn vreturn) throws Exception {
      VOperand value = vreturn.value;
      if(value != null) {
        String retString = value.toString();
        if(value instanceof VLitInt) {
          System.out.printf("  li $v0 %s\n", retString);
        } else {
          System.out.printf("  move $v0 %s\n", retString);
        }
      }
      System.out.println("  lw $ra -4($fp)");
      System.out.println("  lw $fp -8($fp)");
      System.out.printf("  addu $sp $sp %d\n", local*4+8);
      System.out.println("  jr $ra\n");
    }

  }

}
