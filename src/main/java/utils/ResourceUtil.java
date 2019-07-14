package utils;

import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import node.Node;

import java.util.function.Consumer;

public class ResourceUtil {

    private static final Consumer<ResourceManager> MOVE_RESOURCES_ROUTINE = ResourceManager::moveResourcesToPredecessor;
    private static final Consumer<ResourceManager> PROPAGATE_RESOURCES_ROUTINE = ResourceManager::propagateResources;

    private static final Consumer<ResourceManager>[] DEFAULT_ROUTINES = new Consumer[]{MOVE_RESOURCES_ROUTINE, PROPAGATE_RESOURCES_ROUTINE};
    private static final String[] defaultLabels = new String[]{"move_resources_routine", "propagate_resources_routine"};

    public static ResourceManager createDefaultResourceManager(Node node, long[] delays, long[] periods) throws NetworkFailureException {
        return new ResourceManager(node, DEFAULT_ROUTINES, defaultLabels, delays, periods);
    }
}
