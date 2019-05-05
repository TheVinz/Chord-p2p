package resource;

import network.exeptions.NetworkFailureException;
import node.LocalNode;
import node.Node;

public class RemoteResource {

    private final String name;
    private final int id;
    private ChordResource resource=null;
    private final LocalNode localNode;

    public RemoteResource(LocalNode localNode, String name, int id) {
        this.name = name;
        this.id = id;
        this.localNode = localNode;
    }

    private Node find() {
        try {
             return localNode.findSuccessor(id);
        } catch (NetworkFailureException e) {
            e.printStackTrace();
            return find(); // TODO why loop?
        }
    }

    public ChordResource fetch() {
        if(resource != null)
            return resource;
        try {
            Node node = find();
            resource = node.fetch(name);
            node.close();
        } catch (NetworkFailureException e) {
            return fetch();
        }
        if(resource == null)
            return fetch();
        else return resource;
    }

}
