package org.zkoss.zss.app.ui;

import org.zkoss.zss.model.impl.CombinedBTree;
import org.zkoss.zss.model.impl.Model;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetBean<T> extends RODTreeNodeData {
    private T minValue;
    private T maxValue;
    private int startPos;
    private int endPos;
    private String name;
    private String id;
    private String summary;

    private ArrayList<SpreadsheetBean<T>> _children;
    private Model model;
    private CombinedBTree combinedBTree;

    SpreadsheetBean()
    {

    }

    // constructor
    public SpreadsheetBean(Model model,CombinedBTree combinedBTree,T startVal, T endVal, int startPos, int endPos) {

        this.model = model;
        this.combinedBTree = combinedBTree;
        minValue = startVal;
        maxValue = endVal;
        this.startPos = startPos;
        this.endPos = endPos;

        this.setName(false);
        this.setId();
        this.setSummary();

    }

    private void setSummary() {
        summary = this.getName()+"\n";
        summary += "Sub-categories: " + getChildCount()+"\n";
        summary += "[Start,End]: ["+(this.startPos)+","+(this.endPos)+"]\n";
        summary += "Rows: "+getChildCount();
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

        //TODO:generate children using model and combinedBTree
        ArrayList<Integer> rowIDs = model.getIDs(combinedBTree,startPos,endPos);

        T startV,endV;
        int startP,endP;

        for(int i=0;i<rowIDs.size();i++)
        {
            startV = (T) model.getValue(rowIDs.get(i));
            startP = rowIDs.get(i);


            if(i==rowIDs.size()-1) {
                endV = (T) model.getValue(endPos);
                endP = endPos;
            }
            else
            {
                if((rowIDs.get(i+1)-rowIDs.get(i))==1) {
                    endV = (T) (model.getValue(rowIDs.get(i)));
                    endP = rowIDs.get(i);
                }
                else
                {
                    endV = (T) model.getValue(rowIDs.get(i+1)-1);
                    endP = rowIDs.get(i+1)-1;
                }
            }

            _children.add(new SpreadsheetBean<T>(model,combinedBTree,startV,endV,startP,endP));
        }


        return _children;
    }

    /**
     * implement {@link RODTreeNodeData#getChildCount()}
     */
    public int getChildCount () {

        if (_children == null)
            return 10;

        return _children.size();
    }
}
