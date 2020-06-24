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

package com.example.android.exercisemonitor;

import android.app.Activity;

import java.io.IOException;


public class ImageClassifierFloatBodypose extends ImageClassifier {


    private float[][][][] jointProbArray = null;


    ImageClassifierFloatBodypose(Activity activity) throws IOException {
        super(activity);
        jointProbArray = new float[1][getHeatmapWidth()][getHeatmapHeight()][getNumJoint()];
    }

    @Override
    protected String getModelPath() {

        return "model_hour.tflite";
    }



    @Override
    protected int getImageSizeX() {
        return 192;


    }


    @Override
    protected int getImageSizeY() {
        return 192;

    }

    @Override
    protected int getNumBytesPerChannel() {

        return 4;
    }

    @Override
    protected void addPixelValue(int pixelValue) {



        imgData.putFloat(Float.valueOf(pixelValue & 0xFF));
        imgData.putFloat(Float.valueOf(pixelValue >> 8 & 0xFF));
        imgData.putFloat(Float.valueOf(pixelValue >> 16 & 0xFF));

    }

    @Override
    protected float getProbability(int index, int width, int height, int joint) {
        return jointProbArray[index][width][height][joint];
    }





    @Override
    protected void runInference() {
        tflite.run(imgData, jointProbArray);


    }
}
