package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class Part implements Serializable {

    private final String code;
    private final String description;
    private final String category;
    private final String hint;
    private final ArrayList<String> slots;

    Part(String code, String description, String category, String hint, ArrayList<String> slots) {
        this.code = code;
        this.category = category;
        this.description = description;
        this.hint = hint;
        this.slots = slots;
    }

    String getCode() { return code; }
    String getDescription() {return description; }
    String getHint() {return hint; }
    String getCategory() { return category; }
    ArrayList<String> getSlots() { return slots; }
}
