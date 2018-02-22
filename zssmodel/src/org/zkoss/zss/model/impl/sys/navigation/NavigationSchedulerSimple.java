package org.zkoss.zss.model.impl.sys.navigation;

import org.zkoss.zss.model.CellRegion;
import org.zkoss.zss.model.SCell;
import org.zkoss.zss.model.SSheet;
import org.zkoss.zss.model.impl.CellImpl;
import org.zkoss.zss.model.sys.BookBindings;
import org.zkoss.zss.model.sys.formula.DirtyManager;
import org.zkoss.zss.model.sys.navigation.NavigationAsyncScheduler;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Execute formulae in a single thread.
 */
public class NavigationSchedulerSimple extends NavigationAsyncScheduler {
    private static final Logger logger = Logger.getLogger(NavigationSchedulerSimple.class.getName());
    private boolean keepRunning = true;

    @Override
    public void run() {
        while (keepRunning) {

        }
    }

    @Override
    public void shutdown() {
        keepRunning = false;
    }


}
