package com.example.h2ak.model;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendShip {
    private String id;
    private User user1;
    private User user2;
    private String createdDate;
    private String status;
    private FriendShipStatus friendShipStatus;
    String dateFormat;

    private static AtomicInteger count = new AtomicInteger(0);

    {
        Date date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        id = String.format("FriendShip%05d", count.incrementAndGet());
    }

    public FriendShip() {
    }

    public FriendShip(User user1, User user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.createdDate = dateFormat;
        this.friendShipStatus = FriendShipStatus.PENDING;
        this.status = this.friendShipStatus.getStatus();
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", this.createdDate.trim(), this.user1.getId(), this.user2.getId()).trim();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        FriendShip friendShip = (FriendShip) obj;
        return this.getUser2().equals(friendShip.getUser2()) &&
                this.getUser1().equals(friendShip.getUser1()) &&
                this.status.equals(friendShip.status);
    }

    @Override
    public int hashCode() {
        int result = getUser1().hashCode();
        result = 31 * result + getUser2().hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public FriendShipStatus getFriendShipStatus() {
        return friendShipStatus;
    }

    public void setFriendShipStatus(FriendShipStatus friendShipStatus) {
        this.friendShipStatus = friendShipStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public enum FriendShipStatus{
        PENDING("PENDING"),
        ACCEPTED("ACCEPTED"),
        DELETED("DELETED");
        private String status;
        private FriendShipStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

    }

}