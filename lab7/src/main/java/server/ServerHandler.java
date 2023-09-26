package server;

import comm.*;
import db.UserBase;
import interfaces.Command;
import interfaces.Handler;
import interfaces.Managing;
import managers.CommandManager;
import managers.DragonManager;

import java.net.SocketAddress;
import java.util.*;

public class ServerHandler extends Thread implements Handler<Handable> {
    private static int id = 0;
    private Handable current;
    private final CommandManager commands;
    private final UserBase users;
    private final Map<Integer, Complect> box;
    private final static List<User> authedUsers = new ArrayList<>();
    private final static List<SocketAddress> userAdds = new ArrayList<>();
    private HashMap<SocketAddress, LinkedList<FilePart>> files = new HashMap<>();
    private DragonManager dragonManager;
    ServerHandler(CommandManager commandManager, Map<Integer, Complect> box, UserBase userBase, DragonManager dragonManager){
        commands = commandManager;
        this.box = box;
        users = userBase;
        this.dragonManager = dragonManager;
    }

    @Override
    public void run() {
        Complect buff = null;
        switch (current.request.signal()){
            case COMMAND -> {
                CommandClient<?> request = (CommandClient<?>) current.request.object();
                CommType key = request.getType();
                String[] argLine = request.getArgLine();
                System.out.println(Arrays.toString(argLine));
                Command command = commands.get(key);
                command.setLineArgs(argLine);
                command.setArg(request.getArg());
                if (command instanceof Managing){
                    ((Managing) command).setUser(((Managing) request).getUser());
                }
                command.execute();
                String response = command.response();
                current.request = new Request(Signal.TEXT, response);
                buff = new Complect(current.src,
                        new Request(Signal.ALLOC,response.getBytes().length),
                        current.request);
            }
            case LOGIN -> {
                files.put(current.src, new LinkedList<>());
                System.out.println(Arrays.toString(files.keySet().toArray()));
                User user = (User) current.request.object();
                if (authedUsers.contains(user)) {
                    current.request = new Request(Signal.DECLINE, "Такой пользователь уже авторизовался");
                    return;
                }
                if (users.find(user)){
                    current.request = new Request(Signal.ACCEPT, "Добро пожаловать!");
                    System.out.println("Новый вход в " + current.src);
                    authedUsers.add(user);
                    userAdds.add(current.src);
                    files.put(current.src, new LinkedList<>());
                    System.out.println(Arrays.toString(files.keySet().toArray()));
                }
                else {
                    current.request = new Request(Signal.DECLINE, "Не удалось авторизоваться. Проверьте логин/пароль");
                }
                buff = new Complect(current.src,
                        new Request(Signal.ALLOC,1024),
                        current.request);
            } case REG -> {
                files.put(current.src, new LinkedList<>());
                System.out.println(Arrays.toString(files.keySet().toArray()));
                User user = (User) current.request.object();
                if (users.insert(user)) {
                    current.request = new Request(Signal.ACCEPT, "Регистрация успешна!");
                    authedUsers.add(user);
                    userAdds.add(current.src);
                    files.put(current.src, new LinkedList<>());
                    System.out.println(Arrays.toString(files.keySet().toArray()));
                } else current.request = new Request(Signal.DECLINE, "Регистрация безуспешна!");
                buff = new Complect(current.src,
                        new Request(Signal.ALLOC,1024),
                        current.request);
                files.put(current.src, new LinkedList<>());
            } case EXIT -> {
                User user = (User) current.request.object();
                authedUsers.remove(user);
                userAdds.remove(current.src);
            } case TABLE -> {
                System.out.println("_______________________________________________________");
                current.request = new Request(Signal.TABLE, dragonManager.getDragons());
                buff = new Complect(current.src,
                        new Request(Signal.ALLOC, current.request.bytes().length),
                        current.request);
            }
        }
        box.put(id, buff);
        id++;
    }

    @Override
    public void setObj(Handable obj) {
        current = obj;
    }

    public static List<SocketAddress> getConnections() {
        return userAdds;
    }
}
