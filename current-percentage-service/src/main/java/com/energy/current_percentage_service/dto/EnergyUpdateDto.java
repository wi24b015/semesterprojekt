package com.energy.current_percentage_service.dto;

import java.io.Serializable;

public class EnergyUpdateDto implements Serializable {

    private String type;
    private String hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public EnergyUpdateDto() {
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