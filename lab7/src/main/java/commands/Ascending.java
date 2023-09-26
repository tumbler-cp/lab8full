package commands;

import comm.clientReqs.AscendingReq;
import interfaces.Command;
import managers.DragonManager;

public class Ascending implements Command<String> {
    private DragonManager dragons;
    private String response;
    public Ascending (DragonManager dragonManager) {
        dragons = dragonManager;
    }
    @Override
    public boolean execute() {
        response = "";
        dragons.stream().sorted().toList()
                .forEach(d->{response+=d+"\n-----\n";});
        return true;
    }

    @Override
    public String getArgs() {
        return null;
    }

    @Override
    public void setArg(String arg) {

    }

    @Override
    public void setLineArgs(String[] lineArgs) {

    }

    @Override
    public String description() {
        return "print_ascending - вывести элементы коллекции в порядке возрастания";
    }

    @Override
    public String response() {
        return response;
    }
}
