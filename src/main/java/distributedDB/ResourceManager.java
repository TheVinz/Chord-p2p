package distributedDB;

import network.exceptions.NetworkFailureException;
import node.Node;
import resource.ChordResource;
import utils.PeriodicActionsManager;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static utils.Util.isInsideInterval;

public class ResourceManager {

    private Node node;
    private List<ChordResource> chordResourceList;
    private List<ChordResource> replicasList;
    private List<Node> successorList;
    private PeriodicActionsManager periodicActionsManager;
    private Node predecessor;

    public ResourceManager(Node node, Consumer<ResourceManager>[] tasks, String[] labels,
                          long[] delays, long[] periods) throws NetworkFailureException {
        predecessor = node.getPredecessor();
        successorList = node.getSuccessorsList();
        chordResourceList = new ArrayList<>();
        replicasList = new ArrayList<>();
        periodicActionsManager = new PeriodicActionsManager();
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
    }

    public ResourceManager(Node node) {
        this.node = node;
        chordResourceList = new ArrayList<>();
        successorList = new ArrayList<>();
    }


    public synchronized void moveResourcesToPredecessor(){
        try {
            Node newPredecessor = node.getPredecessor();
            List<ChordResource> temp = new ArrayList<>();
            if (newPredecessor.getId() == predecessor.getId())
                return;
            if (isInsideInterval(newPredecessor.getId(), predecessor.getId(), node.getId())) {
                chordResourceList.forEach(x -> {
                    if (isInsideInterval(newPredecessor.getId(), x.getId(), node.getId())) {
                        temp.add(x);
                    }
                });
                temp.forEach(x -> {
                    try {
                        newPredecessor.publish(x);
                        chordResourceList.remove(x);
                    } catch (NetworkFailureException e) {
                        return;
                    }
                });
            }else{
                //predecessor is failed
            }
            }catch(NetworkFailureException e){

            }

    }

    /**
     * Propagate for consistency
     */
    public synchronized void propagateResources(){
        try {
            List <Node> newSuccessorList = node.getSuccessorsList();
            newSuccessorList.add(0, node.getSuccessor());
            int j=0;
            int prec = node.getId();
            for(int i=0; i<newSuccessorList.size(); i++){
                while(j<successorList.size() && isInsideInterval(successorList.get(j).getId(), prec, newSuccessorList.get(i).getId()) && successorList.get(j).getId() != newSuccessorList.get(i).getId()){
                    //deleteResource
                    j++;
                }
                if(successorList.get(j).getId() != newSuccessorList.get(i).getId()){
                    //publish
                }
                prec = newSuccessorList.get(i).getId();
            }
            while(j<successorList.size()){
                //delete
            }
        } catch (NetworkFailureException e) {
        }

    }

    public void start(){
        periodicActionsManager.start();
    }

    public void stop() {
        System.out.println("This node has been closed!");
        periodicActionsManager.stop();
    }



}
