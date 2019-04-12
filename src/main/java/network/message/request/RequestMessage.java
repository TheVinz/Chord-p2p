package network.message.request;

import network.message.Message;
import network.message.reply.ReplyMessage;
import node.LocalNode;

public abstract class RequestMessage extends Message {
    public static final long serialVersionUID = 46513505L;

    public abstract ReplyMessage handleRequest(LocalNode node);
}
