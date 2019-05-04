package network.message.reply;

import resource.ChordResource;

public class ResourceReply extends ReplyMessage {

    private final ChordResource resource;

    public ResourceReply(ChordResource resource) {
        this.resource=resource;
    }

    public ChordResource getResource() {
        return resource;
    }
}
