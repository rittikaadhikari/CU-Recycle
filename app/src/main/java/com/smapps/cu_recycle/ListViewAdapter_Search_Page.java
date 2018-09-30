package com.smapps.cu_recycle;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ListViewAdapter_Search_Page extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Search_Item> arraylist;


    public ListViewAdapter_Search_Page(Context context ) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        ArrayList<Search_Item> arraylist = new ArrayList<Search_Item>();
        arraylist.addAll(Search_Page.searchItemArrayList);
        this.arraylist = new ArrayList<>(arraylist);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return Search_Page.searchItemArrayList.size();
    }

    @Override
    public Search_Item getItem(int position) {
        return Search_Page.searchItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(Search_Page.searchItemArrayList.get(position).getObjectName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        final String charText2 = charText.toLowerCase();
        Search_Page.searchItemArrayList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (charText2.length() == 0) {
            //Search_Page.searchItemArrayList.addAll(arraylist);
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
                                    Search_Page.searchItemArrayList.add(curr_item);
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        } else {
            for (Search_Item wp : arraylist) {
                if (wp.getObjectName().toLowerCase().contains(charText)) {
                    Log.w("TAG", wp.getObjectName());
                    Search_Page.searchItemArrayList.add(wp);
                }
            }
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
                                    if (name.toLowerCase().contains(charText2)) {
                                        Search_Item curr_item = new Search_Item(name, type);
                                        Search_Page.searchItemArrayList.add(curr_item);
                                    }
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
        notifyDataSetChanged();
    }

}