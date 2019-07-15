package network.message.request;

import network.message.reply.ReplyMessage;
import node.LocalNode;
import resource.ChordResource;

public class ReplicaRequest extends RequestMessage {

    private final ChordResource resource;

    public ReplicaRequest(ChordResource resource) {
        this.resource = resource;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        node.publish(resource);
        return null;
    }
}
