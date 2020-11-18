package com.bookanalyzer;

import java.util.Objects;

public class Input {

    private long timestamp;
    private String identifier; // A or R
    private String orderId;
    private String side;
    private double price;
    private int size;

    /**
     * in case "A"
     *
     * @param timestamp
     * @param identifier
     * @param orderId
     * @param side
     * @param price
     * @param size
     */
    public Input(long timestamp, String identifier, String orderId, String side, double price, int size) {

        this.timestamp = timestamp;
        this.identifier = identifier;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.size = size;
    }

    /**
     * in case "R"
     *
     * @param timestamp
     * @param identifier
     * @param orderId
     * @param size
     */
    public Input(long timestamp, String identifier, String orderId, int size) {

        this.timestamp = timestamp;
        this.identifier = identifier;
        this.orderId = orderId;
        this.size = size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Input other = (Input) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.orderId, other.orderId)) {
            return false;
        }
        return true;
    }
}
