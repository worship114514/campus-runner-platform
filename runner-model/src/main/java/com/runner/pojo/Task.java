package com.runner.pojo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "task")
public class Task {
    @Id
    private String id;

    private String title;

    private String description;

    @Column(name = "reward_amount")
    private BigDecimal rewardAmount;

    private Integer status;

    @Column(name = "publisher_id")
    private String publisherId;

    @Column(name = "publisher_name")
    private String publisherName;

    @Column(name = "publisher_avatar")
    private String publisherAvatar;

    @Column(name = "runner_id")
    private String runnerId;

    @Column(name = "runner_name")
    private String runnerName;

    @Column(name = "runner_avatar")
    private String runnerAvatar;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "pickup_lng")
    private BigDecimal pickupLng;

    @Column(name = "pickup_lat")
    private BigDecimal pickupLat;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "delivery_lng")
    private BigDecimal deliveryLng;

    @Column(name = "delivery_lat")
    private BigDecimal deliveryLat;

    @Column(name = "delivery_photo")
    private String deliveryPhoto;

    @Column(name = "face_verified")
    private Integer faceVerified;

    @Column(name = "expire_time")
    private Date expireTime;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherAvatar() {
        return publisherAvatar;
    }

    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }

    public String getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(String runnerId) {
        this.runnerId = runnerId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getRunnerAvatar() {
        return runnerAvatar;
    }

    public void setRunnerAvatar(String runnerAvatar) {
        this.runnerAvatar = runnerAvatar;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public BigDecimal getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(BigDecimal pickupLng) {
        this.pickupLng = pickupLng;
    }

    public BigDecimal getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(BigDecimal pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public BigDecimal getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(BigDecimal deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    public BigDecimal getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(BigDecimal deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public String getDeliveryPhoto() {
        return deliveryPhoto;
    }

    public void setDeliveryPhoto(String deliveryPhoto) {
        this.deliveryPhoto = deliveryPhoto;
    }

    public Integer getFaceVerified() {
        return faceVerified;
    }

    public void setFaceVerified(Integer faceVerified) {
        this.faceVerified = faceVerified;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}