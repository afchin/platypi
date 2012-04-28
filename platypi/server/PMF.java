package cs3.platypi.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class PMF {
    private static final PersistenceManagerFactory pmfInstance = JDOHelper
            .getPersistenceManagerFactory("transactions-optional");

    /**
     * Empty Constructor for PMF object
     */
    private PMF() {
    }

    /**
     * Used to return a PersistenceManagerFactory instance
     * 
     * @param None
     * @return pmfInstance : PersistenceManagerFactory instance
     */
    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }

    public static PersistenceManager getManager() {
        return get().getPersistenceManager();
    }
}
