package org.zkoss.zss.model.impl;

import org.model.AutoRollbackConnection;
import org.model.DBContext;
import org.model.DBHandler;
import org.zkoss.zss.model.CellRegion;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;
import org.zkoss.zss.model.impl.sys.navigation.Bucket;
import org.zkoss.zss.model.impl.sys.navigation.NavigationStructure;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public abstract class Model {
    public String tableName;
    protected SSheet sheet;
    public ArrayList<Bucket<String>> navSbuckets;
    public NavigationStructure navS;
    private String orderString;
    public HashMap<Integer,Integer> trueOrder;

    public static Model CreateModel(DBContext context, SSheet sheet, ModelType modelType, String tableName) {
        Model model = null;
        switch (modelType) {
            case RCV_Model:
                model = new RCV_Model(context, sheet, tableName);
                break;
            case ROM_Model:
                model = new ROM_Model(context, sheet, tableName);
                break;
            case COM_Model:
                model = new COM_Model(context, sheet, tableName);
                break;
            case HYBRID_Model:
                model = new Hybrid_Model(context, sheet, tableName);
                break;
            case TOM_Model:
                /* One tom instance for a table */
                model = TOM_Mapping.instance.createTOMModel(context, tableName);
                // model =  new TOM_Model(context, sheet, tableName);
                break;
        }
        model.sheet = sheet;
        model.orderString = null;
        return model;
    }

    public abstract ArrayList<Bucket<String>> createNavS(String bucketName, int start, int count);
    // Drop the tables created.
    public abstract void dropSchema(DBContext context);


    // Schema is created by the constructor, if it does not exists.

    //Insert count empty rows starting at row(inclusive)
    public abstract void insertRows(DBContext context, int row, int count);

    // For all the functions below, use logical row/column number

    //Insert count empty columns starting at col(inclusive)
    public abstract void insertCols(DBContext context, int col, int count);

    //Delete count rows starting from row(inclusive)
    public abstract void deleteRows(DBContext context, int row, int count);

    //Delete count columns starting from col(inclusive)
    public abstract void deleteCols(DBContext context, int col, int count);

    //Update a range of cells -- Cells should exist in the sheet
    public abstract void updateCells(DBContext context, Collection<AbstractCellAdv> cells);

    //Delete cells
    public abstract void deleteCells(DBContext context, CellRegion cellRegion);

    public abstract void deleteCells(DBContext context, Collection<AbstractCellAdv> cells);

    public abstract boolean deleteTableRows(DBContext context, CellRegion cellRegion);

    //Get a range of cells
    public abstract Collection<AbstractCellAdv> getCells(DBContext context, CellRegion cellRegion);

    // Get all Cells
    public Collection<AbstractCellAdv> getCells(DBContext context) {
        return getCells(context, getBounds(context));
    }

    // Get size of sheet
    public abstract CellRegion getBounds(DBContext context);

    // Flush Cache and clearCache DB connection
    public abstract void clearCache(DBContext context);

    public String getTableName() {
        return tableName;
    }

    // Import a sheet from a inputStream
    // Gets a connection from handler and commits.
    public abstract void importSheet(Reader reader, char delimiter, boolean useNav) throws IOException;

    public abstract boolean deleteTableColumns(DBContext dbContext, CellRegion cellRegion);

    // Clone only the corresponding tables in postgres
    public abstract Model clone(DBContext dbContext, SSheet sheet, String modelName);

    public abstract ROM_Model getROM_Model();

    public abstract ArrayList<Bucket<String>> createNavS(SSheet currentsheet, int start, int count);

    public abstract ArrayList<String> getHeaders();
    public void setOrderString(String str) {
        this.orderString = str;
    }
    public String getOrderString(){return  this.orderString;};

    // If the order does not exist create a new order.
    public CombinedBTree getOrder() {
        String readTable ="SELECT orderTable FROM NavigationOrders WHERE dataTable=? AND orderName=?";
        String bTreeName=null;

        try (AutoRollbackConnection connection = DBHandler.instance.getConnection();
             PreparedStatement stmt = connection.prepareStatement(readTable)) {
            stmt.setString(1, tableName);
            stmt.setString(2, orderString);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                bTreeName = rs.getString(1);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bTreeName==null)
        {
            AutoRollbackConnection connection = DBHandler.instance.getConnection();
            DBContext dbContext = new DBContext(connection);
            bTreeName = tableName + "_nav_" + orderString;
            insertNewOrder(tableName, orderString, bTreeName);
            CombinedBTree combinedBTree = new CombinedBTree(dbContext, bTreeName);
            // Start Thread.
            CompletableFuture.runAsync(() -> populateOrder(dbContext,tableName, orderString, combinedBTree));
            return combinedBTree;
        }
        else {
            AutoRollbackConnection connection = DBHandler.instance.getConnection();
            DBContext dbContext = new DBContext(connection);
            return  new CombinedBTree(dbContext, bTreeName);
        }
    }


    private void populateOrder(DBContext context,String tableName, String orderString, CombinedBTree combinedBTree)
    {
        String readTable ="SELECT row," + orderString + " FROM " + tableName+ " WHERE row!=1"; //ignore header row
        try (AutoRollbackConnection connection = DBHandler.instance.getConnection();
             PreparedStatement stmt = connection.prepareStatement(readTable)) {
            ResultSet rs = stmt.executeQuery();
            int batchSize = 1000;// insert every 1k elements in BTree
            // TODO: Insert records here.
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<CombinedStatistic> statistics = new ArrayList<>();

            int count = 0;
            int row = 0; //row number of record
            while (rs.next()) {

                count++;

                row = rs.getInt(1);
                ids.add(row);
                statistics.add(new CombinedStatistic(new KeyStatistic(new String(rs.getBytes(2),"UTF-8"))));

                if(count%batchSize==0) {
                    combinedBTree.insertIDs(context,statistics,ids);
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void insertNewOrder(String dataTable, String orderName, String orderTable) {
        String insertTable = "INSERT INTO NavigationOrders(dataTable, orderName, orderTable) VALUES (?, ?, ?)";
        try (AutoRollbackConnection connection = DBHandler.instance.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertTable)) {
            stmt.setString(1, dataTable);
            stmt.setString(2, orderName);
            stmt.setString(3, orderTable);
            stmt.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public enum ModelType {
        ROM_Model, COM_Model, RCV_Model, HYBRID_Model, TOM_Model
    }
}