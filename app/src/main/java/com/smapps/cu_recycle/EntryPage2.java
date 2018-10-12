package com.smapps.cu_recycle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;



public class EntryPage2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private static float[] prediction = new float[2];
    private Uri current_image_uri;
    private static AlertDialog.Builder builder;
    private static Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page2);
        Toolbar toolbar = findViewById(R.id.toolbar_entry);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        configureNextButton();

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_entry);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//        drawer.removeAllViews();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_entry);
        navigationView.setNavigationItemSelectedListener(this);

        deleteImages();
        builder = new AlertDialog.Builder(EntryPage2.this);
        ZipResourceFile expansionFile = null;
        try {
            expansionFile = APKExpansionSupport.getAPKExpansionZipFile(this, 4, 0);
            Log.d("expansion", "loaded expansion file");
            AssetFileDescriptor afd = expansionFile.getAssetFileDescriptor("official_model.tflite");
            FileInputStream inputStream = new FileInputStream(afd.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = afd.getStartOffset();
            long declaredLength = afd.getDeclaredLength();
            tflite = null;
            try {
                MappedByteBuffer model_mapped_buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
                tflite = new Interpreter(model_mapped_buffer);
                Log.d("TensorFlow", "Loaded Model");
            } catch (IOException e) {
                Log.w("TensorFlow", "Failed to load model", e);
            }

        } catch (IOException e) {
            Log.w("expansion", "Failed to find expansion file", e);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_entry);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_page2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            launchCamera(null);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_entry);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void configureNextButton() {
        ImageButton toText = (ImageButton) findViewById(R.id.manualButton);
        toText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EntryPage2.this, Search_Page.class));
            }
        });
    }

    private File createImageFile() throws IOException {
        //Create an image file name
        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void deleteImages() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] imageFiles = new File[0];
        if (dir != null) {
            imageFiles = dir.listFiles();
        }
        if (imageFiles.length > 5) {
            Log.d("EntryPage2", "Deleting old images");
            for (File file : imageFiles) {
                if (!file.delete()) {
                    Toast.makeText(this, "Cant delete File", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * Called when the user touches the button
     * Launches the camera API
     */
    public void launchCamera(View view) {
        // Do something in response to button click
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) { //makes sure camera is available
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
            }
            //Continue only if file was created
            if (photoFile != null) {

                Uri currentPhotoURI = FileProvider.getUriForFile(this,
                        "com.smapps.cu_recycle.fileprovider", photoFile);
                current_image_uri = currentPhotoURI;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoURI);
                Log.d("Location", "URI: " + currentPhotoURI.toString());
                Log.d("Location", "PATH: " + mCurrentPhotoPath);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE); //start camera
            }
        }
    }

    public static float[][][][] getMat(Bitmap bitmap){
        final int IMAGE_SIZE = bitmap.getWidth();
        float[][][][] input_array = new float[1][IMAGE_SIZE][IMAGE_SIZE][3];
        for(int r = 0; r < IMAGE_SIZE; r++){
            for(int c = 0; c < IMAGE_SIZE; c++){
                int pixel = bitmap.getPixel(r, c);
                input_array[0][r][c][0] = Color.blue(pixel);
                input_array[0][r][c][1] = Color.green(pixel);
                input_array[0][r][c][2] = Color.red(pixel);
            }

        }
        return input_array;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //constructor

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Log.d("EntryPage2", "Image: " + current_image_uri);
                    File file = new File(mCurrentPhotoPath);
                    Bitmap photoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(photoBitmap, 224, 224, false);

                    predict(resizedBitmap);
                    Toast.makeText(this, "Running Model...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


    public static void predict(final Bitmap bitmap){
        //Runs inference in background thread
        new AsyncTask<Integer,Integer,Integer>(){

            @Override
            protected Integer doInBackground(Integer ...params){
                float[][][][] pixels = getMat(bitmap);
                float[][] output = new float[1][2];
                tflite.run(pixels, output);
                prediction = output[0];
                for(int i = 0; i < prediction.length; i++){
                    Log.d("TensorFlow", "Result " + i + ": " + prediction[i]);
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                String result = "The item is garbage.";
                if(prediction[1] > prediction[0]){
                    result = "The item is recyclable.";
                }
                builder.setMessage(result).setTitle("Result");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", null);
                builder.create().show();
            }
        }.execute(0);

    }
    
}
