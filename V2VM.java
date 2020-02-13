import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.*;
import java.io.*;

class V2VM extends CommandLineLauncher.TextOutput {


  public static void main(String[] args) {
    CommandLineLauncher.run(new V2VM(), args);

  }

  public void run(PrintWriter out, InputStream in, PrintWriter err, String [] args) throws Exit {
    try {
      VaporProgram p = V2VM.parseVapor(in, System.err);
      for(VFunction f : p.functions) {
        System.err.println(f);
      }
    } catch(IOException e) {
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


}




