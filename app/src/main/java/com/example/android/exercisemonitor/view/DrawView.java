package com.example.android.exercisemonitor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DrawView extends View {
    private static final int HEATMAPWIDTH = 48;
    private static final int HEATMAPHEIGHT = 48;
    private static final int NUMJOINT = 14;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    public static int MAX_PREVIEW_HEIGHT;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    public static int MAX_PREVIEW_WIDTH;
    private static int count = 0;


    static View myView;
    private Paint paint[] = new Paint[14];
    private Paint ppaint;
    private static float[][] arr = new float[14][2];

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        myView = DrawView.this;
        ppaint = new Paint();
        ppaint.setStrokeWidth(7);
        ppaint.setColor(Color.RED);

        for (int i = 0; i < NUMJOINT; i++) {
            paint[i] = new Paint();
            paint[i].setStyle(Paint.Style.STROKE);
            paint[i].setStrokeWidth(15);
        }
        paint[0].setColor(Color.BLACK);
        paint[1].setColor(Color.RED);
        paint[2].setColor(Color.GREEN);
        paint[3].setColor(Color.YELLOW);
        paint[4].setColor(Color.BLUE);
        paint[5].setColor(Color.DKGRAY);
        paint[6].setColor(Color.LTGRAY);
        paint[7].setColor(Color.MAGENTA);
        paint[8].setColor(Color.WHITE);
        paint[9].setColor(Color.BLUE);
        paint[10].setColor(Color.RED);
        paint[11].setColor(Color.GREEN);
        paint[12].setColor(Color.YELLOW);
        paint[13].setColor(Color.BLUE);

        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        count++;
if(checkpoints(0,1)){
    canvas.drawLine(arr[0][1], arr[0][0], arr[1][1], arr[1][0], ppaint);}
if(checkpoints(1,2)) {
    canvas.drawLine(arr[1][1], arr[1][0], arr[2][1], arr[2][0], ppaint);
}
if(checkpoints(1,5)) {
    canvas.drawLine(arr[1][1], arr[1][0], arr[5][1], arr[5][0], ppaint);
}
if(checkpoints(2,3)) {
    canvas.drawLine(arr[2][1], arr[2][0], arr[3][1], arr[3][0], ppaint);
}
if(checkpoints(3,4)) {
    canvas.drawLine(arr[3][1], arr[3][0], arr[4][1], arr[4][0], ppaint);
}
if(checkpoints(5,6)) {
    canvas.drawLine(arr[5][1], arr[5][0], arr[6][1], arr[6][0], ppaint);
}
if(checkpoints(6,7)) {
    canvas.drawLine(arr[6][1], arr[6][0], arr[7][1], arr[7][0], ppaint);
}
if(checkpoints(1,8)) {

    canvas.drawLine(arr[1][1], arr[1][0], arr[8][1], arr[8][0], ppaint);
}
if(checkpoints(1,11)) {
    canvas.drawLine(arr[1][1], arr[1][0], arr[11][1], arr[11][0], ppaint);
}
if(checkpoints(8,9)) {

    canvas.drawLine(arr[8][1], arr[8][0], arr[9][1], arr[9][0], ppaint);
}
if(checkpoints(9,10)) {
    canvas.drawLine(arr[9][1], arr[9][0], arr[10][1], arr[10][0], ppaint);
}
if(checkpoints(11,12)) {
    canvas.drawLine(arr[11][1], arr[11][0], arr[12][1], arr[12][0], ppaint);
}
if(checkpoints(12,13)) {
    canvas.drawLine(arr[12][1], arr[12][0], arr[13][1], arr[13][0], ppaint);
}

        for (int i = 0; i < 14; i++) {
            if(arr[i][1]!=0&&arr[i][0]!=0) {

                canvas.drawPoint(arr[i][1], arr[i][0], paint[i]);
            }
        }
    }

    public static void setArr(float[][] inputArr) {
        for (int index = 0; index < NUMJOINT; index++) {
            arr[index][0] = inputArr[index][0] / HEATMAPHEIGHT * MAX_PREVIEW_HEIGHT;

            arr[index][1] = inputArr[index][1] / HEATMAPWIDTH * MAX_PREVIEW_WIDTH;

        }


        myView.invalidate();

    }
    public boolean checkpoints(int b1, int  b2)
    {
        if(arr[b1][0]!=0.0 && arr[b1][1]!=0.0&& arr[b2][0]!=0.0 && arr[b2][1]!=0.0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
