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

    private static AtomicInteger count = new AtomicInteger(0);

    {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        id = String.format("FriendShip%d", count.incrementAndGet());
        this.friendShipStatus = FriendShipStatus.PENDING;
        this.status = this.friendShipStatus.getStatus();
    }

    public FriendShip() {
    }

    public FriendShip(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
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