package com.spx.exoplayertest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by shaopengxiang on 2017/12/22.
 */

public class DraggableLayout extends RelativeLayout {

    private static final String TAG = "DraggableLayout";
    private View mDragView;

    public DraggableLayout(Context context) {
        super(context);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = findViewById(R.id.player_area);
    }

    private float rawX, rawY;
    private float last_x, last_y;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent: ..."+ev.toString());
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent: ...");
////        return super.onInterceptTouchEvent(ev);
//        return true;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: ..."+event.toString());
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                rawX = event.getX();
//                rawY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float touch_x = event.getX();
//                float touch_y = event.getY();
//                mDragView.setTranslationY(touch_y - rawY);
//                mDragView.setTranslationX(touch_x - rawX);
//                last_y = touch_y;
//                last_x = touch_x;
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return true;
//    }
}
