package de.ndnentertainment.ndngameengine.world.physic;

import java.util.ArrayList;

import de.ndnentertainment.ndngameengine.utilities.Edge2D;
import de.ndnentertainment.ndngameengine.utilities.GameSpeedHandler;
import de.ndnentertainment.ndngameengine.utilities.InRange;
import de.ndnentertainment.ndngameengine.utilities.Vec2D;
import de.ndnentertainment.ndngameengine.utilities.Vec3D;
import de.ndnentertainment.ndngameengine.world.Level;
import de.ndnentertainment.ndngameengine.world.model3d.BoundingBox;
import de.ndnentertainment.ndngameengine.world.model3d.Model3D;

/**
 * Created by nickn on 03.10.2017.
 */

public class Physic {
    private Model3D model3D;
    private Level level;
    private GameSpeedHandler gsh;

    private Motion motion;

    private Vec3D pre_Pos;
    private Vec3D cur_Pos;
    private Vec3D new_Pos;

    //Meeple Informations
    //--Basis
    private Vec3D       BASE_feetsMP;
    private Vec3D       BASE_chestLMP;
    private Vec3D       BASE_chestRMP;
    private Vec3D       BASE_headMP;
    private Edge2D      BASE_feetsSurface;
    private Edge2D      BASE_feetsSurface_To_ChestL;
    private Edge2D      BASE_feetsSurface_To_ChestR;
    private Edge2D      BASE_headSurface;
    private Edge2D      BASE_headSurface_To_ChestL;
    private Edge2D      BASE_headSurface_To_ChestR;
    //--Zuvor
    private Vec3D       pre_feetsMP;
    private Vec3D       pre_chestLMP;
    private Vec3D       pre_chestRMP;
    private Vec3D       pre_headMP;
    private Edge2D      pre_feetsSurface;
    private Edge2D      pre_feetsSurface_To_ChestL;
    private Edge2D      pre_feetsSurface_To_ChestR;
    private Edge2D      pre_headSurface;
    private Edge2D      pre_headSurface_To_ChestL;
    private Edge2D      pre_headSurface_To_ChestR;
    //--Aktuell
    private Vec3D       cur_feetsMP;
    private Vec3D       cur_chestLMP;
    private Vec3D       cur_chestRMP;
    private Vec3D       cur_headMP;
    private Edge2D      cur_feetsSurface;
    private Edge2D      cur_feetsSurface_To_ChestL;
    private Edge2D      cur_feetsSurface_To_ChestR;
    private Edge2D      cur_headSurface;
    private Edge2D      cur_headSurface_To_ChestL;
    private Edge2D      cur_headSurface_To_ChestR;
    //Offsets
    private float       BASE_yOffset;


    //Pressed Buttons
    public enum ButtonStages {
        NONE, _PRESS, HOLD, _RELEASE
    }
    private ButtonStages btnWLStage;
    private ButtonStages btnWRStage;
    private ButtonStages btnJStage;
    private ButtonStages btnDStage;

    public enum X_MotionStages {
        NONE, WALK_LEFT, WALK_RIGHT
    }
    public enum Y_MotionStages {
        NONE,
        JUMP_START, JUMP_NO_GRAVITY, JUMP_WITH_GRAVITY, JUMP_WITH_EXTRA_GRAVITY
    }
    private X_MotionStages x_motionStage;
    private Y_MotionStages y_motionStage;

    //Motion
    private float X_SPEED;
    private float X_TIME_TO_REACH_SPEED;
    private float X_ACCELERATION;
    private float Y_SPEED;
    private float Y_TIME_WITHOUT_GRAVITY;

    private float x_startPos;
    private float x_elapsedTime;
    private float x_startSpeed;
    private float x_curSpeed;

    private float y_elapsedTime;

    public enum X_Directions { LEFT, RIGHT, NONE }
    public enum Y_Directions { UP, DOWN, NONE }
    private X_Directions x_direction;
    private Y_Directions y_direction;
    public enum X_Stages {ACCELERATED_MOTION, STEADY_MOTION, DECELERATED_MOTION, NONE}
    private X_Stages x_stage;

    private boolean isWalkRightPressed;
    private boolean isWalkLeftPressed;
    private boolean isJumpPressed;
    private boolean isDuckPressed;


    //
    private int modelIndex;
    private int collisonPathIndex;

    public Physic(Model3D model3D, Level level, GameSpeedHandler gsh, Vec3D startPoint) {
        this.model3D = model3D;
        this.level = level;
        this.gsh = gsh;

        //this.motion = new Motion();

        BoundingBox bb = model3D.getBoundingBox();
        this.BASE_feetsMP                = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMinY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_chestLMP               = new Vec3D ( bb.getMinX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_chestRMP               = new Vec3D ( bb.getMaxX()                 , (bb.getMinY()+bb.getMaxY())/2f, (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_headMP                 = new Vec3D ((bb.getMinX()+bb.getMaxX())/2f,  bb.getMaxY()                 , (bb.getMinZ()+bb.getMaxZ())/2f);
        this.BASE_feetsSurface           = new Edge2D(new Vec2D(bb.getMinX(), bb.getMinY()), new Vec2D(bb.getMaxX(), bb.getMinY()));
        this.BASE_feetsSurface_To_ChestL = new Edge2D(new Vec2D(BASE_feetsSurface.getPOINT_A().x, BASE_feetsSurface.getPOINT_A().y), new Vec2D(BASE_chestLMP.x, BASE_chestLMP.y));
        this.BASE_feetsSurface_To_ChestR = new Edge2D(new Vec2D(BASE_feetsSurface.getPOINT_B().x, BASE_feetsSurface.getPOINT_B().y), new Vec2D(BASE_chestRMP.x, BASE_chestRMP.y));
        this.BASE_headSurface            = new Edge2D(new Vec2D(bb.getMinX(), bb.getMaxY()), new Vec2D(bb.getMaxX(), bb.getMaxY()));
        this.BASE_headSurface_To_ChestL  = new Edge2D(new Vec2D(BASE_chestLMP.x, BASE_chestLMP.y), new Vec2D(BASE_headSurface.getPOINT_A().x, BASE_headSurface.getPOINT_A().y));
        this.BASE_headSurface_To_ChestR  = new Edge2D(new Vec2D(BASE_chestRMP.x, BASE_chestRMP.y), new Vec2D(BASE_headSurface.getPOINT_B().x, BASE_headSurface.getPOINT_B().y));

        pre_Pos = new Vec3D(0f, 0f, 0f);
        cur_Pos = new Vec3D(0f, 0f, 0f);
        new_Pos = new Vec3D(0f, 0f, 0f);

        BASE_yOffset = BASE_feetsMP.y * (-1);

        btnWLStage = ButtonStages.NONE;
        btnWRStage = ButtonStages.NONE;
        btnJStage = ButtonStages.NONE;
        btnDStage = ButtonStages.NONE;

        x_direction = X_Directions.NONE;
        y_direction = Y_Directions.NONE;
        x_stage = X_Stages.NONE;
        x_elapsedTime = 0f;
        x_startSpeed = 0f;
        x_curSpeed = 0f;
        y_elapsedTime = 0f;
        isWalkRightPressed = false;
        isWalkLeftPressed = false;
        isJumpPressed = false;
        isDuckPressed = false;

        modelIndex = 0;
        collisonPathIndex = 0;

        putOnGround(startPoint);
    }

    public void setMotionParameters(float walkSpeed, float walkTimeToReachSpeed, float jumpSpeed, float jumpTimeWithoutGravity) {
        this.X_SPEED = walkSpeed;
        this.X_TIME_TO_REACH_SPEED = walkTimeToReachSpeed;
        this.Y_SPEED = jumpSpeed;
        this.Y_TIME_WITHOUT_GRAVITY = jumpTimeWithoutGravity;

        X_ACCELERATION = X_SPEED / X_TIME_TO_REACH_SPEED;
    }

    public void update(boolean walkLeft, boolean walkRight, boolean jump, boolean duck) {
        /*switch(x_motionStage) {
            case NONE:
                if      (walkLeft && walkRight)     x_motionStage = X_MotionStages.NONE;
                else if (walkLeft)                  x_motionStage = X_MotionStages.WALK_LEFT_START;
                else if (walkRight)                 x_motionStage = X_MotionStages.WALK_RIGHT_START;
                break;
            case WALK_LEFT_START:
            case WALK_LEFT:
            case WALK_RIGHT_SWITCHED_TO_WALK_LEFT:
                if      (walkLeft && walkRight)     x_motionStage = X_MotionStages.WALK_LEFT_END;
                else if (walkLeft)                  x_motionStage = X_MotionStages.WALK_LEFT;
                else if (walkRight)                 x_motionStage = X_MotionStages.WALK_LEFT_SWITCHED_TO_WALK_RIGHT;
                else                                x_motionStage = X_MotionStages.WALK_LEFT_END;
                break;
            case WALK_RIGHT_START:
            case WALK_RIGHT:
            case WALK_LEFT_SWITCHED_TO_WALK_RIGHT:
                if      (walkLeft && walkRight)     x_motionStage = X_MotionStages.WALK_RIGHT_END;
                else if (walkLeft)                  x_motionStage = X_MotionStages.WALK_RIGHT_SWITCHED_TO_WALK_LEFT;
                else if (walkRight)                 x_motionStage = X_MotionStages.WALK_RIGHT;
                else                                x_motionStage = X_MotionStages.WALK_RIGHT_END;
                break;
            case WALK_LEFT_END:
            case WALK_LEFT_PHASE_OUT:
                if      (walkLeft && walkRight)     x_motionStage = X_MotionStages.WALK_LEFT_PHASE_OUT;
                else if (walkLeft)                  x_motionStage = X_MotionStages.WALK_LEFT_START;
                else if (walkRight)                 x_motionStage = X_MotionStages.WALK_RIGHT_START;
                else                                x_motionStage = X_MotionStages.WALK_LEFT_PHASE_OUT;
                break;
            case WALK_RIGHT_END:
            case WALK_RIGHT_PHASE_OUT:
                if      (walkLeft && walkRight)     x_motionStage = X_MotionStages.WALK_RIGHT_PHASE_OUT;
                else if (walkLeft)                  x_motionStage = X_MotionStages.WALK_LEFT_START;
                else if (walkRight)                 x_motionStage = X_MotionStages.WALK_RIGHT_START;
                else                                x_motionStage = X_MotionStages.WALK_RIGHT_PHASE_OUT;
                break;
        }
        switch(x_motionStage) {
            case NONE:
                break;
            case WALK_LEFT_START:
            case WALK_RIGHT_SWITCHED_TO_WALK_LEFT:
                motion.x_StartMovement(cur_Pos.x, X_ACCELERATION * (-1), X_SPEED);
                break;
            case WALK_LEFT:
                cur_Pos.x = motion.x_Update();
                break;
            case WALK_LEFT_END:
                motion.x_EndMovement();
                break;
            case WALK_LEFT_PHASE_OUT:
                cur_Pos.x = motion.x_Update();
                break;
            case WALK_RIGHT_START:
            case WALK_LEFT_SWITCHED_TO_WALK_RIGHT:
                motion.x_StartMovement(cur_Pos.x, X_ACCELERATION, X_SPEED);
                break;
            case WALK_RIGHT:
                cur_Pos.x = motion.x_Update();
                break;
            case WALK_RIGHT_END:
                motion.x_EndMovement();
                break;
            case WALK_RIGHT_PHASE_OUT:
                cur_Pos.x = motion.x_Update();
                break;
        }
        switch(y_motionStage) {
            case NONE:
                if (jump && duck) y_motionStage = Y_MotionStages.NONE;
                else if (jump) y_motionStage = Y_MotionStages.JUMP_START;
                break;
            case JUMP_START:
            case JUMP_NO_GRAVITY:
                if (jump && duck) y_motionStage = Y_MotionStages.JUMP_WITH_GRAVITY;
                else if (jump) y_motionStage = Y_MotionStages.JUMP_NO_GRAVITY;
                else if (duck) y_motionStage = Y_MotionStages.JUMP_WITH_EXTRA_GRAVITY;
                else y_motionStage = Y_MotionStages.JUMP_WITH_GRAVITY;
                break;
            case JUMP_WITH_GRAVITY:
            case JUMP_WITH_EXTRA_GRAVITY:
                if (jump && duck) y_motionStage = Y_MotionStages.JUMP_WITH_GRAVITY;
                else if (jump) y_motionStage = Y_MotionStages.JUMP_WITH_GRAVITY;
                else if (duck) y_motionStage = Y_MotionStages.JUMP_WITH_EXTRA_GRAVITY;
                else y_motionStage = Y_MotionStages.JUMP_WITH_GRAVITY;
                break;
        }
        */

        btnWLStage = analyzeButton(btnWLStage, walkLeft);
        btnWRStage = analyzeButton(btnWRStage, walkRight);
        btnJStage = analyzeButton(btnJStage, jump);
        btnDStage = analyzeButton(btnDStage, duck);


        //Walk
        if(btnWLStage == ButtonStages.NONE && btnWRStage == ButtonStages.NONE) {
            //None
        }
        else if(btnWLStage == ButtonStages.NONE && btnWRStage == ButtonStages._PRESS) {
            //Start WR
            x_motionStage = X_MotionStages.WALK_RIGHT;
        }
        else if(btnWLStage == ButtonStages.NONE && btnWRStage == ButtonStages.HOLD) {
            //Update WR
        }
        else if(btnWLStage == ButtonStages.NONE && btnWRStage == ButtonStages._RELEASE) {
            //End WR
        }
        else if(btnWLStage == ButtonStages._PRESS && btnWRStage == ButtonStages.NONE) {
            //Start WL
            x_motionStage = X_MotionStages.WALK_LEFT;
        }
        else if(btnWLStage == ButtonStages._PRESS && btnWRStage == ButtonStages._PRESS) {
            //None
        }
        else if(btnWLStage == ButtonStages._PRESS && btnWRStage == ButtonStages.HOLD) {
            //End WR
        }
        else if(btnWLStage == ButtonStages._PRESS && btnWRStage == ButtonStages._RELEASE) {
            //Start WL
            x_motionStage = X_MotionStages.WALK_LEFT;
        }
        else if(btnWLStage == ButtonStages.HOLD && btnWRStage == ButtonStages._RELEASE) {
            if(x_motionStage == X_MotionStages.WALK_LEFT) {

            }
        }
        else if(btnWLStage == ButtonStages.HOLD && btnWRStage == ButtonStages._RELEASE) {
            //
        }
        else if(btnWLStage == ButtonStages.HOLD && btnWRStage == ButtonStages._RELEASE) {
            //
        }
        else if(btnWLStage == ButtonStages.HOLD && btnWRStage == ButtonStages._RELEASE) {
            //
        }


        switch(x_motionStage) {
            case NONE:
                switch(btnWLStage) {
                    case NONE:
                        switch(btnWRStage) {
                            case NONE:
                                //NONE
                                break;
                            case _PRESS:
                                //Start WR
                                break;
                            case HOLD:
                            case _RELEASE:
                                //Error
                                break;
                        }
                        break;
                    case _PRESS:
                        switch(btnWRStage) {
                            case NONE:
                                //Start WL
                                break;
                            case _PRESS:
                                //NONE
                                break;
                            case HOLD:
                            case _RELEASE:
                                //Error
                                break;
                        }
                        break;
                    case HOLD:
                    case _RELEASE:
                        //Error
                        break;
                }
            case WALK_LEFT:
                switch(btnWLStage) {
                    case NONE:
                    case _PRESS:
                        //Error
                        break;
                    case HOLD:
                        switch(btnWRStage) {
                            case NONE:
                                //Update WL
                                break;
                            case _PRESS:
                                //End WL
                                break;
                            case HOLD:
                                //NONE
                                break;
                            case _RELEASE:
                                //Start WL
                                break;
                        }
                        break;
                    case _RELEASE:
                        switch(btnWRStage) {
                            case NONE:
                                //End WL
                                break;
                            case _PRESS:
                                //Start WR
                                break;
                            case HOLD:
                                //Start WR
                                break;
                            case _RELEASE:
                                //End WL
                                break;
                        }
                        break;
                }
            case WALK_RIGHT:
        }

    }

    public float getXPos() {
        return new_Pos.x;
    }
    public float getYPos() {
        return new_Pos.y;
    }
    public float getZPos() {
        return new_Pos.z;
    }


    private ButtonStages analyzeButton(ButtonStages btn, boolean isTrue) {
        switch(btn) {
            case NONE:
                if(isTrue)    btn = ButtonStages._PRESS;
                break;
            case _PRESS:
                if(isTrue)    btn = ButtonStages.HOLD;
                else          btn = ButtonStages._RELEASE;
                break;
            case HOLD:
                if(isTrue)    btn = ButtonStages.HOLD;
                else          btn = ButtonStages._RELEASE;
                break;
            case _RELEASE:
                if(isTrue)    btn = ButtonStages._PRESS;
                else          btn = ButtonStages.NONE;
        }
        return btn;
    }

    //Motion
    public void setIsWalkRightPressed(boolean isWalkRightPressed) {
        if(!isWalkLeftPressed) {
            this.isWalkRightPressed = isWalkRightPressed;
            if(isWalkRightPressed) {
                x_StartMotion();
            } else {
                x_EndMotion();
            }
        }
    }
    public void setIsWalkLeftPressed(boolean isWalkLeftPressed) {
        this.isWalkLeftPressed = isWalkLeftPressed;
        if(isWalkLeftPressed) {
            isWalkRightPressed = false;
        }
    }
    public void setIsJumpPressed(boolean isJumpPressed) {
        this.isJumpPressed = isJumpPressed;
        if(isJumpPressed) {
            isDuckPressed = false;
        }
    }
    public void setIsDuckPressed(boolean isDuckPressed) {
        this.isDuckPressed = isDuckPressed;
        if(isDuckPressed) {
            isJumpPressed = false;
        }
    }
    private void x_StartMotion() {
        if(x_stage != X_Stages.NONE) {
            x_stage = X_Stages.ACCELERATED_MOTION;
            x_elapsedTime = 0f;
        } else {
            x_startPos = cur_Pos.x;
            x_stage = X_Stages.ACCELERATED_MOTION;
            x_elapsedTime = 0f;
            x_startSpeed = 0f;
            x_curSpeed = 0f;
        }
    }
    private void x_EndMotion() {
        if(x_stage != X_Stages.NONE) {
            x_elapsedTime = 0f;
            x_startSpeed = x_curSpeed;
            x_startPos = cur_Pos.x;
            x_stage = X_Stages.DECELERATED_MOTION;
        }
    }
/*
    private void updateMotion() {
        if(isWalkRightPressed)      doMotion(X_SPEED);
        else if(isWalkLeftPressed)  doMotion(X_SPEED * (-1));
        else                        doMotion(0f);
    }

    private void doMotion(float speed) {

        if(x_stage == X_Stages.ACCELERATED_MOTION) {
            float walk_deltaTime = 1f / gsh.getCurrFrameRate();
            x_elapsedTime += walk_deltaTime;
            x_curSpeed = X_ACCELERATION * x_elapsedTime + x_startSpeed;
            if(x_curSpeed > X_SPEED) {
                x_elapsedTime = 0f;
                x_startSpeed = X_SPEED;
            }
        }

        float x_deltaTime = 1f / gsh.getCurrFrameRate();
        x_elapsedTime += x_deltaTime;

        float x_preSpeed = x_curSpeed;

        float t;
        float s0;
        float s;

        if(x_stage == X_Stages.ACCELERATED_MOTION) {
            t = x_elapsedTime;
            s0 = x_startPos;
            float a = X_ACCELERATION;
            float v0 = x_startSpeed;
            float v;

            v = a * t + v0;
            x_curSpeed = v;

            if(Math.abs(x_curSpeed) >= Math.abs(X_SPEED)) {
                x_elapsedTime = 0f + x_deltaTime;
                x_curSpeed = X_SPEED;
                x_startSpeed = x_preSpeed;
                x_startPos = cur_Pos.x;
                x_stage = X_Stages.STEADY_MOTION;
            } else {
                s = 0.5f * a * t*t + v0 * t + s0;
                cur_Pos.x = s;
            }
        }
        if(x_stage == X_Stages.STEADY_MOTION) {
            t = x_elapsedTime;
            s0 = x_startPos;
            float v0 = x_startSpeed;

            s = v0 * t + s0;
            cur_Pos.x = s;
        }
        if(x_stage == X_Stages.DECELERATED_MOTION) {
            t = x_elapsedTime;
            s0 = x_Start;
            float a = x_Acceleration * (-1);
            float v0 = x_StartSpeed;
            float v;

            t /= 1000f;

            v = a * t + v0;
            x_CurrSpeed = v;

            if( (x_Acceleration > 0 && x_CurrSpeed <= 0) || (x_Acceleration < 0 && x_CurrSpeed >= 0) ) {
                x_Stage = X_Stages.NO_MOTION;
            } else {
                s = 0.5f * a * t*t + v0 * t + s0;
                x_Curr = s;
            }
        }

    }
*/

    //Collision

    private void detectCollision() {
        new_Pos.x = cur_Pos.x;
        new_Pos.y = cur_Pos.y;
        new_Pos.z = cur_Pos.z;

        updateMeepleInformationVectors(pre_Pos, cur_Pos);

        //Nicht in der Luft, sondern auf dem Boden laufend
        if(y_direction == Y_Directions.NONE) {
            Vec3D result = calcNewPos_NotInAir();
        }
        //In der Luft
        else {

        }

        cur_Pos.x = new_Pos.x;
        cur_Pos.y = new_Pos.y;
        cur_Pos.z = new_Pos.z;
        pre_Pos.x = cur_Pos.x;
        pre_Pos.y = cur_Pos.y;
        pre_Pos.z = cur_Pos.z;
    }

    private Model3D[] getRelevantCollisonModels(boolean checkX, boolean checkY) {
        Model3D[] collisionModels = level.getAllModels();
        ArrayList<Model3D> relevantCollisonModels = new ArrayList<>();
        for(Model3D model : collisionModels) {
            BoundingBox bb = model.getBoundingBox();
            if( (!checkX || InRange.check( new float[] {bb.getMinX(), bb.getMaxX()},
                                           new float[] {pre_chestLMP.x, pre_chestRMP.x, cur_chestLMP.x, cur_chestRMP.x}, true )) &&
                (!checkY || InRange.check( new float[] {bb.getMinY(), bb.getMaxY()},
                                           new float[] {pre_feetsMP.y, pre_headMP.y, cur_feetsMP.y, cur_headMP.y}, true )) )
            {
                relevantCollisonModels.add(model);
            }
        }
        return relevantCollisonModels.toArray(new Model3D[relevantCollisonModels.size()]);
    }
    private Vec3D calcNewPos_NotInAir() {
        Model3D[] collisionModels = level.getAllModels();

        for(int i = modelIndex; i < collisionModels.length; i++) {
            float[] cp = collisionModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }

            Vec2D A = new Vec2D(cp[collisonPathIndex], cp[collisonPathIndex+1]);
            Vec2D B = new Vec2D(0f, 0f);
            for (int j = collisonPathIndex+3; j < cp.length && j >= 0;) {
                B.x = cp[j];
                B.y = cp[j + 1];

                Edge2D curEdge = new Edge2D(new Vec2D(A.x, A.y), new Vec2D(B.x, B.y));
                if( Math.abs(curEdge.getMInDegree()) < 30 ) {
                    if( curEdge.calcY(cur_Pos.x) ) {
                        new_Pos.y = curEdge.getResult_calcY() + BASE_yOffset;
                        return null;
                    } else if(x_direction == X_Directions.RIGHT) {
                        j += 3;
                    } else if(x_direction == X_Directions.LEFT) {
                        j -= 3;
                    }
                } else {
                    if(x_direction == X_Directions.RIGHT) {
                        cur_Pos.x = A.x - cur_feetsSurface.getPOINT_B().x;
                    } else if(x_direction == X_Directions.LEFT) {
                        cur_Pos.x = B.x - cur_feetsSurface.getPOINT_A().x;
                    }

                    j = collisonPathIndex;
                    A = new Vec2D(cp[collisonPathIndex], cp[collisonPathIndex+1]);
                    B = new Vec2D(0f, 0f);
                }

                A.x = B.x;
                A.y = B.y;
            }
        }
        return null;
    }

    private void putOnGround(Vec3D point) {
        updateMeepleInformationVectors(point, point);
        Model3D[] relevantCollisionModels = getRelevantCollisonModels(true, false);

        ArrayList<ArrayList<IntersectionInfo>> intersections = new ArrayList<>();
        for(int i = 0; i < relevantCollisionModels.length; i++) {
            float[] cp = relevantCollisionModels[i].getCollisionPath();
            if (cp == null) {
                continue;
            }

            Vec2D A = new Vec2D(cp[0], cp[1]);
            Vec2D B = new Vec2D(0f, 0f);
            for (int j = 3; j < cp.length; j = j + 3) {
                B.x = cp[j];
                B.y = cp[j + 1];

                Edge2D curEdge = new Edge2D(new Vec2D(A.x, A.y), new Vec2D(B.x, B.y));
                if( curEdge.calcY(point.x) ) {
                    float result = curEdge.getResult_calcY();
                    if( intersections.size() == 0 || intersections.get(intersections.size()-1).get(0).getModelIndex() != relevantCollisionModels[i].getModelIndex() ) {
                        intersections.add(new ArrayList<IntersectionInfo>());
                    }
                    IntersectionInfo intrInfo = new IntersectionInfo();
                    intrInfo.setModelName( relevantCollisionModels[i].getModelName() );
                    intrInfo.setModelIndex( relevantCollisionModels[i].getModelIndex() );
                    intrInfo.setCollisionPathIndex( j - 3 );
                    intrInfo.setIntersectionPoint( new Vec3D(point.x, result, point.z) );

                    intersections.get(intersections.size()-1).add( intrInfo );
                }

                A.x = B.x;
                A.y = B.y;
            }
        }

        IntersectionInfo searchedPoint = null;
        for(ArrayList<IntersectionInfo> inter : intersections) {
            IntersectionInfo topPoint = inter.get(0);
            for(IntersectionInfo interInfo : inter) {
                topPoint = topPoint.getIntersectionPoint().y < interInfo.getIntersectionPoint().y ? interInfo : topPoint;
            }
            if(topPoint.getIntersectionPoint().y < cur_headMP.y) {
                if(searchedPoint == null || topPoint.getIntersectionPoint().y > searchedPoint.getIntersectionPoint().y) {
                    searchedPoint = topPoint;
                }
            }
        }

        if(searchedPoint != null) {
            new_Pos.y = searchedPoint.getIntersectionPoint().y + BASE_yOffset;
            modelIndex = searchedPoint.getModelIndex();
            collisonPathIndex = searchedPoint.getCollisionPathIndex();
        }
        new_Pos.x = point.x;
        new_Pos.z = point.z;

        cur_Pos.x = new_Pos.x;
        cur_Pos.y = new_Pos.y;
        cur_Pos.z = new_Pos.z;
        pre_Pos.x = cur_Pos.x;
        pre_Pos.y = cur_Pos.y;
        pre_Pos.z = cur_Pos.z;
    }

    private void updateMeepleInformationVectors(Vec3D pre_point, Vec3D cur_point) {
        pre_feetsMP                 = new Vec3D (BASE_feetsMP.x  + pre_point.x, BASE_feetsMP.y  + pre_point.y, BASE_feetsMP.z  + pre_point.z);
        pre_chestLMP                = new Vec3D (BASE_chestLMP.x + pre_point.x, BASE_chestLMP.y + pre_point.y, BASE_chestLMP.z + pre_point.z);
        pre_chestRMP                = new Vec3D (BASE_chestRMP.x + pre_point.x, BASE_chestRMP.y + pre_point.y, BASE_chestRMP.z + pre_point.z);
        pre_headMP                  = new Vec3D (BASE_headMP.x   + pre_point.x, BASE_headMP.y   + pre_point.y, BASE_headMP.z   + pre_point.z);
        pre_feetsSurface            = new Edge2D(new Vec2D(BASE_feetsSurface.getPOINT_A().x + pre_point.x, BASE_feetsSurface.getPOINT_A().y + pre_point.y), new Vec2D(BASE_feetsSurface.getPOINT_B().x + pre_point.x, BASE_feetsSurface.getPOINT_B().y + pre_point.y));
        pre_feetsSurface_To_ChestL  = new Edge2D(new Vec2D(BASE_feetsSurface_To_ChestL.getPOINT_A().x + pre_point.x, BASE_feetsSurface_To_ChestL.getPOINT_A().y + pre_point.y), new Vec2D(BASE_feetsSurface_To_ChestL.getPOINT_B().x + pre_point.x, BASE_feetsSurface_To_ChestL.getPOINT_B().y + pre_point.y));
        pre_feetsSurface_To_ChestR  = new Edge2D(new Vec2D(BASE_feetsSurface_To_ChestR.getPOINT_A().x + pre_point.x, BASE_feetsSurface_To_ChestR.getPOINT_A().y + pre_point.y), new Vec2D(BASE_feetsSurface_To_ChestR.getPOINT_B().x + pre_point.x, BASE_feetsSurface_To_ChestR.getPOINT_B().y + pre_point.y));
        pre_headSurface             = new Edge2D(new Vec2D(BASE_headSurface.getPOINT_A().x + pre_point.x, BASE_headSurface.getPOINT_A().y + pre_point.y), new Vec2D(BASE_headSurface.getPOINT_B().x + pre_point.x, BASE_headSurface.getPOINT_B().y + pre_point.y));
        pre_headSurface_To_ChestL   = new Edge2D(new Vec2D(BASE_headSurface_To_ChestL.getPOINT_A().x + pre_point.x, BASE_headSurface_To_ChestL.getPOINT_A().y + pre_point.y), new Vec2D(BASE_headSurface_To_ChestL.getPOINT_B().x + pre_point.x, BASE_headSurface_To_ChestL.getPOINT_B().y + pre_point.y));
        pre_headSurface_To_ChestR   = new Edge2D(new Vec2D(BASE_headSurface_To_ChestR.getPOINT_A().x + pre_point.x, BASE_headSurface_To_ChestR.getPOINT_A().y + pre_point.y), new Vec2D(BASE_headSurface_To_ChestR.getPOINT_B().x + pre_point.x, BASE_headSurface_To_ChestR.getPOINT_B().y + pre_point.y));

        cur_feetsMP                 = new Vec3D (BASE_feetsMP.x  + cur_point.x, BASE_feetsMP.y  + cur_point.y, BASE_feetsMP.z  + cur_point.z);
        cur_chestLMP                = new Vec3D (BASE_chestLMP.x + cur_point.x, BASE_chestLMP.y + cur_point.y, BASE_chestLMP.z + cur_point.z);
        cur_chestRMP                = new Vec3D (BASE_chestRMP.x + cur_point.x, BASE_chestRMP.y + cur_point.y, BASE_chestRMP.z + cur_point.z);
        cur_headMP                  = new Vec3D (BASE_headMP.x   + cur_point.x, BASE_headMP.y   + cur_point.y, BASE_headMP.z   + cur_point.z);
        cur_feetsSurface            = new Edge2D(new Vec2D(BASE_feetsSurface.getPOINT_A().x + cur_point.x, BASE_feetsSurface.getPOINT_A().y + cur_point.y), new Vec2D(BASE_feetsSurface.getPOINT_B().x + cur_point.x, BASE_feetsSurface.getPOINT_B().y + cur_point.y));
        cur_feetsSurface_To_ChestL  = new Edge2D(new Vec2D(BASE_feetsSurface_To_ChestL.getPOINT_A().x + cur_point.x, BASE_feetsSurface_To_ChestL.getPOINT_A().y + cur_point.y), new Vec2D(BASE_feetsSurface_To_ChestL.getPOINT_B().x + cur_point.x, BASE_feetsSurface_To_ChestL.getPOINT_B().y + cur_point.y));
        cur_feetsSurface_To_ChestR  = new Edge2D(new Vec2D(BASE_feetsSurface_To_ChestR.getPOINT_A().x + cur_point.x, BASE_feetsSurface_To_ChestR.getPOINT_A().y + cur_point.y), new Vec2D(BASE_feetsSurface_To_ChestR.getPOINT_B().x + cur_point.x, BASE_feetsSurface_To_ChestR.getPOINT_B().y + cur_point.y));
        cur_headSurface             = new Edge2D(new Vec2D(BASE_headSurface.getPOINT_A().x + cur_point.x, BASE_headSurface.getPOINT_A().y + cur_point.y), new Vec2D(BASE_headSurface.getPOINT_B().x + cur_point.x, BASE_headSurface.getPOINT_B().y + cur_point.y));
        cur_headSurface_To_ChestL   = new Edge2D(new Vec2D(BASE_headSurface_To_ChestL.getPOINT_A().x + cur_point.x, BASE_headSurface_To_ChestL.getPOINT_A().y + cur_point.y), new Vec2D(BASE_headSurface_To_ChestL.getPOINT_B().x + cur_point.x, BASE_headSurface_To_ChestL.getPOINT_B().y + cur_point.y));
        cur_headSurface_To_ChestR   = new Edge2D(new Vec2D(BASE_headSurface_To_ChestR.getPOINT_A().x + cur_point.x, BASE_headSurface_To_ChestR.getPOINT_A().y + cur_point.y), new Vec2D(BASE_headSurface_To_ChestR.getPOINT_B().x + cur_point.x, BASE_headSurface_To_ChestR.getPOINT_B().y + cur_point.y));
    }
}
