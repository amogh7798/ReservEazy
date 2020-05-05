/* This file is part of ReservEazy.

    ReservEazy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ReservEazy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ReservEazy.  If not, see <https://www.gnu.org/licenses/>.*/

package com.covid19.reserveazy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements shop_adapter.SelectedShop {

    RecyclerView recyclerView;
    shop_adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Shop> shop;
    DatabaseReference mref;
    StorageReference StorageRef;
    String customer_key;
    ProgressDialog progressDialog;
    TextView filter;
    RelativeLayout r1;
    Button b1;
    CheckBox diary;
    CheckBox provision;
    CheckBox meat;
    CheckBox vegetables;
    FusedLocationProviderClient client;
    ArrayList<Shop> filter_cate;
    SearchView searchView;

    ImageButton home;
    ImageButton bookings;
    ImageButton profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);
        shop = new ArrayList<Shop>();
        filter_cate = new ArrayList<Shop>();
        r1 = (RelativeLayout) findViewById(R.id.a_m_rl);
        b1 = (Button) findViewById(R.id.a_m_b1);
        StorageRef = FirebaseStorage.getInstance().getReference().child("merchant_pic");
        progressDialog = new ProgressDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.shop_list_recycler);
        home = (ImageButton) findViewById(R.id.a_home_button);
        bookings = (ImageButton) findViewById(R.id.a_bookings);
        profile = (ImageButton) findViewById(R.id.a_profile_button);
        searchView = (SearchView) findViewById(R.id.main_Search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,customer_home.class).putExtra("key",customer_key));
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,mybookings.class).putExtra("key",customer_key));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,edit_customer.class).putExtra("key",customer_key));
            }
        });
        Intent intent = getIntent();

        diary = (CheckBox) findViewById(R.id.a_m_diary);
        provision = (CheckBox) findViewById(R.id.a_m_provision);
        meat = (CheckBox) findViewById(R.id.a_m_meat);
        vegetables = (CheckBox) findViewById(R.id.a_m_vegetables);
        filter = (TextView) findViewById(R.id.filter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setVisibility(View.VISIBLE);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setVisibility(View.GONE);
                filter(diary.isChecked(),provision.isChecked(),meat.isChecked(),vegetables.isChecked());
            }
        });
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(intent.getExtras()!=null) {
                customer_key = (String) intent.getSerializableExtra("key");
                mref = FirebaseDatabase.getInstance().getReference().child("Merchants");
                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.setMessage("Loading....");
                        progressDialog.show();
                        Map<String, Map<String, String>> details = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                        for (final String key : details.keySet()) {
                            final Map<String, String> merchant_detail = details.get(key);
                            if(merchant_detail.get("profile").equals("true")) {
                                StorageRef.child(key + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(final byte[] bytes) {
                                        Bitmap d = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        shop.add(new Shop(key, d, merchant_detail.get("name"), merchant_detail.get("owner"), merchant_detail.get("license"), merchant_detail.get("area"), merchant_detail.get("category"), merchant_detail.get("phone"), merchant_detail.get("timings"), merchant_detail.get("location"), merchant_detail.get("whatsapp"), merchant_detail.get("slot")));
                                    }
                                });
                            }else{
                                shop.add(new Shop(key, null, merchant_detail.get("name"), merchant_detail.get("owner"), merchant_detail.get("license"), merchant_detail.get("area"), merchant_detail.get("category"), merchant_detail.get("phone"), merchant_detail.get("timings"), merchant_detail.get("location"), merchant_detail.get("whatsapp"), merchant_detail.get("slot")));
                            }
                        }
                        adapter = new shop_adapter(shop,MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    }

    private void filter(boolean diary, boolean pro,boolean meat,boolean veg) {
        if(diary){
            for(Shop x: shop){
                if(x.getShop_category().equals("diary")){
                    if(!filter_cate.contains(x)) {
                        filter_cate.add(x);
                    }
                }
            }
        }else{
            for(Shop x:filter_cate){
                if(x.getShop_category().equals("diary")){
                    filter_cate.remove(x);
                }
            }
        }
        if(pro){
            for(Shop x: shop){
                if(x.getShop_category().equals("provision")){
                    if(!filter_cate.contains(x)) {
                        filter_cate.add(x);
                    }
                }
            }
        }
        else{
            for(Shop x:filter_cate){
                if(x.getShop_category().equals("provision")){
                    filter_cate.remove(x);
                }
            }
        }
        if(meat){
            for(Shop x: shop){
                if(x.getShop_category().equals("meat")){
                    if(!filter_cate.contains(x)) {
                        filter_cate.add(x);
                    }
                }
            }
        }else {
            for (Shop x : filter_cate) {
                if (x.getShop_category().equals("meat")) {
                    filter_cate.remove(x);
                }
            }
        }
        if(veg){
            for(Shop x: shop){
                if(x.getShop_category().equals("vegetables/fruits")){
                    if(!filter_cate.contains(x)) {
                        filter_cate.add(x);
                    }
                }
            }
        }else{
            for(Shop x:filter_cate){
                if(x.getShop_category().equals("vegetables/fruits")){
                    filter_cate.remove(x);
                }
            }
        }
        if(!diary && !pro && !meat && !veg)
            adapter = new shop_adapter(shop,MainActivity.this);
        else
            adapter = new shop_adapter(filter_cate,MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void selectedShop(String user_key) {
        startActivity(new Intent(MainActivity.this,shop_detail.class).putExtra("data", user_key).putExtra("customer_key",customer_key));
    }
}

