package org.zkoss.zss.model.sys.navigation;


import org.zkoss.zss.model.CellRegion;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.model.sys.formula.FormulaAsyncUIController;


public abstract class NavigationAsyncScheduler implements Runnable {
    private static NavigationAsyncScheduler _schedulerInstance;
    //private static FormulaAsyncUIController uiController;


    /*public static void initUiController(FormulaAsyncUIController uiController){
        if (NavigationAsyncScheduler.uiController==null)
            NavigationAsyncScheduler.uiController=uiController;
    }

    public static void initScheduler(NavigationAsyncScheduler scheduler){
        if (_schedulerInstance==null)
            _schedulerInstance=scheduler;
    }

    public static FormulaAsyncUIController getUiController(){
        return uiController;
    }*/

    public static NavigationAsyncScheduler getScheduler(){
        return _schedulerInstance;
    }

   /* protected void update(SSheet sheet, CellRegion cellRegion) {
        if (uiController!=null){
            uiController.update(sheet, cellRegion);
        }
    }*/


    public abstract void shutdown();
}