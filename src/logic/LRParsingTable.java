package logic;

import model.LRItem;
import model.LRState;
import util.Util;

import java.util.*;

/**
 * Created by raychen on 2016/11/15.
 */
public class LRParsingTable {
    public List<LRState> parsingTable;
    public List<LRItem> startItems;

    public LRParsingTable(){
        parsingTable = new ArrayList<>();
        startItems = new ArrayList<>();
    }

    //增加s'->s
    public void buildStartItems(String filePath){
        List<String> cfgs = Util.getCFGs(filePath);
        boolean start = true;
        for (String row: cfgs) {
            String sp1[] = row.split("->");
            char left = sp1[0].charAt(0);
            String right = sp1[1];
            LRItem item = new LRItem(left, right, 0, '/');
            if (start){
                LRItem startItem = new LRItem('#', left+"", 0, '$');
                startItems.add(startItem);
                start = false;
            }
            startItems.add(item);
        }
    }

    //求first
    private Set<Character> first(char c){
        Set<Character> ret = new HashSet<>();
        if (!Util.isNTerminal(c)){
            ret.add(c);
            return ret;
        }else {
            for (LRItem item: startItems) {
                if (item.left == c){
                    int i = 0;
                    //防止死循环
                    if (item.right.charAt(i) != c){
                        Set<Character> f = first(item.right.charAt(i));
                        ret.addAll(f);
                        i ++;
                        while (f.contains('@') && i<item.right.length()){
                            ret.remove('@');
                            if (item.right.charAt(i) == c) break;
                            f = first(item.right.charAt(i));
                            ret.addAll(f);
                            i++;
                        }
                        if (f.contains('@')) ret.add('@');
                    }
                }
            }
        }
        return ret;
    }

    //求follow
    private Set<Character> follow(char c){
        Set<Character> ret = new HashSet<>();
        //计算follow(T)
        if (c == '#') ret.add('$');
        for (LRItem item: startItems) {
            for (int i = 0; i < item.right.length(); i++) {
                char itemc = item.right.charAt(i);
                if (itemc == c){
                    if (i == item.right.length()-1) {
                        if (item.left != c) ret.addAll(follow(item.left));
                    } else {
                        Set<Character> f = first(item.right.charAt(i+1));
                        if (f.contains('@')) {
                            f.remove('@');
                            if (item.left != c) ret.addAll(follow(item.left));
                        }
                        ret.addAll(f);
                    }
                }
            }
        }
        return ret;
    }

    //求LR(1)状态内扩展
    private void closure(List<LRItem> group){
        int head = 0;
        int rear = group.size()-1;
        while (head <= rear){
            LRItem item = group.get(head);
            //非规约项
            if (item.dotNum < item.right.length()){
                char testLeft = item.right.charAt(item.dotNum);
                if (!Util.isNTerminal(testLeft)) {
                    head++;
                    continue;
                }
                //求预测位
                Set<Character> tags = new HashSet<>();
                if (item.dotNum == item.right.length()-1) tags.add(item.tag);
                else tags.addAll(first(item.right.charAt(item.dotNum+1)));
                if (tags.contains('@')){
                    tags.remove('@');
                    tags.add(item.tag);
                }
                for (LRItem testItem: startItems) {
                    //如果有符合项,则插入集合
                    if (testItem.left == testLeft) {
                        for (Character tag: tags) {
                            LRItem it = new LRItem(testItem.left, testItem.right, testItem.dotNum, tag);
                            if (!Util.containLRItem(group, it)){
                                group.add(it);
                                rear ++;
                            }
                        }
                    }
                }
            }
            head++;
        }
    }

    //LR(1)状态间扩展
    private LRState getGoto(LRState state, char sign){
        LRState newState = new LRState();
        for (LRItem item: state.lrItemGroup) {
            if (item.dotNum >= item.right.length()) continue;
            if (item.right.charAt(item.dotNum) == sign){
                LRItem newItem = new LRItem(item.left, item.right, item.dotNum+1, item.tag);
                newState.lrItemGroup.add(newItem);
            }
        }
        closure(newState.lrItemGroup);
        return newState;
    }

    //构建所有状态,以及相应的分析表
    public void buildAllStates(){
        LRState startState = new LRState();
        //构造初始状态
        startState.stateNum = 0;
        startState.lrItemGroup.add(startItems.get(0));
        closure(startState.lrItemGroup);
        parsingTable.add(startState);
        //构造所有状态
        int head =0, rear = 0;
        while (head <= rear){
            LRState state = parsingTable.get(head);
            //记录使用过的符号
            Set<Character> usedSigns = new HashSet<>();
            for (LRItem item: state.lrItemGroup) {
                if (item.dotNum >= item.right.length()){
                    //规约
                    int num = Util.getLRItemNum(startItems, item);
                    if (state.actions.get(item.tag) != null) {
                        //冲突
                        char symbol = item.right.charAt(item.dotNum-2);
                        if (Util.getPriority(symbol) >= Util.getPriority(item.tag))
                            state.actions.put(item.tag, "R"+num);
                    } else
                        state.actions.put(item.tag, "R"+num);
                    continue;
                }
                Character sign = item.right.charAt(item.dotNum);
                if (!usedSigns.contains(sign)){
                    LRState newState = getGoto(state, sign);
                    int index = Util.getLRStateNum(parsingTable, newState);
                    if (index == -1){
                        //新建状态
                        newState.stateNum = parsingTable.size();
                        parsingTable.add(newState);
                        rear ++;
                        index = newState.stateNum;
                    }
                    //移入
                    if (Util.isNTerminal(sign)){
                        state.gotos.put(sign, index);
                    } else {
                        if (state.actions.get(sign) != null) {
                            //转移-归约冲突
                            LRItem confItem = startItems.get(Integer.valueOf(state.actions.get(sign).substring(1)));
                            char symbol = confItem.right.charAt(confItem.right.length()-2);
                            if (Util.getPriority(symbol) < Util.getPriority(sign))
                                state.actions.put(sign, "S"+index);
                        } else
                            state.actions.put(sign, "S"+index);
                    }
                    usedSigns.add(sign);
                }
            }
            head ++;
        }
    }

    public void printToTest(){
        for (LRItem pitem: startItems) {
            Util.printItem(pitem);
            for (Character pc: follow(pitem.left)) {
                System.out.print(pc+" ");
            }
            System.out.println();
            for (Character pc: first(pitem.left)) {
                System.out.print(pc+" ");
            }
            System.out.println();
        }
        for (LRState pstate: parsingTable) {
            Util.printState(pstate);
            for (Character key: pstate.actions.keySet()) {
                System.out.println(key+":"+pstate.actions.get(key));
            }
            for (Character key: pstate.gotos.keySet()) {
                System.out.println(key+":"+pstate.gotos.get(key));
            }
        }
    }

    public static void main(String[] args) {
        LRParsingTable parsingTable = new LRParsingTable();
        parsingTable.buildStartItems("src/cfg.txt");
        parsingTable.buildAllStates();
        parsingTable.printToTest();
    }
}
