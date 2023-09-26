package gui.frames;

import client.Client;
import client.DragonFactory;
import comm.clientReqs.InsertReq;
import comm.clientReqs.UpdateReq;
import dragon.*;
import dragon.Color;
import exceptions.IncorrectValueException;
import gui.elements.DButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class DragonAdderScreen implements ActionListener {
    private final JFrame frame;
    private final JTextField keyField;
    private final JTextField nameField;
    private final JTextField coordField;
    private final JTextField ageField;
    private final JComboBox<Color> colorBox;
    private final JComboBox<DragonType> typeBox;
    private final JComboBox<DragonCharacter> characterBox;
    private final JTextField caveField;
    private final DButton ready;
    private final DButton back;
    private Dragon result;
    private final boolean upd;

    //============

    private static String keyS;
    private static String nameS;
    private static String coorS;
    private static String ageS;
    private static String colorS;
    private static String typeS;
    private static String characterS;
    private static String caveS;
    private static String readyL;
    private static String backL;
    private static JLabel warn;

    static {
        updLayout();
    }

    //============

    public DragonAdderScreen(boolean updating) {
        upd = updating;

        JLabel keyLabel = new JLabel(keyS);
        keyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        keyField = new JTextField();
        keyField.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (updating) {
            keyLabel.setVisible(false);
            keyField.setVisible(false);
        }

        JLabel nameLabel = new JLabel(nameS);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField();
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel coordLabel = new JLabel(coorS);
        coordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        coordField = new JTextField();
        coordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ageLabel = new JLabel(ageS);
        ageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ageField = new JTextField();
        ageField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel colorLabel = new JLabel(colorS);
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        colorBox = new JComboBox<>(Color.values());
        colorBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel typeLabel = new JLabel(typeS);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        typeBox = new JComboBox<>(DragonType.values());
        typeBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel characterLabel = new JLabel(characterS);
        characterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        characterBox = new JComboBox<>(DragonCharacter.values());
        characterBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel caveLabel = new JLabel(caveS);
        caveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        caveField = new JTextField();
        caveField.setAlignmentX(Component.CENTER_ALIGNMENT);

        ready = new DButton();
        ready.setText(readyL);
        ready.addActionListener(this);
        ready.setAlignmentX(Component.CENTER_ALIGNMENT);

        back = new DButton();
        back.setText(backL);
        back.addActionListener(this);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);

        warn = new JLabel("Введенные данные некорректны");
        warn.setForeground(java.awt.Color.RED);
        warn.setAlignmentX(Component.CENTER_ALIGNMENT);

        frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        frame.add(keyLabel);
        frame.add(keyField);
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(coordLabel);
        frame.add(coordField);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(colorLabel);
        frame.add(colorBox);
        frame.add(typeLabel);
        frame.add(typeBox);
        frame.add(characterLabel);
        frame.add(characterBox);
        frame.add(caveLabel);
        frame.add(caveField);

        frame.add(warn);
        warn.setVisible(false);

        frame.add(ready);
        frame.add(back);

        frame.setVisible(true);
    }

    public void setResult(Dragon dragon) {
        this.result = dragon;
        nameField.setText(dragon.getName());
        coordField.setText(dragon.getCoordinates().toString());
        colorBox.setSelectedItem(dragon.getColor());
        typeBox.setSelectedItem(dragon.getType());
        characterBox.setSelectedItem(dragon.getCharacter());
        caveField.setText(dragon.getCave().toString());
        ageField.setText(String.valueOf(dragon.getAge()));
    }

    public static void updLayout() {
        keyS = Client.dragBundle.getString("flKey");
        nameS = Client.dragBundle.getString("flName");
        coorS = Client.dragBundle.getString("flCoords");
        ageS = Client.dragBundle.getString("flAge");
        colorS = Client.dragBundle.getString("flColor");
        typeS = Client.dragBundle.getString("flType");
        characterS = Client.dragBundle.getString("flCharacter");
        caveS = Client.dragBundle.getString("flCave");
        readyL = Client.additBundle.getString("buttReady");
        backL = Client.additBundle.getString("buttBack");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ready) {
            try {
                String name = nameField.getText();
                Coordinates coords = Coordinates.toCoordinates(coordField.getText());
                Color color = (Color) colorBox.getSelectedItem();
                DragonType type = (DragonType) typeBox.getSelectedItem();
                DragonCharacter character = (DragonCharacter) characterBox.getSelectedItem();
                DragonCave cave;
                try {
                    cave = new DragonCave(Integer.parseInt(caveField.getText()));
                } catch (IncorrectValueException ex) {
                    throw new RuntimeException(ex);
                }
                int age = Integer.parseInt(ageField.getText());
                Dragon d = DragonFactory.mkFromVars(name, coords, color, type, character, cave, age);
                if (!upd) {
                    int key = Integer.parseInt(keyField.getText());
                    InsertReq req = new InsertReq();
                    req.setDragon(d);
                    req.setArgLine(new String[]{String.valueOf(key)});
                    if (req.create()) Client.terminal.mkCommand(req);
                } else {
                    UpdateReq req = new UpdateReq();
                    req.setDragon(d);
                    req.setArgLine(new String[]{result.getId().toString()});
                    if (req.create()) Client.terminal.mkCommand(req);
                }
                frame.setVisible(false);
                warn.setVisible(false);
            } catch (NumberFormatException ignored) {
                warn.setVisible(true);
            }
        }
        if (e.getSource() == back) {
            frame.setVisible(false);
        }
    }
}
