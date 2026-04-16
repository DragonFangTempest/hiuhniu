package com.example.mmmmp1;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;

public class RoomManagementTab {

    private final HotelDataManager dm;
    private final Runnable onRefresh;

    private TableView<Room> table;
    private TextField tfNumber, tfPrice, tfFloor, tfCapacity;
    private ComboBox<Room.RoomType> cbType;
    private CheckBox chkWifi, chkBreakfast, chkAC;
    private Label statusLabel;
    private TextField searchField;

    public RoomManagementTab(HotelDataManager dm, Runnable onRefresh) {
        this.dm = dm;
        this.onRefresh = onRefresh;
    }

    public Node build() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: transparent;");

        // Left: form wrapped in a ScrollPane so the button is always reachable
        VBox form = buildAddForm();
        form.setPrefWidth(320);
        form.setMinWidth(300);

        ScrollPane formScroll = new ScrollPane(form);
        formScroll.setFitToWidth(true);
        formScroll.setPrefWidth(340);
        formScroll.setMinWidth(320);
        formScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Right: table
        VBox tableSection = buildTableSection();
        HBox.setHgrow(tableSection, Priority.ALWAYS);

        layout.getChildren().addAll(formScroll, tableSection);
        return layout;
    }

    private VBox buildAddForm() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;"
        );

        Label heading = new Label("Add New Room");
        heading.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        tfNumber   = styledField("Room Number (e.g. 501)");
        tfFloor    = styledField("Floor");
        cbType     = new ComboBox<>(FXCollections.observableArrayList(Room.RoomType.values()));
        cbType.setPromptText("Room Type");
        cbType.setMaxWidth(Double.MAX_VALUE);
        cbType.getStyleClass().add("glass-combo");

        tfPrice    = styledField("Price per Night (₹)");
        tfCapacity = styledField("Max Capacity (persons)");

        chkWifi      = styledCheck("🌐  Free WiFi");
        chkBreakfast = styledCheck("🍳  Complimentary Breakfast");
        chkAC        = styledCheck("❄  Air Conditioning");
        chkAC.setSelected(true);

        Button btnAdd = new Button("➕  Add Room");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> handleAddRoom());

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size:12px;");

        card.getChildren().addAll(
                heading, sep,
                fieldLabel("Room Number"), tfNumber,
                fieldLabel("Floor"),       tfFloor,
                fieldLabel("Room Type"),   cbType,
                fieldLabel("Price / Night"), tfPrice,
                fieldLabel("Capacity"),    tfCapacity,
                fieldLabel("Amenities"),   chkAC, chkWifi, chkBreakfast,
                new Separator(),
                btnAdd, statusLabel
        );
        return card;
    }

    private VBox buildTableSection() {
        VBox vb = new VBox(12);

        // Search + filter bar
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        searchField = styledField("Search by room no, type, status…");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((o, ov, nv) -> filterTable(nv));

        ComboBox<String> filterStatus = new ComboBox<>(
                FXCollections.observableArrayList("All", "AVAILABLE", "BOOKED", "MAINTENANCE"));
        filterStatus.setValue("All");
        filterStatus.getStyleClass().add("glass-combo");
        filterStatus.setOnAction(e -> filterByStatus(filterStatus.getValue()));

        Label lbl = new Label("Filter:");
        lbl.setStyle("-fx-text-fill:rgba(255,255,255,0.6);");
        bar.getChildren().addAll(searchField, lbl, filterStatus);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setItems(dm.getRooms());

        vb.getChildren().addAll(bar, table);
        return vb;
    }

    @SuppressWarnings("unchecked")
    private TableView<Room> buildTable() {
        TableView<Room> tv = new TableView<>();
        tv.getStyleClass().add("glass-table");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room,Integer> colNum  = col("Room #",   "roomNumber",   80);
        TableColumn<Room,Integer> colFloor= col("Floor",    "floor",        60);
        TableColumn<Room,Room.RoomType> colType = col("Type","roomType",    110);
        TableColumn<Room,Double>  colPrice= col("₹/Night",  "pricePerNight",110);
        TableColumn<Room,Integer> colCap  = col("Capacity", "capacity",     80);

        TableColumn<Room,String> colAmen = new TableColumn<>("Amenities");
        colAmen.setPrefWidth(150);
        colAmen.setCellValueFactory(cell ->
                new javafx.beans.binding.StringBinding() {
                    { bind(cell.getValue().hasACProperty(),
                            cell.getValue().hasWifiProperty(),
                            cell.getValue().hasBreakfastProperty()); }
                    @Override protected String computeValue() {
                        return cell.getValue().getAmenitiesSummary(); }
                });
        colAmen.setCellFactory(c -> plainCell());

        TableColumn<Room,Room.Status> colStatus = new TableColumn<>("Status");
        colStatus.setPrefWidth(120);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(c -> statusCell());

        TableColumn<Room,String> colGuest = new TableColumn<>("Guest");
        colGuest.setPrefWidth(140);
        colGuest.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        colGuest.setCellFactory(c -> plainCell());

        tv.getColumns().addAll(colNum, colFloor, colType, colPrice, colCap, colAmen, colStatus, colGuest);
        return tv;
    }

    private <T> TableColumn<Room,T> col(String title, String prop, double w) {
        TableColumn<Room,T> c = new TableColumn<>(title);
        c.setPrefWidth(w);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setCellFactory(col -> plainCell());
        return c;
    }

    private <T> TableCell<Room,T> plainCell() {
        return new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-alignment: CENTER-LEFT;");
            }
        };
    }

    private TableCell<Room,Room.Status> statusCell() {
        return new TableCell<>() {
            @Override protected void updateItem(Room.Status item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(item.name());
                String color = switch (item) {
                    case AVAILABLE   -> "#4ade80";
                    case BOOKED      -> "#f7971e";
                    case MAINTENANCE -> "#ef4444";
                };
                badge.setStyle(
                        "-fx-background-color:" + color + "22;" +
                                "-fx-text-fill:" + color + ";" +
                                "-fx-padding: 3 10 3 10;" +
                                "-fx-background-radius: 20;" +
                                "-fx-font-size:11px; -fx-font-weight:bold;"
                );
                setGraphic(badge); setText(null);
            }
        };
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) { table.setItems(dm.getRooms()); return; }
        String q = query.toLowerCase();
        ObservableList<Room> filtered = FXCollections.observableArrayList();
        for (Room r : dm.getRooms()) {
            if (String.valueOf(r.getRoomNumber()).contains(q) ||
                    r.getRoomTypeString().toLowerCase().contains(q) ||
                    r.getStatusString().toLowerCase().contains(q) ||
                    r.getGuestName().toLowerCase().contains(q)) {
                filtered.add(r);
            }
        }
        table.setItems(filtered);
    }

    private void filterByStatus(String status) {
        if ("All".equals(status)) { table.setItems(dm.getRooms()); return; }
        ObservableList<Room> filtered = FXCollections.observableArrayList();
        for (Room r : dm.getRooms()) {
            if (r.getStatusString().equals(status)) filtered.add(r);
        }
        table.setItems(filtered);
    }

    private void handleAddRoom() {
        try {
            int num      = Integer.parseInt(tfNumber.getText().trim());
            int floor    = Integer.parseInt(tfFloor.getText().trim());
            double price = Double.parseDouble(tfPrice.getText().trim());
            int cap      = Integer.parseInt(tfCapacity.getText().trim());
            Room.RoomType type = cbType.getValue();

            if (type == null) { showStatus("Please select a room type.", false); return; }
            if (price <= 0 || cap <= 0) { showStatus("Price and capacity must be positive.", false); return; }

            Room room = new Room(num, type, price, floor, cap,
                    chkWifi.isSelected(), chkBreakfast.isSelected(), chkAC.isSelected());
            if (dm.addRoom(room)) {
                showStatus("✅ Room " + num + " added successfully!", true);
                clearForm();
                onRefresh.run();
            } else {
                showStatus("⚠ Room number already exists!", false);
            }
        } catch (NumberFormatException ex) {
            showStatus("❌ Please enter valid numeric values.", false);
        }
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-font-size:12px; -fx-text-fill:" + (success ? "#4ade80" : "#f87171") + ";");
    }

    private void clearForm() {
        tfNumber.clear(); tfFloor.clear(); tfPrice.clear(); tfCapacity.clear();
        cbType.setValue(null); chkWifi.setSelected(false);
        chkBreakfast.setSelected(false); chkAC.setSelected(true);
    }

    public void refresh() {
        if (table != null) table.refresh();
    }

    // Helper builders
    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("glass-field");
        return tf;
    }

    private CheckBox styledCheck(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setStyle("-fx-text-fill: rgba(255,255,255,0.75); -fx-font-size:13px;");
        return cb;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size:11px;");
        return l;
    }
}
