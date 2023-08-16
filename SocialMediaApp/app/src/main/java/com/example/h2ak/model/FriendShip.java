package com.example.h2ak.model;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendShip {
    private String id;
    private User user1;
    private User user2;
    private String createdDate;
    private String status;
    private FriendShipStatus friendShipStatus;

    {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        id = UUID.randomUUID().toString();
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
        return "FriendShip{" +
                "id='" + id + '\'' +
                ", user1=" + user1.getId() +
                ", user2=" + user2.getId() +
                ", createdDate='" + createdDate + '\'' +
                ", status='" + status + '\'' +
                ", friendShipStatus=" + friendShipStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendShip that = (FriendShip) o;
        return id.equals(that.id) && user1.getId().equals(that.user1.getId()) && user2.getId().equals(that.user2.getId()) && createdDate.equals(that.createdDate) && status.equals(that.status) && friendShipStatus == that.friendShipStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user1, user2, createdDate, status, friendShipStatus);
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