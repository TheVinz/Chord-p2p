package resource;

import java.io.Serializable;

import static utils.Util.calculateDigest;

public class ChordResource implements Serializable {

    private final String title, content;
    private int id;
    private final boolean notFound;

    public ChordResource(String title, String content){
        this.title=title;
        this.content=content;
        this.id = calculateDigest(title);
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

    public int getId() {
        return id;
    }
    public boolean isNotFound() {
        return notFound;
    }
}
