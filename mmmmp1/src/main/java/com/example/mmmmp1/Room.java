package com.example.mmmmp1;

import javafx.beans.property.*;

public class Room {

    public enum RoomType { STANDARD, DELUXE, SUITE, PRESIDENTIAL }
    public enum Status     { AVAILABLE, BOOKED, MAINTENANCE }

    private final IntegerProperty  roomNumber  = new SimpleIntegerProperty();
    private final ObjectProperty<RoomType> roomType = new SimpleObjectProperty<>();
    private final DoubleProperty   pricePerNight = new SimpleDoubleProperty();
    private final IntegerProperty  floor       = new SimpleIntegerProperty();
    private final IntegerProperty  capacity    = new SimpleIntegerProperty();
    private final BooleanProperty  hasWifi     = new SimpleBooleanProperty();
    private final BooleanProperty  hasBreakfast= new SimpleBooleanProperty();
    private final BooleanProperty  hasAC       = new SimpleBooleanProperty();
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.AVAILABLE);

    // booking info
    private final StringProperty   guestName   = new SimpleStringProperty("");
    private final StringProperty   guestPhone  = new SimpleStringProperty("");
    private final StringProperty   checkIn     = new SimpleStringProperty("");
    private final StringProperty   checkOut    = new SimpleStringProperty("");
    private final IntegerProperty  nights      = new SimpleIntegerProperty(0);

    public Room(int roomNumber, RoomType type, double price, int floor, int capacity,
                boolean wifi, boolean breakfast, boolean ac) {
        this.roomNumber.set(roomNumber);
        this.roomType.set(type);
        this.pricePerNight.set(price);
        this.floor.set(floor);
        this.capacity.set(capacity);
        this.hasWifi.set(wifi);
        this.hasBreakfast.set(breakfast);
        this.hasAC.set(ac);
    }

    /* ---- Getters / Setters ---- */
    public int getRoomNumber()          { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    public RoomType getRoomType()       { return roomType.get(); }
    public ObjectProperty<RoomType> roomTypeProperty() { return roomType; }
    public String getRoomTypeString()   { return roomType.get().name(); }

    public double getPricePerNight()    { return pricePerNight.get(); }
    public DoubleProperty pricePerNightProperty() { return pricePerNight; }

    public int getFloor()               { return floor.get(); }
    public IntegerProperty floorProperty() { return floor; }

    public int getCapacity()            { return capacity.get(); }
    public IntegerProperty capacityProperty() { return capacity; }

    public boolean isHasWifi()          { return hasWifi.get(); }
    public BooleanProperty hasWifiProperty() { return hasWifi; }

    public boolean isHasBreakfast()     { return hasBreakfast.get(); }
    public BooleanProperty hasBreakfastProperty() { return hasBreakfast; }

    public boolean isHasAC()            { return hasAC.get(); }
    public BooleanProperty hasACProperty() { return hasAC; }

    public Status getStatus()           { return status.get(); }
    public ObjectProperty<Status> statusProperty() { return status; }
    public void setStatus(Status s)     { status.set(s); }
    public String getStatusString()     { return status.get().name(); }

    public String getGuestName()        { return guestName.get(); }
    public StringProperty guestNameProperty() { return guestName; }
    public void setGuestName(String n)  { guestName.set(n); }

    public String getGuestPhone()       { return guestPhone.get(); }
    public StringProperty guestPhoneProperty() { return guestPhone; }
    public void setGuestPhone(String p) { guestPhone.set(p); }

    public String getCheckIn()          { return checkIn.get(); }
    public StringProperty checkInProperty() { return checkIn; }
    public void setCheckIn(String d)    { checkIn.set(d); }

    public String getCheckOut()         { return checkOut.get(); }
    public StringProperty checkOutProperty() { return checkOut; }
    public void setCheckOut(String d)   { checkOut.set(d); }

    public int getNights()              { return nights.get(); }
    public IntegerProperty nightsProperty() { return nights; }
    public void setNights(int n)        { nights.set(n); }

    public double getTotalCost()        { return pricePerNight.get() * nights.get(); }

    public String getAmenitiesSummary() {
        StringBuilder sb = new StringBuilder();
        if (hasAC.get())        sb.append("AC ");
        if (hasWifi.get())      sb.append("WiFi ");
        if (hasBreakfast.get()) sb.append("Breakfast");
        return sb.toString().trim();
    }

    @Override public String toString() {
        return "Room " + roomNumber.get() + " [" + roomType.get() + "]";
    }
}