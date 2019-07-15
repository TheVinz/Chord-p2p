package resource;

import java.io.Serializable;

public class ChordResource implements Serializable {

    private final String title, content;
    private final boolean notFound;

    public ChordResource(String title, String content){
        this.title=title;
        this.content=content;
        this.notFound = false;
    }

    /**
     * Create an empty resource for a certain resource name
     * @param title the name of the resource
     */
    public ChordResource(String title) {
        this.title=title;
        this.content=null;
        this.notFound=true;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isNotFound() {
        return notFound;
    }
}
