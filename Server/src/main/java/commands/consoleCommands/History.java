package commands.consoleCommands;

import commands.Receiver;

import java.io.IOException;
import java.io.Serializable;

/**
 * This command prints the last 15 executed commands without their arguments.
 */

public class History implements Command, Serializable {
    public String name = "history";
    public String getName(){
        return name;
    }

    public String required = "";
    public String getRequired(){
        return required;
    }

    public final static String[] args = new String[0];
    public static String[] inputs = new String[0];
    public String[] getInputs() {
        return inputs;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean complicated = false;

    public boolean isComplicated(){
        return complicated;
    }

    boolean isAuthorizationRequired = false;
    public boolean isAuthorizationRequired(){
        return isAuthorizationRequired;
    };

    Receiver receiver;
    public History (Receiver receiver){
        this.receiver = receiver;
    }

    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws IOException {
        receiver.historyCommand(arguments, path, isScript, object);
    }

    @Override
    public String[] args() {
        return args;
    }
}
