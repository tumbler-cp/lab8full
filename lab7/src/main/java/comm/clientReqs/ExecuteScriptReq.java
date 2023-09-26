package comm.clientReqs;

import comm.CommType;
import comm.CommandClient;
import comm.User;
import interfaces.FileCommand;
import interfaces.Managing;

import java.io.File;

public class ExecuteScriptReq implements CommandClient<File>, FileCommand, Managing {
    private File file;
    private User user;

    @Override
    public CommType getType() {
        return CommType.EXECUTE_SCRIPT;
    }

    @Override
    public File getArg() {
        return file;
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
        return file.exists();
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
