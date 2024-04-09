package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class Slot implements Serializable {

    private final int id;
    private final String name;
    private final String type;
    private final int quantity;
    private final String description;
    private final ArrayList<String> parts;

    Slot(String name, String type, int quantity, String description, ArrayList<String> parts) {
        this.id = name.hashCode();
        this.name = name;
        this.type = type;
        this.quantity =  quantity;
        this.description = description;
        this.parts = parts;
    }

    int getId() { return id; }
    String getName() { return name; }
    String getType() { return type; }
    int getQuantity() { return quantity; }
    String getDescription() { return description; }
    ArrayList<String> getParts() { return parts;}
}
