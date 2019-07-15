package network.message.request;

import network.message.reply.ReplyMessage;
import network.message.reply.ResourceReply;
import node.LocalNode;

public class FetchMessage extends RequestMessage {

    private final String name;

    public FetchMessage(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        return new ResourceReply(node.fetch(name));
    }
}
