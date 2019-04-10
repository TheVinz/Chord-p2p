package utils;

import java.io.Serializable;

public class ChordResource implements Serializable {

    private String title, content;

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

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
