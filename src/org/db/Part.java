package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class Part implements Serializable {

    private int id;
    private String code;
    private String description;
    private String category;
    private String hint;
    private ArrayList<String> slots;

    Part(String code, String description, String category, String hint, ArrayList<String> slots) {
        this.id = code.hashCode();
        this.code = code;
        this.category = category;
        this.description = description;
        this.hint = hint;
        this.slots = slots;
    }

    int getId() { return id; }
    String getCode() { return code; }
    String getDescription() {return description; }
    String getHint() {return hint; }
    String getCategory() { return category; }
    ArrayList<String> getSlots() { return slots; }
}
