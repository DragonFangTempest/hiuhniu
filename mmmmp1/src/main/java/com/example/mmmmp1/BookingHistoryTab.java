package com.example.mmmmp1;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class BookingHistoryTab {

    private final HotelDataManager dm;
    private TableView<Booking> table;
    private Label totalRevLabel;

    public BookingHistoryTab(HotelDataManager dm) { this.dm = dm; }

    public Node build() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label heading = new Label("Booking History");
        heading.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:white;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        totalRevLabel = new Label();
        totalRevLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#4ade80;");

        topBar.getChildren().addAll(heading, sp, new Label("Total Revenue: ") {{
            setStyle("-fx-text-fill:rgba(255,255,255,0.55); -fx-font-size:14px;");
        }}, totalRevLabel);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setItems(dm.getBookings());

        root.getChildren().addAll(topBar, table);
        refresh();
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<Booking> buildTable() {
        TableView<Booking> tv = new TableView<>();
        tv.getStyleClass().add("glass-table");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tv.getColumns().addAll(
                col("ID",          "bookingId",  60),
                col("Room #",      "roomNumber", 80),
                col("Type",        "roomType",  110),
                col("Guest",       "guestName", 160),
                col("Phone",       "guestPhone",130),
                col("Check-in",    "checkIn",   110),
                col("Check-out",   "checkOut",  110),
                col("Nights",      "nights",     70),
                costCol()
        );
        return tv;
    }

    private <T> TableColumn<Booking,T> col(String title, String prop, double w) {
        TableColumn<Booking,T> c = new TableColumn<>(title);
        c.setPrefWidth(w);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-text-fill: rgba(255,255,255,0.85);");
            }
        });
        return c;
    }

    private TableColumn<Booking,Double> costCol() {
        TableColumn<Booking,Double> c = new TableColumn<>("Total (₹)");
        c.setPrefWidth(120);
        c.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(String.format("₹%,.0f", item));
                setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;");
            }
        });
        return c;
    }

    public void refresh() {
        if (table != null) table.refresh();
        if (totalRevLabel != null)
            totalRevLabel.setText(String.format("₹%,.0f", dm.totalRevenue()));
    }
}