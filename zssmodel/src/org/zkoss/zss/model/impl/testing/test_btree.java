package org.zkoss.zss.model.impl.testing;

import org.model.BlockStore;
import org.model.DBContext;
import org.model.DBHandler;
import org.model.DBhand;
import org.zkoss.zss.model.impl.BTree;
import org.zkoss.util.logging.Log;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by Stan on 5/30/2017.
 */
public class test_btree  {



    public static void testRootInsDelByCount()throws Exception{

        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());
        String tableName = "testRootInsDelByCount";
        BTree testTree = new BTree(context, tableName);

        testTree.addByCount(context, 0, 10, false);
        int test = testTree.getByCount(context, 1);

        testTree.removeByCount(context, 0);

        testTree.addByCount(context, 0, 20, false);
        //test = testTree.getByCount(context, 0);

        testTree.addByCount(context, 1, 10, false);
        //test = testTree.getByCount(context, 2);
        boolean valid = testTree.validateParentPointers(context);
        assertEquals("valid parent pointers", true, valid);
        testTree.removeByCount(context, 0);
        //test = testTree.getByCount(context, 1);
        testTree.removeByCount(context, 0);


    }

    public static void testRootSplitByCount()throws Exception{
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());
        int value;
        int[] a = {5, 25, 50};
        int[] rootids = {0, 0, 0};
        boolean valid;
        for(int i = 0; i < 3; i++){
            String testName = "testRootSplit"+i;
            BTree testTree = new BTree(context, testName);
            testTree.addByCount(context, 0, 100, false);
            testTree.addByCount(context, 1, 200, false);
            testTree.addByCount(context, 2, 300, false);
            testTree.addByCount(context, 3, 400, false);

            testTree.addByCount(context, i*2, a[i]*10, false);
            valid = testTree.validateParentPointers(context);
            assertEquals("valid parent pointers", true, valid);
        }

    }

    public static void testSplitNodeByCount()throws Exception{
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());
        BTree testTree = new BTree(context, "testSplitNodeByCount");

        testTree.addByCount(context, 0, 50, false);
        testTree.addByCount(context, 1, 100, false);
        testTree.addByCount(context, 2, 200, false);
        testTree.addByCount(context, 3, 250, false);
        testTree.addByCount(context, 4, 300, false);
        testTree.addByCount(context, 5, 400, false);
        testTree.addByCount(context, 6, 500, false);
        testTree.addByCount(context, 7, 600, false);
        testTree.addByCount(context, 8, 700, false);
        testTree.addByCount(context, 9, 800, false);

        testTree.addByCount(context, 0, 30, false);
        testTree.addByCount(context, 3, 150, false);
        testTree.addByCount(context, 5, 230, false);
        testTree.addByCount(context, 7, 270, false);
        testTree.addByCount(context, 9, 350, false);
        testTree.addByCount(context, 11, 450, false);
        testTree.addByCount(context, 13, 550, false);
        testTree.addByCount(context, 16, 800, false);
        boolean valid = testTree.validateParentPointers(context);
        assertEquals("valid parent pointers", true, valid);
    }

    public static void testSplitNodeSplitParentByCount()throws Exception{
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());
        int[] a = {1, 8, 16};
        int[] aa = {3, 8, 17};
        boolean valid;
        for(int i = 0; i < 3; i++){
            BTree testTree = new BTree(context, "testSplitNodeSplitParentByCount"+i);
            testTree.addByCount(context, 0, 50, false);
            testTree.addByCount(context, 1, 100, false);
            testTree.addByCount(context, 2, 200, false);
            testTree.addByCount(context, 3, 250, false);
            testTree.addByCount(context, 4, 300, false);
            testTree.addByCount(context, 5, 400, false);
            testTree.addByCount(context, 6, 500, false);
            testTree.addByCount(context, 7, 600, false);
            testTree.addByCount(context, 8, 700, false);
            testTree.addByCount(context, 9, 800, false);

            testTree.addByCount(context, 0, 30, false);
            testTree.addByCount(context, 3, 150, false);
            testTree.addByCount(context, 5, 230, false);
            testTree.addByCount(context, 7, 270, false);
            testTree.addByCount(context, 9, 350, false);
            testTree.addByCount(context, 11, 450, false);
            testTree.addByCount(context, 13, 550, false);
            testTree.addByCount(context, 16, 800, false);

            testTree.addByCount(context, aa[i], a[i]*10, false);
            valid = testTree.validateParentPointers(context);
            assertEquals("valid parent pointers", true, valid);
        }
    }

    public static void testNodeMergeByCount() throws Exception {
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());


        BTree testTree = new BTree(context, "testNodeMergeByCount");
        testTree.addByCount(context, 0, 100, false);
        testTree.addByCount(context, 1, 200, false);
        testTree.addByCount(context, 2, 300, false);
        testTree.addByCount(context, 3, 400, false);
        testTree.addByCount(context, 4, 500, false);
        testTree.addByCount(context, 5, 600, false);
        testTree.addByCount(context, 6, 700, false);
        testTree.removeByCount(context, 2);
        boolean valid = testTree.validateParentPointers(context);
        assertEquals("valid parent pointers", true, valid);
    }

    public static void NodeMergeRootMergeByCount() throws Exception {
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());


        BTree testTree = new BTree(context, "NodeMergeRootMergeByCount");
        testTree.addByCount(context, 0, 50, false);
        testTree.addByCount(context, 1, 100, false);
        testTree.addByCount(context, 2, 200, false);
        testTree.addByCount(context, 3, 230, false);
        testTree.addByCount(context, 4, 270, false);
        testTree.addByCount(context, 5, 300, false);
        testTree.addByCount(context, 6, 330, false);
        testTree.addByCount(context, 7, 400, false);
        testTree.addByCount(context, 8, 500, false);
        testTree.addByCount(context, 9, 550, false);
        testTree.addByCount(context, 10, 700, false);
        testTree.addByCount(context, 11, 800, false);
        testTree.addByCount(context, 12, 850, false);
        testTree.removeByCount(context, 0);
        boolean valid = testTree.validateParentPointers(context);
        assertEquals("valid parent pointers", true, valid);
    }

    public static void NodeMergeRootMerge1ByCount() throws Exception {
        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());

        // left rotate
        BTree testTree = new BTree(context, "NodeMergeRootMerge1ByCount");
        testTree.addByCount(context, 0, 50, false);
        testTree.addByCount(context, 1, 100, false);
        testTree.addByCount(context, 2, 200, false);
        testTree.addByCount(context, 3, 230, false);
        testTree.addByCount(context, 4, 270, false);
        testTree.addByCount(context, 5, 300, false);
        testTree.addByCount(context, 6, 330, false);
        testTree.addByCount(context, 7, 400, false);
        testTree.addByCount(context, 8, 500, false);
        testTree.addByCount(context, 9, 550, false);
        testTree.addByCount(context, 10, 700, false);
        testTree.addByCount(context, 11, 800, false);
        testTree.addByCount(context, 12, 850, false);
        testTree.addByCount(context, 13, 900, false);
        testTree.addByCount(context, 14, 950, false);
        testTree.addByCount(context, 15, 1000, false);
        testTree.addByCount(context, 16, 1050, false);
        testTree.addByCount(context, 17, 1100, false);
        testTree.addByCount(context, 18, 1150, false);
        testTree.removeByCount(context, 0);
        testTree.removeByCount(context, 13);
        testTree.removeByCount(context, 16);
        boolean valid = testTree.validateParentPointers(context);
        assertEquals("valid parent pointers", true, valid);

    }

    public static void main(String[] args) throws Exception {
        NodeMergeRootMerge1ByCount();

    }
}
