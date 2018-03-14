package org.zkoss.zss.model.impl;

import org.apache.xmlbeans.impl.piccolo.util.IndexedObject;
import org.model.BlockStore;
import org.model.DBContext;
import org.zkoss.zss.model.impl.statistic.AbstractStatistic;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.CountStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;

import java.util.ArrayList;

public class CombinedBTree{
    public BTree<CombinedStatistic> btree;
    private int treesize;

    public CombinedBTree(DBContext context, String tableName, BlockStore sourceBlockStore) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, sourceBlockStore, emptyStatistic, false);
        treesize=0;
    }

    public CombinedBTree(DBContext context, String tableName) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, emptyStatistic, true);
        btree.updateMaxValue(context, 0);
        treesize=0;
    }

    public CombinedBTree(DBContext context, String tableName, boolean useKryo) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, emptyStatistic, useKryo);
        btree.updateMaxValue(context, 0);
        treesize=0;
    }


    public void dropSchema(DBContext context) {
        btree.dropSchema(context);
    }


    public ArrayList getIDs(DBContext context, CombinedStatistic statistic, int count, AbstractStatistic.Type type) {
        return btree.getIDs(context, statistic, count, type);
    }

    public ArrayList<Integer> getKeys(DBContext context, int start, int end) {

        ArrayList<Integer> keys = new ArrayList<Integer>();

        CombinedStatistic statistic = new CombinedStatistic(new KeyStatistic(30), new CountStatistic(start));//-1 to acount for the header which is not inserted
        keys= btree.getIDs(context, statistic, end-start+1, AbstractStatistic.Type.COUNT);
        return keys;

    }

    public int getKey(DBContext context, int start) {

        ArrayList<Integer> keys = new ArrayList<Integer>();

        CombinedStatistic statistic = new CombinedStatistic(new KeyStatistic(30), new CountStatistic(start));//-1 to acount for the header which is not inserted
        keys= btree.getIDs(context, statistic, 1, AbstractStatistic.Type.COUNT);
        return keys.get(0);

    }


    public ArrayList deleteIDs(DBContext context, ArrayList<CombinedStatistic> statistics, AbstractStatistic.Type type) {
        return btree.deleteIDs(context, statistics, type);
    }


    public void clearCache(DBContext context) {
        btree.clearCache(context);
    }


    public PosMapping clone(DBContext context, String tableName) {
        return new CountedBTree(context, tableName, btree.bs);
    }


    public int size(DBContext context) {
        return btree.size(context);
    }


    public String getTableName() {
        return btree.getTableName();
    }


    public void insertIDs(DBContext context, ArrayList<CombinedStatistic> statistics, ArrayList ids) {
        this.treesize += ids.size();

        btree.insertIDs(context, statistics, ids, AbstractStatistic.Type.KEY);
    }

    public void useKryo(boolean useKryo) {
        btree.useKryo(useKryo);
    }

    public void setBlockSize(int b) {
        btree.setB(b);
    }

    public int getSize() {
        return this.treesize;
    }
}

