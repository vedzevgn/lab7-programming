package logic.serverLogic;

import connection.DatagramConnection;
import responces.CommandType;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommandsSender {
    DatagramConnection manager;
    Map<String, Object[]> commands = new HashMap<String, Object[]>();


    public CommandsSender(DatagramConnection manager){
        this.manager = manager;
    }

    public void sendCommands(){
        Object[] helpAtr = new Object[3];
        helpAtr[0] = CommandType.NON_ARGUMENT;
        helpAtr[1] = 0;
        helpAtr[2] = 0;
        commands.put("help", helpAtr);

        Object[] addAtr = new Object[3];
        addAtr[0] = CommandType.OBJECT;
        addAtr[1] = 0;
        addAtr[2] = 6;
        commands.put("add", addAtr);

        Object[] showAtr = new Object[3];
        showAtr[0] = CommandType.NON_ARGUMENT;
        showAtr[1] = 0;
        showAtr[2] = 0;
        commands.put("show", showAtr);


        Object[] exitAtr = new Object[3];
        exitAtr[0] = CommandType.EXIT;
        exitAtr[1] = 0;
        showAtr[2] = 0;
        commands.put("exit", exitAtr);

        Object[] clearAtr = new Object[3];
        clearAtr[0] = CommandType.NON_ARGUMENT;
        clearAtr[1] = 0;
        clearAtr[2] = 0;
        commands.put("clear", clearAtr);

        Object[] reorderAtr = new Object[3];
        reorderAtr[0] = CommandType.NON_ARGUMENT;
        reorderAtr[1] = 0;
        reorderAtr[2] = 0;
        commands.put("reorder", reorderAtr);

        Object[] shuffleAtr = new Object[3];
        shuffleAtr[0] = CommandType.NON_ARGUMENT;
        shuffleAtr[1] = 0;
        shuffleAtr[2] = 0;
        commands.put("shuffle", shuffleAtr);

        Object[] sortAtr = new Object[3];
        sortAtr[0] = CommandType.NON_ARGUMENT;
        sortAtr[1] = 0;
        sortAtr[2] = 0;
        commands.put("sort", sortAtr);

        Object[] historyAtr = new Object[3];
        historyAtr[0] = CommandType.NON_ARGUMENT;
        historyAtr[1] = 0;
        historyAtr[2] = 0;
        commands.put("history", historyAtr);

        Object[] removeAtr = new Object[3];
        removeAtr[0] = CommandType.NON_ARGUMENT;
        removeAtr[1] = 1;
        removeAtr[2] = 0;
        commands.put("remove_by_id", removeAtr);

        Object[] updateAtr = new Object[3];
        updateAtr[0] = CommandType.POST_OBJECT;
        updateAtr[1] = 1;
        updateAtr[2] = 0;
        commands.put("update", updateAtr);

        Object[] exeAtr = new Object[3];
        exeAtr[0] = CommandType.EXECUTE;
        exeAtr[1] = 0;
        exeAtr[2] = 0;
        commands.put("execute_script", exeAtr);

        Object[] avgAtr = new Object[3];
        avgAtr[0] = CommandType.NON_ARGUMENT;
        avgAtr[1] = 0;
        avgAtr[2] = 0;
        commands.put("average_of_number_of_participants", avgAtr);

        Object[] avgnAtr = new Object[3];
        avgnAtr[0] = CommandType.NON_ARGUMENT;
        avgnAtr[1] = 0;
        avgnAtr[2] = 0;
        commands.put("average_of_nop", avgnAtr);

        Object[] infoAtr = new Object[3];
        infoAtr[0] = CommandType.NON_ARGUMENT;
        infoAtr[1] = 0;
        infoAtr[2] = 0;
        commands.put("info", infoAtr);

        Object[] filterArg = new Object[3];
        filterArg[0] = CommandType.NON_ARGUMENT;
        filterArg[1] = 1;
        filterArg[2] = 0;
        commands.put("filter_by_genre", filterArg);

        Object[] prtAtr = new Object[3];
        prtAtr[0] = CommandType.NON_ARGUMENT;
        prtAtr[1] = 0;
        prtAtr[2] = 0;
        commands.put("print_field_descending_genre", prtAtr);

        Object[] saveAtr = new Object[3];
        saveAtr[0] = CommandType.NON_ARGUMENT;
        saveAtr[1] = 0;
        saveAtr[2] = 0;
        commands.put("save", saveAtr);

        Object[] loginAtr = new Object[3];
        loginAtr[0] = CommandType.LOGIN;
        loginAtr[1] = 0;
        loginAtr[2] = 0;
        commands.put("login", loginAtr);

        Object[] signUpAtr = new Object[3];
        signUpAtr[0] = CommandType.SIGN_UP;
        signUpAtr[1] = 0;
        signUpAtr[2] = 0;
        commands.put("sign_up", signUpAtr);

        Object[] logoutAtr = new Object[3];
        logoutAtr[0] = CommandType.LOGOUT;
        logoutAtr[1] = 0;
        logoutAtr[2] = 0;
        commands.put("logout", logoutAtr);
        manager.send((Serializable) commands);
    }
}
