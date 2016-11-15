package logic;

import model.LRItem;
import model.LRState;
import util.Util;

import java.util.List;
import java.util.Stack;

/**
 * Created by raychen on 2016/11/15.
 */
public class Parser {
    private void printStack(Stack stack, boolean space){
        String sp = space?" ":"";
        for (Object v: stack) {
            System.out.print(v+sp);
        }
    }

    public void parse(List<LRState> parsingTable, List<LRItem> cfgs, String seq) {
        seq += "$";
        Stack<Integer> stateStack = new Stack<>();
        Stack<Character> signStack = new Stack<>();
        int readTop = 0;
        stateStack.push(0);
        int count = 1;
        boolean success = false;
        while (readTop < seq.length() && !success) {
            //输出目前栈状态
            System.out.print(count+". 状态栈: ");
            printStack(stateStack, true);
            System.out.print("符号栈: ");
            printStack(signStack, false);
            System.out.print(" 输入: ");
            System.out.print(seq.substring(readTop)+" 动作: ");
            int stateNum = stateStack.peek();
            LRState state = parsingTable.get(stateNum);
            //输入只可能是终结符
            String action = state.actions.get(seq.charAt(readTop));
            switch (action.charAt(0)) {
                case 'S':
                    System.out.println(" 移入");
                    int nextState = Integer.valueOf(action.substring(1));
                    stateStack.push(nextState);
                    signStack.push(seq.charAt(readTop));
                    readTop++;
                    break;
                case 'R':
                    int reduceNum = Integer.valueOf(action.substring(1));
                    LRItem item = cfgs.get(reduceNum);
                    System.out.println(" 规约: "+item.left+"->"+item.right);
                    //成功
                    if (reduceNum == 0) {
                        System.out.println("success!");
                        success = true;
                        break;
                    }
                    int p = item.right.length();
                    while (p > 0 && signStack.peek() == item.right.charAt(p - 1)) {
                        stateStack.pop();
                        signStack.pop();
                        p--;
                    }
                    signStack.push(item.left);
                    LRState stateNow = parsingTable.get(stateStack.peek());
                    stateStack.push(stateNow.gotos.get(item.left));
                    break;
                default:
                    System.err.println("error when parsing!");
                    break;
            }
            count++;
        }
    }

    public static void main(String[] args) {
        LRParsingTable parsingTable = new LRParsingTable();
        Parser parser = new Parser();
        parsingTable.buildStartItems("src/cfg.txt");
        parsingTable.buildAllStates();
        parsingTable.printToTest();
        parser.parse(parsingTable.parsingTable, parsingTable.startItems, "i+i*i");
    }
}
