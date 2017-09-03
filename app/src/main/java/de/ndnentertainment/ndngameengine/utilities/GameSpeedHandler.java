package de.ndnentertainment.ndngameengine.utilities;

import android.util.Log;

import de.ndnentertainment.ndngameengine.config.Configuration;

/**
 * Created by nickn on 06.08.2017.
 */

public class GameSpeedHandler {
    private long startTime;
    private long endTime;
    private long deltaFrame;
    private long timeSinceNewSecond = 0;

    private long startTime2;
    private long endTime2;

    private int frames = 0;
    private int currFrameRate = 0;
    private int adjustedFrameRate = 0;

    private boolean first = true;
    private boolean limitFrameRate = false;
    private boolean logFrameRate = false;

    public GameSpeedHandler() {
        startTime = System.currentTimeMillis();
        startTime2 = System.currentTimeMillis();
        currFrameRate = Configuration.FPS;
    }

    public void update() {
        if(first) {
            first = false;
            return;
        }

        //Limit Frame-Rate
        if(limitFrameRate) {
            limitFrameRate();
        }
        //Frame-Rate berechnen
        calculateFrameRate();

    }

    private void limitFrameRate() {
        endTime = System.currentTimeMillis();
        long dt = endTime - startTime;
        if (dt < (1000/adjustedFrameRate))
            try {
                Thread.sleep((1000/adjustedFrameRate) - dt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        startTime = System.currentTimeMillis();
    }
    private void calculateFrameRate() {
        endTime2 = System.currentTimeMillis();
        deltaFrame = endTime2 - startTime2;
        timeSinceNewSecond += deltaFrame;
        frames++;
        if(timeSinceNewSecond >= 1000) {
            currFrameRate = frames;
            if(logFrameRate) { logFrameRate(); }

            frames = 0;
            timeSinceNewSecond = 0;
        }
        startTime2 = System.currentTimeMillis();
    }
    private void logFrameRate() {
        Log.d("FPSCounter", "fps: " + currFrameRate);
    }


    public void setLogFrameRate(boolean bool) {
        logFrameRate = bool;
    }

    public void setLimitFrameRate(boolean limitFrameRate) {
        this.adjustedFrameRate = Configuration.FPS;
        this.limitFrameRate = limitFrameRate;
    }
    public void setLimitFrameRate(int fps) {
        this.adjustedFrameRate = fps;
        this.limitFrameRate = true;
    }

    public int getCurrFrameRate() {
        return currFrameRate;
    }

}
