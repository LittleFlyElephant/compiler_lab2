package model;

import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raychen on 2016/11/15.
 */
public class LRState {
    public int stateNum;
    public List<LRItem> lrItemGroup;
    public Map<Character, String> actions;
    public Map<Character, Integer> gotos;

    public LRState(){
        this.stateNum = -1;
        this.lrItemGroup = new ArrayList<>();
        this.actions = new HashMap<>();
        this.gotos = new HashMap<>();
    }

    public boolean equalTo(LRState state){
        if (state.lrItemGroup.size() != this.lrItemGroup.size()) return false;
        for (LRItem item: state.lrItemGroup) {
            if (!Util.containLRItem(this.lrItemGroup, item)) return false;
        }
        return true;
    }
}
