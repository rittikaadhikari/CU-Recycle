package com.smapps.cu_recycle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntryPage2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private Bitmap mBitmap;
    private Uri current_image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_entry);
        setSupportActionBar(toolbar);

        configureNextButton();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_entry);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_entry);
        navigationView.setNavigationItemSelectedListener(this);

        deleteImages();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
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
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean deleteImages() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] imageFiles = new File[0];
        if (dir != null) {
            imageFiles = dir.listFiles();
        }
        if (imageFiles.length > 5) {
            for (File file : imageFiles) {
                if (!file.delete()) {
                    Toast.makeText(this, "Cant delete File", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
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
                    break;
                default:
                    break;
            }
        }
    }

}
