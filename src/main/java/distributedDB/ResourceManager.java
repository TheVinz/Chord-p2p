package distributedDB;

import network.exceptions.NetworkFailureException;
import node.Node;
import resource.ChordResource;
import utils.PeriodicActionsManager;
import utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static utils.Util.isInsideInterval;

public class ResourceManager {

    private Node node;
    private List<ChordResource> chordResourceList;
    private List<ChordResource> replicasList;
    private List<Node> successorList;
    private PeriodicActionsManager periodicActionsManager;
    private Node predecessor;

    public ResourceManager(Consumer<ResourceManager>[] tasks, String[] labels,
                          long[] delays, long[] periods) {
        chordResourceList = new ArrayList<>();
        replicasList = new ArrayList<>();
        periodicActionsManager = new PeriodicActionsManager();
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
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
                        replicasList.add(x);
                    } catch (NetworkFailureException e) {
                        return;
                    }
                });
            }else{
                //predecessor is failed
                replicasList.forEach(x -> {
                    if (isInsideInterval( x.getId(), newPredecessor.getId(), node.getId()) && x.getId() != newPredecessor.getId()) {
                        chordResourceList.add(x);
                        replicasList.remove(x);
                    }
                });

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
                    for(ChordResource x : chordResourceList)
                        successorList.get(j).notifyDelete(x.getTitle());
                    j++;
                }
                if(successorList.get(j).getId() != newSuccessorList.get(i).getId()){
                    //publish
                    for(ChordResource x : chordResourceList)
                        successorList.get(i).sendReplica(x);
                }
                prec = newSuccessorList.get(i).getId();
            }
            while(j<successorList.size()){
                for(ChordResource x : chordResourceList)
                    successorList.get(j).notifyDelete(x.getTitle());
            }
        } catch (NetworkFailureException e) {
        }

    }

    public void start(){
        periodicActionsManager.start();
    }

    public void stop() {
        periodicActionsManager.stop();
    }

    public void saveToFile(ChordResource resource) {
        File file = new File("data/node_"+ node.getId() + "/" +resource.getTitle());
        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            writer.print(resource.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChordResource getFile(String name) {
        File file = new File("data/node_"+node.getId() + "/" +name);
        if(!file.exists())
            return new ChordResource(name);
        else{
            try(Scanner sc = new Scanner(new FileInputStream(file))) {
                String content = sc.nextLine();
                return new ChordResource(name, content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ChordResource(name);
    }

    public void deleteFromFile(String name){
        //TODO
    }

    public synchronized void addNewResource(ChordResource chordResource){
        saveToFile(chordResource);
        chordResourceList.add(chordResource);
    }

    public synchronized void addReplica(ChordResource chordResource){
        saveToFile(chordResource);
        replicasList.add(chordResource);

    }

    public synchronized void deleteReplica(String title){
        deleteFromFile(title);
        ChordResource chordResource = null;
        for(ChordResource x : replicasList)
            if(x.getTitle().equals(title)){
                chordResource = x;
                break;
            }
        replicasList.remove(chordResource);
    }

    public synchronized Boolean isReplicaPresent(String title){
        for(int i=0; i<replicasList.size(); i++){
            if(replicasList.get(i).getTitle().equals(title))
                return true;
        }
        return false;
    }


    public void setNode(Node node)  throws NetworkFailureException{
        this.node = node;
        predecessor = node.getPredecessor();
        successorList = node.getSuccessorsList();
        start();
    }
}
