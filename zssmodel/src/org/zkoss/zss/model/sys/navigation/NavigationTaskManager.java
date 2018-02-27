package org.zkoss.zss.model.sys.navigation;


import org.zkoss.zss.model.impl.sys.navigation.NavigationTask;

import java.util.ArrayList;

public abstract class NavigationTaskManager {
    private static NavigationTaskManager _instance;
    private ArrayList<NavigationTask> navigationTasks;

    private NavigationTaskManager()
    {
        navigationTasks = new ArrayList<>();
    }

    public void startNavigationBuilderTask()
    {
        NavigationTask navigationTask = new NavigationTask();
        navigationTasks.add(navigationTask);
        navigationTask.start();

    }

    public void shutDown()
    {
        navigationTasks.forEach(NavigationTask::shutdown);
        for (NavigationTask navigationTask : navigationTasks) {
            try {
                navigationTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static NavigationTaskManager getInstance(){
        return _instance;
    }



    public abstract void shutdown();
}