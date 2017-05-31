package org.zkoss.zss.model.impl.testing;

import org.model.BlockStore;
import org.model.DBContext;
import org.model.DBHandler;
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
        String tableName = "Test";
        DBHandler connection = new DBHandler();
        DBContext context = new DBContext(connection.getConnection());
        BTree testTree = new BTree(context, tableName);
        Integer [] ids = testTree.createIDs(context, 0 , 10);
        System.out.println(ids);

    }
}
