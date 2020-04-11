package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class buildSlot implements Serializable {

    private String name;
    private String type;
    private int quantity;
    private String description;
    private ArrayList<String> parts;
    private int id;
    private int parentId;
    private int content;

    buildSlot(Slot slot) {
        id = slot.getId();
        name = slot.getName();
        type = slot.getType();
        quantity = slot.getQuantity();
        description = slot.getDescription();
        parts = slot.getParts();
        parentId = 0;
        content = 0;
    }

    int getId() { return id; }
    String getName() { return name; }
    String getType() { return type; }
    String getDescription() { return description; }
    int getQuantity() { return quantity; }
    ArrayList<String> getParts() { return parts;}

    void setContent(int quantity) {
        content = quantity;
    }
    void setParentId(int id) {parentId = id;}
    int getContent() {
        return content;
    }
    int getParentId() { return parentId; }
}

