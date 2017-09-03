package de.ndnentertainment.ndngameengine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private GameRenderer gameRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);


        final boolean supportsEs2 = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion >= 0x2;

        if(supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);

            gameRenderer = new GameRenderer(this);
            glSurfaceView.setRenderer(gameRenderer);
        } else {
            return;
        }

        setContentView(glSurfaceView);

        initBtns();
        /////////

    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        glSurfaceView.onPause();
    }


    /** Verhindert, dass die Softkeys beim berühren des Bildschirms erscheinen. Sie erscheinen erst beim hineinswipen. **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    private void initBtns() {
        LinearLayout linLay = new LinearLayout(this);
        Button btnWalkLeft = new Button(this);
        Button btnWalkRight = new Button(this);
        Button btnJump = new Button(this);
        Button btnDuck = new Button(this);
        btnWalkLeft.setText("Left");
        btnWalkRight.setText("Right");
        btnJump.setText("Jump");
        btnDuck.setText("Stoop");
        linLay.addView(btnWalkLeft);
        linLay.addView(btnWalkRight);
        linLay.addView(btnJump);
        linLay.addView(btnDuck);
        linLay.setGravity(Gravity.BOTTOM);
        addContentView(linLay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        btnWalkLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !gameRenderer.getMeeple().isWalkRight()) {
                    gameRenderer.getMeeple().setWalkLeft(true);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    gameRenderer.getMeeple().setWalkLeft(false);
                }
                return false;
            }
        });
        btnWalkRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !gameRenderer.getMeeple().isWalkLeft()) {
                    gameRenderer.getMeeple().setWalkRight(true);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    gameRenderer.getMeeple().setWalkRight(false);
                }
                return false;
            }
        });
        btnJump.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !gameRenderer.getMeeple().isDuck()) {
                    gameRenderer.getMeeple().setJump(true);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    gameRenderer.getMeeple().setJump(false);
                }
                return false;
            }
        });
        btnDuck.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !gameRenderer.getMeeple().isJump()) {
                    gameRenderer.getMeeple().setDuck(true);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    gameRenderer.getMeeple().setDuck(false);
                }
                return false;
            }
        });
    }

    private void initTestBtns() {
        LinearLayout linLay = new LinearLayout(this);
        Button btnZoomIn = new Button(this);
        Button btnZoomOut = new Button(this);
        Button btnMoveUp = new Button(this);
        Button btnMoveDown = new Button(this);
        Button btnMoveLeft = new Button(this);
        Button btnMoveRight = new Button(this);
        btnZoomIn.setText("In");
        btnZoomOut.setText("Out");
        btnMoveUp.setText("Up");
        btnMoveDown.setText("Down");
        btnMoveLeft.setText("Left");
        btnMoveRight.setText("Right");
        linLay.addView(btnZoomIn);
        linLay.addView(btnZoomOut);
        linLay.addView(btnMoveUp);
        linLay.addView(btnMoveDown);
        linLay.addView(btnMoveLeft);
        linLay.addView(btnMoveRight);
        linLay.setGravity(Gravity.BOTTOM);
        addContentView(linLay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().zoomIn();
            }
        });
        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().zoomOut();
            }
        });
        btnMoveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().moveUp();
            }
        });
        btnMoveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().moveDown();
            }
        });
        btnMoveLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().moveLeft();
            }
        });
        btnMoveRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameRenderer.getwCamera().moveRight();
            }
        });
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }
}
