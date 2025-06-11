package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class buildSlot implements Serializable {

    private final String name;
    private final String type;
    private final int quantity;
    private final ArrayList<String> parts;
    private String parent;
    private int content;

    buildSlot(Slot slot) {

        name = slot.getName();
        type = slot.getType();
        quantity = slot.getQuantity();
        String description = slot.getDescription();
        parts = slot.getParts();
        parent = "";
        content = 0;
    }


    String getName() {
        return name;
    }

    String getType() {
        return type;
    }

    int getQuantity() {
        return quantity;
    }

    ArrayList<String> getParts() {
        return parts;
    }

    int getContent() {
        return content;
    }

    void setContent(int quantity) {
        content = quantity;
    }

    String getParent() {
        return parent;
    }

    void setParent(String item) {
        parent = item;
    }
}

