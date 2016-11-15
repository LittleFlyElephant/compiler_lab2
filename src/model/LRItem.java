package model;

/**
 * Created by raychen on 2016/11/15.
 */
public class LRItem {
    public char left;
    public String right;
    public int dotNum;
    public char tag;

    public LRItem(char left, String right, int dotNum, char tag){
        this.left = left;
        this.right = right;
        this.dotNum = dotNum;
        this.tag = tag;
    }

    public boolean equalTo(LRItem item){
        if (this.left != item.left ||
                this.right != item.right ||
                this.dotNum != item.dotNum ||
                this.tag !=item.tag) return false;
        return true;
    }
}
