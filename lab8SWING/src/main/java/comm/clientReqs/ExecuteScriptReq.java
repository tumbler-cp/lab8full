package comm.clientReqs;

import comm.CommType;
import comm.CommandClient;
import interfaces.FileCommand;

import java.io.File;

public class ExecuteScriptReq implements CommandClient<File>, FileCommand {
    @Override
    public CommType getType() {
        return null;
    }

    @Override
    public File getArg() {
        return null;
    }

    @Override
    public void setArgLine(String[] argLine) {

    }

    @Override
    public String[] getArgLine() {
        return new String[0];
    }

    @Override
    public boolean create() {
        return false;
    }

    @Override
    public void setFile(File file) {

    }
}
