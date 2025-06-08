package org.db;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

import static org.db.javelin.partHashMap;
import static org.db.javelin.selectedTreeItem;

public class createPart {

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


        if (code.isEmpty() || description.isEmpty() || category.isEmpty()) {
            labelAddStatus.setText("Fields cannot be NULL");
            labelAddStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }

        ArrayList<String> slots = new ArrayList<>();
        Part newPart = new Part(code, description, category, hint, slots);
        if (partHashMap.containsKey(newPart.hashCode())) {
            labelAddStatus.setText("Duplicate part code %s".formatted(code));
            labelAddStatus.setStyle("-fx-text-fill: status-error-color");
        } else {
            partHashMap.put(code.hashCode(), newPart);
            labelAddStatus.setText("New Part %s - %s created".formatted(code, description));
            labelAddStatus.setStyle("-fx-text-fill: status-good-color");
        }
    }

    public void ButtonAddPartDoneAction() {
        Stage stage = (Stage) buttonAddPartDone.getScene().getWindow();
        stage.close();
    }

}
