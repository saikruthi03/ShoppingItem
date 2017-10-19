package com.example.anush.shoppingitem;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anush on 10/18/2017.
 */

public class FindRect {


    public static Bitmap findRectangle(Bitmap image) throws Exception {
        Mat tempor = new Mat();
        Mat src = new Mat();
        Utils.bitmapToMat(image, tempor);

        Imgproc.cvtColor(tempor, src, Imgproc.COLOR_BGR2RGB);

        Mat blurred = src.clone();
        Imgproc.medianBlur(src, blurred, 9);

        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U), gray = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        List<Mat> blurredChannel = new ArrayList<Mat>();
        blurredChannel.add(blurred);
        List<Mat> gray0Channel = new ArrayList<Mat>();
        gray0Channel.add(gray0);

        MatOfPoint2f approxCurve;

        double maxArea = 0;
        int maxId = -1;

        for (int c = 0; c < 3; c++) {
            int ch[] = { c, 0 };
            Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));

            int thresholdLevel = 1;
            for (int t = 0; t < thresholdLevel; t++) {
                if (t == 0) {
                    Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1), 1); // 1
                    // ?
                } else {
                    Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel,
                            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                            Imgproc.THRESH_BINARY,
                            (src.width() + src.height()) / 200, t);
                }

                Imgproc.findContours(gray, contours, new Mat(),
                        Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                for (MatOfPoint contour : contours) {
                    MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

                    double area = Imgproc.contourArea(contour);
                    approxCurve = new MatOfPoint2f();
                    Imgproc.approxPolyDP(temp, approxCurve,
                            Imgproc.arcLength(temp, true) * 0.02, true);

                    if (approxCurve.total() == 4 && area >= maxArea) {
                        double maxCosine = 0;

                        List<Point> curves = approxCurve.toList();
                        for (int j = 2; j < 5; j++) {

                            //double cosine = Math.abs(angle(curves.get(j % 4),
                            //        curves.get(j - 2), curves.get(j - 1)));
                          //  maxCosine = Math.max(maxCosine, cosine);
                        }

                        //if (maxCosine < 0.3) {
                            maxArea = area;
                            maxId = contours.indexOf(contour);
                       // }
                    }
                }
            }
        }

        if (maxId >= 0) {
            Rect rect = Imgproc.boundingRect(contours.get(maxId));

           // Imgproc.rectangle(src, rect.tl(), rect.br(), new Scalar(255, 0, 0,
            //        .8), 4);


            int mDetectedWidth = rect.width;
            int mDetectedHeight = rect.height;
            int x = rect.x;
            int y = rect.y;
            Log.i("X",x+"");
            Log.i("Y",y+"");

            //Log.d(TAG, "Rectangle width :"+mDetectedWidth+ " Rectangle height :"+mDetectedHeight);

        }

        Bitmap bmp;
        bmp = Bitmap.createBitmap(src.cols(), src.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, bmp);


        return bmp;

    }
}
