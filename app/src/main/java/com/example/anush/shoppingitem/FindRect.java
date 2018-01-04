package com.example.anush.shoppingitem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anush on 11/2/2017.
 */

public class FindRect {

    public static List<Point> findRect(Bitmap photo){
        Log.e("Erooo","in find rect");
        List<Point> pointsList = new ArrayList<Point>();
       /* final int lnth=photo.getByteCount();
        ByteBuffer dst= ByteBuffer.allocate(lnth);
        photo.copyPixelsToBuffer( dst);
        byte[] bytes=dst.array();
        originalPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);*/

        Mat imgMat=new Mat();

        Utils.bitmapToMat(photo,imgMat);

        Mat imgSource=imgMat.clone();

        Imgproc.cvtColor( imgMat, imgMat, Imgproc.COLOR_BGR2GRAY);
        Bitmap grayscale=Bitmap.createBitmap(imgMat.cols(),imgMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat,grayscale);
        Log.e("Erooo","after mat to bit");
       /* String root = Environment.getExternalStorageDirectory().toString();
        Log.e("Erooo",root);
        File myDir = new File(root + "/saved_images");*/

        Imgproc.Canny(imgMat,imgMat,0,255);
        Bitmap canny=Bitmap.createBitmap(imgMat.cols(),imgMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat,canny);

        Imgproc.GaussianBlur(imgMat, imgMat, new  org.opencv.core.Size(1, 1), 2, 2);
        Bitmap blur=Bitmap.createBitmap(imgMat.cols(),imgMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat,blur);

        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imgMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point

        for (int idx = 0; idx < contours.size(); idx++) {
            Log.i("Points","In for loop");
            temp_contour = contours.get(idx);
            MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
            int contourSize = (int)temp_contour.total();
            MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
            Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.05, true);
            if (approxCurve_temp.total() == 4) {
                MatOfPoint points = new MatOfPoint( approxCurve_temp.toArray() );
                Rect rect = Imgproc.boundingRect(points);
                Log.i("Points",rect.x+"");
                Log.i("Points",rect.y+"");
                pointsList.add(new Point(rect.x,rect.y));
                pointsList.add(new Point(rect.x+rect.width,rect.y+rect.height));
                // Core.rectangle(imgSource, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0, 0, 255), 3);

            }

        }
        Bitmap analyzed=Bitmap.createBitmap(imgSource.cols(),imgSource.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgSource,analyzed);


       /* if(!myDir.exists()) myDir.mkdirs();

       String fname = "ImageAnalyzed.png";
       File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            analyzed.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            Log.e("Erooo","in catch");
            e.printStackTrace();
        }*/

        //OutputStream output;
       // File filepath = Environment.getExternalStorageDirectory();
       // File dir = new File(filepath.getAbsolutePath()
        //        + "/WhatSappIMG/");
       // dir.mkdirs();
       // File file = new File(dir, "Wallpaper.jpg" );
      /*  try {
            output = new FileOutputStream(file);
            analyzed.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), analyzed,
                    "Wallpaper.jpg", null);
        }

        catch (Exception e) {
            Log.e("erorr","in catch");
            e.printStackTrace();
        }*/
      return pointsList;
    }
}


