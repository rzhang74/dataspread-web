package org.zkoss.zss.model.impl.sys.navigation;

import org.model.AutoRollbackConnection;
import org.model.DBContext;
import org.model.DBHandler;
import org.zkoss.zss.model.CellRegion;
import org.zkoss.zss.model.SCell;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.model.impl.*;
import org.zkoss.zss.model.impl.statistic.AbstractStatistic;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.CountStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;
import org.zkoss.zss.model.sys.BookBindings;
import org.zkoss.zss.model.sys.formula.DirtyManager;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Execute formulae in a single thread.
 */
public class NavigationTask extends Thread {
    private static final Logger logger = Logger.getLogger(NavigationTask.class.getName());
    private boolean keepRunning = true;
    SheetImpl sheet;
    public NavigationTask(SheetImpl currentSheet)
    {
        this.sheet = currentSheet;
    }

    @Override
    public void run() {
        while (keepRunning) {
            //TODO: code for creating navigation structure
            ArrayList<String> recordList =  new ArrayList<String>();


            StringBuffer select = null;


            //for new sheet with no navS
            if(sheet.getDataModel().getOrderString()==null)
            {
                //Todo:write new list in a table
                ArrayList<Bucket<String>> newList = sheet.getDataModel().navS.getUniformBuckets(0,sheet.getEndRowIndex());
            }

            ROM_Model rom_model = this.sheet.getDataModel().getROM_Model();

            int columnIndex = Integer.parseInt(sheet.getDataModel().getOrderString().split("_")[1])-1;


            //TODO: do we get cells from sheet or DB?
            CellRegion tableRegion =  new CellRegion(1, columnIndex,//100000,20);
                    sheet.getEndRowIndex(),columnIndex);

            sheet.clearCache();
            ArrayList<SCell> result = (ArrayList<SCell>) sheet.getCells(tableRegion);

            try (AutoRollbackConnection connection = DBHandler.instance.getConnection()) {
                DBContext context = new DBContext(connection);

                ArrayList<Integer> rowIds=null;

                if(rom_model.rowOrderTable.keySet().isEmpty()) {
                    rowIds = rom_model.rowMapping.getIDs(context, tableRegion.getRow(), tableRegion.getLastRow() - tableRegion.getRow() + 1);
                }
                else
                {
                    CombinedStatistic startRow = new CombinedStatistic(new KeyStatistic(30), new CountStatistic(tableRegion.getRow()-1));//-1 to acount for the header which is not inserted
                    rowIds = rom_model.rowOrderTable.get(sheet.getDataModel().getROM_Model().getOrderString()).getIDs(context, startRow, tableRegion.getLastRow() - tableRegion.getRow() + 1, AbstractStatistic.Type.COUNT);
                }

                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<CombinedStatistic> statistics = new ArrayList<>();

                for(int i = 0; i < rowIds.size(); i++) {
                    ids.add(rowIds.get(i));
                    statistics.add(new CombinedStatistic(new KeyStatistic(result.get(i).getStringValue())));
                    recordList.add(result.get(i).getStringValue());
                }


                if(rom_model.rowOrderTable.containsKey(sheet.getDataModel().getOrderString()))
                    rom_model.rowCombinedTree = rom_model.rowOrderTable.get(sheet.getDataModel().getOrderString());
                else{
                    CombinedBTree newOrder = new CombinedBTree(context, sheet.getDataModel().tableName + "_row_com_"+sheet.getDataModel().getOrderString()+"_idx");
                    newOrder.insertIDs(context,statistics,ids);

                    rom_model.rowOrderTable.put(sheet.getDataModel().getOrderString(),newOrder);

                    rom_model.rowCombinedTree = newOrder;

                    sheet.getDataModel().getROM_Model().setOrderString(sheet.getDataModel().getOrderString());


                }
                connection.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }




            //create nav data structure
            Collections.sort(recordList);//TODO: replace by BTree getKeys() function
            sheet.getDataModel().navS.setRecordList(recordList);

            //Todo:write new list in a table
            ArrayList<Bucket<String>> newList = sheet.getDataModel().navS.getNonOverlappingBuckets(0,recordList.size()-1);//getBucketsNoOverlap(0,recordList.size()-1,true);

        }
    }

    public void shutdown() {
        keepRunning = false;
    }
}
