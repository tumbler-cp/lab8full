package comm;

import java.io.Serializable;

public class User implements Serializable {
    private final String login;
    private final String password;

    public User(String LOGIN, String password){
        this.login = LOGIN;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
