package me.calebjones.spacelaunchnow.content.adapter;

import android.zetterstrom.com.forecast.models.Forecast;

import com.robinhood.spark.SparkAdapter;


public class DailySparkAdapter extends SparkAdapter {
    private Forecast yData;

    public DailySparkAdapter(Forecast yData) {
        this.yData = yData;
    }

    @Override
    public int getCount() {
        return yData.getDaily().getDataPoints().size();
    }

    @Override
    public Object getItem(int index) {
        return yData.getDaily().getDataPoints().get(index).getTime();
    }

    @Override
    public float getY(int index) {
        double precipProbability = yData.getDaily().getDataPoints().get(index).getPrecipProbability();
        return (float) precipProbability;
    }
}