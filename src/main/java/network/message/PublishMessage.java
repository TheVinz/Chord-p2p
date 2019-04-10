package network.message;

import utils.ChordResource;
import node.LocalNode;

public class PublishMessage extends RequestMessage{

    private final ChordResource resource;

    public PublishMessage(ChordResource resource) {
        this.resource=resource;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        node.publish(resource);
        ReplyMessage msg = new ReplyMessage(method);
        msg.setRequestId(getRequestId());
        return msg;
    }
}
