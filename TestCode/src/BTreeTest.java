import org.model.DBContext;
import org.model.DBHandler;
import org.zkoss.zss.model.impl.BTree;
import org.zkoss.zss.model.impl.CountedBTree;
import org.zkoss.zss.model.impl.KeyBTree;
import org.zkoss.zss.model.impl.CombinedBTree;
import org.zkoss.zss.model.impl.statistic.AbstractStatistic;
import org.zkoss.zss.model.impl.statistic.CombinedStatistic;
import org.zkoss.zss.model.impl.statistic.CountStatistic;
import org.zkoss.zss.model.impl.statistic.KeyStatistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class BTreeTest {
    enum Test{
        KEY,
        COUNT,
        COMBINED
    }

    public static void main(String[] args) {
        deepTest();
    }

    public static void deepTest(){
        String url = "jdbc:postgresql://127.0.0.1:5432/ibd";
        String driver = "org.postgresql.Driver";
        String userName = "mangesh";
        String password = "mangesh";
        DBHandler.connectToDB(url, driver, userName, password);
        DBContext dbContext = new DBContext(DBHandler.instance.getConnection());

        CombinedDNETest(dbContext);
        dbContext.getConnection().commit();
        dbContext.getConnection().close();
    }
    public static void simpleTest(){
        String url = "jdbc:postgresql://127.0.0.1:5432/ibd";
        String driver = "org.postgresql.Driver";
        String userName = "mangesh";
        String password = "mangesh";
        DBHandler.connectToDB(url, driver, userName, password);
        DBContext dbContext = new DBContext(DBHandler.instance.getConnection());
        CountedBTree btree = new CountedBTree(dbContext, "Test1", false);
        ArrayList<Integer> arrayList = new ArrayList<>();

        // Random operations
        final int operations=5;
        Random random = new Random(1);
        Random randomOperation = new Random(1);
        int stats_add =0;
        int stats_remove =0;


        System.out.println("Start adding Initial Records");
        // Add initial data points.
        for (int i=0;i<operations;i++)
        {
            stats_add++;
            int randomValue = random.nextInt();
            ArrayList<Integer> insertList = new ArrayList<>();
            insertList.add(randomValue);
            btree.insertIDs(dbContext, i, insertList);
            System.out.println("Records added:" + randomValue);
            dbContext.getConnection().commit();
            arrayList.add(i,randomValue);
        }

        System.out.println("Random operations");
        // Perform RANDOM operations.
        for (int i=0;i<operations;i++)
        {
            int operation = randomOperation.nextInt(2);
            int randomPos = random.nextInt(arrayList.size());
            int randomValue = random.nextInt();

            switch (operation)
            {
                case 0:
                    stats_add++;
                    ArrayList<Integer> randomValueList = new ArrayList<>();
                    randomValueList.add(randomValue);
                    //System.out.println(btree.size(dbContext) + " add " + randomPos + " " + randomValueList);
                    btree.insertIDs(dbContext, randomPos, randomValueList);
                    arrayList.add(randomPos, randomValue);
                    break;
                case 1:
                    stats_remove++;
                    ArrayList<Integer> randomPosList = new ArrayList<>();
                    //System.out.println(btree.size(dbContext) + " remove " + randomPos);
                    btree.deleteIDs(dbContext, randomPos, 1);
                    arrayList.remove(randomPos);
                    break;
            }
            dbContext.getConnection().commit();
        }


        System.out.println("Records added:" + stats_add);
        System.out.println("Records deleated:" + stats_remove);
        System.out.println("BTree size:" + btree.size(dbContext));
        System.out.println("ArrayList size:" + arrayList.size());

        ArrayList<Integer> btree_list = btree.getIDs(dbContext, 0, arrayList.size());
        if (btree.size(dbContext) == arrayList.size()
                && btree_list.equals(arrayList))
            System.out.println("Results Match");
        else
            System.err.println("Results do not match");



        dbContext.getConnection().commit();
        dbContext.getConnection().close();
    }


    public static void testRootInsDel(DBContext context, Test test) {
        String tableName = "testRootInsDel";
        if (test == Test.COUNT) {
            CountedBTree testTree = new CountedBTree(context, tableName, false);
            ArrayList<Integer> key = new ArrayList<>();
            key.add(1);

            testTree.insertIDs(context, 0, key);
            //Integer test = testTree.lookup(context, key, AbstractStatistic.Type.COUNT);
            testTree.deleteIDs(context, 0, 1);

            testTree.insertIDs(context, 0, key);
            //test = testTree.getByCount(context, 0);
            ArrayList<Integer> key1 = new ArrayList<>();
            testTree.insertIDs(context, 1, key1);
            //test = testTree.getByCount(context, 2);
            testTree.insertIDs(context, 1, key);
            testTree.insertIDs(context, 1, key);
        }
        else if( test == Test.KEY){
            KeyBTree testTree = new KeyBTree(context, tableName,  false);
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(300);
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            statistics.add(new KeyStatistic(30));
            testTree.insertIDs(context, statistics, ids);
            testTree.deleteIDs(context, statistics);
            testTree.insertIDs(context, statistics, ids);
            ids.set(0, 200);
            ids.add(400);
            ArrayList<KeyStatistic> new_statistics = new ArrayList<>();
            statistics.set(0, new KeyStatistic(20));
            statistics.add(new KeyStatistic(40));
            testTree.insertIDs(context, statistics, ids);
        }

    }

    public static void testRootSplit(DBContext context, Test test) {
        String tableName = "testRootSplit";
        if(test == Test.COUNT) {
            int[] a = {5, 25, 50};
            int[] rootids = {0, 0, 0};

            for (int i = 0; i < 3; i++) {
                CountedBTree testTree = new CountedBTree(context, tableName + i, false);
                ArrayList<Integer> ids = new ArrayList<>();
                ids.add(100);
                ids.add(200);
                ids.add(300);
                ids.add(400);
                ids.add(a[i] * 10);
                testTree.insertIDs(context, 0, ids);

            }
        }
        else if(test == Test.KEY){
            int[] numbers = {10, 20, 30, 40};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for(int j = 0; j < 4; j++){
                ids.add(numbers[j]*10);
                statistics.add(new KeyStatistic(numbers[j]));
            }
            int[] a = {5, 25, 50};
            for(int i = 0; i < 3; i++) {
                KeyBTree testTree = new KeyBTree(context, tableName + i,  false);
                testTree.insertIDs(context, statistics, ids);
                ArrayList<Integer> new_ids = new ArrayList<>();
                new_ids.add(a[i]*10);
                ArrayList<KeyStatistic> new_statistics = new ArrayList<>();
                new_statistics.add(new KeyStatistic(a[i]));
                testTree.insertIDs(context, new_statistics, new_ids);
            }
        }
        else{
            CombinedBTree testTree = new CombinedBTree(context, tableName, false);
            int [] a = {5, 10, 20, 25, 30, 40, 50, 60, 70, 3, 15, 23, 27, 35, 45, 55, 80};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<CombinedStatistic> statistics = new ArrayList<>();
            for( int i = 0; i < 17; i++){
                ids.add(a[i]*10);
                statistics.add(new CombinedStatistic(new KeyStatistic(a[i])));
            }
            testTree.insertIDs(context, statistics, ids);
        }
    }

    public static void testSplitNode(DBContext context, Test test) {
        String tableName = "testSplitNode";
        if( test == Test.COUNT) {
            CountedBTree testTree = new CountedBTree(context, tableName, false);
            ArrayList<Integer> ids = new ArrayList<>();
            int[] numbers = {50, 100, 200, 250, 300, 400, 500, 600, 700, 800};
            for (int i = 0; i < 10; i++) {
                ids.add(numbers[i]);

            }
            testTree.insertIDs(context, 0, ids);

            ArrayList<Integer> new_ids = new ArrayList<>();
            new_ids.add(30);
            testTree.insertIDs(context, 1, new_ids);
            new_ids.set(0, 150);
            testTree.insertIDs(context, 3, new_ids);
            new_ids.set(0, 230);
            testTree.insertIDs(context, 5, new_ids);
            new_ids.set(0, 270);
            testTree.insertIDs(context, 7, new_ids);
            new_ids.set(0, 350);
            testTree.insertIDs(context, 9, new_ids);
            new_ids.set(0, 450);
            testTree.insertIDs(context, 11, new_ids);
            new_ids.set(0, 550);
            testTree.insertIDs(context, 13, new_ids);
            new_ids.set(0, 800);
            testTree.insertIDs(context, 16, new_ids);
        }
        else if( test == Test.KEY){
            KeyBTree testTree = new KeyBTree(context, tableName,  false);
            int [] a = {5, 10, 20, 25, 30, 40, 50, 60, 70, 3, 15, 23, 27, 35, 45, 55, 80};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for( int i = 0; i < 17; i++){
                ids.add(a[i]*10);
                statistics.add(new KeyStatistic(a[i]));
            }
            testTree.insertIDs(context, statistics, ids);
        }
    }

    public static void testSplitNodeSplitParent(DBContext context, Test test) {
        String tableName = "testSplitNodeSplitParent";
        if( test == Test.COUNT ) {
            ArrayList<Integer> ids = new ArrayList<>();
            int[] numbers = {50, 100, 200, 250, 300, 400, 500, 600, 700};
            for (int i = 0; i < 9; i++) {
                ids.add(numbers[i]);
            }

            int[] a = {1, 8, 16};
            int[] aa = {0, 2, 4};
            for (int i = 0; i < 3; i++) {
                CountedBTree testTree = new CountedBTree(context, tableName + i, false);
                testTree.insertIDs(context, 0, ids);
                ArrayList<Integer> new_ids = new ArrayList<>();
                new_ids.add(30);
                testTree.insertIDs(context, 0, new_ids);
                new_ids.set(0, 150);
                testTree.insertIDs(context, 3, new_ids);
                new_ids.set(0, 230);
                testTree.insertIDs(context, 5, new_ids);
                new_ids.set(0, 270);
                testTree.insertIDs(context, 7, new_ids);
                new_ids.set(0, 350);
                testTree.insertIDs(context, 9, new_ids);
                new_ids.set(0, 450);
                testTree.insertIDs(context, 11, new_ids);
                new_ids.set(0, 550);
                testTree.insertIDs(context, 13, new_ids);
                new_ids.set(0, a[i] * 10);
                testTree.insertIDs(context, aa[i], new_ids);

            }
        }
        else if( test == Test.KEY ){
            int [] a = {5, 10, 20, 25, 30, 40, 50, 60, 70, 3, 15, 23, 27, 35, 45, 55, 80};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for( int i = 0; i < 17; i++){
                ids.add(a[i]*10);
                statistics.add(new KeyStatistic(a[i]));
            }
            int [] aa = {1, 8, 16};
            for(int j = 0; j < 3; j++) {
                KeyBTree testTree = new KeyBTree(context, tableName + j, false);
                testTree.insertIDs(context, statistics, ids);
                ArrayList<Integer> new_ids = new ArrayList<>();
                new_ids.add(aa[j]*10);
                ArrayList<KeyStatistic> new_statistics = new ArrayList<>();
                new_statistics.add(new KeyStatistic(aa[j]));
                testTree.insertIDs(context, new_statistics, new_ids);
            }
        }
    }

    public static void testNodeMerge(DBContext context, Test test) {
        String tableName = "testNodeMerge";
        if( test == Test.COUNT ) {
            CountedBTree testTree = new CountedBTree(context, tableName, false);
            ArrayList<Integer> ids = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                ids.add(i * 100);
            }
            testTree.insertIDs(context, 0, ids);
            ids = new ArrayList<>();
            ids.add(50);
            testTree.insertIDs(context, 0, ids);
            testTree.deleteIDs(context, 3, 1);
        }
        else if( test == Test.KEY ){
            KeyBTree testTree = new KeyBTree(context, tableName, false);
            int [] a = {10, 20, 30, 40, 50, 60, 70};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for(int i = 0; i < 7; i++){
                ids.add(a[i]*10);
                statistics.add(new KeyStatistic(a[i]));
            }
            testTree.insertIDs(context, statistics, ids);
            ArrayList<KeyStatistic> del_statistics = new ArrayList<>();
            del_statistics.add(new KeyStatistic(20));
            testTree.deleteIDs(context, del_statistics);
        }
        else{
            CombinedBTree testTree = new CombinedBTree(context, tableName, false);
            int [] a = {5, 10, 20, 23, 27, 30, 33, 40, 50, 55, 70, 80, 85};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<CombinedStatistic> statistics = new ArrayList<>();
            for(int i = 0; i < 13; i++){
                ids.add(a[i]*10);
                statistics.add(new CombinedStatistic(new KeyStatistic(a[i])));
            }
            testTree.insertIDs(context, statistics, ids);
            ArrayList<CombinedStatistic> del_statistics = new ArrayList<>();
            for(int i = 0; i < 5; i++){
                del_statistics.add(new CombinedStatistic(new KeyStatistic(a[i])));
            }
            testTree.deleteIDs(context, del_statistics, AbstractStatistic.Type.KEY);
        }
    }

    public static void NodeMergeRootMerge(DBContext context, Test test) {
        String tableName = "NodeMergeRootMerge";
        if( test == Test.COUNT) {
            CountedBTree testTree = new CountedBTree(context, tableName, false);
            ArrayList<Integer> ids = new ArrayList<>();
            int[] numbers = {50, 100, 200, 230, 270, 300, 330, 400, 500, 550, 700, 800, 850};
            for (int i = 0; i < 13; i++) {
                ids.add(numbers[i]);

            }
            testTree.insertIDs(context, 0, ids);
            testTree.deleteIDs(context, 0, 1);
            testTree.deleteIDs(context, 0, 1);
            testTree.deleteIDs(context, 0, 1);
            testTree.deleteIDs(context, 0, 1);
            testTree.deleteIDs(context, 0, 1);
        }
        else if( test == Test.KEY ){
            KeyBTree testTree = new KeyBTree(context, tableName, false);
            int [] a = {5, 10, 20, 23, 27, 30, 33, 40, 50, 55, 70, 80, 85};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for(int i = 0; i < 13; i++){
                ids.add(a[i]*10);
                statistics.add(new KeyStatistic(a[i]));
            }
            testTree.insertIDs(context, statistics, ids);
            ArrayList<KeyStatistic> del_statistics = new ArrayList<>();
            for(int i = 0; i < 5; i++){
                del_statistics.add(new KeyStatistic(a[i]));
            }
            testTree.deleteIDs(context, del_statistics);
        }
    }

    public static void NodeMergeRootMerge1(DBContext context, Test test) {
        String tableName = "NodeMergeRootMerge1";
        if( test == Test.COUNT ) {
            CountedBTree testTree = new CountedBTree(context, tableName, false);
            ArrayList<Integer> ids = new ArrayList<>();
            int[] numbers = {50, 100, 200, 230, 270, 300, 330, 400, 500, 550, 700, 800, 850, 900, 950, 1000, 1050, 1100, 1150};
            for (int i = 0; i < 19; i++) {
                ids.add(numbers[i]);
            }
            testTree.insertIDs(context, 0, ids);
            testTree.insertIDs(context, 0, ids);
            testTree.deleteIDs(context, 0, 1);
            testTree.deleteIDs(context, 13, 1);
            testTree.deleteIDs(context, 16, 1);
        }
        else if( test == Test.KEY){
            KeyBTree testTree = new KeyBTree(context, tableName, false);
            int [] a = {5, 10, 20, 23, 27, 30, 33, 40, 50, 55, 70, 80, 85, 90, 95, 100, 105, 110, 115};
            ArrayList<Integer> ids = new ArrayList<>();
            ArrayList<KeyStatistic> statistics = new ArrayList<>();
            for(int i = 0; i < 19; i++){
                ids.add(a[i]*10);
                statistics.add(new KeyStatistic(a[i]));
            }
            testTree.insertIDs(context, statistics, ids);
            ArrayList<KeyStatistic> del_statistics = new ArrayList<>();
            int [] aa = {5, 95, 115};
            for(int i = 0; i < 3; i++){
                del_statistics.add(new KeyStatistic(aa[i]));
            }
            testTree.deleteIDs(context, del_statistics);
        }

    }

    public static void CombinedOneLevel(DBContext context){
        String tableName = "CombinedOneLevel";
        CombinedBTree testTree = new CombinedBTree(context, tableName, false);
        int [] num = {10, 20, 30, 40};
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<CombinedStatistic> statistics = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            ids.add(num[i]*10);
            statistics.add(new CombinedStatistic(new KeyStatistic(num[i])));
        }
        testTree.insertIDs(context, statistics, ids);
        CombinedStatistic start = new CombinedStatistic(new KeyStatistic(20), new CountStatistic(1));
        ArrayList<Integer> results = testTree.getIDs(context, start, 2, AbstractStatistic.Type.COUNT);
        ArrayList<Integer> outofbounds = testTree.getIDs(context, start, 5 , AbstractStatistic.Type.COUNT);
        System.out.println(results);
        System.out.println(outofbounds);
    }
    public static void CombinedTwoLevels(DBContext context){
        String tableName = "CombinedTwoLevels";
        CombinedBTree testTree = new CombinedBTree(context, tableName, false);
        int [] num = {10, 20, 30, 40, 50, 60, 70};
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<CombinedStatistic> statistics = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            ids.add(num[i]*10);
            statistics.add(new CombinedStatistic(new KeyStatistic(num[i])));
        }
        testTree.insertIDs(context, statistics, ids);
        CombinedStatistic start = new CombinedStatistic(new KeyStatistic(20), new CountStatistic(1));
        ArrayList<Integer> results = testTree.getIDs(context, start, 4, AbstractStatistic.Type.KEY);
        System.out.println(results);
    }
    public static void CombinedDNETest(DBContext context){
        String tableName = "CombinedDNETest";
        CombinedBTree testTree = new CombinedBTree(context, tableName, false);
        int [] num = {10, 30, 40, 50, 60, 70, 80};
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<CombinedStatistic> statistics = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            ids.add(num[i]*10);
            statistics.add(new CombinedStatistic(new KeyStatistic(num[i])));
        }
        testTree.insertIDs(context, statistics, ids);
        CombinedStatistic start = new CombinedStatistic(new KeyStatistic(20), new CountStatistic(1));
        ArrayList<Integer> results = testTree.getIDs(context, start, 4, AbstractStatistic.Type.KEY);
    }

}
