package org.zkoss.zss.app.ui;

import org.zkoss.zss.model.impl.CombinedBTree;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetBean<T> extends RODTreeNodeData {
    private T minValue;
    private T maxValue;
    private int startPos;
    private int endPos;
    private int size;
    private int childrenCount;
    private String name;
    private String id;
    private String summary;

    private ArrayList<SpreadsheetBean<T>> _children;

    SpreadsheetBean()
    {

    }

    // constructor
    public SpreadsheetBean(T startVal, T endVal, int startPos, int endPos) {
        minValue = startVal;

        maxValue = endVal;
        childrenCount = 10;
        size = 10;
        this.startPos = startPos;
        this.endPos = endPos;

        this.setName(false);
        this.setId();
        this.setSummary();
    }

    private void setSummary() {
        summary = this.getName()+"\n";
        summary += "Sub-categories: " + this.childrenCount+"\n";
        summary += "[Start,End]: ["+(this.startPos)+","+(this.endPos)+"]\n";
        summary += "Rows: "+this.size;
    }

    private void setId() {
        this.id = this.name.replaceAll(" ","_");
    }

    @Override
    public String toString() {
        if (minValue==null || maxValue==null)
            return null;
        return minValue.toString().equals(maxValue.toString())?minValue.toString():minValue.toString()+" - "+maxValue.toString();
    }

    public void setName(boolean isUniform)
    {
        if(isUniform)
            name = (this.startPos)+"_"+(this.endPos);
        else
            name = this.toString();
    }

    // getter, setter
    public String getName () {
        if(name.contains("_"))
            return "Rows:"+name.replaceAll("_","-");
        return name;
    }
    public String getId () {
        return this.id;
    }
    public String getSummary () {
        return this.summary;
    }
    /**
     * implement {@link RODTreeNodeData#getChildren()}
     */
    public List<SpreadsheetBean<T>> getChildren() {
        if (_children == null) {
            _children = new ArrayList<SpreadsheetBean<T>>();

        }

        //TODO:generate children

        return _children;
    }
    /**
     * implement {@link RODTreeNodeData#getChildCount()}
     */
    public int getChildCount () {

        return childrenCount;
    }
}
