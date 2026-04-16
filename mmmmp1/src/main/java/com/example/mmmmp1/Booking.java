package com.example.mmmmp1;

import javafx.beans.property.*;

public class Booking {
    private final IntegerProperty bookingId    = new SimpleIntegerProperty();
    private final IntegerProperty roomNumber   = new SimpleIntegerProperty();
    private final StringProperty  roomType     = new SimpleStringProperty();
    private final StringProperty  guestName    = new SimpleStringProperty();
    private final StringProperty  guestPhone   = new SimpleStringProperty();
    private final StringProperty  checkIn      = new SimpleStringProperty();
    private final StringProperty  checkOut     = new SimpleStringProperty();
    private final IntegerProperty nights       = new SimpleIntegerProperty();
    private final DoubleProperty  totalCost    = new SimpleDoubleProperty();

    public Booking(int id, int roomNumber, String roomType, String guestName,
                   String guestPhone, String checkIn, String checkOut,
                   int nights, double totalCost) {
        this.bookingId.set(id);
        this.roomNumber.set(roomNumber);
        this.roomType.set(roomType);
        this.guestName.set(guestName);
        this.guestPhone.set(guestPhone);
        this.checkIn.set(checkIn);
        this.checkOut.set(checkOut);
        this.nights.set(nights);
        this.totalCost.set(totalCost);
    }

    public int getBookingId()           { return bookingId.get(); }
    public IntegerProperty bookingIdProperty() { return bookingId; }

    public int getRoomNumber()          { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    public String getRoomType()         { return roomType.get(); }
    public StringProperty roomTypeProperty() { return roomType; }

    public String getGuestName()        { return guestName.get(); }
    public StringProperty guestNameProperty() { return guestName; }

    public String getGuestPhone()       { return guestPhone.get(); }
    public StringProperty guestPhoneProperty() { return guestPhone; }

    public String getCheckIn()          { return checkIn.get(); }
    public StringProperty checkInProperty() { return checkIn; }

    public String getCheckOut()         { return checkOut.get(); }
    public StringProperty checkOutProperty() { return checkOut; }

    public int getNights()              { return nights.get(); }
    public IntegerProperty nightsProperty() { return nights; }

    public double getTotalCost()        { return totalCost.get(); }
    public DoubleProperty totalCostProperty() { return totalCost; }
}
