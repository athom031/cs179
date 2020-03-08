import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.*;
import java.io.*;

class V2VM extends CommandLineLauncher.TextOutput {

  // a [t       ] numInstrs
  // b [        ]
  // c [        ]
  //
  //
  //

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
      LivenessVisitor lvisitor = new LivenessVisitor();

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
        int inVar = function.stack.in;//function.params.length < 4? 0 : function.params.length-4;
        int outVar = function.stack.out;//function.params.length < 3? 0 : function.params.length-3;
        int localVar = function.stack.local;
        lvisitor.vars = function.vars;
        lvisitor.params = function.params;
        BasicBlock [] basicBlocks = BasicBlock.generateBlocks(function.body, function.labels);
        BasicBlock.printBasicBlocks(basicBlocks, function.body);
        // this is a bit silly.
        // calculate the last line of code. then create an array of line+1.
        /*int num = function.body[function.body.length-1].sourcePos.line+1;
        lvisitor.livenessArray = new boolean [function.vars.length][num];// [2*function.body.length];



        visitor.vars = function.vars;
        visitor.params = function.params;
        System.out.printf("func %s [in %d, out %d, local %d]\n", funcName, inVar, outVar, localVar);
        if(!funcName.equals("Main")) {
          visitor.handleParams();
        }


        for(int i=function.body.length-1; i>=0;i--) {
          VInstr instr = function.body[i];
          instr.accept(lvisitor);

        }
        for(int i=0; i<lvisitor.livenessArray.length; i++) {
          System.err.printf("%7s: ", lvisitor.vars[i]);
          int length = lvisitor.livenessArray[0].length;
          for(int j=0; j<length; j++) {
            System.err.print(lvisitor.livenessArray[i][j]? "T " : "_ ");
          }
          System.err.println();
        }

        int labelIndex = 0;
        int instrIndex = 0;
        int i = 0;

        VCodeLabel label = function.labels==null || function.labels.length==0? null : function.labels[labelIndex]; 
        VInstr instr = function.body==null || function.body.length==0? null : function.body[instrIndex];*/
/*
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
*/
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

  class LivenessVisitor extends VInstr.Visitor<Exception> {

    public String [] vars = null;
    public VVarRef.Local [] params = null;
    public boolean [][] livenessArray = null;

    int getID(String v) {
      for(int i=0; i<vars.length; i++) {
        // TODO: this is bad...
        if(v.equals(vars[i])) return i;
        //if(v.equals(params[i])) return i;
      }
      return -1;
    }

    @Override
    public void visit(VAssign a) throws Exception {
      VVarRef dest = a.dest;
      VOperand src = a.source;

      int idx = getID(dest.toString());
      int line = a.sourcePos.line + 1;
      /*while(line < livenessArray[0].length && !livenessArray[idx][line]) {
        livenessArray[idx][line] = true;
        line++;
      }*/

      if(src instanceof VLitInt) {
        return;
      } 
      
      idx = getID(src.toString());
      livenessArray[idx][line] = true;

    }

    @Override
    public void visit(VBranch b) throws Exception {
      boolean positive = b.positive;
      VLabelRef<VCodeLabel> target = b.target;
      VOperand value = b.value;

      int idx = getID(value.toString());
      int line = b.sourcePos.line;

      livenessArray[idx][line] = true;
    }

    @Override
    public void visit(VBuiltIn b) throws Exception {
      VOperand [] args = b.args;
      VVarRef dest = b.dest;
      VBuiltIn.Op op = b.op;
      switch(op.name) {
      case "Add": {
        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "Sub": {

        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }


        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "MulS": {

        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }



        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "Eq": {

        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }


        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "Lt": {

        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }



        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "LtS": {

        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        if(!(args[1] instanceof VLitInt)) {
          int idx = getID(args[1].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }



        //String d = mapToRegister(dest.toString());
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String a1 = (args[1] instanceof VLitInt)? args[1].toString() : mapToRegister(args[1].toString());
        break;
      }

      case "PrintIntS": {
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        if(args[0] instanceof VOperand) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }

        break;
      }

      case "HeapAllocZ": {
        //String a0 = (args[0] instanceof VLitInt)? args[0].toString() : mapToRegister(args[0].toString());
        //String d = mapToRegister(dest.toString());
        if(!(args[0] instanceof VLitInt)) {
          int idx = getID(args[0].toString());
          int line = b.sourcePos.line;
          livenessArray[idx][line] = true;
        }
        break;
      }

      case "Error": {
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

      /*for(int i = 0; i < min; i++) {
        //String a = mapToRegister(args[i].toString());
      }

      for(int i = 4; i < args.length; i++) {
        //String a = mapToRegister(args[i].toString());
      }*/

      //String a = mapToRegister(addr.toString());
      //String d = mapToRegister(dest.toString());

      int line = c.sourcePos.line;
      for(VOperand a : args) {
        int idx = getID(a.toString());
        if(idx != -1) {
          livenessArray[idx][line] = true;
        }
      }

      int idx = getID(addr.toString());
      if(idx != -1) {
        livenessArray[idx][line] = true;
      }
    }

    @Override
    public void visit(VGoto g) throws Exception {
      VAddr<VCodeLabel> target = g.target;
    }

    @Override
    public void visit(VMemRead r) throws Exception {
      VVarRef dest = r.dest;
      VMemRef src = r.source;
      //String d = mapToRegister(dest.toString());
      int offset = 0;
      String s;
      if(src instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) src;
        s = gg.base.toString();
        offset = gg.byteOffset;
      } else {
        s = "";
      }
      int idx = getID(s);
      int line = r.sourcePos.line;
      livenessArray[idx][line] = true;

    }

    @Override
    public void visit(VMemWrite w) throws Exception {
      VMemRef dest = w.dest;
      VOperand src = w.source;
      if(src instanceof VLitInt) {
        return;
      } else if(src instanceof VLabelRef) {
        return;
      } else {
        int idx = getID(src.toString());
        int line = w.sourcePos.line;
        System.out.println(src.toString() + " "+src.getClass().toString());
        livenessArray[idx][line] = true;
      }
    }

    @Override
    public void visit(VReturn r) throws Exception {
      VOperand value = r.value;
      if(value != null) {
        //String v = (value instanceof VLitInt)? value.toString() : mapToRegister(value.toString());
        int idx = getID(value.toString());
        int line = r.sourcePos.line;

        if(idx == -1) return;
        livenessArray[idx][line] = true;
      }
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

    public void handleParams() {
      int min = params.length < 4? params.length : 4;
  
      for(int i = 0; i < min; i++) {
        String a = mapToRegister(params[i].toString());
        System.out.printf("  %s = $a%d\n", a, i);
      }
    
      for(int i = 4; i < params.length; i++) {
        String a = mapToRegister(params[i].toString());
        System.out.printf("  %s = in[%d]\n", a, i-4);
      }
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




