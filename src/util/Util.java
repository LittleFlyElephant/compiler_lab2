package util;

import model.LRItem;
import model.LRState;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by raychen on 2016/11/15.
 */
public class Util {

    public static List<String> getCFGs(String filePath){
        List<String> ret = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                ret.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean isNTerminal(char c){
        if (c>='A' && c<='Z') return true;
        return false;
    }

    public static boolean containLRItem(List<LRItem> group, LRItem item){
        for (LRItem test: group) {
            if (test.equalTo(item)) return true;
        }
        return false;
    }

    public static int getLRStateNum(List<LRState> states, LRState state){
        int ret = -1;
        for (LRState sta: states) {
            if (sta.equalTo(state)) return sta.stateNum;
        }
        return ret;
    }

    public static int getLRItemNum(List<LRItem> starts, LRItem item){
        for (int i = 0; i < starts.size(); i++) {
            LRItem it = starts.get(i);
            if (it.left == item.left && it.right == item.right) return i;
        }
        return -1;
    }

    public static void printItem(LRItem item){
        System.out.print(item.left+"->"+item.right.substring(0,item.dotNum)+'.'+item.right.substring(item.dotNum,item.right.length()));
        System.out.println(","+item.tag);
    }

    public static void printState(LRState state){
        System.out.println("state number: "+ state.stateNum);
        for (LRItem item: state.lrItemGroup) {
            printItem(item);
        }
    }

    public static int getPriority(char c){
        switch (c){
            case 'i':
                return 1;
            case 'e':
                return 2;
            case ';':
                return 0;
        }
        return -1;
    }

    public static void main(String[] args) {
//        List<String> cfgs = Util.getCFG("src/cfg.txt");
//        System.out.println(cfgs.get(0));
        String s1 = "123";
        String s2 = "123";
//        System.out.println(s2.substring(0,1)+'.'+s2.substring(1,s2.length()));
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        set1.add(1);set2.add(2);set2.add(4);
        set1.addAll(set2);
//        System.out.println(set1.size());
        Stack<Integer> st1 = new Stack<>();
        st1.push(5);
        System.out.println(st1.peek());
        System.out.println(st1.peek());
    }
}
