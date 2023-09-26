package server;

import comm.Request;
import comm.Signal;
import db.UserBase;
import managers.CommandManager;
import managers.DragonManager;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTerminal {
    private final Scanner in = new Scanner(System.in);
    private final CommandManager commands;
    private final ServerNetwork network;
    private final UserBase userBase;
    private final DragonManager dragonManager;
    ServerTerminal(CommandManager commandManager, ServerNetwork network, UserBase userBase, DragonManager dragonManager) {
        this.commands = commandManager;
        this.network = network;
        this.userBase = userBase;
        this.dragonManager = dragonManager;
    }
    ConcurrentMap<Integer, Handable> par1 = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, Complect> par2 = new ConcurrentHashMap<>();
    boolean admin(){
        String[] line = in.nextLine().split(" ");
        return !line[0].equals("exit");
    }
    void run() throws CloneNotSupportedException {
        Reader reader = new Reader();
        reader.queue = par1;
        reader.network = network;
        ExecutorService pool = Executors.newCachedThreadPool();
        ExecutorService updater = Executors.newCachedThreadPool();
        reader.start();
        boolean tumbler = true;
        boolean updating = false;
        while (tumbler)
        {
            if (!updating) {
                if (!reader.isAlive()) {
                    reader = new Reader();
                    reader.queue = par1;
                    reader.network = network;
                    reader.start();
                }
                try {
                    if (System.in.available() > 0) tumbler = admin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (!par1.isEmpty()) {
                    for (Integer key : par1.keySet()) {
                        new ServerHandler(commands, par2, userBase, dragonManager) {{
                            setObj(par1.get(key));
                            start();
                        }};
                        par1.remove(key);
                    }
                }
                reader.setLocked(true);
                if (!par2.isEmpty()) {
                    System.out.println(par2);
                    for (Integer key : par2.keySet()) {
                        pool.submit(
                                new Sender() {{
                                    setObject(par2.get(key));
                                    setNetwork(network);
                                }});
                        par2.remove(key);
                    }
                }
            }
            if (DragonManager.isChanges()) {
                updating = true;
                int size = SerializationUtils.serialize(dragonManager.getDragons()).length;
                for (SocketAddress s : ServerHandler.getConnections()){
                    updater.submit(
                            new Sender(){{
                                setObject(
                                        new Complect(
                                                s,
                                                new Request(Signal.ALLOC, size),
                                                new Request(Signal.TABLE, dragonManager.getDragons())
                                        )
                                );
                                setNetwork(network);
                            }}
                    );
                }
            } else {
                updating = false;
            }
            reader.setLocked(false);
        }
    }
}