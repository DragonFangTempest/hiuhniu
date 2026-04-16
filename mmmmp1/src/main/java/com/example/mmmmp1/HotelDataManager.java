package com.example.mmmmp1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;
import java.util.stream.Collectors;

public class HotelDataManager {

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();
    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    public HotelDataManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        rooms.addAll(
                new Room(101, Room.RoomType.STANDARD,  3500, 1, 2, true,  false, true),
                new Room(102, Room.RoomType.STANDARD,  3500, 1, 2, true,  false, true),
                new Room(103, Room.RoomType.DELUXE,    5500, 1, 2, true,  true,  true),
                new Room(201, Room.RoomType.DELUXE,    5500, 2, 2, true,  true,  true),
                new Room(202, Room.RoomType.SUITE,     9500, 2, 4, true,  true,  true),
                new Room(203, Room.RoomType.SUITE,     9500, 2, 4, true,  true,  true),
                new Room(301, Room.RoomType.PRESIDENTIAL,18000, 3, 4, true, true, true),
                new Room(302, Room.RoomType.STANDARD,  3500, 3, 2, true,  false, true),
                new Room(303, Room.RoomType.DELUXE,    5500, 3, 3, true,  true,  true),
                new Room(401, Room.RoomType.SUITE,     9500, 4, 4, true,  true,  true),
                new Room(402, Room.RoomType.PRESIDENTIAL,18000, 4, 6, true,true, true),
                new Room(403, Room.RoomType.STANDARD,  3200, 4, 2, true,  false, false)
        );
    }

    public ObservableList<Room> getRooms()    { return rooms; }
    public ObservableList<Booking> getBookings() { return bookings; }

    public boolean addRoom(Room room) {
        boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == room.getRoomNumber());
        if (exists) return false;
        rooms.add(room);
        return true;
    }

    public boolean bookRoom(int roomNumber, String guestName, String guestPhone,
                            String checkIn, String checkOut, int nights) {
        Optional<Room> opt = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber && r.getStatus() == Room.Status.AVAILABLE)
                .findFirst();
        if (opt.isEmpty()) return false;
        Room r = opt.get();
        r.setStatus(Room.Status.BOOKED);
        r.setGuestName(guestName);
        r.setGuestPhone(guestPhone);
        r.setCheckIn(checkIn);
        r.setCheckOut(checkOut);
        r.setNights(nights);

        Booking b = new Booking(bookings.size() + 1, roomNumber, r.getRoomTypeString(),
                guestName, guestPhone, checkIn, checkOut, nights, r.getTotalCost());
        bookings.add(b);
        return true;
    }

    public boolean checkoutRoom(int roomNumber) {
        Optional<Room> opt = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber && r.getStatus() == Room.Status.BOOKED)
                .findFirst();
        if (opt.isEmpty()) return false;
        Room r = opt.get();
        r.setStatus(Room.Status.AVAILABLE);
        r.setGuestName("");
        r.setGuestPhone("");
        r.setCheckIn("");
        r.setCheckOut("");
        r.setNights(0);
        return true;
    }

    public List<Room> getAvailableRooms() {
        return rooms.stream().filter(r -> r.getStatus() == Room.Status.AVAILABLE).collect(Collectors.toList());
    }

    public long countByStatus(Room.Status status) {
        return rooms.stream().filter(r -> r.getStatus() == status).count();
    }

    public long countByType(Room.RoomType type) {
        return rooms.stream().filter(r -> r.getRoomType() == type).count();
    }

    public double totalRevenue() {
        return bookings.stream().mapToDouble(Booking::getTotalCost).sum();
    }

    public double occupancyRate() {
        if (rooms.isEmpty()) return 0;
        return (double) countByStatus(Room.Status.BOOKED) / rooms.size() * 100;
    }

    public Optional<Room> findRoom(int number) {
        return rooms.stream().filter(r -> r.getRoomNumber() == number).findFirst();
    }
}
