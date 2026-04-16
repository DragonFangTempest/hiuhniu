package com.example.mmmmp1;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

public class HotelManagementApp extends Application {

    // -------------------- Models --------------------
    public static class Room {
        private final IntegerProperty roomNumber;
        private final StringProperty roomType;
        private final DoubleProperty pricePerDay;
        private final IntegerProperty capacity;
        private final StringProperty bedType;
        private final BooleanProperty ac;
        private final BooleanProperty wifi;
        private final BooleanProperty available;

        public Room(int roomNumber, String roomType, double pricePerDay, int capacity,
                    String bedType, boolean ac, boolean wifi, boolean available) {
            this.roomNumber = new SimpleIntegerProperty(roomNumber);
            this.roomType = new SimpleStringProperty(roomType);
            this.pricePerDay = new SimpleDoubleProperty(pricePerDay);
            this.capacity = new SimpleIntegerProperty(capacity);
            this.bedType = new SimpleStringProperty(bedType);
            this.ac = new SimpleBooleanProperty(ac);
            this.wifi = new SimpleBooleanProperty(wifi);
            this.available = new SimpleBooleanProperty(available);
        }

        public int getRoomNumber() { return roomNumber.get(); }
        public IntegerProperty roomNumberProperty() { return roomNumber; }

        public String getRoomType() { return roomType.get(); }
        public StringProperty roomTypeProperty() { return roomType; }

        public double getPricePerDay() { return pricePerDay.get(); }
        public DoubleProperty pricePerDayProperty() { return pricePerDay; }

        public int getCapacity() { return capacity.get(); }
        public IntegerProperty capacityProperty() { return capacity; }

        public String getBedType() { return bedType.get(); }
        public StringProperty bedTypeProperty() { return bedType; }

        public boolean isAc() { return ac.get(); }
        public BooleanProperty acProperty() { return ac; }

        public boolean isWifi() { return wifi.get(); }
        public BooleanProperty wifiProperty() { return wifi; }

        public boolean isAvailable() { return available.get(); }
        public BooleanProperty availableProperty() { return available; }
        public void setAvailable(boolean value) { available.set(value); }
    }

    public static class Booking {
        private final IntegerProperty bookingId;
        private final StringProperty customerName;
        private final StringProperty phone;
        private final IntegerProperty roomNumber;
        private final StringProperty roomType;
        private final IntegerProperty days;
        private final DoubleProperty totalAmount;
        private final StringProperty bookingStatus;

        public Booking(int bookingId, String customerName, String phone, int roomNumber,
                       String roomType, int days, double totalAmount, String bookingStatus) {
            this.bookingId = new SimpleIntegerProperty(bookingId);
            this.customerName = new SimpleStringProperty(customerName);
            this.phone = new SimpleStringProperty(phone);
            this.roomNumber = new SimpleIntegerProperty(roomNumber);
            this.roomType = new SimpleStringProperty(roomType);
            this.days = new SimpleIntegerProperty(days);
            this.totalAmount = new SimpleDoubleProperty(totalAmount);
            this.bookingStatus = new SimpleStringProperty(bookingStatus);
        }

        public int getBookingId() { return bookingId.get(); }
        public IntegerProperty bookingIdProperty() { return bookingId; }

        public String getCustomerName() { return customerName.get(); }
        public StringProperty customerNameProperty() { return customerName; }

        public String getPhone() { return phone.get(); }
        public StringProperty phoneProperty() { return phone; }

        public int getRoomNumber() { return roomNumber.get(); }
        public IntegerProperty roomNumberProperty() { return roomNumber; }

        public String getRoomType() { return roomType.get(); }
        public StringProperty roomTypeProperty() { return roomType; }

        public int getDays() { return days.get(); }
        public IntegerProperty daysProperty() { return days; }

        public double getTotalAmount() { return totalAmount.get(); }
        public DoubleProperty totalAmountProperty() { return totalAmount; }

        public String getBookingStatus() { return bookingStatus.get(); }
        public StringProperty bookingStatusProperty() { return bookingStatus; }
        public void setBookingStatus(String status) { bookingStatus.set(status); }
    }

    // -------------------- Data --------------------
    private final ArrayList<Room> roomList = new ArrayList<>();
    private final ArrayList<Booking> bookingList = new ArrayList<>();
    private final Map<Integer, Booking> activeBookingByRoom = new HashMap<>();

    private final ObservableList<Room> roomData = FXCollections.observableArrayList();
    private final ObservableList<Booking> bookingData = FXCollections.observableArrayList();

    private int nextBookingId = 1;

    // -------------------- UI references --------------------
    private TableView<Room> roomTable;
    private TableView<Booking> bookingTable;
    private ComboBox<Integer> bookingRoomCombo;
    private ComboBox<Integer> checkoutRoomCombo;
    private PieChart roomTypePieChart;
    private BarChart<String, Number> bookingBarChart;

    private Label totalRoomsLabel;
    private Label availableRoomsLabel;
    private Label occupiedRoomsLabel;
    private Label revenueLabel;

    @Override
    public void start(Stage stage) {
        preloadRooms();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        VBox topSection = new VBox(18, createHeader(), createDashboardCards());
        topSection.setPadding(new Insets(18, 20, 8, 20));

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("premium-tabs");

        Tab dashboardTab = new Tab("Dashboard", createDashboardTab());
        Tab roomTab = new Tab("Room Management", createRoomManagementTab());
        Tab bookingTab = new Tab("Book Room", createBookingTab());
        Tab checkoutTab = new Tab("Checkout", createCheckoutTab());

        dashboardTab.setClosable(false);
        roomTab.setClosable(false);
        bookingTab.setClosable(false);
        checkoutTab.setClosable(false);

        tabPane.getTabs().addAll(dashboardTab, roomTab, bookingTab, checkoutTab);

        root.setTop(topSection);
        root.setCenter(tabPane);

        refreshAll();

        Scene scene = new Scene(root, 1450, 860);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/example/mmmmp1/style.css")).toExternalForm()
        );

        stage.setTitle("HotelLux - Premium Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    // -------------------- Header --------------------
    private HBox createHeader() {
        Label title = new Label("HotelLux");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Premium Hotel Management Dashboard");
        subtitle.getStyleClass().add("app-subtitle");

        VBox textBox = new VBox(2, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label("● Live System");
        status.getStyleClass().add("live-status");

        HBox header = new HBox(15, textBox, spacer, status);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    // -------------------- Dashboard Cards --------------------
    private HBox createDashboardCards() {
        totalRoomsLabel = new Label("0");
        availableRoomsLabel = new Label("0");
        occupiedRoomsLabel = new Label("0");
        revenueLabel = new Label("₹0.00");

        VBox card1 = createStatCard("Total Rooms", totalRoomsLabel);
        VBox card2 = createStatCard("Available Rooms", availableRoomsLabel);
        VBox card3 = createStatCard("Occupied Rooms", occupiedRoomsLabel);
        VBox card4 = createStatCard("Revenue", revenueLabel);

        HBox box = new HBox(18, card1, card2, card3, card4);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox createStatCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        valueLabel.getStyleClass().add("stat-value");

        VBox card = new VBox(10, titleLabel, valueLabel);
        card.getStyleClass().add("glass-card");
        card.setPrefWidth(300);
        card.setPadding(new Insets(18));
        return card;
    }

    // -------------------- Dashboard Tab --------------------
    private VBox createDashboardTab() {
        roomTypePieChart = new PieChart();
        roomTypePieChart.setTitle("Room Type Distribution");
        roomTypePieChart.getStyleClass().add("chart-card");
        roomTypePieChart.setLegendVisible(true);
        roomTypePieChart.setLabelsVisible(true);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        bookingBarChart = new BarChart<>(xAxis, yAxis);
        bookingBarChart.setTitle("Active Bookings by Room Type");
        xAxis.setLabel("Room Type");
        yAxis.setLabel("Bookings");
        bookingBarChart.setLegendVisible(false);
        bookingBarChart.getStyleClass().add("chart-card");

        VBox leftChart = new VBox(roomTypePieChart);
        leftChart.getStyleClass().add("glass-card");
        leftChart.setPadding(new Insets(15));
        VBox.setVgrow(roomTypePieChart, Priority.ALWAYS);

        VBox rightChart = new VBox(bookingBarChart);
        rightChart.getStyleClass().add("glass-card");
        rightChart.setPadding(new Insets(15));
        VBox.setVgrow(bookingBarChart, Priority.ALWAYS);

        HBox charts = new HBox(18, leftChart, rightChart);
        HBox.setHgrow(leftChart, Priority.ALWAYS);
        HBox.setHgrow(rightChart, Priority.ALWAYS);

        Label info = new Label("Smart overview of hotel operations, bookings, room distribution and revenue performance.");
        info.getStyleClass().add("soft-info");

        VBox root = new VBox(18, info, charts);
        root.setPadding(new Insets(20));
        VBox.setVgrow(charts, Priority.ALWAYS);
        return root;
    }

    // -------------------- Room Management Tab --------------------
    private BorderPane createRoomManagementTab() {
        TextField roomNumberField = new TextField();
        roomNumberField.setPromptText("Room Number");

        ComboBox<String> roomTypeCombo = new ComboBox<>();
        roomTypeCombo.getItems().addAll("Single", "Double", "Deluxe", "Suite");
        roomTypeCombo.setPromptText("Room Type");

        TextField priceField = new TextField();
        priceField.setPromptText("Price / Day");

        Spinner<Integer> capacitySpinner = new Spinner<>(1, 10, 2);
        capacitySpinner.setEditable(true);

        ComboBox<String> bedTypeCombo = new ComboBox<>();
        bedTypeCombo.getItems().addAll("Single Bed", "Double Bed", "Queen Bed", "King Bed");
        bedTypeCombo.setPromptText("Bed Type");

        CheckBox acCheck = new CheckBox("AC");
        CheckBox wifiCheck = new CheckBox("WiFi");

        Button addRoomBtn = new Button("Add Room");
        addRoomBtn.getStyleClass().add("primary-button");

        Button showAllBtn = new Button("Show All");
        Button showAvailableBtn = new Button("Available Only");
        Button sortPriceBtn = new Button("Sort by Price");
        Button sortRoomBtn = new Button("Sort by Room No");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by room no / type");

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(14);

        form.add(createFieldLabel("Room Number"), 0, 0);
        form.add(roomNumberField, 1, 0);

        form.add(createFieldLabel("Room Type"), 2, 0);
        form.add(roomTypeCombo, 3, 0);

        form.add(createFieldLabel("Price / Day"), 0, 1);
        form.add(priceField, 1, 1);

        form.add(createFieldLabel("Capacity"), 2, 1);
        form.add(capacitySpinner, 3, 1);

        form.add(createFieldLabel("Bed Type"), 0, 2);
        form.add(bedTypeCombo, 1, 2);

        HBox features = new HBox(15, acCheck, wifiCheck);
        features.setAlignment(Pos.CENTER_LEFT);
        form.add(createFieldLabel("Amenities"), 2, 2);
        form.add(features, 3, 2);

        HBox buttons = new HBox(12, addRoomBtn, showAllBtn, showAvailableBtn, sortPriceBtn, sortRoomBtn, searchField);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox topBox = new VBox(16, form, buttons);
        topBox.getStyleClass().add("glass-card");
        topBox.setPadding(new Insets(20));

        roomTable = createRoomTable();

        VBox centerBox = new VBox(roomTable);
        centerBox.getStyleClass().add("glass-card");
        centerBox.setPadding(new Insets(15));
        VBox.setVgrow(roomTable, Priority.ALWAYS);

        addRoomBtn.setOnAction(e -> {
            try {
                String roomNoText = roomNumberField.getText().trim();
                String type = roomTypeCombo.getValue();
                String priceText = priceField.getText().trim();
                int capacity = capacitySpinner.getValue();
                String bedType = bedTypeCombo.getValue();

                if (roomNoText.isEmpty() || type == null || priceText.isEmpty() || bedType == null) {
                    showError("Please fill all room details.");
                    return;
                }

                int roomNo = Integer.parseInt(roomNoText);
                double price = Double.parseDouble(priceText);

                if (price <= 0) {
                    showError("Price must be greater than 0.");
                    return;
                }

                if (findRoom(roomNo) != null) {
                    showError("Room number already exists.");
                    return;
                }

                roomList.add(new Room(roomNo, type, price, capacity, bedType,
                        acCheck.isSelected(), wifiCheck.isSelected(), true));

                clearRoomForm(roomNumberField, roomTypeCombo, priceField, capacitySpinner, bedTypeCombo, acCheck, wifiCheck);
                refreshAll();
                showInfo("Room added successfully.");
            } catch (NumberFormatException ex) {
                showError("Room number and price must be valid numeric values.");
            }
        });

        showAllBtn.setOnAction(e -> refreshRooms());

        showAvailableBtn.setOnAction(e -> {
            roomData.setAll(roomList.stream().filter(Room::isAvailable).collect(Collectors.toList()));
        });

        sortPriceBtn.setOnAction(e -> {
            roomList.sort(Comparator.comparingDouble(Room::getPricePerDay));
            refreshRooms();
        });

        sortRoomBtn.setOnAction(e -> {
            roomList.sort(Comparator.comparingInt(Room::getRoomNumber));
            refreshRooms();
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String key = newVal.trim().toLowerCase();
            if (key.isEmpty()) {
                refreshRooms();
            } else {
                roomData.setAll(roomList.stream().filter(r ->
                        String.valueOf(r.getRoomNumber()).contains(key) ||
                                r.getRoomType().toLowerCase().contains(key)
                ).collect(Collectors.toList()));
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setPadding(new Insets(20));
        BorderPane.setMargin(centerBox, new Insets(18, 0, 0, 0));
        return root;
    }

    private TableView<Room> createRoomTable() {
        TableView<Room> table = new TableView<>();

        TableColumn<Room, Integer> c1 = new TableColumn<>("Room No");
        c1.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Room, Double> c3 = new TableColumn<>("Price");
        c3.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        TableColumn<Room, Integer> c4 = new TableColumn<>("Capacity");
        c4.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<Room, String> c5 = new TableColumn<>("Bed Type");
        c5.setCellValueFactory(new PropertyValueFactory<>("bedType"));

        TableColumn<Room, Boolean> c6 = new TableColumn<>("AC");
        c6.setCellValueFactory(new PropertyValueFactory<>("ac"));

        TableColumn<Room, Boolean> c7 = new TableColumn<>("WiFi");
        c7.setCellValueFactory(new PropertyValueFactory<>("wifi"));

        TableColumn<Room, Boolean> c8 = new TableColumn<>("Available");
        c8.setCellValueFactory(new PropertyValueFactory<>("available"));

        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8);
        table.setItems(roomData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    // -------------------- Booking Tab --------------------
    private BorderPane createBookingTab() {
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        bookingRoomCombo = new ComboBox<>();
        bookingRoomCombo.setPromptText("Available Room");

        Spinner<Integer> stayDaysSpinner = new Spinner<>(1, 30, 1);
        stayDaysSpinner.setEditable(true);

        Label selectedRoomInfo = new Label("Select a room to preview its details.");
        selectedRoomInfo.getStyleClass().add("soft-info");

        Button bookBtn = new Button("Confirm Booking");
        bookBtn.getStyleClass().add("primary-button");

        bookingRoomCombo.setOnAction(e -> {
            Integer roomNo = bookingRoomCombo.getValue();
            if (roomNo == null) return;
            Room room = findRoom(roomNo);
            if (room != null) {
                selectedRoomInfo.setText(
                        "Room " + room.getRoomNumber() + " | " +
                                room.getRoomType() + " | ₹" + room.getPricePerDay() +
                                " / day | Capacity: " + room.getCapacity() +
                                " | " + room.getBedType()
                );
            }
        });

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(14);

        form.add(createFieldLabel("Customer Name"), 0, 0);
        form.add(customerNameField, 1, 0);

        form.add(createFieldLabel("Phone"), 2, 0);
        form.add(phoneField, 3, 0);

        form.add(createFieldLabel("Room"), 0, 1);
        form.add(bookingRoomCombo, 1, 1);

        form.add(createFieldLabel("Days"), 2, 1);
        form.add(stayDaysSpinner, 3, 1);

        VBox top = new VBox(14, form, selectedRoomInfo, bookBtn);
        top.getStyleClass().add("glass-card");
        top.setPadding(new Insets(20));

        bookingTable = createBookingTable();

        VBox center = new VBox(bookingTable);
        center.getStyleClass().add("glass-card");
        center.setPadding(new Insets(15));
        VBox.setVgrow(bookingTable, Priority.ALWAYS);

        bookBtn.setOnAction(e -> {
            String name = customerNameField.getText().trim();
            String phone = phoneField.getText().trim();
            Integer roomNo = bookingRoomCombo.getValue();
            int days = stayDaysSpinner.getValue();

            if (name.isEmpty() || phone.isEmpty() || roomNo == null) {
                showError("Please complete all booking details.");
                return;
            }

            Room room = findRoom(roomNo);
            if (room == null) {
                showError("Selected room not found.");
                return;
            }

            if (!room.isAvailable()) {
                showError("This room is already booked.");
                return;
            }

            double total = room.getPricePerDay() * days;

            Booking booking = new Booking(
                    nextBookingId++,
                    name,
                    phone,
                    roomNo,
                    room.getRoomType(),
                    days,
                    total,
                    "Active"
            );

            bookingList.add(booking);
            activeBookingByRoom.put(roomNo, booking);
            room.setAvailable(false);

            customerNameField.clear();
            phoneField.clear();
            bookingRoomCombo.setValue(null);
            stayDaysSpinner.getValueFactory().setValue(1);
            selectedRoomInfo.setText("Select a room to preview its details.");

            refreshAll();
            showInfo("Room booked successfully.");
        });

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(center);
        root.setPadding(new Insets(20));
        BorderPane.setMargin(center, new Insets(18, 0, 0, 0));
        return root;
    }

    private TableView<Booking> createBookingTable() {
        TableView<Booking> table = new TableView<>();

        TableColumn<Booking, Integer> c1 = new TableColumn<>("Booking ID");
        c1.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> c2 = new TableColumn<>("Customer");
        c2.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Booking, String> c3 = new TableColumn<>("Phone");
        c3.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Booking, Integer> c4 = new TableColumn<>("Room No");
        c4.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Booking, String> c5 = new TableColumn<>("Type");
        c5.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Booking, Integer> c6 = new TableColumn<>("Days");
        c6.setCellValueFactory(new PropertyValueFactory<>("days"));

        TableColumn<Booking, Double> c7 = new TableColumn<>("Total");
        c7.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<Booking, String> c8 = new TableColumn<>("Status");
        c8.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));

        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8);
        table.setItems(bookingData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    // -------------------- Checkout Tab --------------------
    private VBox createCheckoutTab() {
        checkoutRoomCombo = new ComboBox<>();
        checkoutRoomCombo.setPromptText("Select Occupied Room");

        Label checkoutInfo = new Label("Select an occupied room to checkout.");
        checkoutInfo.getStyleClass().add("soft-info");

        checkoutRoomCombo.setOnAction(e -> {
            Integer roomNo = checkoutRoomCombo.getValue();
            if (roomNo == null) return;
            Booking booking = activeBookingByRoom.get(roomNo);
            if (booking != null) {
                checkoutInfo.setText(
                        "Booking ID: " + booking.getBookingId() +
                                " | Customer: " + booking.getCustomerName() +
                                " | Total: ₹" + booking.getTotalAmount()
                );
            }
        });

        Button checkoutBtn = new Button("Checkout Room");
        checkoutBtn.getStyleClass().add("danger-button");

        checkoutBtn.setOnAction(e -> {
            Integer roomNo = checkoutRoomCombo.getValue();
            if (roomNo == null) {
                showError("Please select a room for checkout.");
                return;
            }

            Room room = findRoom(roomNo);
            Booking booking = activeBookingByRoom.get(roomNo);

            if (room == null || booking == null) {
                showError("Active booking not found.");
                return;
            }

            room.setAvailable(true);
            booking.setBookingStatus("Checked Out");
            activeBookingByRoom.remove(roomNo);

            refreshAll();
            checkoutRoomCombo.setValue(null);
            checkoutInfo.setText("Select an occupied room to checkout.");
            showInfo("Checkout completed successfully.");
        });

        VBox root = new VBox(18,
                createSectionTitle("Checkout Management"),
                checkoutRoomCombo,
                checkoutInfo,
                checkoutBtn
        );
        root.getStyleClass().add("glass-card");
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_LEFT);
        root.setMaxWidth(500);

        VBox wrapper = new VBox(root);
        wrapper.setPadding(new Insets(25));
        return wrapper;
    }

    // -------------------- Refresh Methods --------------------
    private void refreshAll() {
        refreshRooms();
        refreshBookings();
        refreshRoomChoices();
        refreshStats();
        refreshCharts();
    }

    private void refreshRooms() {
        roomData.setAll(roomList);
    }

    private void refreshBookings() {
        bookingData.setAll(bookingList);
    }

    private void refreshRoomChoices() {
        bookingRoomCombo.getItems().clear();
        checkoutRoomCombo.getItems().clear();

        for (Room room : roomList) {
            if (room.isAvailable()) {
                bookingRoomCombo.getItems().add(room.getRoomNumber());
            } else {
                checkoutRoomCombo.getItems().add(room.getRoomNumber());
            }
        }
    }

    private void refreshStats() {
        int total = roomList.size();
        long available = roomList.stream().filter(Room::isAvailable).count();
        long occupied = total - available;
        double revenue = bookingList.stream()
                .filter(b -> "Active".equals(b.getBookingStatus()) || "Checked Out".equals(b.getBookingStatus()))
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        totalRoomsLabel.setText(String.valueOf(total));
        availableRoomsLabel.setText(String.valueOf(available));
        occupiedRoomsLabel.setText(String.valueOf(occupied));
        revenueLabel.setText(String.format("₹%.2f", revenue));
    }

    private void refreshCharts() {
        Map<String, Long> roomTypeCount = roomList.stream()
                .collect(Collectors.groupingBy(Room::getRoomType, Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        roomTypeCount.forEach((type, count) -> pieData.add(new PieChart.Data(type, count)));
        roomTypePieChart.setData(pieData);

        Map<String, Long> bookingTypeCount = bookingList.stream()
                .filter(b -> "Active".equals(b.getBookingStatus()))
                .collect(Collectors.groupingBy(Booking::getRoomType, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        bookingTypeCount.forEach((type, count) -> series.getData().add(new XYChart.Data<>(type, count)));

        bookingBarChart.getData().clear();
        bookingBarChart.getData().add(series);
    }

    // -------------------- Helpers --------------------
    private Room findRoom(int roomNo) {
        for (Room room : roomList) {
            if (room.getRoomNumber() == roomNo) return room;
        }
        return null;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private void clearRoomForm(TextField roomNumberField, ComboBox<String> roomTypeCombo,
                               TextField priceField, Spinner<Integer> capacitySpinner,
                               ComboBox<String> bedTypeCombo, CheckBox acCheck, CheckBox wifiCheck) {
        roomNumberField.clear();
        roomTypeCombo.setValue(null);
        priceField.clear();
        capacitySpinner.getValueFactory().setValue(2);
        bedTypeCombo.setValue(null);
        acCheck.setSelected(false);
        wifiCheck.setSelected(false);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Success");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // -------------------- Sample Data --------------------
    private void preloadRooms() {
        roomList.add(new Room(101, "Single", 1200, 1, "Single Bed", true, true, true));
        roomList.add(new Room(102, "Double", 1800, 2, "Double Bed", true, true, true));
        roomList.add(new Room(103, "Deluxe", 2600, 3, "Queen Bed", true, true, true));
        roomList.add(new Room(104, "Suite", 4200, 4, "King Bed", true, true, true));
        roomList.add(new Room(105, "Single", 1300, 1, "Single Bed", false, true, true));
        roomList.add(new Room(106, "Deluxe", 2800, 3, "Queen Bed", true, true, true));
    }

    public static void main(String[] args) {
        launch(args);
    }
}