/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
//https://github.com/Rene4100/DTWJava/tree/master/src/dk/rene4100
package com.example.android.exercisemonitor;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Toast;


import com.example.android.exercisemonitor.view.DTW;
import com.example.android.exercisemonitor.view.DrawView;
import com.example.android.exercisemonitor.view.SimpleMovingAverage;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.Interpreter;


public abstract class ImageClassifier {



    private static final float ALPHA = 0.5f;

    private static final int HEATMAPWIGHT = 48;
    private static final int HEATMAPHEIGHT = 48;
    private static final int NUMJOINT = 14;
    public static long start_time=0;
    public static double sum_angles;



    private static final String TAG = "TfLiteCameraDemo";


    private static final int DIM_BATCH_SIZE = 1;


    private static final int DIM_PIXEL_SIZE = 3;

public static boolean firsthalf;
public static boolean onbuttonclick;
public List<Double> angle_list;
    public List<Double> new_angle_list;







    private int[] intValues = new int[getImageSizeX() * getImageSizeY()];


    private final Interpreter.Options tfliteOptions = new Interpreter.Options();


    private MappedByteBuffer tfliteModel;


    protected Interpreter tflite;


    protected ByteBuffer imgData = null;

    Delegate gpuDelegate = null;


    ImageClassifier(Activity activity) throws IOException {

        tfliteModel = loadModelFile(activity);

        tflite = new Interpreter(tfliteModel, tfliteOptions);
onbuttonclick=false;
angle_list=new ArrayList<>();
new_angle_list=new ArrayList<>();
sum_angles=0;
        imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE
                                * getImageSizeX()
                                * getImageSizeY()
                                * DIM_PIXEL_SIZE
                                * getNumBytesPerChannel());


        imgData.order(ByteOrder.nativeOrder());

        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");
    }


    //TODO: 어디서 활용되는지 확인 필요
    @RequiresApi(api = Build.VERSION_CODES.N)
    void classifyFrame(Bitmap bitmap, SpannableStringBuilder builder) {
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            builder.append(new SpannableString("Uninitialized Classifier."));
        }

        convertBitmapToByteBuffer(bitmap);


        long startTime = SystemClock.uptimeMillis();
        runInference();
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));

        drawBodyPoint();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void drawBodyPoint() {
        int index = 0;
        float sum=0;
        float[][] arr = new float[14][2];
        for (int k = 0; k < getNumJoint(); k++) {
            float[][] heatmap = new float[getHeatmapWidth()][getHeatmapHeight()];
            for (int i = 0; i < getHeatmapWidth(); i++) {
                for (int j = 0; j < getHeatmapHeight(); j++) {
                    heatmap[i][j] = getProbability(index, i, j, k);
                }
            }
            float[] result;
            result = findMaximumIndex(heatmap);
            Log.w("sam",result[0]+","+result[1]);



                arr[k] = result;


        }
        Log.w("results", String.valueOf(sum/getNumJoint()));
        float hip[] =new float[2];
      hip[0]=(arr[11][0]+arr[8][0])/2;
      hip[1]=(arr[11][1]+arr[8][1])/2;
        float leg[] =new float[2];
        leg[0]=(arr[12][0]+arr[9][0])/2;
        leg[1]=(arr[12][1]+arr[9][1])/2;
double angle=findAngle(arr[1],hip,leg);
        DecimalFormat dec = new DecimalFormat("#0.00");



Camera2BasicFragment.update_angle(dec.format(angle));
if(onbuttonclick)
{
    if(!Double.isNaN(angle)) {


        angle_list.add(angle);
        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(1);
        new_angle_list = simpleMovingAverage.getMA(angle_list);
        angle_list.clear();
        ;
        angle_list.addAll(new_angle_list);
        new_angle_list.clear();
        double[] arrray = new double[angle_list.size()];
        double[] conv = convertAsPrimitiveArray(angle_list);
        arrray = applyLowPassFilter(conv, arrray);
        if (Math.abs(arrray[0] - arrray[angle_list.size() - 1]) < 4 && getMax(arrray) - arrray[angle_list.size() - 1] > 9 && (System.currentTimeMillis()-start_time>4000)) {

            double [] reference={71.06250817445091, 71.06250817445091, 71.19520460464385, 74.71686505522273, 74.71686505522275, 74.71686505522273, 82.67432071408892, 82.67432071408892, 84.69017236192242, 84.69017236192245, 88.91946025588676, 88.91946025588676, 84.69017236192245, 82.67432071408892, 74.71686505522275};
          double dist= DTW.DTWDistance(arrray,reference);

          Camera2BasicFragment.update_dtw("  "+String.valueOf(10000/dist));

            start_time = System.currentTimeMillis();
            Camera2BasicFragment.update_reps();
            angle_list.clear();
            ;
            new_angle_list.clear();
            ;


        }





}

//    SimpleMovingAverage simpleMovingAverage=new SimpleMovingAverage(5);
//
//    new_angle_list=simpleMovingAverage.getMA(angle_list);
//    angle_list.clear();
//    angle_list.addAll(new_angle_list);
//    new_angle_list.clear();
//    if(angle_list.size()>4)
//    {
//        Log.w("arra", String.valueOf(angle_list));
//        int n=angle_list.size();
//        if(angle_list.get(1)>angle_list.get(0)&&angle_list.get(n-2)>angle_list.get(n-1))
//        {
//            firsthalf=true;
//            angle_list.clear();;
//        }
//        if(firsthalf&& angle_list.get(1)<angle_list.get(0)&&angle_list.get(n-2)<angle_list.get(n-1))
//        {
//            Camera2BasicFragment.update_reps();
//
//        }
//    }
}
            DrawView.setArr(arr);


    }
    public double getMax(double[] values){
        double ret = values[0];
        for(int i = 1; i < values.length; i++)
            ret = Math.max(ret,values[i]);
        return ret;
    }
    public double  findAngle(float p0[],float p1[],float p2[]) {
       double  a = Math.pow(p1[0]-p0[0],2) + Math.pow(p1[1]-p0[1],2);
          double  b = Math.pow(p1[0]-p2[0],2) + Math.pow(p1[1]-p2[1],2);
              double   c = Math.pow(p2[0]-p0[0],2) + Math.pow(p2[1]-p0[1],2);
        return Math.acos( (a+b-c) / Math.sqrt(4*a*b) )* 180/Math.PI;
    }

    private static float[] findMaximumIndex(float[][] a) {
        float maxVal = -99999;
        float[] answerArray = new float[2];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[row].length; col++) {
                Log.w("prob", String.valueOf(a[row][col]));
                if (a[row][col] > maxVal&&a[row][col]>0.08) {
                    maxVal = a[row][col];
                    answerArray[0] = row;
                    answerArray[1] = col;

                }
            }
        }
        return answerArray;
    }



    private double[] applyLowPassFilter(double[] input, double[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }






    private void recreateInterpreter() {
        if (tflite != null) {
            tflite.close();

            tflite = new Interpreter(tfliteModel, tfliteOptions);
        }
    }

    public void useGpu() {
        if (gpuDelegate == null && GpuDelegateHelper.isGpuDelegateAvailable()) {
            gpuDelegate = GpuDelegateHelper.createGpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
            recreateInterpreter();
        }
    }

    public void useCPU() {
        tfliteOptions.setUseNNAPI(false);
        recreateInterpreter();
    }

    public void useNNAPI() {
        tfliteOptions.setUseNNAPI(true);
        recreateInterpreter();
    }

    public void setNumThreads(int numThreads) {
        tfliteOptions.setNumThreads(numThreads);
        recreateInterpreter();
    }



    public double[] convertAsPrimitiveArray(List<Double> list){
        double[] intArray = new double[list.size()];
        for(int i = 0; i < list.size(); i++) intArray[i] = list.get(i);
        return intArray;
    }
    public void close() {
        tflite.close();
        tflite = null;
        tfliteModel = null;
    }


    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < getImageSizeX(); ++i) {
            for (int j = 0; j < getImageSizeY(); ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
        long endTime = SystemClock.uptimeMillis();

    }

public static void onclick()
{

    sum_angles=0;
    start_time=System.currentTimeMillis();
    onbuttonclick=true;
}

    protected abstract String getModelPath();


    protected abstract int getImageSizeX();

    /**
     * Get the image size along the y axis.
     *
     * @return
     */
    protected abstract int getImageSizeY();

    /**
     * Get the number of bytes that is used to store a single color channel value.
     *
     * @return
     */
    protected abstract int getNumBytesPerChannel();

    /**
     * Add pixelValue to byteBuffer.
     *
     * @param pixelValue
     */
    protected abstract void addPixelValue(int pixelValue);

    /**
     * Read the probability value for the specified label This is either the original value as it was
     * read from the net's output or the updated value after the filter was applied.
     *
     * @param index
     * @param width
     * @param height
     * @param joint
     * @return
     */
    protected abstract float getProbability(int index, int width, int height, int joint);

    /**
     * Set the probability value for the specified label.
     *
     * @param labelIndex
     * @param labelIndex
     * @param value
     */
    //protected abstract void setProbability(int labelIndex, Number value);

    /**
     * Get the normalized probability value for the specified label. This is the final value as it
     * will be shown to the user.
     *
     * @return
     */
    //protected abstract float getNormalizedProbability(int labelIndex);

    /**
     * Run inference using the prepared input in {@link #imgData}. Afterwards, the result will be
     * provided by getProbability().
     *
     * <p>This additional method is necessary, because we don't have a common base for different
     * primitive data types.
     */
    protected abstract void runInference();

    /**
     * Get the total number of labels.
     *
     * @return
     */
  /*
  protected int getNumLabels() {
    return labelList.size();
  }
  */

    /**
     * Get the shape of output(heatmap) .
     *
     * @return
     */
    protected int getHeatmapWidth() {
        return HEATMAPWIGHT;
    }

    protected int getHeatmapHeight() {
        return HEATMAPHEIGHT;
    }

    protected int getNumJoint() {
        return NUMJOINT;
    }
}
