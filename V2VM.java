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

        BasicBlock [] basicBlocks = BasicBlock.generateBlocks(function.body, function.labels);

        {
          // initialize liveness visitor
          int functVars = function.vars.length;
          int linesCode = function.body.length;
          lvisitor.livenessArray = new boolean [functVars][linesCode];
          lvisitor.vars = function.vars;
          lvisitor.params = function.params;
          lvisitor.instr = function.body;
          lvisitor.outVar = 0;
          // visit instructions, compute liveness
          do {
            int bbIdx = basicBlocks.length-1;
            lvisitor.currentBlock = basicBlocks[bbIdx];
            lvisitor.loopAgain=false;
            for(int i=function.body.length-1; i>=0;i--) {
              // change to a new block..
              if(i < lvisitor.currentBlock.start) {
                bbIdx = bbIdx - 1;
                lvisitor.currentBlock = basicBlocks[bbIdx];
              }
              VInstr instr = function.body[i];
              instr.accept(i, lvisitor);
            }
          } while(lvisitor.loopAgain==true);
        }

        // initialize visitor
        visitor.vars = function.vars;
        visitor.params = function.params;
        
        {
          lvisitor.printLiveness();
          boolean [][] livenessArray = lvisitor.livenessArray;
          int functVars = function.vars.length;
          int linesCode = function.body.length;
          boolean [][] matrix = new boolean[functVars][functVars];
          for(int k=0; k<linesCode; k++) {
            for(int i=0; i<functVars; i++) {
              for(int j=i+1; j<functVars; j++) {
                if(livenessArray[i][k] && livenessArray[j][k]) {
                  edge(matrix, i, j);
                }
              }
            }
          }

          int numRegs = 8+9;
          visitor.graphColor = graphColor(matrix, numRegs);
          for(int i=0; i<functVars; i++) {
            boolean notUsed = true;
            for(int j=0; j<linesCode; j++) {
              if(livenessArray[i][j]) {
                notUsed = false;
                break;
              }
            }
            if(notUsed) visitor.graphColor[i] = -1;
          }

          for(int i=0; i<visitor.graphColor.length;i++) {
            visitor.maxColor = Integer.max(visitor.graphColor[i], visitor.maxColor);
          }
        }

        int labelIndex = 0;
        int instrIndex = 0;
        int i = 0;

        VCodeLabel label = function.labels==null || function.labels.length==0? null : function.labels[labelIndex]; 
        VInstr instr = function.body==null || function.body.length==0? null : function.body[instrIndex];

        {
          String funcName = function.ident;
          int inVar = function.params.length<4? 0 : function.params.length-4;
          int outVar = lvisitor.outVar;
          int localVar = visitor.maxColor+1;
          System.out.printf("func %s [in %d, out %d, local %d]\n", funcName, inVar, outVar, localVar);
          if(!funcName.equals("Main")) {
            visitor.handleParams();
          }
        }

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
            if(instr instanceof VReturn) {
              instr.accept(visitor);
              break;
            }

            instr.accept(visitor);
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

  class LivenessVisitor extends VInstr.VisitorP<Integer, Exception> {

    public String [] vars = null;
    public VVarRef.Local [] params = null;
    public boolean [][] livenessArray = null;
    public boolean loopAgain = false;
    public VInstr [] instr = null;
    public BasicBlock currentBlock = null;
    public int outVar = 0;

    int getID(String v) {
      for(int i=0; i<vars.length; i++)
        if(v.equals(vars[i])) return i;
      return -1;
    }

    void setLivenessTrue(int i, int j) {
      if(livenessArray[i][j] == false) {
        livenessArray[i][j] = true;
        loopAgain = true;
      }
    }

    void printLiveness() {
      System.err.println();
      for(int i=0; i<livenessArray.length; i++) {
        System.err.printf("%15s:  ", vars[i]);
        int lines = livenessArray[i].length;
        for(int j=0; j<lines; j++) {
          System.err.printf("%s", livenessArray[i][j]? "|" : "_");
        }
        System.err.println();
      }
    }

    void propagateLiveness(int currentLine, int nextLine, String ... args) {
      for(int i=0; i<vars.length; i++) {
        boolean isReferredTo = false;
        for(String a : args) {
          if(a==null) continue;
          if(vars[i].equals(a)) {
            isReferredTo = true;
            break;
          }
        }
        if(isReferredTo) continue;
        if(livenessArray[i][nextLine] == true) {
          setLivenessTrue(i, currentLine);
        } 
      }
    }

    @Override
    public void visit(Integer line, VAssign a) throws Exception {
      VVarRef dest = a.dest;
      VOperand src = a.source;

      String s_str = src.toString();
      String d_str = dest.toString();

      // source
      if(!(src instanceof VLitInt)) {
        int idx = getID(s_str);
        setLivenessTrue(idx, line);
        if(!s_str.equals(d_str))
        propagateLiveness(line, line+1, d_str, s_str);
        return;
      } 
      
      propagateLiveness(line, line+1, d_str);
    }

    @Override
    public void visit(Integer line, VBranch b) throws Exception {
      boolean positive = b.positive;
      VLabelRef<VCodeLabel> target = b.target;
      VOperand value = b.value;
      String variable = value.toString();
      int idx = getID(variable);

      setLivenessTrue(idx, line);
      //System.err.println(currentBlock.exits+" "+currentBlock.exits.length);
      propagateLiveness(line, line+1, variable);
      if(currentBlock.exits != null) {
        for(BasicBlock exit : currentBlock.exits) {
          if(exit != null)
            propagateLiveness(line, exit.start, variable);
        }
      }
    }

    @Override
    public void visit(Integer line, VBuiltIn builtIn) throws Exception {
      VOperand [] args = builtIn.args;
      VVarRef dest = builtIn.dest;
      VBuiltIn.Op op = builtIn.op;
      String varName = dest!=null? dest.toString() : null;
      switch(op.name) {
      case "Add": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();
        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }

        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "Sub": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();

        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }
        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "MulS": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();

        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }
        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "Eq": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();

        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }
        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "Lt": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();

        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }
        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "LtS": {
        String a = args[0] instanceof VLitInt? null : args[0].toString();
        String b = args[1] instanceof VLitInt? null : args[1].toString();

        if(a != null) {
          int idx = getID(a);
          setLivenessTrue(idx, line);
        }

        if(b != null) {
          int idx = getID(b);
          setLivenessTrue(idx, line);
        }
        propagateLiveness(line, line+1, varName, a, b);
        break;
      }

      case "PrintIntS": {
        if(args[0] instanceof VOperand) {
          String a = args[0].toString();
          int idx = getID(a);
          if(idx != -1) {
            setLivenessTrue(idx, line);
            propagateLiveness(line, line+1, a);
          }
        }

        break;
      }

      case "HeapAllocZ": {
        if(!(args[0] instanceof VLitInt)) {
          String a = args[0].toString();
          int idx = getID(a);
          if(idx != -1) { 
            setLivenessTrue(idx, line);
            propagateLiveness(line, line+1, varName, a);
          }
        } else {
          propagateLiveness(line, line+1, varName);
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
    public void visit(Integer line, VCall c) throws Exception {
      VAddr<VFunction> addr = c.addr;
      VOperand [] args = c.args;
      VVarRef.Local dest = c.dest;
      int min = args.length < 4? args.length : 4;

      this.outVar = Integer.max(outVar, args.length<4? 0 : args.length-4);
      int length = args.length+1;
      String addrName = addr.toString();
      int idx = getID(addrName);
      if(idx != -1) {
        setLivenessTrue(idx, line);
        length = 1+length;
      }
      String [] function = new String[length];// function args length + addr of function
      for(int j=0; j<args.length; j++) {
        String arg = args[j].toString();
        int id = getID(arg);
        if(id != -1) {
          setLivenessTrue(id, line);
          function[j] = arg;
        } else {
          //System.err.printf(" ARGUMENT %s %d\n", arg, line);
        }
      }
      if(idx != -1) {
        int last = length-1;
        function[last] = addrName;
        function[last-1] = dest.toString();
      }
      propagateLiveness(line, line+1, function);
    }

    @Override
    public void visit(Integer line, VGoto g) throws Exception {
      VAddr<VCodeLabel> target = g.target;
      propagateLiveness(line, currentBlock.exits[0].start);
    }

    @Override
    public void visit(Integer line, VMemRead r) throws Exception {
      VVarRef dest = r.dest;
      VMemRef src = r.source;
      if(src instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) src;
        String s = gg.base.toString();
        String d = dest.toString();
        int idx = getID(s);
        setLivenessTrue(idx, line);
        propagateLiveness(line, line+1, s, dest.toString());

      } else if(src instanceof VMemRef.Stack) {
        VMemRef.Stack ss = (VMemRef.Stack) src;
      } 
    }

    @Override
    public void visit(Integer line, VMemWrite w) throws Exception {
      VMemRef dest = w.dest;
      VOperand src = w.source;
      if(dest instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) dest;
        String s = gg.base.toString();
        int idx = getID(s);
        setLivenessTrue(idx, line);
      } else if(dest instanceof VMemRef.Stack) {

      }

      if(src instanceof VLitInt || src instanceof VLabelRef) {
        propagateLiveness(line, line+1, dest.toString());
      } else {
        int idx = getID(src.toString());
        setLivenessTrue(idx, line);
        propagateLiveness(line, line+1, src.toString(), dest.toString());
      }
    }

    @Override
    public void visit(Integer line, VReturn r) throws Exception {
      VOperand value = r.value;
      if(value != null) {
        int idx = getID(value.toString());
        if(idx == -1) return;
        setLivenessTrue(idx, line);
      }
    }

  }

  class InstructionVisitor extends VInstr.Visitor<Exception> {

    public String [] vars = null;
    public VVarRef.Local [] params = null;
    public int [] graphColor = null;
    public int maxColor = 0;

    String mapToRegister(String name) {
      for(int i = 0; i < vars.length; i++) {
        if(name.equals(vars[i])) {
          int index = graphColor[i];
          if(index == -1)
            return "$v1";
          else if(index < 9)
            return "$t"+index;
          else {
            return "$s"+(graphColor[i]-8);
          }
        }
      }
      return name;
    }

    public void handleParams() {

      int maxT = maxColor < 9? maxColor : 8;
      int maxS = maxColor-9;
      for(int i=0; i<=maxT; i++) {
        System.out.printf("  local[%d] = $t%d\n", i, i);
      }
      for(int i=0; i<=maxS; i++) {
        System.out.printf("  local[%d] = $s%d\n", i+8, i);
      }

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
      if(!d.equals(s))
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
      //System.err.printf(" MAX COLORS: %d\n", maxColor);
      // save the registers.

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
      if(!d.equals("$v0"))
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
      // NOTE: Vapor line nums do not correspond to VaporM line nums
      if(src instanceof VMemRef.Global) {
        VMemRef.Global gg = (VMemRef.Global) src;
        String s = mapToRegister(gg.base.toString());
        int offset = gg.byteOffset;
        System.out.printf("  %s = [%s+%d]\n", d, s, offset);
      } else if(src instanceof VMemRef.Stack) {
        ;
      }
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

      System.out.printf("  [%s+%d] = %s\n", d, offset, s);
    }

    @Override
    public void visit(VReturn r) throws Exception {
      VOperand value = r.value;
      if(value != null) {
        String v = (value instanceof VLitInt)? value.toString() : mapToRegister(value.toString());
        System.out.printf("  $v0 = %s\n", v);
      }

      int maxT = maxColor < 9? maxColor : 8;
      int maxS = maxColor-9;
      //load back the registers
      for(int i=0; i<=maxT; i++) {
        System.out.printf("  $t%d = local[%d]\n", i, i);
      }
      for(int i=0; i<=maxS; i++) {
        System.out.printf("  $s%d = local[%d]\n", i, i+8);
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
    for(boolean [] m : matrix) {
      for(boolean b : m) if(b!=false) {System.err.println("BIG FAT ERROR");assert(b == false);}
    }

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




