package org.db;


import com.google.gson.*;

import com.google.gson.stream.JsonReader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.util.*;

import static com.google.gson.JsonParser.parseReader;
import static org.db.javelinMain.getPrimaryStage;

public class javelin {

    static HashMap<Integer, Part> partHashMap = new HashMap<>();
    static HashMap<Integer, Slot> slotHashMap = new HashMap<>();
    static HashMap<Integer, TreeItem<Object>> buildHashMap = new HashMap<>();
    static TreeItem<Object> selectedTreeItem;
    private final HashMap<String, TreeItem<Object>> catHashMap = new HashMap<>();
    private final ContextMenu partContext = new ContextMenu();
    private final ContextMenu folderContext = new ContextMenu();
    private final ContextMenu quantityContext = new ContextMenu();

    private static final Logger logger = LogManager.getLogger(javelin.class);

    @FXML
    Button buttonQuit;
    @FXML
    TabPane tabPaneMain;
    @FXML
    Label labelStatus, labelJDK, labelJavaFX;
    @FXML
    TreeView<Object> treeViewPart, treeViewBuild;
    @FXML
    ImageView imageViewTrash;
    @FXML
    ToggleButton togglePart, toggleBuild;
    private TreeItem<Object> partTreeRootItem, buildTreeRootItem;
    private DataFormat dfPart, dfSlot, dfBuild, dfParentPart, dfParentSlot;

    @FXML
    private void partDragDetected(MouseEvent event) {
        TreeItem<Object> source, target;
        if (event.isDragDetect()) {
            if (treeViewPart.getSelectionModel().getSelectedItem() != null) {
                source = treeViewPart.getSelectionModel().getSelectedItem();
                Object object = source.getValue();
                Dragboard db = treeViewPart.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                ClipboardContent content = new ClipboardContent();


                /* Drag a Part */

                if (object.getClass() == Part.class) {
                    content.put(dfPart, object);
                    target = source.getParent();
                    Object parentObj = target.getValue();
                    if (parentObj.getClass() != Folder.class) {
                        content.put(dfParentSlot, parentObj);
                    }
                }

                /* Drag a Slot */

                else if (object.getClass() == Slot.class) {
                    content.put(dfSlot, object);
                    target = source.getParent();
                    Object parentObj = target.getValue();
                    content.put(dfParentPart, parentObj);
                }
                db.setContent(content);
            }
            event.consume();

        }
    }

    @FXML
    private void buildDragDetected(MouseEvent event) {
        TreeItem<Object> source, target;
        if (event.isDragDetect()) {
            if (treeViewBuild.getSelectionModel().getSelectedItem() != null) {
                source = treeViewBuild.getSelectionModel().getSelectedItem();
                Object object = source.getValue();
                Dragboard db = treeViewBuild.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                ClipboardContent content = new ClipboardContent();

                /* Drag a build Part */

                if (object.getClass() == buildPart.class) {
                    content.put(dfBuild, object);
                    target = source.getParent();
                    Object parentObj = target.getValue();
                    content.put(dfParentSlot, parentObj);
                } else if (object.getClass() == buildSlot.class) {
                    labelStatus.setText("build slot cannot be removed");
                    labelStatus.getStyleClass().removeFirst();
                    labelStatus.getStyleClass().addFirst("label-failure");
                }
                db.setContent(content);
            }
            event.consume();
        }
    }

    public void initialize() {
        labelJDK.setText("Java SDK version %s".formatted(Runtime.version()));
        labelJavaFX.setText("JavaFX version %s".formatted(System.getProperties().get("javafx.runtime.version")));

        start();
        definePartsTreeView();
        defineTrash();
        defineBuildTreeView();
        addParts();
        loadPartTree();
        addContext();
    }


    private void start() {

        dfPart = new DataFormat("part");
        dfSlot = new DataFormat("slot");
        dfBuild = new DataFormat("build");
        dfParentPart = new DataFormat("parent part");
        dfParentSlot = new DataFormat("parent slot");

    }

    /* Initialize Parts treeView */

    private void definePartsTreeView() {
        Folder partRoot = new Folder("parts");
        partTreeRootItem = new TreeItem<>(partRoot);
        treeViewPart.setRoot(partTreeRootItem);
        treeViewPart.setShowRoot(false);

        /* Cell Factory for Parts treeView */

        treeViewPart.setCellFactory(cellData -> {
            final Tooltip tooltip = new Tooltip();
            TreeCell<Object> cell = new TreeCell<>() {
                @Override
                protected void updateItem(Object object, boolean empty) {
                    super.updateItem(object, empty);
                    if (empty || object == null) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {
                        if (object.getClass() == Folder.class) {
                            Folder folder = (Folder) object;
                            setText(folder.getCategory());
                            setStyle("-fx-text-fill: folder-leaf-color");
                            setContextMenu(folderContext);
                        } else {

                            /* Render part */

                            if (object.getClass() == Part.class) {
                                Part part = (Part) object;
                                setText(part.getDescription());
                                Object parent = this.getTreeItem().getParent().getValue();
                                if (parent.getClass() == Folder.class)
                                    setStyle("-fx-text-fill: part-leaf-color");
                                else
                                    setStyle("-fx-text-fill: part-link-color");
                                setGraphic(getTreeItem().getGraphic());
                                tooltip.setText(part.getCode() + " - " + part.getHint());
                                setTooltip(tooltip);
                                setContextMenu(partContext);
                            }

                            /* Render slot */

                            else if (object.getClass() == Slot.class) {
                                Slot slot = (Slot) object;
                                setText(slot.getName());
                                setStyle("-fx-text-fill: slot-leaf-color");
                                tooltip.setText(slot.getType() + "-" + slot.getQuantity() + " " + slot.getDescription());
                                setTooltip(tooltip);
                            }
                        }
                    }
                }

            };

            cell.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                TreeItem<Object> target = cell.getTreeItem();
                Object object = target.getValue();
                if (object.getClass() == Slot.class) {
                    if (event.getDragboard().hasContent(dfPart)) {

                        /* Add Part to Slot */

                        Part part = (Part) event.getDragboard().getContent(dfPart);
                        String code = part.getCode();

                        Slot slot = (Slot) object;

                        if (!slot.getParts().contains(code)) {
                            slot.getParts().add(code);
                            slotHashMap.replace(slot.getId(), slot);
                            loadPartTree();
                            labelStatus.setText("part " + code + " added to slot " + slot.getName());
                            labelStatus.getStyleClass().removeFirst();
                            labelStatus.getStyleClass().addFirst("label-success");
                        }
                    }
                } else if (object.getClass() == Part.class) {
                    if (event.getDragboard().hasContent(dfSlot)) {

                        /* Add Slot to Part */

                        Slot slot = (Slot) event.getDragboard().getContent(dfSlot);
                        Part part = (Part) object;
                        if (!part.getSlots().contains(slot.getName())) {
                            part.getSlots().add(slot.getName());
                            partHashMap.replace(part.getId(), part);
                            loadPartTree();
                            labelStatus.setText("slot " + slot + " added to part " + part.getCode());
                            labelStatus.getStyleClass().removeFirst();
                            labelStatus.getStyleClass().addFirst("label-success");
                        }

                    }
                }
                event.setDropCompleted(true);
                event.consume();
            });

            return cell;

        });
    }

    private void defineTrash() {

        /* Trash image handler */

        imageViewTrash.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });
        imageViewTrash.setOnDragEntered(event -> {
            String iconPath = "/img/glass trash.png";
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            imageViewTrash.setImage(icon);
            event.consume();
        });

        imageViewTrash.setOnDragExited(event -> {
            String iconPath = "/img/trash.png";
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            imageViewTrash.setImage(icon);
            event.consume();
        });

        imageViewTrash.setOnDragDropped(event -> {

            if (event.getDragboard().hasContent(dfPart)) {
                if (event.getDragboard().hasContent(dfParentSlot)) {

                    /* Remove Part from Slot in Part TreeView  */

                    Part part = (Part) event.getDragboard().getContent(dfPart);
                    Slot slot = (Slot) event.getDragboard().getContent(dfParentSlot);

                    ArrayList<String> slotParts = slot.getParts();
                    String code = part.getCode();

                    if (slotParts.contains(code)) {
                        slotParts.remove(code);
                        slotHashMap.replace(slot.getId(), slot);
                        loadPartTree();
                    }

                    labelStatus.setText("part " + part.getCode() + " removed from slot " + slot.getName());
                    labelStatus.getStyleClass().removeFirst();
                    labelStatus.getStyleClass().addFirst("label-success");
                }

                /* Delete part */

                else {

                    Part part = (Part) event.getDragboard().getContent(dfPart);
                    String code = part.getCode();
                    partHashMap.remove(code.hashCode());

                    /* Remove part from containing slots */

                    for (Slot slot : slotHashMap.values()) {
                        if (slot.getParts().contains(code)) {
                            ArrayList<String> slotParts = slot.getParts();
                            slotParts.remove(code);
                            slotHashMap.replace(slot.getId(), slot);
                        }
                    }
                    loadPartTree();
                }

            } else if (event.getDragboard().hasContent(dfSlot)) {

                /* Remove Slot from Part */

                Slot slot = (Slot) event.getDragboard().getContent(dfSlot);
                Part part = (Part) event.getDragboard().getContent(dfParentPart);
                ArrayList<String> partSlots = part.getSlots();
                partSlots.remove(slot.getId());
                loadPartTree();


                /* Remove build part */

            } else if (event.getDragboard().hasContent(dfBuild)) {
                buildPart part = (buildPart) event.getDragboard().getContent(dfBuild);
                buildSlot slot = (buildSlot) event.getDragboard().getContent(dfParentSlot);
                TreeItem<Object> parentItem = buildHashMap.get(slot.getId());
                TreeItem<Object> partItem = buildHashMap.get(part.getId());
                parentItem.getChildren().remove(partItem);
                buildHashMap.remove(part.getId());

                labelStatus.setText(slot.getContent() + " parts removed from slot " + slot.getName());
                labelStatus.getStyleClass().removeFirst();
                labelStatus.getStyleClass().addFirst("label-success");

                slot.setContent(0);
                parentItem.setValue(slot);
                treeViewBuild.refresh();

            }

            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void defineBuildTreeView() {

        ArrayList<String> parts = new ArrayList<>();
        Slot buildSlot = new Slot("build", "U", 0, "", parts);
        slotHashMap.put("build".hashCode(), buildSlot);
        ArrayList<String> slots = new ArrayList<>();
        slots.add("build");
        buildPart buildPart = new buildPart("build", "build", "", slots);

        buildTreeRootItem = new TreeItem<>(buildPart);
        treeViewBuild.setRoot(buildTreeRootItem);
        treeViewBuild.setShowRoot(true);

        /* Cell Factory for Build treeView */

        treeViewBuild.setCellFactory(cellData -> {
            final Tooltip tooltip = new Tooltip();
            TreeCell<Object> cell = new TreeCell<>() {
                @Override
                protected void updateItem(Object object, boolean empty) {
                    super.updateItem(object, empty);
                    if (empty || object == null) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {
                        if (object.getClass() == buildPart.class) {
                            buildPart part = (buildPart) object;
                            tooltip.setText(part.getTotalCount() + " x " + part.getCode());
                            setTooltip(tooltip);
                            if (part.getBuildCount() > 1)
                                setText(part.getBuildCount() + " x " + part.getDescription());
                            else
                                setText(part.getDescription());
                            setStyle("-fx-text-fill: part-leaf-color");
                            setGraphic(getTreeItem().getGraphic());
                            setContextMenu(quantityContext);
                        } else if (object.getClass() == buildSlot.class) {
                            buildSlot slot = (buildSlot) object;
                            setText(slot.getName());
                            setStyle("-fx-text-fill: slot-leaf-color");
                            ArrayList<String> parts = slot.getParts();
                            StringBuilder toolText = new StringBuilder();
                            if (slot.getType().equals("E"))
                                toolText = new StringBuilder("exact - " + slot.getQuantity() + "\n");
                            else if (slot.getType().equals("M"))
                                toolText = new StringBuilder("maximum - " + slot.getQuantity() + "\n");
                            toolText.append("current - ");
                            toolText.append(slot.getContent());
                            toolText.append("\n");
                            for (String code : parts) {
                                Part p = partHashMap.get(code.hashCode());
                                toolText.append(p.getDescription());
                                toolText.append("\n");
                            }
                            tooltip.setText(toolText.toString());
                            setTooltip(tooltip);
                        }

                    }
                }
            };

            cell.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                TreeItem<Object> target = cell.getTreeItem();
                if (event.getDragboard().hasContent(dfPart)) {
                    Object object = target.getValue();
                    Part part = (Part) event.getDragboard().getContent(dfPart);

                    /* Add Part to Build Base */

                    if (object.getClass() == buildPart.class) {
                        buildPart targetPart = (buildPart) object;
                        int addQty = 0;

                        Optional<String> addCount;
                        TextInputDialog countDialog = new TextInputDialog();
                        countDialog.setHeaderText("enter part quantity:");
                        addCount = countDialog.showAndWait();
                        if (addCount.isPresent())
                            addQty = Integer.parseInt(addCount.get());
                        buildPart addPart = new buildPart(part);
                        addPart.setBuildCount(addQty);
                        addPart.setTotalCount(addQty);
                        addPart.setParentId(targetPart.getId());
                        TreeItem<Object> partTreeItem = newTreeItem(addPart);
                        buildHashMap.put(addPart.getId(), partTreeItem);
                        buildTreeRootItem.getChildren().add(partTreeItem);

                        if (!addPart.getSlots().isEmpty()) {
                            for (String slotName : addPart.getSlots()) {
                                Slot slot = slotHashMap.get(slotName.hashCode());
                                buildSlot addSlot = new buildSlot(slot);
                                addSlot.setParentId(addPart.getId());
                                TreeItem<Object> slotTreeItem = newTreeItem(addSlot);
                                buildHashMap.put(addSlot.getId(), slotTreeItem);
                                partTreeItem.getChildren().add(slotTreeItem);
                            }
                        }
                        labelStatus.setText(addQty + " " + addPart.getCode() + " parts added to base ");
                        labelStatus.getStyleClass().removeFirst();
                        labelStatus.getStyleClass().addFirst("label-success");

                    }

                    /* Add Parts to Slot */

                    else {
                        buildSlot targetSlot = (buildSlot) object;
                        if (!targetSlot.getParts().isEmpty()) {
                            ArrayList<String> slotParts = targetSlot.getParts();
                            if (slotParts.contains(part.getCode())) {
                                int addQty = 0;
                                buildPart addPart = new buildPart(part);
                                int currentQty = targetSlot.getContent();
                                int maxQty = targetSlot.getQuantity();
                                if (targetSlot.getType().equals("U") || currentQty < maxQty) {
                                    if (targetSlot.getType().equals("E")) {
                                        addQty = targetSlot.getQuantity();
                                    } else {
                                        Optional<String> addCount;
                                        TextInputDialog countDialog = new TextInputDialog();

                                        int limitQty = maxQty - currentQty;
                                        if (targetSlot.getType().equals("U"))
                                            countDialog.setHeaderText("enter part quantity :");
                                        else
                                            countDialog.setHeaderText("enter part quantity <= " + limitQty + " :");
                                        addCount = countDialog.showAndWait();
                                        if (addCount.isPresent())
                                            addQty = Integer.parseInt(addCount.get());
                                        if ((targetSlot.getType().equals("M")) && (addQty > limitQty))
                                            addQty = limitQty;
                                    }

                                    addPart.setBuildCount(addQty);
                                    buildPart targetPart = (buildPart) target.getParent().getValue();
                                    addPart.setTotalCount(targetPart.getTotalCount() * addQty);
                                    targetSlot.setContent(currentQty + addQty);
                                    addPart.setParentId(targetSlot.getId());
                                    TreeItem<Object> partTreeItem = newTreeItem(addPart);
                                    buildHashMap.put(addPart.getId(), partTreeItem);
                                    target.getChildren().add(partTreeItem);

                                    /* Add slots to new part */

                                    if (!addPart.getSlots().isEmpty()) {
                                        for (String slotName : addPart.getSlots()) {
                                            Slot slot = slotHashMap.get(slotName.hashCode());
                                            buildSlot addSlot = new buildSlot(slot);
                                            addSlot.setParentId(addPart.getId());
                                            TreeItem<Object> slotTreeItem = newTreeItem(addSlot);
                                            buildHashMap.put(addSlot.getId(), slotTreeItem);
                                            partTreeItem.getChildren().add(slotTreeItem);
                                        }
                                    }
                                    labelStatus.setText(addQty + " parts added to slot " + targetSlot.getName());
                                    labelStatus.getStyleClass().removeFirst();
                                    labelStatus.getStyleClass().addFirst("label-success");
                                }
                            }
                        }
                    }
                }
                event.setDropCompleted(true);
                event.consume();

            });
            return cell;
        });

    }

    /* Load parts from JSON file 'parts.json' into HashMap */

    private void addParts() {
        Gson gson = new Gson();
        try {
            FileReader fr = new FileReader("parts.json");
            JsonReader jsonReader = new JsonReader(fr);
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Part part = gson.fromJson(parseReader(jsonReader), Part.class);
                partHashMap.put(part.getId(), part);
                logger.info("addParts - hash %d %d part %s".formatted(part.getId(), part.getCode().hashCode(), part.getDescription()));
            }
            jsonReader.endArray();

            fr = new FileReader("slots.json");
            jsonReader = new JsonReader(fr);
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Slot slot = gson.fromJson(parseReader(jsonReader), Slot.class);
                slotHashMap.put(slot.getId(), slot);
                logger.info("addSlots - hash %d %d part %s".formatted(slot.getId(), slot.getName().hashCode(), slot.getDescription()));
            }
            jsonReader.endArray();
            fr.close();

        } catch (IOException e) {
            logger.error("addParts - {}", e.getMessage());
        }

    }

    private void loadPartTree() {

        String category;
        partTreeRootItem.getChildren().clear();
        catHashMap.clear();
        TreeItem<Object> treeItem;
        Slot slot;

        for (Part part : partHashMap.values()) {
            category = part.getCategory();
            treeItem = catHashMap.get(category);
            if (treeItem == null) {
                Folder folder = new Folder(category);
                treeItem = new TreeItem<>(folder);
                catHashMap.put(category, treeItem);
                treeViewPart.getRoot().getChildren().add(treeItem);
            }
            TreeItem<Object> leafItem = newTreeItem(part);
            treeItem.getChildren().add(leafItem);

            // Add Slots to Part

            if (!part.getSlots().isEmpty()) {
                for (String item : part.getSlots()) {
                    if ((slot = slotHashMap.get(item.hashCode())) != null) {
                        TreeItem<Object> slotItem = newTreeItem(slot);
                        leafItem.getChildren().add(slotItem);

                        // Add Parts to each Slot

                        if (!slot.getParts().isEmpty()) {
                            for (String code : slot.getParts()) {
                                Part slotPart = partHashMap.get(code.hashCode());
                                TreeItem<Object> slotPartItem = newTreeItem(slotPart);
                                slotItem.getChildren().add(slotPartItem);
                            }
                        }
                    }
                }
            }
        }
    }

    private TreeItem<Object> newTreeItem(Object object) {
        TreeItem<Object> treeItem;
        if (object.getClass() == Part.class) {
            Part part = (Part) object;
            String image = part.getCategory();
            String iconPath = "/img/%s.png".formatted(image);
            InputStream iconStream = getClass().getResourceAsStream(iconPath);
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                treeItem = new TreeItem<>(part, new ImageView(icon));
            } else
                treeItem = new TreeItem<>(part);
        } else if (object.getClass() == buildPart.class) {
            buildPart buildPart = (buildPart) object;
            String image = buildPart.getCategory();
            String iconPath = "/img/" + image + ".png";
            InputStream iconStream = getClass().getResourceAsStream(iconPath);
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                treeItem = new TreeItem<>(buildPart, new ImageView(icon));
            } else
                treeItem = new TreeItem<>(buildPart);
        } else {
            treeItem = new TreeItem<>(object);
        }

        return treeItem;
    }


    /* Context Menus */

    private void addContext() {

        /*  Create new part context menu */

        MenuItem newPart = new MenuItem("create part");
        newPart.setOnAction(e -> {
            selectedTreeItem = treeViewPart.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlFormLoader = new FXMLLoader(getClass().getResource("createPart.fxml"));
                Parent partForm = fxmlFormLoader.load();
                Stage partStage = new Stage();
                partStage.setOnHiding(event -> loadPartTree());
                partStage.setTitle("Create New Part");
                partStage.setScene(new Scene(partForm));
                partStage.show();
            } catch (IOException ex) {
                labelStatus.setText(ex.getMessage());
                labelStatus.getStyleClass().removeFirst();
                labelStatus.getStyleClass().addFirst("label-failure");
            }
        });

        /*  Create new slot context menu */

        MenuItem newSlot = new MenuItem("create slot");
        newSlot.setOnAction(e -> {
            selectedTreeItem = treeViewPart.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlFormLoader = new FXMLLoader(getClass().getResource("createSlot.fxml"));
                Parent slotForm = fxmlFormLoader.load();
                Stage slotStage = new Stage();
                slotStage.setOnHiding(event -> loadPartTree());
                slotStage.setTitle("create new slot");
                slotStage.setScene(new Scene(slotForm));
                slotStage.show();

            } catch (IOException ex) {
                labelStatus.setText(ex.getMessage());
                labelStatus.getStyleClass().removeFirst();
                labelStatus.getStyleClass().addFirst("label-failure");
            }
        });

        /*  Create Build Part context menu */

        MenuItem partQty = new MenuItem("change quantity");
        partQty.setOnAction(e -> {
            selectedTreeItem = treeViewBuild.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlFormLoader = new FXMLLoader(getClass().getResource("changeQty.fxml"));
                Parent qtyForm = fxmlFormLoader.load();
                Stage qtyStage = new Stage();
                qtyStage.setOnHiding(event -> loadBuildTree());
                qtyStage.setTitle("change part quantity");
                qtyStage.setScene(new Scene(qtyForm));
                qtyStage.show();

            } catch (IOException ex) {
                labelStatus.setText(ex.getMessage());
                labelStatus.getStyleClass().removeFirst();
                labelStatus.getStyleClass().addFirst("label-failure");
            }

        });


        quantityContext.getItems().add(partQty);
        folderContext.getItems().add(newPart);
        partContext.getItems().add(newSlot);
    }

    void loadBuildTree() {
        for (TreeItem<Object> item : buildTreeRootItem.getChildren()) {
            item.setExpanded(true);
        }
        treeViewBuild.refresh();
    }

    /* Button Actions */

    public void ButtonQuitOnAction() {

        saveParts();
        Stage stage = (Stage) buttonQuit.getScene().getWindow();
        stage.close();
    }

    public void ButtonPartCollapseOnAction() {
        if (togglePart.isSelected()) {
            for (TreeItem<Object> item : partTreeRootItem.getChildren()) {
                item.setExpanded(true);
            }
            togglePart.setText("collapse");
        } else {
            for (TreeItem<Object> item : partTreeRootItem.getChildren()) {
                item.setExpanded(false);
            }
            togglePart.setText("expand");
        }

    }

    public void ButtonBuildCollapseOnAction() {
        if (toggleBuild.isSelected()) {
            expandTreeView(buildTreeRootItem, true);
            toggleBuild.setText("collapse");
        } else {
            expandTreeView(buildTreeRootItem, false);
            toggleBuild.setText("expand");
        }

    }

    private void expandTreeView(TreeItem<?> item, boolean expand) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(expand);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child, expand);
            }
        }
    }

    /* Save parts and slots */

    private void saveParts() {
        writeHashMap(partHashMap, "parts.json");
        writeHashMap(slotHashMap, "slots.json");
    }

    private void writeHashMap(HashMap<Integer, ?> map, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(filename);
        try {
            if (!file.exists())
                if (!file.createNewFile()) {
                    labelStatus.setText("file create error " + filename);
                    labelStatus.getStyleClass().removeFirst();
                    labelStatus.getStyleClass().addFirst("label-failure");
                }
            FileWriter fw = new FileWriter(filename);
            JsonArray jsonArray = new JsonArray();
            for (Object obj : map.values()) {
                JsonElement element = gson.toJsonTree(obj);
                jsonArray.add(element);
            }
            gson.toJson(jsonArray, fw);
            fw.close();
        } catch (IOException e) {
            logger.error("writeHashMap - {}", e.getMessage());
        }
    }

    public void ButtonSaveConfigOnAction() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file (*.json)", "*.json"));
        fileChooser.setTitle("save json file");
        File jsonFile = fileChooser.showSaveDialog(getPrimaryStage());
        Gson gson = new Gson();
        try {
            if (jsonFile.exists()) {
                if (!jsonFile.delete()) {
                    labelStatus.setText("file delete error " + jsonFile);
                    labelStatus.getStyleClass().removeFirst();
                    labelStatus.getStyleClass().addFirst("label-failure");
                }
            }
            if (jsonFile.createNewFile()) {

                FileWriter fw = new FileWriter(jsonFile);
                BufferedWriter bw = new BufferedWriter(fw);
                saveTree(buildTreeRootItem, bw, gson);

                bw.close();
                fw.close();
            }


        } catch (IOException e) {
            logger.error("Save Config - {}", e.getMessage());
        }
    }

    private void saveTree(TreeItem<Object> item, BufferedWriter bw, Gson gs) {
        try {
            String json = gs.toJson(item.getValue());
            bw.write(json);
            bw.newLine();
            for (TreeItem<Object> cp : item.getChildren()) {
                if (cp.getChildren().isEmpty()) {
                    json = gs.toJson(cp.getValue());
                    bw.write(json);
                    bw.newLine();
                } else
                    saveTree(cp, bw, gs);
            }

        } catch (IOException ex) {
            labelStatus.setText(ex.getMessage());
            labelStatus.getStyleClass().removeFirst();
            labelStatus.getStyleClass().addFirst("label-failure");
        }
    }


    public void ButtonLoadConfigOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file (*.json)", "*.json"));
        fileChooser.setTitle("open json file");
        File jsonFile = fileChooser.showOpenDialog(getPrimaryStage());
        Gson gson = new Gson();
        try {

            FileReader fr = new FileReader(jsonFile);
            BufferedReader br = new BufferedReader(fr);

            readTree(br, gson);
            br.close();
            fr.close();

        } catch (IOException e) {
            logger.error("LoadConfig - {}", e.getMessage());
        }
    }

    private void readTree(BufferedReader br, Gson gson) {
        String line;
        try {
            buildPart part;
            buildSlot slot;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (line.contains("code")) {
                    part = gson.fromJson(line, buildPart.class);

                    if (part.getCode().equals("build")) {
                        buildTreeRootItem = new TreeItem<>(part);
                        buildHashMap.put(part.getId(), buildTreeRootItem);
                        treeViewBuild.setRoot(buildTreeRootItem);
                        treeViewBuild.setShowRoot(true);
                    } else {
                        TreeItem<Object> partItem = newTreeItem(part);
                        buildHashMap.put(part.getId(), partItem);
                        TreeItem<Object> parentItem = buildHashMap.get(part.getParentId());
                        parentItem.getChildren().add(partItem);
                    }

                    /* add Slots to Parts */

                } else {
                    slot = gson.fromJson(line, buildSlot.class);
                    TreeItem<Object> slotItem = newTreeItem(slot);
                    buildHashMap.put(slot.getId(), slotItem);
                    TreeItem<Object> parentItem = buildHashMap.get(slot.getParentId());
                    parentItem.getChildren().add(slotItem);
                }

            }
        } catch (IOException e) {
            logger.error("readTree - {}", e.getMessage());
        }
    }

    public void ButtonExportOnAction() {
        HashMap<String, Integer> exportHash = new HashMap<>();
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv file (*.csv)", "*.csv"));
            fileChooser.setTitle("save csv file");
            File exportFile = fileChooser.showSaveDialog(getPrimaryStage());
            if (exportFile.exists())
                if (!exportFile.delete())
                    labelStatus.setText("file delete error");
            if (exportFile.createNewFile()) {
                FileWriter fw = new FileWriter(exportFile);
                BufferedWriter bw = new BufferedWriter(fw);

                exportTree(buildTreeRootItem, exportHash);
                for (Map.Entry<String, Integer> entry : exportHash.entrySet()) {
                    bw.write(entry.getKey() + "," + entry.getValue());
                    bw.newLine();
                }
                bw.close();
                fw.close();
            }

        } catch (IOException e) {
            logger.error("Export - {}", e.getMessage());
        }
    }

    private void exportTree(TreeItem<Object> item, HashMap<String, Integer> map) {
        Object object = item.getValue();
        if (object.getClass() == buildPart.class) {
            buildPart part = (buildPart) object;
            if (part.getTotalCount() > 0)
                updateTotal(part, map);
        }
        for (TreeItem<Object> childItem : item.getChildren()) {
            if (childItem.getChildren().isEmpty()) {
                Object childObject = childItem.getValue();
                if (childObject.getClass() == buildPart.class) {
                    buildPart part = (buildPart) childObject;
                    updateTotal(part, map);
                }
            } else
                exportTree(childItem, map);
        }
    }

    private void updateTotal(buildPart part, HashMap<String, Integer> map) {
        if (map.containsKey(part.getCode()))
            map.put(part.getCode(), map.get(part.getCode() + part.getTotalCount()));
        else
            map.put(part.getCode(), part.getTotalCount());
    }

}
