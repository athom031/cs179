import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.*;
import java.io.*;

class V2VM extends CommandLineLauncher.TextOutput {

  // TODO: Registor allocation graph coloring algorithm is working consistently
  // Code generation works for trivial cases like 1-Basic and 2-Loop
  // However, it cannot handle more complicated cases.

  public static void main(String[] args) {
    CommandLineLauncher.run(new V2VM(), args);
  }

  @Override
  public void run(PrintWriter out, InputStream in, PrintWriter err, String [] args) throws Exit {
    try {
      VaporProgram p = V2VM.parseVapor(in, System.err);
      InstructionVisitor visitor = new InstructionVisitor();

      // data segment
      for(VDataSegment dataSegment : p.dataSegments) {
        System.out.printf("const %s\n", dataSegment.ident);
        for(VOperand.Static value : dataSegment.values) {
          System.out.printf("  %s\n", value.toString());
        }
        System.out.println();
      }

      // code generation
      for(VFunction function : p.functions) {
        String funcName = function.ident;
        int inVar = function.params.length < 3? function.params.length : 3;
        int outVar = 10;//function.params.length < 3? 0 : function.params.length-3;
        int localVar = 8;
        visitor.vars = function.vars;
        visitor.params = function.params;
        System.out.printf("func %s [in %d, out %d, local %d]\n", funcName, inVar, outVar, localVar);

        int labelIndex = 0;
        int instrIndex = 0;
        int i = 0;

        VCodeLabel label = function.labels==null || function.labels.length==0? null : function.labels[labelIndex]; 
        VInstr instr = function.body==null || function.body.length==0? null : function.body[instrIndex];

        while(true) {
          // print out the label
          if(label != null && label.sourcePos.line == i) {
            System.out.printf("  %s:\n", label.ident);
            labelIndex++;
            if(labelIndex < function.labels.length) {
              label = function.labels[labelIndex];
            } else {
              label = null;
            }
          }
          
          // print out the instruction
          if(instr.sourcePos.line == i) {
            instr.accept(visitor);
            if(instr instanceof VReturn) 
              break;
            instrIndex++;
            if(instrIndex < function.body.length) {
              instr = function.body[instrIndex];
            }
          }

          i++;
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
    Op [] ops = {Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS, Op.PrintIntS, Op.HeapAllocZ, Op.Error};
    boolean allowLocals = true;
    String [] registers = {
      "$a0", "$a1", "$a2", "$a3",
      "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
      "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8",
      "$v0"
    };
//null;
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

  class InstructionVisitor extends VInstr.Visitor<Exception> {

    public String [] vars = null;
    public VVarRef.Local [] params = null;

    String mapToRegister(String name) {
      for(int i = 0; i < vars.length; i++) {
        if(name.equals(vars[i])) {
          return "$t"+i;
        }
      }
      return name;
    }

    @Override
    public void visit(VAssign a) throws Exception {
      VVarRef dest = a.dest;
      VOperand src = a.source;
      String d = mapToRegister(dest.toString());
      String s = (src instanceof VLitInt)? src.toString() : mapToRegister(src.toString());
      System.out.printf("  %s = %s\n", d, s);
    }

    @Override
    public void visit(VBranch b) throws Exception {
      boolean positive = b.positive;
      VLabelRef<VCodeLabel> target = b.target;
      VOperand value = b.value;
      String v = mapToRegister(value.toString());
      System.out.printf("  if%s %s goto %s\n", positive? "" : 0, v, target);
    }

    @Override
    public void visit(VBuiltIn b) throws Exception {
      VOperand [] args = b.args;
      VVarRef dest = b.dest;
      VBuiltIn.Op op = b.op;
      switch(op.name) {
      case "Add": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "Sub": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "MulS": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "Eq": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "Lt": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "LtS": {
        String d = mapToRegister(dest.toString());
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        System.out.printf("  %s = %s(%s %s)\n", d, op.name, a0, a1);
        break;
      }

      case "PrintIntS": {
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        System.out.printf("  %s(%s)\n", op.name, a0);
        break;
      }

      case "HeapAllocZ": {
        String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        String d = mapToRegister(dest.toString());
        System.out.printf("  %s = %s(%s)\n", d, op.name, a0);
        break;
      }

      case "Error": {
        System.out.printf("  %s(%s)\n", op.name, args[0]);
        break;
      }

      default: {
        assert(false); // Should not happen
        break;
      }

      }
    }

    @Override
    public void visit(VCall c) throws Exception {
      VAddr<VFunction> addr = c.addr;
      VOperand [] args = c.args;
      VVarRef.Local dest = c.dest;
      int min = args.length < 4? args.length : 4;

      for(int i = 0; i < min; i++) {
        String a = mapToRegister(args[i].toString());
        System.out.printf("  $a%d = %s\n", i, a);
      }

      for(int i = 4; i < args.length; i++) {
        String a = mapToRegister(args[i].toString());
        System.out.printf("  out[%d] = %s\n", i-4, a);
      }

      String a = mapToRegister(addr.toString());


      String d = mapToRegister(dest.toString());
      System.out.printf("  call %s\n", a);
      System.out.printf("  %s = $v0\n", d);
    }

    @Override
    public void visit(VGoto g) throws Exception {
      VAddr<VCodeLabel> target = g.target;
      System.out.printf("  goto %s\n", target);
    }

    @Override
    public void visit(VMemRead r) throws Exception {
      VVarRef dest = r.dest;
      VMemRef src = r.source;
      String d = mapToRegister(dest.toString());
      int offset = 0;
      String s;
      if(src instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) src;
        s = mapToRegister(gg.base.toString());
        offset = gg.byteOffset;
      } else {
        s = "";
      }

      System.out.printf("  %s = [%s%s]\n", d, s, offset>0?"+"+offset:"");
    }

    @Override
    public void visit(VMemWrite w) throws Exception {
      VMemRef dest = w.dest;
      VOperand src = w.source;
      String d; 
      int offset = 0;;
      String s = mapToRegister(src.toString());

      if(dest instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) dest;
        d = mapToRegister(gg.base.toString());
        offset = gg.byteOffset;
      } else {
        d = "";
      }

      System.out.printf("  [%s%s] = %s\n", d, offset>0?"+"+offset:"", s);
    }

    @Override
    public void visit(VReturn r) throws Exception {
      VOperand value = r.value;
      if(value != null) {
        String v = (value instanceof VLitInt)? value.toString() : mapToRegister(value.toString());
        System.out.printf("  $v0 = %s\n", v);
      }
      System.out.printf("  ret\n\n");
    }
  }

  public static int [] graphColor(boolean [][] matrix, int maxColor) {
    assert(matrix != null && matrix.length == matrix[0].length);
    int size = matrix.length;
    int [] nodeStack = new int[size];
    boolean [][] edgeStack = new boolean[size][size];
    int top = -1;

    int [] colors = new int[size];
    boolean repeat = false;
    do {
      repeat = false;
      for(int i = 0; i < size; i++) {
        int edgeCount = numEdges(matrix[i]);
        if(edgeCount != 0 && edgeCount < maxColor) {
          repeat = true;
          top++;
          nodeStack[top] = i;
          for(int j = 0; j < size; j++) {
            edgeStack[top][j] = matrix[i][j];
            if(matrix[i][j] == true) {
              matrix[i][j] = false;
              matrix[j][i] = false;
            }
          }
        }
      }
    } while(repeat==true);


    boolean [] col = new boolean[size];
    while(top > -1) {
      int i = nodeStack[top];
      for(int j = 0; j < size; j++) {
        if(edgeStack[top][j] == true) {
          matrix[i][j] = true;
          matrix[j][i] = true;
        }
      }

      for(int j = 0; j < size; j++) {
        col[j] = false;
      }

      for(int j = 0; j < size; j++) {
        if(matrix[i][j]==true) {
          col[colors[j]] = true;
        }
      }

      for(int j = 0; j < size; j++) {
        if(col[j]==false) {
          colors[i] = j;
          break;
        }
      }
      top--;
    }

    return colors;
  }

  public static void edge(boolean [][] matrix, int x, int y) {
    matrix[x][y] = true;
    matrix[y][x] = true;
  }

  public static int numEdges(boolean [] row) {
    int count = 0;
    for(boolean r : row)
      if(r)
        count++;
    return count;
  }




}




