package com.example.myradarlibrary;

/**
 */

public class RadarData {

    //名字
    private String title;
    //分数
    private double percentage;
    //目标分数
    private double mbPercentage;

    public RadarData(String title, double percentage, double mbPercentage) {
        this.title = title;
        this.percentage = percentage;
        this.mbPercentage = mbPercentage;
    }



    public String getTitle() {
        return title;
    }

    public double getPercentage() {
        return percentage;
    }

    public double getMbPercentage() {
        return mbPercentage;
    }
}
