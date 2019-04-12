package network.message.request;

import network.message.reply.ReplyMessage;
import resource.ChordResource;
import node.LocalNode;

public class PublishRequest extends RequestMessage {

    private final ChordResource resource;

    public PublishRequest(ChordResource resource) {
        this.resource=resource;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        node.publish(resource);
        return null;
    }
}
