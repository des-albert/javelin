package org.db;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

import static org.db.javelin.partHashMap;
import static org.db.javelin.selectedTreeItem;

public class createPart {

    public ToggleButton toggle0D1;
    @FXML
    Button buttonAddPartDone, buttonAddPart;
    @FXML
    TextField textFieldPartCode, textFieldPartDescription, textFieldPartCat, textFieldPartHint;
    @FXML
    Label labelAddStatus;

    public void initialize() {
        Folder folder = (Folder) selectedTreeItem.getValue();
        textFieldPartCat.setText(folder.getCategory());
    }

    public void ButtonAddPartAction() {

        String code = textFieldPartCode.getText();
        String description = textFieldPartDescription.getText();
        String category = textFieldPartCat.getText();
        String hint = textFieldPartHint.getText();
        Boolean od1 = Objects.equals(toggle0D1.getText(), "0D1");

        if (code.isEmpty() || description.isEmpty() || category.isEmpty()) {
            labelAddStatus.setText("Fields cannot be NULL");
            labelAddStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }

        ArrayList<String> slots = new ArrayList<>();
        Part newPart = new Part(code, description, category, hint, slots, od1);
        if (partHashMap.containsKey(newPart.getCode())) {
            labelAddStatus.setText("Duplicate part code %s".formatted(code));
            labelAddStatus.setStyle("-fx-text-fill: status-error-color");
        } else {
            partHashMap.put(code, newPart);
            labelAddStatus.setText("New Part %s - %s created".formatted(code, description));
            labelAddStatus.setStyle("-fx-text-fill: status-good-color");
        }
    }

    public void toggle0D1Action() {
        if (toggle0D1.isSelected())
            toggle0D1.setText("");
        else
            toggle0D1.setText("0D1");
    }
    public void ButtonAddPartDoneAction() {
        Stage stage = (Stage) buttonAddPartDone.getScene().getWindow();
        stage.close();
    }

}
