package commands.consoleCommands;

import commands.Receiver;

import java.io.Serializable;

/**
 * This command sorts collection ArrayList<MusicBand> in reverted order.
 */

public class Reorder implements Command, Serializable {
    public String name = "reorder";
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

    boolean isAuthorizationRequired = true;
    public boolean isAuthorizationRequired(){
        return isAuthorizationRequired;
    };

    Receiver receiver;
    public Reorder (Receiver receiver){
        this.receiver = receiver;
    }

    public void execute(String[] arguments, String path, boolean isScript, Object object, Integer ID){
        receiver.reorderCommand(arguments, path, isScript, object);
    }

    @Override
    public String[] args() {
        return args;
    }
}
