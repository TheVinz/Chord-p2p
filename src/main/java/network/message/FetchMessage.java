package network.message;

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
        return new ResourceMessage(node.fetch(name));
    }
}
