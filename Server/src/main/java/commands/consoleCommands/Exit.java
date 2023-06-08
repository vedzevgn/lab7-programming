package commands.consoleCommands;

import commands.Receiver;

import java.io.IOException;
import java.io.Serializable;

/**
 * This command end shuts down the program
 */

public class Exit implements Command, Serializable {
    public String name = "exit";
    public String getName(){
        return name;
    }

    public String required = "exit";
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
    public Exit (Receiver receiver){
        this.receiver = receiver;
    }

    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID) throws IOException {
        receiver.exitCommand(arguments, path, isScript, object);
    }

    @Override
    public String[] args() {
        return args;
    }
}
