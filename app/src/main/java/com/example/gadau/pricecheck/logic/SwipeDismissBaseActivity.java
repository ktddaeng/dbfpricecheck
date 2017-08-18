package com.example.gadau.pricecheck.logic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.Contants;

public class SwipeDismissBaseActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_dismiss_base);
        gestureDetector = new GestureDetector(new SwipeDetector());
    }

    private class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > Contants.SWIPE_MAX_OFF_PATH)
                return false;

            if (e2.getX() - e1.getX() > Contants.SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > Contants.SWIPE_THRESHOLD_VELOCITY){
                finish();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
