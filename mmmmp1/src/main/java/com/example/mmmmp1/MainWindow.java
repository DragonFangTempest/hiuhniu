package com.example.mmmmp1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

public class MainWindow {

    private final Stage stage;
    private final HotelDataManager dataManager = new HotelDataManager();

    // Tabs
    private DashboardTab dashboardTab;
    private RoomManagementTab roomTab;
    private BookingTab bookingTab;
    private BookingHistoryTab historyTab;

    public MainWindow(Stage stage) { this.stage = stage; }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0c29;");

        // Animated gradient background
        Pane bgPane = createAnimatedBackground();
        root.setCenter(buildContent());

        StackPane stackRoot = new StackPane(bgPane, root);
        Scene scene = new Scene(stackRoot, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("hotel.css").toExternalForm());
        return scene;
    }

    private Pane createAnimatedBackground() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);");

        // Decorative blurred circles
        for (int i = 0; i < 5; i++) {
            Circle c = new Circle(80 + i * 30);
            c.setFill(Color.web(i % 2 == 0 ? "#c471ed" : "#12c2e9", 0.08));
            c.setLayoutX(100 + i * 200);
            c.setLayoutY(150 + (i % 3) * 200);
            c.setEffect(new javafx.scene.effect.GaussianBlur(60));
            pane.getChildren().add(c);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(6 + i * 2), c);
            tt.setByY(40 + i * 10);
            tt.setAutoReverse(true);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.play();
        }
        return pane;
    }

    private Node buildContent() {
        VBox layout = new VBox(0);
        layout.setStyle("-fx-background-color: transparent;");

        // Header
        layout.getChildren().add(buildHeader());

        // Tab pane
        TabPane tabPane = buildTabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        layout.getChildren().add(tabPane);

        return layout;
    }

    private Node buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 32, 18, 32));
        header.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-border-color: rgba(255,255,255,0.12);" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Logo gem
        Label gem = new Label("✦");
        gem.setStyle("-fx-font-size: 28px; -fx-text-fill: #c471ed;");

        VBox titleBox = new VBox(2);
        Label title  = new Label("LuxeStay");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
        Label sub    = new Label("Premium Hotel Management System");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.5);");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label clock = new Label();
        clock.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size:13px;");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clock.setText(java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy  •  HH:mm:ss")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Label badge = new Label("● LIVE");
        badge.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 11px; -fx-font-weight: bold;");

        header.getChildren().addAll(gem, titleBox, spacer, clock, badge);
        return header;
    }

    private TabPane buildTabPane() {
        TabPane tp = new TabPane();
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.getStyleClass().add("main-tab-pane");

        dashboardTab = new DashboardTab(dataManager);
        roomTab      = new RoomManagementTab(dataManager, this::refreshAll);
        bookingTab   = new BookingTab(dataManager, this::refreshAll);
        historyTab   = new BookingHistoryTab(dataManager);

        Tab t1 = styledTab("⬡  Dashboard",   dashboardTab.build());
        Tab t2 = styledTab("⬡  Rooms",        roomTab.build());
        Tab t3 = styledTab("⬡  Book / Checkout", bookingTab.build());
        Tab t4 = styledTab("⬡  Booking History", historyTab.build());

        tp.getTabs().addAll(t1, t2, t3, t4);

        tp.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> refreshAll());

        return tp;
    }

    private Tab styledTab(String title, Node content) {
        Tab t = new Tab(title, content);
        t.setStyle("-fx-font-size: 13px;");
        return t;
    }

    private void refreshAll() {
        if (dashboardTab != null) dashboardTab.refresh();
        if (roomTab      != null) roomTab.refresh();
        if (bookingTab   != null) bookingTab.refresh();
        if (historyTab   != null) historyTab.refresh();
    }
}
