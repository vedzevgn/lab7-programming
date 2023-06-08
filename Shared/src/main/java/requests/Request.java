package requests;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    String text;
    String[] args;
    Object object;
    String username;
    String password;

    @Serial
    private static final long serialVersionUID = 8842532001314005038L;

    public Request(String text, String[] args, Object object, String username, String password){
        this.text = text;
        this.args = args;
        this.object = object;
        this.username = username;
        this.password = password;
    }

    public String getText() {
        return text;
    }
    public String[] getArgs(){
        return args;
    }
    public Object getObject() {
        return object;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
