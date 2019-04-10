package network.message;

import utils.ChordResource;

public class ResourceMessage extends ReplyMessage{

    private final ChordResource resource;

    public ResourceMessage(ChordResource resource) {
        this.resource=resource;
    }

    public ChordResource getResource() {
        return resource;
    }
}
