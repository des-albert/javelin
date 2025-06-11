package org.db;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.util.HashMap;

import static org.db.javelin.buildHashMap;
import static org.db.javelin.selectedTreeItem;

public class changeQty {

    @FXML
    TextField textFieldNewQuantity;
    @FXML
    Label labelCurrentQuantity, labelPartCode, labelStatus;
    @FXML
    Button buttonChangeQtyDone, buttonChangeQty;

    buildPart part;
    int prevTotal, prevQty;


    public void initialize() {
        part = (buildPart) selectedTreeItem.getValue();
        prevQty = part.getBuildCount();
        prevTotal = part.getTotalCount();
        labelCurrentQuantity.setText(Integer.toString(prevQty));
        labelPartCode.setText(part.getCode());
    }

    public void ButtonChangeQtyAction() {

        String quantity = textFieldNewQuantity.getText();
        if (quantity.isEmpty()) {
            labelStatus.setText("quantity cannot be NULL");
            labelStatus.setStyle("-fx-text-fill: status-error-color");
            return;
        }
        int nextQty = Integer.parseInt(textFieldNewQuantity.getText());
        part.setBuildCount(nextQty);

        /* Update totalCount of child Parts */

        updateTree(selectedTreeItem, buildHashMap, nextQty, prevQty);

    }

    private void updateTree(TreeItem<Object> item, HashMap<String, TreeItem<Object>> map, int next, int prev) {
        updateTotal(item, next, prev);

        for (TreeItem<Object> child : item.getChildren()) {
            if (child.getChildren().isEmpty()) {
                updateTotal(child, next, prev);
            } else {
                updateTree(child, map, next, prev);
            }
        }
    }

    private void updateTotal(TreeItem<Object> item, int after, int before) {
        if (item.getValue().getClass() == buildPart.class) {
            buildPart part = (buildPart) item.getValue();
            int beforeTotal = part.getTotalCount();
            part.setTotalCount(beforeTotal * after / before);
        }
    }

    public void ButtonChangeQtyDoneAction() {
        Stage stage = (Stage) buttonChangeQtyDone.getScene().getWindow();
        stage.close();
    }

}
