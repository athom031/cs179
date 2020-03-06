import cs132.util.CommandLineLauncher;
import cs132.util.CommandLineLauncher.Exit;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.*;
import java.io.*;
import java.util.Arrays;
import java.util.*;

class BasicBlock {

  public int start;
  public int end;
  public int [] entries;
  public int [] exits;

  public BasicBlock() {

  }

  public static BasicBlock [] generateBlocks(VInstr [] body, VCodeLabel [] labels) {
    // count the number of basic blocks...
    ArrayList<BasicBlock> arr = new ArrayList<BasicBlock>();
    

    {
      // 1. 1st Instruction=Leader
      BasicBlock b = new BasicBlock();
      b.start = body[0].sourcePos.line;
      arr.add(b);
    }

    // 2. Target of conditional/unconditional goto/jump is leader
    for(VCodeLabel label : labels) {
      BasicBlock b = new BasicBlock();
      b.start = label.sourcePos.line;
      arr.add(b);
    }

    {
      // 3. Instruction that follows conditional/uncondition goto/jump is a leader
      int l = 0;
      for(VInstr instr : body) {
        if(l < labels.length && instr.sourcePos.line > labels[l].sourcePos.line) {
          l++;
        }

        if(instr instanceof VGoto || instr instanceof VBranch) {
          int i = instr.sourcePos.line + 1;
          if(l >= labels.length || labels[l].sourcePos.line != i) {
            BasicBlock b = new BasicBlock();
            b.start = i;
            arr.add(b);
          } 
        }
      }
    }

    // TODO: ugly hack code.
    BasicBlock [] blocks = new BasicBlock[arr.size()];
    blocks = arr.toArray(blocks);
    for(int xx=0; xx<blocks.length; xx++) {
      blocks[xx].start--;
    }
    for(int xx=1; xx<blocks.length; xx++) {
      blocks[xx-1].end = blocks[xx].start-1;
    }
    blocks[blocks.length-1].end = body[body.length-1].sourcePos.line;
    return blocks;
  }

  public static void printBasicBlocks(BasicBlock [] blocks, VInstr [] instr) {
    System.err.printf("BASIC BLOCK>>>>>> %d \n", blocks.length);
    for(BasicBlock b : blocks) {
      int start = b.start;
      int end = b.end;
      System.err.printf("BASIC BLOCK:------------%d %d\n", start, end);
      for(int i=start; i<=end; i++) {
        System.err.println(instr);
      }
    }
  }

  public static boolean search(int [] array, int y) {
    for(int a : array) {
      if(a==y) return true;
    }
    return false;
  }

}




