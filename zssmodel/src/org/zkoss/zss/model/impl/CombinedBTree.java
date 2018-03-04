package org.zkoss.zss.model.impl;

import org.model.BlockStore;
import org.model.DBContext;
import org.zkoss.zss.model.impl.statistic.AbstractStatistic;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.CountStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;

import java.util.ArrayList;

public class CombinedBTree{
    public BTree<CombinedStatistic> btree;

    public CombinedBTree(DBContext context, String tableName, BlockStore sourceBlockStore) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, sourceBlockStore, emptyStatistic, false);
    }

    public CombinedBTree(DBContext context, String tableName) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, emptyStatistic, true);
        btree.updateMaxValue(context, 0);
    }

    public CombinedBTree(DBContext context, String tableName, boolean useKryo) {
        CombinedStatistic emptyStatistic = new CombinedStatistic(new KeyStatistic(0));
        btree = new BTree<>(context, tableName, emptyStatistic, useKryo);
        btree.updateMaxValue(context, 0);
    }


    public void dropSchema(DBContext context) {
        btree.dropSchema(context);
    }


    public ArrayList getIDs(DBContext context, CombinedStatistic statistic, int count, AbstractStatistic.Type type) {
        return btree.getIDs(context, statistic, count, type);
    }

    public ArrayList getKeys(DBContext context, int start, int count) {

        int jump = count/10;
        ArrayList values=new ArrayList();

        CombinedStatistic statistic = new CombinedStatistic(new KeyStatistic(30), new CountStatistic(start));//-1 to acount for the header which is not inserted
        if(jump == 0) {
            values= btree.getIDs(context, statistic, count, AbstractStatistic.Type.COUNT);
            return values;
        }

        ArrayList keys = new ArrayList();

        for(int i=0;i<10;i++) {
            start += jump*i;
            statistic = new CombinedStatistic(new KeyStatistic(30), new CountStatistic(start));//-1 to acount for the header which is not inserted
            values = btree.getIDs(context, statistic, 1, AbstractStatistic.Type.COUNT);
            keys.add(values.get(0));
        }

        return keys;
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
        btree.insertIDs(context, statistics, ids, AbstractStatistic.Type.KEY);
    }

    public void useKryo(boolean useKryo) {
        btree.useKryo(useKryo);
    }

    public void setBlockSize(int b) {
        btree.setB(b);
    }

}

