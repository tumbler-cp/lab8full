package client;

import comm.*;
import comm.clientReqs.*;
import interfaces.Managing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ClientTerminal implements Runnable, ActionListener {
    private final ClientNetwork network;
    private final ClientHandler handler;
    private User user;
    private Receiver receiver;
    private JTextArea area;
    private final Timer timer;
    public ClientTerminal(ClientNetwork network) {
        this.network = network;
        handler = new ClientHandler(network, area);
        this.user = null;
        timer = new Timer(500, this);
        receiver = new Receiver(network, handler);
    }


    public void run() {
        network.request(Signal.TABLE, user);
        timer.start();
    }

    public void setArea(JTextArea area) {
        this.area = area;
        this.handler.setArea(area);
    }

    public void scriptMode(File file) {
        var map = new HashMap<>(){{
            put(CommType.HELP, new HelpReq());
            put(CommType.SHOW, new ShowReq());
            put(CommType.INSERT, new InsertReq());
            put(CommType.REMOVE_KEY, new RemoveKeyReq());
            put(CommType.CLEAR, new ClearReq());
            put(CommType.REMOVE_LOWER_KEY, new RemoveLowerReq());
            put(CommType.UPDATE, new UpdateReq());
            put(CommType.REPLACE_IF_GREATER, new ReplaceGreaterReq());
            put(CommType.INFO, new InfoReq());
            put(CommType.PRINT_ASCENDING, new AscendingReq());
            put(CommType.PRINT_FIELD_ASCENDING_COLOR, new AscendingColorReq());
            put(CommType.FILTER_CONTAINS_NAME, new FilterReq());
        }};
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String line;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            String[] arr = line.trim().split(" ");
            CommandClient<?> command = (CommandClient<?>) map.get(CommType.valueOf(arr[0].toUpperCase()));
            String[] args = new String[arr.length - 1];
            System.arraycopy(arr, 1, args, 0, arr.length - 1);
            command.setArgLine(args);
            if (command.create()){
                mkCommand(command);
            }
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void mkCommand(CommandClient<?> command){
        if (command instanceof Managing) ((Managing) command).setUser(user);
        network.request(Signal.COMMAND, command);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!receiver.isAlive()){
            receiver = new Receiver(network, handler);
            Executors.newSingleThreadExecutor().execute(receiver);
        }
    }
}
