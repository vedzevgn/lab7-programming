package responces;

import java.io.Serializable;

public class Response implements Serializable {
    public boolean interactive;
    public String text;
    public boolean ready;


    public Response(String text, boolean interactive, boolean ready){
        this.text = text;
        this.interactive = interactive;
        this.ready = ready;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public String getText() {
        return text;
    }

    public boolean isReady() {
        return ready;
    }

}
