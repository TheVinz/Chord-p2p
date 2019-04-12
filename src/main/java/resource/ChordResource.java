package resource;

import java.io.Serializable;

public class ChordResource implements Serializable {

    private final String title, content;

    public ChordResource(String title, String content){
        this.title=title;
        this.content=content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
