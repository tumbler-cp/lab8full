package gui.panels;

import client.Client;
import comm.CommType;
import comm.CommandClient;
import comm.clientReqs.*;
import gui.data.Colors;
import gui.elements.DButton;
import gui.frames.DragonAdderScreen;
import interfaces.FileCommand;
import interfaces.Other;
import interfaces.Updating;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

public class DTerminal extends JPanel implements ActionListener {
    protected final HashMap<CommType, CommandClient<?>> map;
    private final JComboBox<CommType> comboBox;
    private final JTextArea area;
    private final DButton execButt;
    private final JTextField argField;
    private final DButton fileButton;
    private JFileChooser fileChooser;
    public DTerminal(){
        argField = new JTextField();
        argField.setVisible(false);

        setBackground(Colors.background);
        area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setColumns(100);
        area.setRows(50);
        area.setEditable(false);
        setFont(new Font(super.getFont().getName(), Font.BOLD, 16));

        comboBox = new JComboBox<>(CommType.values());
        comboBox.addActionListener(this);

        execButt = new DButton();
        execButt.setText(Client.terminalBundle.getString("exec"));
        execButt.addActionListener(this);

        map = new HashMap<>(){{
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
            put(CommType.EXECUTE_SCRIPT, new ExecuteScriptReq());
        }};

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        fileButton = new DButton();
        fileButton.setText("Выбрать файл");
        fileButton.setAlignmentX(CENTER_ALIGNMENT);
        fileButton.addActionListener(this);
        fileButton.setVisible(false);

        add(new JScrollPane(area));
        add(comboBox);
        add(argField);
        add(fileButton);
        execButt.setAlignmentX(CENTER_ALIGNMENT);
        add(execButt);
    }

    public JTextArea getArea() {
        return area;
    }

    private void print(String s) {
        area.append(s + "\n");
    }

    public void updLayout(){
        execButt.setText(Client.terminalBundle.getString("exec"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==execButt){
            CommandClient<?> command = map.get((CommType) comboBox.getSelectedItem());
            if (command instanceof FileCommand){
                File chosen = fileChooser.getSelectedFile();
                Client.terminal.scriptMode(chosen);
            }
            else {
                String[] args = argField.getText().trim().split(" ");
                command.setArgLine(args);
                if (command.create()) {
                    Client.terminal.mkCommand(command);
                }
                print("-^-^-^-^-   COMMAND " + command.getType() + "   -^-^-^-^-");
            }
        }
        if (e.getSource()==comboBox){
            argField.setVisible(false);
            execButt.setVisible(true);
            fileButton.setVisible(false);
            CommandClient<?> command = map.get((CommType) comboBox.getSelectedItem());
            if (command instanceof Updating){
                new DragonAdderScreen(!(command instanceof InsertReq));
                execButt.setVisible(false);
                revalidate();
                repaint();
            }
            if (command instanceof Other) {
                argField.setVisible(true);
                execButt.setVisible(true);
            }
            if (command instanceof FileCommand) {
                fileButton.setVisible(true);
            }
        }
        if (e.getSource()==fileButton){
            fileChooser = new JFileChooser("");
            fileChooser.showOpenDialog(null);
        }
    }
}
