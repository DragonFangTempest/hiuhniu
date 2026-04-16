package com.example.mmmmp1;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BookingTab {

    private final HotelDataManager dm;
    private final Runnable onRefresh;

    // Book fields
    private ComboBox<String> cbAvailRooms;
    private TextField tfGuestName, tfGuestPhone;
    private DatePicker dpCheckIn, dpCheckOut;
    private Label lblRoomInfo, lblTotalCost, bookStatus;

    // Checkout fields
    private ComboBox<String> cbBookedRooms;
    private Label lblCheckoutInfo, checkoutStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BookingTab(HotelDataManager dm, Runnable onRefresh) {
        this.dm = dm;
        this.onRefresh = onRefresh;
    }

    public Node build() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: transparent;");

        VBox bookPanel    = buildBookPanel();
        VBox checkoutPanel= buildCheckoutPanel();
        bookPanel.setPrefWidth(420);
        checkoutPanel.setPrefWidth(380);

        layout.getChildren().addAll(bookPanel, checkoutPanel);
        return layout;
    }

    /* ===== BOOK ROOM ===== */
    private VBox buildBookPanel() {
        VBox card = glassCard();
        card.setMaxHeight(Double.MAX_VALUE);

        Label heading = sectionHeading("🛎  Book a Room");

        cbAvailRooms = new ComboBox<>();
        cbAvailRooms.setPromptText("Select Available Room");
        cbAvailRooms.setMaxWidth(Double.MAX_VALUE);
        cbAvailRooms.getStyleClass().add("glass-combo");
        cbAvailRooms.setOnAction(e -> updateRoomInfo());

        lblRoomInfo = new Label("Select a room to see details.");
        lblRoomInfo.setWrapText(true);
        lblRoomInfo.setStyle("-fx-text-fill:rgba(255,255,255,0.55); -fx-font-size:12px;" +
                "-fx-background-color:rgba(255,255,255,0.05); -fx-background-radius:8; -fx-padding:10;");

        tfGuestName  = glassField("Guest Full Name");
        tfGuestPhone = glassField("Contact Number");

        dpCheckIn  = datePicker("Check-in Date");
        dpCheckOut = datePicker("Check-out Date");
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));
        dpCheckIn.setOnAction(e  -> recalcCost());
        dpCheckOut.setOnAction(e -> recalcCost());

        lblTotalCost = new Label("Total: ₹0");
        lblTotalCost.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#4ade80;");

        Button btnBook = new Button("✅  Confirm Booking");
        btnBook.setMaxWidth(Double.MAX_VALUE);
        btnBook.getStyleClass().add("btn-primary");
        btnBook.setOnAction(e -> handleBooking());

        bookStatus = new Label();
        bookStatus.setWrapText(true);

        card.getChildren().addAll(
                heading, new Separator(),
                fieldLbl("Select Room"),   cbAvailRooms, lblRoomInfo,
                fieldLbl("Guest Name"),    tfGuestName,
                fieldLbl("Contact"),       tfGuestPhone,
                fieldLbl("Check-in"),      dpCheckIn,
                fieldLbl("Check-out"),     dpCheckOut,
                lblTotalCost,
                new Separator(),
                btnBook, bookStatus
        );
        return card;
    }

    /* ===== CHECKOUT ===== */
    private VBox buildCheckoutPanel() {
        VBox card = glassCard();

        Label heading = sectionHeading("🔓  Checkout Guest");

        cbBookedRooms = new ComboBox<>();
        cbBookedRooms.setPromptText("Select Booked Room");
        cbBookedRooms.setMaxWidth(Double.MAX_VALUE);
        cbBookedRooms.getStyleClass().add("glass-combo");
        cbBookedRooms.setOnAction(e -> updateCheckoutInfo());

        lblCheckoutInfo = new Label("Select a booked room to see guest info.");
        lblCheckoutInfo.setWrapText(true);
        lblCheckoutInfo.setStyle("-fx-text-fill:rgba(255,255,255,0.55); -fx-font-size:12px;" +
                "-fx-background-color:rgba(255,255,255,0.05); -fx-background-radius:8; -fx-padding:10;");

        Button btnCheckout = new Button("🔓  Checkout Guest");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.getStyleClass().add("btn-danger");
        btnCheckout.setOnAction(e -> handleCheckout());

        checkoutStatus = new Label();
        checkoutStatus.setWrapText(true);

        // Separator + currently booked list
        Label listHeading = new Label("Currently Occupied Rooms");
        listHeading.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:rgba(255,255,255,0.8);");

        VBox bookedList = new VBox(6);
        bookedList.setStyle("-fx-padding: 8 0 0 0;");

        // Will be refreshed
        card.getChildren().addAll(
                heading, new Separator(),
                fieldLbl("Booked Room"), cbBookedRooms, lblCheckoutInfo,
                new Separator(),
                btnCheckout, checkoutStatus,
                new Separator(),
                listHeading, bookedList
        );

        refresh(bookedList);

        // Store bookedList ref so refresh() can update it
        card.setUserData(bookedList);
        return card;
    }

    private void refresh(VBox bookedList) {
        bookedList.getChildren().clear();
        for (Room r : dm.getRooms()) {
            if (r.getStatus() == Room.Status.BOOKED) {
                HBox row = new HBox(10);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setStyle("-fx-background-color: rgba(247,151,30,0.1); -fx-background-radius:10;");
                Label num  = new Label("Room " + r.getRoomNumber());
                num.setStyle("-fx-text-fill:#f7971e; -fx-font-weight:bold; -fx-min-width:80;");
                Label name = new Label(r.getGuestName());
                name.setStyle("-fx-text-fill:white; -fx-min-width:120;");
                Label dates = new Label(r.getCheckIn() + " → " + r.getCheckOut());
                dates.setStyle("-fx-text-fill:rgba(255,255,255,0.45); -fx-font-size:11px;");
                row.getChildren().addAll(num, name, dates);
                bookedList.getChildren().add(row);
            }
        }
        if (bookedList.getChildren().isEmpty()) {
            Label none = new Label("No rooms currently booked.");
            none.setStyle("-fx-text-fill:rgba(255,255,255,0.3); -fx-font-size:12px;");
            bookedList.getChildren().add(none);
        }
    }

    private void updateRoomInfo() {
        String sel = cbAvailRooms.getValue();
        if (sel == null) return;
        int num = Integer.parseInt(sel.split(" ")[1]);
        dm.findRoom(num).ifPresent(r -> {
            lblRoomInfo.setText(String.format(
                    "Type: %s  |  Floor: %d  |  Capacity: %d\n₹%.0f / night  |  %s",
                    r.getRoomTypeString(), r.getFloor(), r.getCapacity(),
                    r.getPricePerNight(), r.getAmenitiesSummary()
            ));
        });
        recalcCost();
    }

    private void recalcCost() {
        String sel = cbAvailRooms.getValue();
        if (sel == null || dpCheckIn.getValue() == null || dpCheckOut.getValue() == null) return;
        try {
            int num = Integer.parseInt(sel.split(" ")[1]);
            dm.findRoom(num).ifPresent(r -> {
                long nights = ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
                if (nights <= 0) { lblTotalCost.setText("Invalid dates"); return; }
                double total = nights * r.getPricePerNight();
                lblTotalCost.setText(String.format("Total: ₹%,.0f  (%d nights × ₹%.0f)",
                        total, nights, r.getPricePerNight()));
            });
        } catch (Exception ignored) {}
    }

    private void updateCheckoutInfo() {
        String sel = cbBookedRooms.getValue();
        if (sel == null) return;
        int num = Integer.parseInt(sel.split(" ")[1]);
        dm.findRoom(num).ifPresent(r -> {
            lblCheckoutInfo.setText(String.format(
                    "Guest: %s\nPhone: %s\nCheck-in: %s  →  Check-out: %s\n" +
                            "Stay: %d nights  |  Total: ₹%,.0f",
                    r.getGuestName(), r.getGuestPhone(),
                    r.getCheckIn(), r.getCheckOut(),
                    r.getNights(), r.getTotalCost()
            ));
        });
    }

    private void handleBooking() {
        String sel = cbAvailRooms.getValue();
        String gname = tfGuestName.getText().trim();
        String gphone = tfGuestPhone.getText().trim();

        if (sel == null || gname.isEmpty() || gphone.isEmpty() ||
                dpCheckIn.getValue() == null || dpCheckOut.getValue() == null) {
            showBookStatus("⚠ Please fill in all fields.", false);
            return;
        }
        long nights = ChronoUnit.DAYS.between(dpCheckIn.getValue(), dpCheckOut.getValue());
        if (nights <= 0) { showBookStatus("⚠ Check-out must be after check-in.", false); return; }

        int num = Integer.parseInt(sel.split(" ")[1]);
        boolean ok = dm.bookRoom(num, gname, gphone,
                dpCheckIn.getValue().format(FMT), dpCheckOut.getValue().format(FMT), (int)nights);

        if (ok) {
            showBookStatus("✅ Room " + num + " booked for " + gname + "!", true);
            tfGuestName.clear(); tfGuestPhone.clear();
            cbAvailRooms.setValue(null);
            onRefresh.run();
        } else {
            showBookStatus("❌ Booking failed. Room may be unavailable.", false);
        }
    }

    private void handleCheckout() {
        String sel = cbBookedRooms.getValue();
        if (sel == null) { showCheckoutStatus("⚠ Select a room.", false); return; }
        int num = Integer.parseInt(sel.split(" ")[1]);
        if (dm.checkoutRoom(num)) {
            showCheckoutStatus("✅ Guest checked out from Room " + num + ".", true);
            cbBookedRooms.setValue(null);
            lblCheckoutInfo.setText("Select a booked room to see guest info.");
            onRefresh.run();
        } else {
            showCheckoutStatus("❌ Checkout failed.", false);
        }
    }

    private void showBookStatus(String msg, boolean ok) {
        bookStatus.setText(msg);
        bookStatus.setStyle("-fx-font-size:12px; -fx-text-fill:" + (ok ? "#4ade80" : "#f87171") + ";");
    }
    private void showCheckoutStatus(String msg, boolean ok) {
        checkoutStatus.setText(msg);
        checkoutStatus.setStyle("-fx-font-size:12px; -fx-text-fill:" + (ok ? "#4ade80" : "#f87171") + ";");
    }

    public void refresh() {
        // Repopulate combos
        cbAvailRooms.getItems().clear();
        for (Room r : dm.getAvailableRooms())
            cbAvailRooms.getItems().add("Room " + r.getRoomNumber() + " [" + r.getRoomTypeString() + "]");

        cbBookedRooms.getItems().clear();
        for (Room r : dm.getRooms())
            if (r.getStatus() == Room.Status.BOOKED)
                cbBookedRooms.getItems().add("Room " + r.getRoomNumber() + " - " + r.getGuestName());

        // Refresh booked list (stored in card's userData)
        // We'll just do nothing extra — the parent calls rebuild via refresh chain
    }

    // Helpers
    private VBox glassCard() {
        VBox v = new VBox(12);
        v.setPadding(new Insets(24));
        v.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;"
        );
        return v;
    }

    private Label sectionHeading(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;");
        return l;
    }

    private Label fieldLbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:rgba(255,255,255,0.5); -fx-font-size:11px;");
        return l;
    }

    private TextField glassField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("glass-field");
        return tf;
    }

    private DatePicker datePicker(String prompt) {
        DatePicker dp = new DatePicker();
        dp.setPromptText(prompt);
        dp.setMaxWidth(Double.MAX_VALUE);
        dp.getStyleClass().add("glass-combo");
        return dp;
    }
}
