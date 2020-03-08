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
  public ArrayList<BasicBlock> entries = new ArrayList<BasicBlock>();
  public BasicBlock [] exits;

  public BasicBlock() {

  }

  public static BasicBlock [] generateBlocks(VInstr [] body, VCodeLabel [] labels) {
    HashMap<Integer, VInstr> mapInstr = new HashMap<Integer, VInstr>();
    HashMap<Integer, VCodeLabel> mapLabel = new HashMap<Integer, VCodeLabel>();
    for(VInstr i : body) {
      mapInstr.put(i.sourcePos.line, i);
    }
    for(VCodeLabel l : labels) {
      mapLabel.put(l.sourcePos.line, l);
    }

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
            b.end = i;
            arr.add(b);
          } 
        }
      }
      // sort just in case basic blocks end up out of order
      Collections.sort(arr, (a, b) -> Integer.compare(a.start, b.start));
    }

    // TODO: ugly hack code.
    BasicBlock [] blocks = new BasicBlock[arr.size()];
    blocks = arr.toArray(blocks);
    for(int xx=1; xx<blocks.length; xx++) {
      blocks[xx-1].end = blocks[xx].start-1;
    }

    for(int xx=0; xx<blocks.length; xx++) {
      int end = blocks[xx].end;
      //@ Hacky Hacky McHack Hack
      //
      VInstr instr = mapInstr.get(end);
      if(instr == null) instr = mapInstr.get(end+1);
      //if(instr == null) instr = mapInstr.get(end-1);
      if(instr instanceof VGoto) {
        VGoto g = (VGoto) instr;
        VAddr<VCodeLabel> target = g.target;
        int line = -1;
        if(target instanceof VAddr.Label) {
          VAddr.Label l = (VAddr.Label) target;
          //line = l.label.sourcePos.line;
          for(VCodeLabel lab : labels) {
            if(l.label.ident.equals(lab.ident)) {
              line = lab.sourcePos.line;
            }
          }
          
        } else if(target instanceof VAddr.Var) {
          VAddr.Var l = (VAddr.Var) target;
          assert(false);//not handled
        }

        blocks[xx].exits = new BasicBlock[1];
        System.err.printf(" LINE %d\n", line);
        BasicBlock gotoBlock = search(blocks, line);
        blocks[xx].exits[0] = gotoBlock; //instruction following branch
        gotoBlock.entries.add(blocks[xx]);

      } else if (instr instanceof VBranch) {
        VBranch b = (VBranch) instr;
        boolean positive = b.positive;
        VLabelRef<VCodeLabel> target = b.target;
        VOperand value = b.value;
        int line = -1;//target.sourcePos.line;
        for(VCodeLabel lab : labels) {
          if(target.ident.equals(lab.ident)) {
            line = lab.sourcePos.line;
            break;
          }
        }

        blocks[xx].exits = new BasicBlock[2];
        BasicBlock followingBlock = blocks[xx+1];
        BasicBlock gotoBlock = search(blocks, line);
        blocks[xx].exits[0] = followingBlock; //instruction following branch
        blocks[xx].exits[1] = gotoBlock;
        followingBlock.entries.add(blocks[xx]);
        gotoBlock.entries.add(blocks[xx]);
      } else if(instr instanceof VBuiltIn  &&   ((VBuiltIn)instr).op.name=="Error"   ) {
        // block is an error.
        blocks[xx].exits = null;
      } else if(xx+1 < blocks.length) {
        BasicBlock next = blocks[xx+1];
        next.entries.add(blocks[xx]);
        blocks[xx].exits = new BasicBlock[1];
        blocks[xx].exits[0] = next;
      }
    }

    blocks[blocks.length-1].end = body[body.length-1].sourcePos.line;
    return blocks;
  }

  public static BasicBlock search(BasicBlock [] blocks, int line) {
    for(BasicBlock b : blocks) {
      int start = b.start;
      if(start == line) return b;
    }
    System.err.printf(" SHIT %d\n", line);
    assert(false);
    return null;
  }

  public static void printBasicBlocks(BasicBlock [] blocks, VInstr [] instr) {
    System.err.printf("BASIC BLOCK>>>>>> %d \n", blocks.length);
    for(BasicBlock b : blocks) {
      int start = b.start;
      int end = b.end;
      System.err.printf("BASIC BLOCK:------------%d %d. ENTRIES: %d, EXITS: %d\n", start, end, b.entries.size(), b.exits != null? b.exits.length : null);
      for(BasicBlock e : b.entries) {
        System.err.printf(" >>>ENTRIES: %d-%d\n", e.start, e.end);
      }
      if(b.exits != null) {
        for(BasicBlock e : b.exits) {
          System.err.printf(" <<<EXITS: %d-%d\n", e.start, e.end);
        }
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




