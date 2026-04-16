package com.example.mmmmp1;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;

import java.util.*;

public class DashboardTab {

    private final HotelDataManager dm;
    private Label availLabel, bookedLabel, revenueLabel, occupancyLabel;
    private Canvas donutCanvas;
    private Canvas barCanvas;
    private VBox recentList;

    public DashboardTab(HotelDataManager dm) { this.dm = dm; }

    public Node build() {
        ScrollPane sp = new ScrollPane(buildContent());
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return sp;
    }

    private Node buildContent() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: transparent;");

        Label heading = new Label("Dashboard Overview");
        heading.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:white;");

        // Stat cards row
        HBox stats = new HBox(16);
        stats.setAlignment(Pos.CENTER_LEFT);

        availLabel    = new Label("0");
        bookedLabel   = new Label("0");
        revenueLabel  = new Label("₹0");
        occupancyLabel= new Label("0%");

        stats.getChildren().addAll(
                statCard("Available Rooms",   availLabel,    "#12c2e9", "#c471ed", "🛏"),
                statCard("Booked Rooms",      bookedLabel,   "#f7971e", "#ffd200", "🔑"),
                statCard("Total Revenue",     revenueLabel,  "#56ab2f", "#a8e063", "💰"),
                statCard("Occupancy Rate",    occupancyLabel,"#c471ed", "#f64f59", "📊")
        );

        // Charts row
        HBox charts = new HBox(20);
        HBox.setHgrow(charts, Priority.ALWAYS);

        donutCanvas = new Canvas(300, 260);
        VBox donutCard = glassCard("Room Status", donutCanvas, 340);

        barCanvas = new Canvas(420, 220);
        VBox barCard = glassCard("Revenue by Room Type", barCanvas, 480);
        HBox.setHgrow(barCard, Priority.ALWAYS);

        // Recent bookings
        recentList = new VBox(8);
        VBox recentCard = glassCard("Recent Bookings", recentList, -1);
        recentCard.setMaxWidth(Double.MAX_VALUE);

        charts.getChildren().addAll(donutCard, barCard);

        root.getChildren().addAll(heading, stats, charts, recentCard);
        refresh();
        return root;
    }

    private VBox statCard(String title, Label valueLabel, String color1, String color2, String icon) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setPrefWidth(210);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(255,255,255,0.15);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:28px;");

        valueLabel.setStyle(
                "-fx-font-size:28px; -fx-font-weight:bold;" +
                        "-fx-text-fill: linear-gradient(to right," + color1 + "," + color2 + ");"
        );
        // JavaFX doesn't support gradient text directly, use a solid
        valueLabel.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:" + color1 + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:12px; -fx-text-fill:rgba(255,255,255,0.6);");

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);

        // Hover glow
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.13);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: " + color1 + ";" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian," + color1 + ",20,0.4,0,0);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.07);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(255,255,255,0.15);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;"
        ));
        return card;
    }

    private VBox glassCard(String title, Node content, double prefW) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;"
        );
        if (prefW > 0) card.setPrefWidth(prefW);

        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:rgba(255,255,255,0.85);");
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
        card.getChildren().addAll(lbl, sep, content);
        return card;
    }

    public void refresh() {
        long avail   = dm.countByStatus(Room.Status.AVAILABLE);
        long booked  = dm.countByStatus(Room.Status.BOOKED);
        double rev   = dm.totalRevenue();
        double occ   = dm.occupancyRate();

        availLabel.setText(String.valueOf(avail));
        bookedLabel.setText(String.valueOf(booked));
        revenueLabel.setText(String.format("₹%,.0f", rev));
        occupancyLabel.setText(String.format("%.1f%%", occ));

        drawDonut(avail, booked);
        drawBar();
        refreshRecentBookings();
    }

    private void drawDonut(long avail, long booked) {
        GraphicsContext gc = donutCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, donutCanvas.getWidth(), donutCanvas.getHeight());

        double cx = 130, cy = 125, r = 90, inner = 52;
        long total = avail + booked + dm.countByStatus(Room.Status.MAINTENANCE);
        if (total == 0) return;

        double[] values = {avail, booked, dm.countByStatus(Room.Status.MAINTENANCE)};
        Color[] colors  = {Color.web("#12c2e9"), Color.web("#f7971e"), Color.web("#ef4444")};
        String[] labels = {"Available", "Booked", "Maintenance"};

        double start = -Math.PI / 2;
        for (int i = 0; i < values.length; i++) {
            double sweep = 2 * Math.PI * values[i] / total;
            gc.setFill(colors[i]);
            gc.fillArc(cx - r, cy - r, r * 2, r * 2,
                    Math.toDegrees(-start), Math.toDegrees(-sweep),
                    javafx.scene.shape.ArcType.ROUND);
            start += sweep;
        }
        // Hole
        gc.setFill(Color.web("#1a1635"));
        gc.fillOval(cx - inner, cy - inner, inner * 2, inner * 2);

        // Center text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        gc.fillText(String.valueOf(total), cx - 14, cy + 4);
        gc.setFont(Font.font("Segoe UI", 11));
        gc.setFill(Color.web("rgba(255,255,255,0.5)"));
        gc.fillText("Total", cx - 14, cy + 20);

        // Legend
        double lx = 230, ly = 80;
        for (int i = 0; i < labels.length; i++) {
            gc.setFill(colors[i]);
            gc.fillRoundRect(lx, ly + i * 30, 12, 12, 4, 4);
            gc.setFill(Color.web("rgba(255,255,255,0.8)"));
            gc.setFont(Font.font("Segoe UI", 12));
            gc.fillText(labels[i] + " (" + (int)values[i] + ")", lx + 18, ly + i * 30 + 11);
        }
    }

    private void drawBar() {
        GraphicsContext gc = barCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, barCanvas.getWidth(), barCanvas.getHeight());

        Room.RoomType[] types = Room.RoomType.values();
        Color[] colors = {Color.web("#12c2e9"), Color.web("#c471ed"), Color.web("#f7971e"), Color.web("#4ade80")};
        double[] revenues = new double[types.length];

        for (Booking b : dm.getBookings()) {
            for (int i = 0; i < types.length; i++) {
                if (b.getRoomType().equals(types[i].name())) revenues[i] += b.getTotalCost();
            }
        }

        double maxRev = Arrays.stream(revenues).max().orElse(1);
        if (maxRev == 0) maxRev = 1;

        double barW = 60, gap = 30, startX = 30, chartH = 160, baseY = 200;
        gc.setFill(Color.web("rgba(255,255,255,0.15)"));
        gc.fillRect(startX - 5, baseY - chartH - 10, types.length * (barW + gap) + 20, 1);

        for (int i = 0; i < types.length; i++) {
            double h = (revenues[i] / maxRev) * chartH;
            double x = startX + i * (barW + gap);

            // Bar glow
            gc.setFill(colors[i]);
            gc.setEffect(new javafx.scene.effect.DropShadow(10, colors[i]));
            gc.fillRoundRect(x, baseY - h, barW, h, 8, 8);
            gc.setEffect(null);

            // Label
            gc.setFill(Color.web("rgba(255,255,255,0.7)"));
            gc.setFont(Font.font("Segoe UI", 11));
            gc.fillText(types[i].name(), x, baseY + 18);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            if (revenues[i] > 0)
                gc.fillText("₹" + String.format("%,.0f", revenues[i]), x - 2, baseY - h - 6);
        }
    }

    private void refreshRecentBookings() {
        recentList.getChildren().clear();
        List<Booking> all = new ArrayList<>(dm.getBookings());
        Collections.reverse(all);
        int count = Math.min(5, all.size());
        if (count == 0) {
            Label none = new Label("No bookings yet.");
            none.setStyle("-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size:13px;");
            recentList.getChildren().add(none);
            return;
        }
        for (int i = 0; i < count; i++) {
            Booking b = all.get(i);
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.05);" +
                            "-fx-background-radius: 10;"
            );

            Label num  = new Label("Room " + b.getRoomNumber());
            num.setStyle("-fx-text-fill:#12c2e9; -fx-font-weight:bold; -fx-min-width:80;");
            Label name = new Label(b.getGuestName());
            name.setStyle("-fx-text-fill:white; -fx-min-width:160;");
            Label type = new Label("[" + b.getRoomType() + "]");
            type.setStyle("-fx-text-fill:rgba(255,255,255,0.5); -fx-min-width:110;");

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Label cost = new Label(String.format("₹%,.0f", b.getTotalCost()));
            cost.setStyle("-fx-text-fill:#4ade80; -fx-font-weight:bold;");

            row.getChildren().addAll(num, name, type, sp, cost);
            recentList.getChildren().add(row);
        }
    }
}