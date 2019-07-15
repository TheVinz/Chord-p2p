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
        //System.out.println("mover");
        try {
            Node newPredecessor = node.getPredecessor();
            if(newPredecessor == null)
                return;
            List<ChordResource> temp = new ArrayList<>();
            if (predecessor != null && newPredecessor.getId() == predecessor.getId())
                return;
            if(newPredecessor.getId() == node.getId()){
                predecessor = node;
                return;
            }
            if (predecessor == null || isInsideInterval(newPredecessor.getId(), predecessor.getId(), node.getId())) {
                chordResourceList.forEach(x -> {
                    if (isInsideInterval(newPredecessor.getId(), x.getId(), node.getId())) {
                        temp.add(x);
                    }
                });
                temp.forEach(x -> {
                    try {
                        //System.out.println("hereeeeee "+predecessor.getId());
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
                        temp.add(x);
                    }
                });
                temp.forEach(x -> {
                    chordResourceList.add(x);
                    replicasList.remove(x);
                });

            }
            predecessor = newPredecessor;
            }catch(NetworkFailureException e){

            }


    }

    /**
     * Propagate for consistency
     */
    public synchronized void propagateResources(){
        //System.out.println("propagator");
        try {
            List <Node> newSuccessorList = getSuccessorList();
            if(newSuccessorList == null)
                return;
            newSuccessorList.add(0, node.getSuccessor());
            cutSuccessorList(newSuccessorList);
            /*newSuccessorList.forEach( x -> {
                System.out.print(x.getId()+" ");
            });*/
            //System.out.print("\n");
            int j=0;
            int prec = node.getId();
            for(int i=0; i<newSuccessorList.size(); i++){
                while(j<successorList.size() && isInsideInterval(successorList.get(j).getId(), prec, newSuccessorList.get(i).getId()) && successorList.get(j).getId() != newSuccessorList.get(i).getId()){
                    //deleteResource
                    for(ChordResource x : chordResourceList)
                        successorList.get(j).notifyDelete(x.getTitle());
                    j++;
                }
                if(j >=successorList.size() || successorList.get(j).getId() != newSuccessorList.get(i).getId()){
                    //publish
                    //System.out.println(successorList.get(j).getId() + " " + newSuccessorList.get(i).getId() );
                    for(ChordResource x : chordResourceList)
                        moveReplica(newSuccessorList.get(i), x);
                }

                if(j < successorList.size() && successorList.get(j).getId() == newSuccessorList.get(i).getId())
                    j++;

                prec = newSuccessorList.get(i).getId();
            }
            while(j<successorList.size()){
                for(ChordResource x : chordResourceList)
                    successorList.get(j).notifyDelete(x.getTitle());
                j++;
            }
            successorList = newSuccessorList;
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
        //System.out.println(chordResource.getContent());
        saveToFile(chordResource);
        chordResourceList.add(chordResource);
        for(Node x: successorList) {
            moveReplica(x, chordResource);
        }
    }

    public synchronized void addReplica(ChordResource chordResource){
        //System.out.println("new replicaaaa");
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
        successorList = getSuccessorList();
        cutSuccessorList(successorList);

        start();
    }

    private List<Node> getSuccessorList() throws NetworkFailureException {
        List<Node> temp = node.getSuccessorsList();
        if(temp == null)
            return new ArrayList<>();
        return (List) ((ArrayList) temp).clone();
    }

    private void cutSuccessorList(List<Node> nodeList){
        int i=0;
        for(i=0; i<nodeList.size(); i++)
            if(nodeList.get(i).getId() == node.getId())
                break;
        while(nodeList.size()>i)
            nodeList.remove(i);

    }

    private void moveReplica(Node destination, ChordResource chordResource){
        try {
            if(!destination.notifyPropagation(chordResource.getTitle()))
                destination.sendReplica(chordResource);
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        destination.close();
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nmyResources: ");
        chordResourceList.forEach(s -> sb.append(s.getTitle()).append(','));
        sb.append("\nmyReplicas: ");
        replicasList.forEach(s -> sb.append(s.getTitle()).append(','));
        return sb.toString();
    }
}
