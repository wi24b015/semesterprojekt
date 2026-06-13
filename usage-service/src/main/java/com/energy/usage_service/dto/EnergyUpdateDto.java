package com.energy.usage_service.dto;

import java.io.Serializable;

public class EnergyUpdateDto implements Serializable {

    private String type;
    private String hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public EnergyUpdateDto() {
    }

    public EnergyUpdateDto(String type, String hour, double communityProduced, double communityUsed, double gridUsed) {
        this.type = type;
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getType() {
        return type;
    }

    public String getHour() {
        return hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }
}