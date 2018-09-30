package com.smapps.cu_recycle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;



public class EntryPage2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    interface Service {
        @Multipart
        @POST("/image/{image}")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String SERVER_PATH = "http://10.195.244.149:5000";
    private String mCurrentPhotoPath;
    private Uri current_image_uri;
    Service service;

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
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_entry);
//        navigationView.setEnabled(false);
//        navigationView.setVisibility(View.GONE);
//        navigationView.removeAllViews();
//        navigationView.setNavigationItemSelectedListener(this);

        deleteImages();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(SERVER_PATH).client(client).build().create(Service.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //constructor

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Log.d("EntryPage2", "Image: " + current_image_uri);

                    Toast.makeText(this, "Analyzing image...", Toast.LENGTH_LONG).show();

                    File file = new File(mCurrentPhotoPath);
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                    retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            String response_msg = "";
                            try {
                                if (response.body() != null) {
                                    response_msg = response.body().string();
                                    Log.d("EntryPage2", "Response received: " + response_msg);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(!response_msg.equals("")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(EntryPage2.this);
                                builder.setMessage(response_msg).setTitle("Result");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.create().show();
                            }
                            else {
                                Toast.makeText(EntryPage2.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
    
}
