package de.ndnentertainment.ndngameengine.world;

import de.ndnentertainment.ndngameengine.config.Configuration;
import de.ndnentertainment.ndngameengine.utilities.GameSpeedHandler;

/**
 * Created by nickn on 06.08.2017.
 */

public class PhysicsV2 {
    private GameSpeedHandler gsh;

    public enum X_Stages {
        ACCELERATED_MOTION,
        STEADY_MOTION,
        DECELERATED_MOTION,
        NO_MOTION
    }
    public enum X_Directions {
        LEFT, RIGHT, NONE
    }
    private double          x_StartTime;
    private double          x_CurrTime;
    private double          x_deltaTime;
    private float           x_StartSpeed;
    private float           x_CurrSpeed;
    private float           x_MaxSpeed;
    private float           x_Acceleration;
    private float           x_Start;
    private float           x_Curr;
    private X_Stages        x_Stage;
    private X_Directions    x_Direction;

    public enum Y_Stages {
        STEADY_MOTION,
        DECELERATED_MOTION,
        NO_MOTION
    }
    public enum Y_Directions {
        UP, DOWN, NONE
    }
    private double          y_StartTime;
    private double          y_CurrTime;
    private double          y_deltaTime;
    private float           y_MaxNoGravityTime;
    private float           y_StartSpeed;
    private float           y_Start;
    private float           y_Curr;
    private Y_Stages        y_Stage;
    private Y_Directions    y_Direction;

    public PhysicsV2(GameSpeedHandler gsh) {
        this.gsh = gsh;

        x_Stage = X_Stages.NO_MOTION;
        y_Stage = Y_Stages.NO_MOTION;

        x_Direction = X_Directions.NONE;
        y_Direction = Y_Directions.NONE;
    }

    public void x_StartMovement(float x_Start, float x_Acceleration, float x_MaxSpeed) {
        this.x_Start = x_Start;
        this.x_Acceleration = x_Acceleration;
        this.x_MaxSpeed = x_MaxSpeed;
        this.x_StartTime = 0;
        this.x_StartSpeed = this.x_CurrSpeed;
        this.x_Stage = X_Stages.ACCELERATED_MOTION;

        if(x_Acceleration < 0) {
            this.x_Direction = X_Directions.LEFT;
        } else if(x_Acceleration > 0) {
            this.x_Direction = X_Directions.RIGHT;
        } else {
            this.x_Direction = X_Directions.NONE;
        }
    }
    public void x_EndMovement() {
        if(x_Stage != X_Stages.NO_MOTION) {
            x_StartTime = x_CurrTime - x_deltaTime;
            x_StartSpeed = x_CurrSpeed;
            x_Start = x_Curr;
            x_Stage = X_Stages.DECELERATED_MOTION;
        }
    }
    public void x_StopMovement() {
        x_Stage = X_Stages.NO_MOTION;
        x_Acceleration = 0f;
        x_CurrSpeed = 0f;
        x_StartTime = 0;
        x_Direction = X_Directions.NONE;
    }
    public void x_Accelerate(float acceleration, float maxSpeed) {
        this.x_Acceleration = acceleration;
        this.x_MaxSpeed = maxSpeed;
    }
    public float x_Update() {
        if(x_Stage == X_Stages.NO_MOTION) {
            x_CurrSpeed = 0f;
            return x_Curr;
        }

        x_deltaTime = x_CurrTime;
        x_CurrTime += 1.0/gsh.getCurrFrameRate();
        x_deltaTime = x_CurrTime - x_deltaTime;

        float t;
        float s0;
        float s;

        if(x_Stage == X_Stages.ACCELERATED_MOTION) {
            t = (float)(x_CurrTime - x_StartTime);
            s0 = x_Start;
            float a = x_Acceleration;
            float v0 = x_StartSpeed;
            float v;

            v = a * t + v0;
            x_CurrSpeed = v;

            if(Math.abs(x_CurrSpeed) >= Math.abs(x_MaxSpeed)) {
                x_StartTime = x_CurrTime - x_deltaTime;
                x_CurrSpeed = x_MaxSpeed;
                x_StartSpeed = x_CurrSpeed;
                x_Start = x_Curr;
                x_Stage = X_Stages.STEADY_MOTION;
            } else {
                s = 0.5f * a * t*t + v0 * t + s0;
                x_Curr = s;
            }
        }
        if(x_Stage == X_Stages.STEADY_MOTION) {
            t = (float)(x_CurrTime - x_StartTime);
            s0 = x_Start;
            float v0 = x_StartSpeed;

            s = v0 * t + s0;
            x_Curr = s;
        }
        if(x_Stage == X_Stages.DECELERATED_MOTION) {
            t = (float)(x_CurrTime - x_StartTime);
            s0 = x_Start;
            float a = x_Acceleration * (-1);
            float v0 = x_StartSpeed;
            float v;

            v = a * t + v0;
            x_CurrSpeed = v;

            if( (x_Acceleration > 0 && x_CurrSpeed <= 0) || (x_Acceleration < 0 && x_CurrSpeed >= 0) ) {
                x_Stage = X_Stages.NO_MOTION;
            } else {
                s = 0.5f * a * t*t + v0 * t + s0;
                x_Curr = s;
            }
        }

        return x_Curr;
    }

    public void y_StartMovement(float y_Start,float y_StartSpeed, float y_MaxNoGravityTime) {
        this.y_Start = y_Start;
        this.y_StartSpeed = y_StartSpeed;
        this.y_MaxNoGravityTime = y_MaxNoGravityTime;
        this.y_StartTime = 0;
        this.y_Stage = Y_Stages.STEADY_MOTION;

        y_Direction = Y_Directions.UP;
    }
    public void y_EndMovement() {
        this.y_MaxNoGravityTime = 0l;
    }
    public void y_StopMovement() {
        this.y_Stage = Y_Stages.NO_MOTION;
    }
    public void y_Accelerate(float y_StartSpeed, float y_MaxNoGravityTime) {
        this.y_StartSpeed = y_StartSpeed;
        this.y_MaxNoGravityTime = y_MaxNoGravityTime;
    }
    public float y_Update() {
        y_deltaTime = y_CurrTime;
        y_CurrTime += 1.0/gsh.getCurrFrameRate();
        y_deltaTime = y_CurrTime - y_deltaTime;

        float t;
        float v0;
        float s0;
        float y;
        float g;

        if(y_Stage == Y_Stages.STEADY_MOTION) {
            t = (float)(y_CurrTime - y_StartTime);
            s0 = y_Start;
            v0 = y_StartSpeed;

            if(t > y_MaxNoGravityTime) {
                y = v0 * y_MaxNoGravityTime + s0;
                y_Start = y;
                t = t - y_MaxNoGravityTime;
                y_StartTime = y_CurrTime - t;
                y_Stage = Y_Stages.DECELERATED_MOTION;
            } else {
                y = v0 * t + s0;
                y_Curr = y;
            }
        }
        if(y_Stage == Y_Stages.DECELERATED_MOTION) {
            t = (float)(y_CurrTime - y_StartTime);
            s0 = y_Start;
            v0 = y_StartSpeed;
            g = Configuration.GRAVITY;

            y = v0 * t - (g/2) * t*t + s0;
            y_Curr = y;
        }

        return y_Curr;
    }

    public X_Stages getX_Stage() {
        return x_Stage;
    }

    public Y_Stages getY_Stage() {
        return y_Stage;
    }

    public X_Directions getX_Direction() {
        return x_Direction;
    }

    public Y_Directions getY_Direction() {
        return y_Direction;
    }
}
