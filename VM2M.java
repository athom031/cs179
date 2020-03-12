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
    CommandLineLauncher.run(new V2VM(), args);
  }

  @Override
  public void run(PrintWriter out, InputStream in, PrintWriter err, String [] args) throws Exit {
    try {
      VaporProgram program = VM2M.parseVapor(in, System.err);
    } catch(IOException e) {
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




}
