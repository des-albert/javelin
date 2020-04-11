package org.db;

import java.io.Serializable;
import java.util.ArrayList;

public class buildPart implements Serializable {

    private String code;
    private String description;
    private String category;
    private ArrayList<String> slots;

    private int id;
    private int parentId;
    private int buildCount;
    private int totalCount;

    buildPart(String code, String description, String category, ArrayList<String> slots) {
        this.id = code.hashCode();
        this.code = code;
        this.description = description;
        this.category = category;
        this.slots = slots;
    }

    buildPart(Part part) {
        id = part.getId();
        code = part.getCode();
        description = part.getDescription();
        category = part.getCategory();
        slots = part.getSlots();
        parentId = 0;
        buildCount = 0;
        totalCount = 0;
    }

    int getId() { return id; }
    String getCode() { return code; }
    String getDescription() {return description; }
    String getCategory() { return category; }
    ArrayList<String> getSlots() { return slots; }

    void setParentId(int id) {parentId = id;}
    void setBuildCount(int count) {buildCount = count;}
    void setTotalCount(int count) {totalCount = count;}

    int getParentId() { return parentId; }
    int getBuildCount() { return buildCount; }
    int getTotalCount() { return totalCount; }
}
