package com.example.anush.shoppingitem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements RecognitionListener {
    static Context ctx ;
    protected static final int CAMERA_PIC_REQUEST = 2;
    protected static final int MY_DATA_CHECK_CODE = 10;
    private SpeechRecognizer speech;
    private ImageView imageView;
    public static Bitmap photo;

    String URL ="http://ec2-34-213-137-131.us-west-2.compute.amazonaws.com:8080/ShoppingItem-1.0/wishlist?userId=";
    String URLP ="http://ec2-34-213-137-131.us-west-2.compute.amazonaws.com:8080/ShoppingItem-1.0/uploadFile";
    String URLA ="http://ec2-34-213-137-131.us-west-2.compute.amazonaws.com:8080/ShoppingItem-1.0/addUser?deviceId=";
    String URLW ="http://ec2-34-213-137-131.us-west-2.compute.amazonaws.com:8080/ShoppingItem-1.0/addItems?userId=";
    Set<String> wishList = new HashSet<String>();
    protected static final String YOUR_SONG_URI = "C:/Users/SCS_USER/Desktop/enter.aac";
    public View view;
    private int userId;
    protected static MediaPlayer beep;
    protected static MediaPlayer processingMessage;
    TextToSpeech textToSpeech;
    TextToSpeech textToSpeechNoResult;
    TextToSpeech textToSpeechImageCapture;
    TextToSpeech textToSpeechProcessingImage;
    TextToSpeech textToSpeechAdd;
    TextToSpeech textToSpeechAddedItemsToWishlist;
    TextToSpeech textToSpeechAddingItemsToWishlist;
    TextToSpeech textToSpeechWishList;
    TextToSpeech textToSpeechbackground;
    TextToSpeech textToSpeecherror;
    TextToSpeech textToSpeechDint;
    TextToSpeech textToSpeechResult;
    TextToSpeech textError;
    public String readWishList = "";
    Context context = null;
    Intent recognizerIntent;
    String message= "";
    boolean playMessage  = false;
    boolean playMessageCamera  = false;
    String images = "";
    byte[] capturedImage;
    SharedPreferences prefs = null;
    String deviceId = null;
    String presentMessage = null;
    Bitmap capPhoto = null;

    HashMap<String, String> labelsMap = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(android.R.id.content);
        beep = MediaPlayer.create(MainActivity.this,R.raw.beep);
        processingMessage=MediaPlayer.create(MainActivity.this,R.raw.process);
        context = MainActivity.ctx;
        playIntroMessage();
        labelsMap.put("cokecan","coke can");
        labelsMap.put("tidepods","tide pods");
        labelsMap.put("tide-liquid","tide liquid");
        labelsMap.put("pepsi","pepsi can");
        photo = BitmapFactory.decodeResource(getResources(),
                R.drawable.shopicon);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        // imageView.setImageBitmap(photo);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        //capturedImage = bos.toByteArray();
        prefs = getSharedPreferences("com.example.anush.shoppingitem", MODE_PRIVATE);
        prefs.getInt("userId",0);
        prefs.getBoolean("camera",false);
        if (prefs.getBoolean("firstrun", true)) {
            Date currentDate = new Date(System.currentTimeMillis());
            deviceId = currentDate+""+Math.random()+"";
            deviceId=deviceId.replaceAll("\\s","");
            new AddUser().execute();
        }
        wishList = new HashSet<String>();
        if(!prefs.getBoolean("camera", false)){
            new GetFromServer().execute();
        }

        //new uploadToServer().execute();
        // new addWishListToServer().execute();
    }



    @Override
    public void onResume(){
        super.onResume();
        if(playMessage && !cameraPause){
            Log.i("on resumeee", "if");
            playMessage = false;
            cameraPause = false;
            playIntroMessage();
            wishList = new HashSet<>();
            new GetFromServer().execute();
            try {
                Thread.sleep(2000);
            }catch (Exception e){

            }
        }else {
            cameraPause= false;
            Log.i("on resumeee", "else");

        }

    }
    public void detect(double height, double width, double xmin, double ymin, double xmax, double ymax, String object){

        double midWidth = width/2;
        double midHeight = height/2;
        double ybounmid = (ymax+ymin)/2;
        double xbound = (xmax+xmin)/2;
        object = object.replaceAll("[{}\\[\\]]", " ");
        object = object.replaceAll("\\s","");
        object= object.substring(1, object.length()-1);
        String arr[] = object.split(":");
        if(arr.length > 0){
            object = arr[0];
        }
        Log.i("Math.abs(midHeig",Math.abs(midHeight-ybounmid)+"");
        Log.i("Math.abs(midHeig",xmax+"");
        Log.i("Math.abs(midHeig",ymax+"");
        Log.i("Math.abs(midHeig",xmin+"");
        Log.i("Math.abs(midHeig",ymin+"");
        String val = labelsMap.get(object);
        boolean flag = false;
        if(wishList.size() > 0 && wishList.contains(val)){
            flag = true;
        }
        if(flag) {
            if (Math.abs(midHeight - ybounmid) < 50.0) {
                if (xmin > midWidth || (Math.abs(midWidth - xmin) < 100.0 && xmax > midWidth)) {

                    presentMessage = "The" + object + " is on your right";
                } else {
                    Log.i("Math.abs(midHeig", "in left");
                    presentMessage = "The" + object + " is on your left";
                }
            } else {
                if (xmin > midWidth || (Math.abs(midWidth - xmin) < 100.0 && xmax > midWidth)) {
                    if (ymin > midHeight || (Math.abs(midHeight - ymin) < 100.0 && ymax > midHeight)) {
                        presentMessage = "The" + object + " is on the top right corner";
                    } else {
                        presentMessage = "The" + object + "is on the bottom right corner.";
                    }
                } else {
                    if (ymin > midHeight || (Math.abs(midHeight - ymin) < 100 && ymax > midHeight)) {
                        presentMessage = "The" + object + " is on the top left corner";
                    } else {
                        presentMessage = "The" + object + "is on the bottom left corner.";
                    }
                }
            }
        }else{
            presentMessage = "The" + object + "is not present in your wish list;";
        }

        playPresentResult();

    }

    @Override
    public void onPause(){
        super.onPause();
        if(speech != null)
            speech.cancel();
        if(null != textToSpeech && textToSpeech.isSpeaking()){
            textToSpeech.stop();
        }
        if(null != textToSpeechResult && textToSpeechResult.isSpeaking()){
            textToSpeechResult.stop();
        }
        if( null != textToSpeechDint && textToSpeechDint.isSpeaking()){
            textToSpeechDint.stop();
        }
        if(null != textToSpeechAdd && textToSpeechAdd.isSpeaking()){
            textToSpeechAdd.stop();
        }
        if(null != textToSpeechProcessingImage && textToSpeechProcessingImage.isSpeaking())
            textToSpeechProcessingImage.stop();



        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        if(!playMessageCamera){
            playMessage = true;
            playMessageCamera = false;
            textToSpeechbackground = new TextToSpeech(getApplicationContext() , new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeechbackground.setLanguage(Locale.US);
                        textToSpeechbackground.speak("The app is going into background", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }else{
            playMessageCamera = false;
        }

    }
    public void playIntroMessage(){
        textToSpeech = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.speak("Hi welcome to the app. " +
                            "Please wait while we are fetching your wishlist.", TextToSpeech.QUEUE_FLUSH, null,null);
                }

            }
        });
    }
    public void playError(){
        textToSpeecherror = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeecherror.speak("There was some error. Please try again.", TextToSpeech.QUEUE_FLUSH, null,null);
                }

            }
        });
    }

    public void playPresentResult(){

        textToSpeechResult = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechResult.setLanguage(Locale.US);
                    textToSpeechResult.speak(presentMessage, TextToSpeech.QUEUE_FLUSH, null,null);
                }

            }
        });
        prefs.edit().putBoolean("camera", false).commit();

    }

    public void playNotPresentResult(){

        textToSpeechNoResult = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechNoResult.setLanguage(Locale.US);
                    textToSpeechNoResult.speak("The items in your wishlist are not present in the image.", TextToSpeech.QUEUE_FLUSH, null,null);
                }

            }
        });
        prefs.edit().putBoolean("camera", false).commit();

    }

    public void startSpeechRecognition(View view){
        if (speech == null) {
            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
        }
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                MainActivity.this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }

    public void openCameraIntent(){
        textToSpeechImageCapture = new TextToSpeech(getApplicationContext() , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechImageCapture.setLanguage(Locale.US);
                    textToSpeechImageCapture.speak("Camera is opened. Tap on the screen to capture image", TextToSpeech.QUEUE_FLUSH, null,null);

                }

            }
        });
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        playMessageCamera= true;
        prefs.edit().putBoolean("camera", true).commit();
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }
    public void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null)
            textToSpeech.shutdown();
        if(speech != null)
            speech.destroy();

    }

    public void readText(String wishList){
        wishList = wishList.replaceAll("\\[", "").replaceAll("\\]","");
        String[] wishListArr = wishList.split("\\,");
        StringBuilder sb = new StringBuilder();
        this.wishList = new HashSet<>();
        if(wishListArr.length > 0 && !(wishListArr[0].equals(" ")) && !(wishListArr[0].equals(""))) {
            sb.append("Your wish list has,");
            for (int j = 0; j < wishListArr.length; j++) {
                sb.append(" ");
                sb.append(wishListArr[j]);
                this.wishList.add(wishListArr[j].substring(1,wishListArr[j].length()-1));
            }
        }else{
            sb.append("Your wish list is empty");
        }
        sb.append(".Do you want to continue shopping or add items to wishlist. Tap on the screen and give your command.");
        readWishList = sb.toString();
        textToSpeechWishList = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechWishList.setLanguage(Locale.US);
                    textToSpeechWishList.speak(readWishList, TextToSpeech.QUEUE_FLUSH, null,null);

                }

            }
        });

    }
    public void addedToWishlist(){
        textToSpeechAddedItemsToWishlist = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechAddedItemsToWishlist.setLanguage(Locale.US);
                    textToSpeechAddedItemsToWishlist.speak("Added Items to wishlist", TextToSpeech.QUEUE_FLUSH, null,null);

                }
            }
        });

    }
    public void addingToWishlist(){
        textToSpeechAddingItemsToWishlist = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechAddingItemsToWishlist.setLanguage(Locale.US);
                    textToSpeechAddingItemsToWishlist.speak("Adding Items to wishlist", TextToSpeech.QUEUE_FLUSH, null,null);

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_camera, menu);
        return true;
    }
    private boolean cameraPause= false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_PIC_REQUEST: {
                cameraPause = true;
                if (resultCode == RESULT_OK && null != data) {
                    Bitmap capPhoto = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(capPhoto);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    capPhoto.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    capturedImage = bos.toByteArray();
                  /*  textToSpeechProcessingImage = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener() {

                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                textToSpeechProcessingImage.setLanguage(Locale.US);
                                textToSpeechProcessingImage.speak("Please wait while we are processing the image and matching with your wishlist. This might take a while.", TextToSpeech.QUEUE_FLUSH, null,null);

                            }

                        }
                    });*/
                    processingMessage.start();
                    new uploadToServer().execute();
                    //finish();
                }
                break;
            }
            case MY_DATA_CHECK_CODE:{
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // success, create the TTS instance

                } else {
                    // missing data, install it
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
            }
        }
    }


    public class uploadToServer extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String ba = Base64.encodeToString(capturedImage, Base64.NO_WRAP);
            String responseBody = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URLP);
                Log.i("baaaaaaaaaaaaaa",ba.length()+"");
                httppost.setEntity(new StringEntity(ba));
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                //  HttpResponse response = httpclient.execute(httppost);
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                responseBody = httpclient.execute(httppost, responseHandler);
            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return responseBody;

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("False")){
                playNotPresentResult();
            }else{
                try {
                    String[] arr = result.split(",");
                    int set = (arr.length - 2) / 4;
                    int index = 2;
                    double h = Double.parseDouble(arr[0]);
                    double w = Double.parseDouble(arr[1]);
                    while (set != 0 && (index + 3) < arr.length) {
                        double x1 = Double.parseDouble(arr[index]);
                        double y1 = Double.parseDouble(arr[index + 1]);
                        double x2 = Double.parseDouble(arr[index + 2]);
                        double y2 = Double.parseDouble(arr[index + 3]);
                        detect(h, w, x1, y1, x2, y2,arr[index + 4] );
                        set--;
                        index = index + 4;
                    }

                }catch(Exception e){
                    textError = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                textError.setLanguage(Locale.US);
                                textError.speak("There was an error in the processing. Please try again.", TextToSpeech.QUEUE_FLUSH, null,null);
                            }

                        }
                    });
                    prefs.edit().putBoolean("camera", false).commit();
                }

            }

        }
    }

    public class addWishListToServer extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            addingToWishlist();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                //if(userId==0){
                userId=prefs.getInt("userId",0);
                Log.i("userIdddddddddd",userId+"");
                //}
                String url = "&&wishList="+images;
                HttpGet httpget = new HttpGet(URLW+userId+url);
                HttpResponse response = httpclient.execute(httpget);
                result = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return result;

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null && !(result.equals("")) && result.equals("added")){
                addedToWishlist();
            } else if(result != null && !(result.equals("")) && result.equals("failed")){
                playError();
            }else{
                playError();
            }

        }
    }

    public class GetFromServer extends AsyncTask<Void, Void, String> {

        // private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String wlist = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                //if(userId==0){
                userId=prefs.getInt("userId",0);
                //}
                HttpGet httpget = new HttpGet(URL+userId);
                HttpResponse response = httpclient.execute(httpget);
                wlist = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return wlist;

        }
        @Override
        protected void onPostExecute(String result) {
            readText(result);
            //pd.dismiss();

        }
    }


    public class AddUser extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URLA+deviceId);
                HttpResponse response = httpclient.execute(httpget);
                resp = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return resp;

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            userId = Integer.parseInt(result);
            prefs.edit().putInt("userId", userId).commit();
        }
    }

    // Speech Recognizer
    @Override
    public void onBeginningOfSpeech() {
        Log.i("Beginning","Beginin");
    }


    @Override
    public void onBufferReceived(byte[] buffer) {
    }
    @Override
    public void onEndOfSpeech() {
        speech.stopListening();
    }
    @Override
    public void onError(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "audio error";
                dintgetMessage();
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "client error";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                //dintgetMessage();
                message = "insufficient permissions";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                // dintgetMessage();
                message = "network error";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                //dintgetMessage();
                message = "network timeout";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                //dintgetMessage();
                message = "error_match";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                dintgetMessage();
                message = "recognizer busy";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_SERVER:
                // dintgetMessage();
                message = "server error";
                Log.i("Beginning",message);
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                //dintgetMessage();
                message = "timeout error";
                Log.i("Beginning",message);
                break;
            default:
                //dintgetMessage();
                message = "understand error";
                Log.i("Beginning",message);
                break;
        }
    }
    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }
    @Override
    public void onPartialResults(Bundle arg0) {
    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i("Beginning","in on ready");
    }
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(matches.get(0).contains("camera") || matches.get(0).contains("capture")){
            openCameraIntent();
        }else if(matches.get(0).contains("add") || matches.get(0).contains("items")  || matches.get(0).contains("wishlist")){
            textToSpeechAdd = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeechAdd.setLanguage(Locale.US);
                        textToSpeechAdd.speak("Please say the names of the items that you want to add to the wishlist after the beep. Please say end after adding all the items.", TextToSpeech.QUEUE_FLUSH, null,null);

                    }

                }
            });
            beep.start();
        } else if(matches.get(0).contains("end")){
            new addWishListToServer().execute();
            speech.stopListening();

        }else if(matches.get(0).contains("home")){
            moveTaskToBack(true);
        }else{
            Log.i("ressssssssss",matches.get(0));
            String res = checkCorrectNames(matches.get(0));
            if(!res.equals("")){
                Log.i("ressssssssss1",res);
                if(this.wishList.add(res)){
                    Log.i("ressssssssss2",res);
                    images = images+res+",";
                }

                // this.wishList.add(res);

            }
        }

    }
    @Override
    public void onRmsChanged(float rmsdB) {
    }


    public String checkCorrectNames(String item){
        if(item.toLowerCase().contains("c") && !item.toLowerCase().contains("pepsi") && !item.toLowerCase().contains("campbell")){
            return "coke can";
        }else if(item.toLowerCase().contains("knorr") && item.toLowerCase().contains("tomato") && item.toLowerCase().contains("soup")){
            return "knorr tomato soup";
        }else if(item.toLowerCase().contains("knorr") && item.toLowerCase().contains("corn") && item.toLowerCase().contains("soup")){
            return "knorr corn soup";
        }else if(item.toLowerCase().contains("pods")){
            return "tide pods";
        }else if(item.toLowerCase().contains("liquid")){
            return "tide liquid";
        }else if(item.toLowerCase().contains("pepsi")){
            return "pepsi can";
        }else if(item.toLowerCase().contains("campbell") && item.toLowerCase().contains("soup")){
            return "campbell tomato soup";
        }

        return "";
    }

    public void dintgetMessage(){
        textToSpeechDint = new TextToSpeech(MainActivity.this , new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechDint.setLanguage(Locale.US);
                    textToSpeechDint.speak("Dint get you. Please tap and say again.", TextToSpeech.QUEUE_FLUSH, null,null);

                }

            }
        });
    }
}