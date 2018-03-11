package org.zkoss.zss.app.ui;

import org.zkoss.zss.model.impl.CombinedBTree;
import org.zkoss.zss.model.impl.KeyIndexMap;
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

    SpreadsheetBean()
    {

    }

    // constructor
    public SpreadsheetBean(Model model,T startVal, T endVal, int startPos, int endPos) {

        this.model = model;
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
        ArrayList<KeyIndexMap> kim = model.getIDs(startPos,endPos);
        int jump = (endPos-startPos+1)/10;
        T startV,endV;
        int startP,endP;

        for(int i=0;i<kim.size();i++)
        {
            startV = (T) kim.get(i).getKey();
            startP = kim.get(i).getPos();

            endV = (T) kim.get(i).getKey();
            endP = kim.get(i).getPos();


            _children.add(new SpreadsheetBean<T>(model,startV,endV,startP,endP));
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
