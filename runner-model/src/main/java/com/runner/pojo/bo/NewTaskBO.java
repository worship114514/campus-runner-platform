package com.runner.pojo.bo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class NewTaskBO {
    @NotBlank(message = "任务标题不能为空")
    private String title;

    @NotBlank(message = "任务描述不能为空")
    private String description;

    @NotNull(message = "酬劳不能为空")
    private BigDecimal rewardAmount;

    @NotBlank(message = "取件地址不能为空")
    private String pickupLocation;

    private BigDecimal pickupLng;

    private BigDecimal pickupLat;

    @NotBlank(message = "送件地址不能为空")
    private String deliveryLocation;

    private BigDecimal deliveryLng;

    private BigDecimal deliveryLat;

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
}