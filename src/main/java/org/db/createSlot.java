package org.db;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import static org.db.javelin.*;


public class createSlot {
    @FXML
    Button buttonCreateSlotDone, buttonAddSlot;
    @FXML
    TextField textFieldSlotName, textFieldSlotCount, textFieldSlotDescription;
    @FXML
    RadioButton radioButtonMax, radioButtonExact, radioButtonUnlimited;
    @FXML
    ToggleGroup CountGroup;
    @FXML
    Label labelCreateSlotStatus;

    public void initialize() {

    }

    public void ButtonAddSlotOnAction() {
        String name = textFieldSlotName.getText();
        String description = textFieldSlotDescription.getText();
        String type = CountGroup.getSelectedToggle().getUserData().toString();
        int count;

        if (name.isEmpty() || type.isEmpty() || textFieldSlotCount.getText().isEmpty()) {
            labelCreateSlotStatus.setText("fields cannot be empty");
            labelCreateSlotStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }

        if (partHashMap.containsKey(name.hashCode())) {
            labelCreateSlotStatus.setText("duplicate slot name " + name);
            labelCreateSlotStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }

        if (radioButtonUnlimited.isSelected())
            count = 0;
        else
            count = Integer.parseInt(textFieldSlotCount.getText());
        ArrayList<String> parts = new ArrayList<>();
        Slot newSlot = new Slot(name, type, count, description, parts);
        slotHashMap.put(newSlot.getId(), newSlot);
        Part select = (Part) selectedTreeItem.getValue();
        int id = select.getId();
        Part part = partHashMap.get(id);
        ArrayList<String> slotList = part.getSlots();
        slotList.add(name);

        partHashMap.replace(id, part);
        labelCreateSlotStatus.setText("Slot " + name + " created");
        labelCreateSlotStatus.setStyle("-fx-text-fill: status-good-color");

    }

    public void ButtonCreateSlotDoneOnAction() {
        Stage stage = (Stage) buttonCreateSlotDone.getScene().getWindow();
        stage.close();
    }
}
