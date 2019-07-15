package network.message.request;

import network.message.reply.ReplyMessage;
import node.LocalNode;

public class DeleteRequest extends RequestMessage {

    private String title;

    public DeleteRequest(String title) {
        this.title = title;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        node.notifyDelete(title);
        return null;
    }
}
