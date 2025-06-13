package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class Part implements Serializable {

    private final String code;
    private final String description;
    private final String category;
    private final String hint;
    private final ArrayList<String> slots;
    private final Boolean od1;

    Part(String code, String description, String category, String hint, ArrayList<String> slots, Boolean od1) {
        this.code = code;
        this.category = category;
        this.description = description;
        this.hint = hint;
        this.slots = slots;
        this.od1 = od1;
    }

    String getCode() { return code; }
    String getDescription() {return description; }
    String getHint() {return hint; }
    String getCategory() { return category; }
    ArrayList<String> getSlots() { return slots; }
    Boolean getOd1() { return od1; }
}
