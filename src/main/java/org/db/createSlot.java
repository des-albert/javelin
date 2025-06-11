package org.db;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import static org.db.javelin.partHashMap;
import static org.db.javelin.slotHashMap;
import static org.db.javelin.selectedTreeItem;

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

        if (slotHashMap.containsKey(name)) {
            labelCreateSlotStatus.setText("duplicate slot name %s".formatted(name));
            labelCreateSlotStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }

        if (radioButtonUnlimited.isSelected())
            count = 0;
        else
            count = Integer.parseInt(textFieldSlotCount.getText());
        ArrayList<String> parts = new ArrayList<>();
        Slot newSlot = new Slot(name, type, count, description, parts);
        slotHashMap.put(newSlot.getName(), newSlot);
        Part select = (Part) selectedTreeItem.getValue();

        Part part = partHashMap.get(select.getCode());
        ArrayList<String> slotList = part.getSlots();
        slotList.add(name);

        partHashMap.replace(select.getCode(), part);
        labelCreateSlotStatus.setText("Slot %s created".formatted(name));
        labelCreateSlotStatus.setStyle("-fx-text-fill: status-good-color");

    }

    public void ButtonCreateSlotDoneOnAction() {
        Stage stage = (Stage) buttonCreateSlotDone.getScene().getWindow();
        stage.close();
    }
}
