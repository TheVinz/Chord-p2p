package resource;

import network.exeptions.NetworkFailureException;
import network.remoteNode.RemoteNode;
import node.LocalNode;
import node.Node;
import node.exceptions.NodeNotFoundException;

import java.io.IOException;

public class RemoteResource {

    private final String name;
    private final int id;
    private ChordResource resource=null;
    private Node node;
    private final LocalNode localNode;

    public RemoteResource(LocalNode localNode, String name, int id){
        this.name = name;
        this.id = id;
        this.localNode=localNode;
    }

    private void find(){
        try {
            if(node instanceof RemoteNode)
                ((RemoteNode) node).close();
            node = localNode.findSuccessor(id);
            if(node instanceof RemoteNode)
                ((RemoteNode) node).close();
        } catch (NodeNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public ChordResource fetch(){
        if(resource != null)
            return resource;
        try {
            find();
            resource = node.fetch(name);
            if(node instanceof RemoteNode)
                ((RemoteNode) node).close();
        } catch (NetworkFailureException e) {
            find();
            fetch();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(resource==null)
            return fetch();
        else return resource;
    }

}
