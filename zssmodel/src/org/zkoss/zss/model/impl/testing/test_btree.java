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
/**
 * Created by Stan on 5/30/2017.
 */
public class test_btree  {
    public static void main(String []args){

        DBhand connection = new DBhand();
        connection.connectDB();
        DBContext context = new DBContext(connection.getConnection());
        String tableName = "test2";
        BTree testTree = new BTree(context, tableName);

        Integer [] ints = testTree.getIDs(context, 0, 10);
        Integer lo = testTree.createEmpty(context, 10, 10);
        for(int i = 0; i < 3 ; i++ ){
            System.out.println(ints[i]);
        }
        System.out.println(lo);

    }
}
