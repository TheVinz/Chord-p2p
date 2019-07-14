package resource;

import java.io.Serializable;

import static utils.Util.calculateDigest;

public class ChordResource implements Serializable {

    private final String title, content;
    private int id;

    public ChordResource(String title, String content){
        this.title=title;
        this.content=content;
        this.id = calculateDigest(title);
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
}
