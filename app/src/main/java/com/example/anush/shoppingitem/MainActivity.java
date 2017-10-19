package com.example.anush.shoppingitem;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {
    static Context ctx ;
    protected static final int RESULT_SPEECH = 1;
    protected static final int CAMERA_PIC_REQUEST = 2;
    private ImageButton btnSpeak;
    private TextView txtText;
    private ImageView imageView;
    public boolean flag = true;
    public static Bitmap photo;
    String ba1;
    String URL ="http://10.0.2.2:8009/uploadImage";
    protected static final String YOUR_SONG_URI = "C:/Users/SCS_USER/Desktop/enter.aac";

    /*static {
        if(!OpenCVLoader.initDebug()){

        }else{
            System.loadLibrary("my_jni_lib1");
            System.loadLibrary("my_jni_lib2");
        }

    }*/

    private BaseLoaderCallback mLoaderCallbacks;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ctx=
        setContentView(R.layout.activity_main);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        //if(flag){
            MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.enter);
            ring.start();
        //}

       // txtText = (TextView) findViewById(R.id.txtText);
        imageView = (ImageView)this.findViewById(R.id.imageView1);
       // btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        photo = BitmapFactory.decodeResource(getResources(),
                R.drawable.image6);
        mLoaderCallbacks = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                      /*  MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "OpenCV library is loaded",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });*/
                      Log.i("OpenCv","Loaded");
                        break;
                    }
                    // Open CV failed to load
                    case LoaderCallbackInterface.INIT_FAILED: {
                      /*  MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Could not load OpenCV", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });*/
                        Log.i("OpenCv","Not Loaded");
                        break;
                    }

                    default: {
                        super.onManagerConnected(status);
                        break;
                    }
                }
            }
        };
        // Initialize OpenCv library
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                mLoaderCallbacks);
        try {
            findRectangle(photo);
        }catch (Exception ex){

        }
        startVoiceInput();
    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {

        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    //ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtText.setText(text.get(0));
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.get(0).contains("capture image") || result.get(0).contains("open camera") ){
                        super.onResume();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                    }else if(result.get(0).contains("open wish list")){

                    }
                }
                break;
            }
            case CAMERA_PIC_REQUEST: {
                if (resultCode == RESULT_OK && null != data) {
                    Log.i("h","opening camera");
                     photo = BitmapFactory.decodeResource(getResources(),
                            R.drawable.image6);
                    try {
                        FindRect.findRectangle(photo);
                    }catch (Exception ex){

                    }
                   // upload(photo);
                    imageView.setImageBitmap(photo);
                }
                break;
            }
        }
    }


    private void upload(Bitmap bm) {
        //Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
        Log.e("base64", "-----" + ba1);
        new uploadToServer().execute();

    }

    public class uploadToServer extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Wait image uploading!");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image", "hii"));
            //nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            //ImageData img = new ImageData();
            //img.setImageData(ba1);
            //img.setImagePath(System.currentTimeMillis() + ".jpg");
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    public Bitmap findRectangle(Bitmap image) throws Exception {
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



