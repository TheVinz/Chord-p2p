package network.message.request;

import network.exceptions.NetworkFailureException;
import network.message.reply.NotifyPropagationReply;
import network.message.reply.ReplyMessage;
import node.LocalNode;

public class NotifyPropagationRequest extends RequestMessage{

    private String title;


    public NotifyPropagationRequest(String title) {
        this.title = title;
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        NotifyPropagationReply reply = null;
        reply = new NotifyPropagationReply(node.notifyPropagation(title));
        reply.setRequestId(getRequestId());
        return reply;
    }
}
