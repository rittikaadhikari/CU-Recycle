package com.smapps.cu_recycle;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Search_Page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {


    // Declare Variables
    private ListView list;
    private ListViewAdapter_Search_Page adapter;
    private SearchView editsearch;
//    private String[] moviewList;
    public static ArrayList<Search_Item> searchItemArrayList = new ArrayList<Search_Item>();
    FirebaseFirestore db;
    private ImageButton beverage;
    private ImageButton food_container;
    private ImageButton residential_paper;
    private ImageButton resin_codes;
    private ImageButton unacceptable;
    private ImageButton recycling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search__page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

//        moviewList = new String[]{"Xmen", "Titanic", "Captain America",
//                "Iron man", "Rocky", "Transporter", "Lord of the rings", "The jungle book",
//                "Tarzan","Cars","Shreck"};
//
        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listView1);
        searchItemArrayList = new ArrayList<>();
//
//        for (int i = 0; i < moviewList.length; i++) {
//            Search_Item movieNames = new Search_Item(moviewList[i], "lol");
//            // Binds all strings into an array
//            searchItemArrayList.add(movieNames);
//        }
        db = FirebaseFirestore.getInstance();
        db.collection("Urbana")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Map<String, Object> objectMap = document.getData();
                                String name = (String)objectMap.get("name");
                                String type = (String)objectMap.get("type");
                                Search_Item curr_item = new Search_Item(name, type);
                                searchItemArrayList.add(curr_item);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter_Search_Page(this);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search_search);
        editsearch.setOnQueryTextListener(this);

        list.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Search_Page.this, searchItemArrayList.get(position).getTypeName(), Toast.LENGTH_SHORT).show();
            }
        });

        beverage = (ImageButton) findViewById(R.id.imageButtonCan);
        beverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, BeverageContainer.class));
            }
        });

        food_container = (ImageButton) findViewById(R.id.imageButtonPaint);
        food_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, FoodContainers.class));
            }
        });

        recycling = (ImageButton) findViewById(R.id.imageButtonBin);
        recycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, RecyclingProcedure.class));
            }
        });

        residential_paper = (ImageButton) findViewById(R.id.imageButtonMail);
        residential_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, ResidentialPaper.class));
            }
        });

        resin_codes = (ImageButton) findViewById(R.id.imageButtonMilk);
        resin_codes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, ResinCodes.class));
            }
        });

        unacceptable = (ImageButton) findViewById(R.id.imageButtonBag);
        unacceptable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search_Page.this, UnacceptableMaterial.class));
            }
        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_search);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_search);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_search);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search__page, menu);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_search);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }
}
