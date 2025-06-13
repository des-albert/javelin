package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class buildPart implements Serializable {

    private final String code;
    private final String description;
    private final String category;
    private final ArrayList<String> slots;
    private final Boolean od1;

    private String parent;
    private int buildCount;
    private int totalCount;

    buildPart(String code, String description, String category, ArrayList<String> slots, Boolean od1) {
        this.code = code;
        this.description = description;
        this.category = category;
        this.slots = slots;
        this.od1 = od1;
    }

    buildPart(Part part) {
        code = part.getCode();
        description = part.getDescription();
        category = part.getCategory();
        slots = part.getSlots();
        od1 = part.getOd1();
        parent = "0";
        buildCount = 0;
        totalCount = 0;
    }


    String getCode() {
        return code;
    }

    String getDescription() {
        return description;
    }

    String getCategory() {
        return category;
    }

    ArrayList<String> getSlots() {
        return slots;
    }

    Boolean getOd1() { return od1; }

    String getParent() {
        return parent;
    }

    void setParent(String item) {
        parent = item;
    }

    int getBuildCount() { return buildCount; }

    void setBuildCount(int count) {
        buildCount = count;
    }

    int getTotalCount() {
        return totalCount;
    }

    void setTotalCount(int count) {
        totalCount = count;
    }
}
