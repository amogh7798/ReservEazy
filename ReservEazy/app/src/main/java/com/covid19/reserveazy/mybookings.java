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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;


public class mybookings extends Activity implements booking_adapter.SelectedBooking {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<bookings> book;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference ref;
    String user_key;
    ImageButton home;
    ImageButton shop;
    ImageButton profile;
    DatabaseReference merchant_user;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookings);

        book = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.shop_list_recycler);

        home = (ImageButton) findViewById(R.id.b_home_button);
        shop = (ImageButton) findViewById(R.id.b_shops);
        profile = (ImageButton) findViewById(R.id.b_profile_button);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mybookings.this,customer_home.class).putExtra("key",user_key));
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mybookings.this,MainActivity.class).putExtra("key",user_key));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mybookings.this,edit_customer.class).putExtra("key",user_key));
            }
        });
        Intent intent = getIntent();

        progressDialog = new ProgressDialog(this);
        ref = FirebaseDatabase.getInstance().getReference().child("Merchants");
        storageReference = FirebaseStorage.getInstance().getReference().child("merchant_pic");
        merchant_user = FirebaseDatabase.getInstance().getReference().child("merchant_user_detail");
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(intent.getExtras()!=null){
            user_key = (String)intent.getSerializableExtra("key");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("customer_bookings").child(user_key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    Map<String, Map<String,String>> detail = (Map<String, Map<String,String>>)dataSnapshot.getValue();
                    if(detail!=null) {
                        for (final String key : detail.keySet()) {
                            final Map<String, String> book_detail = detail.get(key);
                            ref.child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final Map<String, String> merchant_detail = (Map<String, String>) dataSnapshot.getValue();
                                    if (merchant_detail.get("profile").equals("true")) {
                                        storageReference.child(key + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                final Bitmap d = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                book.add(new bookings(key,merchant_detail.get("name"), d, merchant_detail.get("timings"), merchant_detail.get("area"), merchant_detail.get("phone"), book_detail.get("slot"), book_detail.get("date")));
                                                adapter = new booking_adapter(book,mybookings.this);
                                                recyclerView.setAdapter(adapter);

                                            }
                                        });
                                    } else {
                                        book.add(new bookings(key,merchant_detail.get("name"), null, merchant_detail.get("timings"), merchant_detail.get("area"), merchant_detail.get("phone"), book_detail.get("slot"), book_detail.get("date")));
                                        adapter = new booking_adapter(book,mybookings.this);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void selectedBooking(String slot, String date, final String merchant_key) {
         progressDialog.setMessage("Please Wait");
         progressDialog.show();
         String today = LocalDate.now().toString();
         int day_offset = getDay(today,date);
         merchant_user=merchant_user.child(merchant_key).child("day_"+day_offset).child(slot);
         merchant_user.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Map<String,String> detail = (Map<String,String>) dataSnapshot.getValue();
                 for(String key:detail.keySet()){
                     if(detail.get(key).equals(user_key)){
                         merchant_user.child(key).removeValue();
                         merchant_user.child("count").setValue(Integer.parseInt(detail.get("count"))-1);
                         databaseReference.child(merchant_key).removeValue();
                         startActivity(new Intent(mybookings.this,order_cancel.class).putExtra("key",user_key));
                         progressDialog.dismiss();
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private int getDay(String today,String booked_date){
        int day=0;
        int today_year = Integer.parseInt(today.substring(0,today.indexOf("-")));
        Log.i("d",Integer.toString(today_year));
        int today_month = Integer.parseInt(today.substring(today.indexOf("-")+1,today.lastIndexOf("-")));
        int today_date = Integer.parseInt(today.substring(today.lastIndexOf("-")+1));
        int booked_date_month = Integer.parseInt(booked_date.substring(booked_date.indexOf("-")+1,booked_date.lastIndexOf("-")))+1;
        int booked_date_date = Integer.parseInt(booked_date.substring(booked_date.lastIndexOf("-")+1));

        if(today_year%4 != 0){
            switch(today_month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                    }
                    else{
                        switch(today_date){
                            case 30:
                                day=2;
                                break;
                            case 31:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
                case 4:
                case 9:
                case 6:
                case 11:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                    }
                    else{
                        switch(today_date){
                            case 29:
                                day=2;
                                break;
                            case 30:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
                case 2:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                    }
                    else{
                        switch(today_date){
                            case 27:
                                day=2;
                                break;
                            case 28:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
            }
        }
        else{
            switch(today_month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                        Log.i("c",Integer.toString(day));
                    }
                    else{
                        switch(today_date){
                            case 30:
                                day=2;
                                break;
                            case 31:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
                case 4:
                case 9:
                case 6:
                case 11:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                    }
                    else{
                        switch(today_date){
                            case 29:
                                day=2;
                                break;
                            case 30:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
                case 2:
                    if(booked_date_month==today_month){
                        day = booked_date_date-today_date;
                    }
                    else{
                        switch(today_date){
                            case 28:
                                day=2;
                                break;
                            case 29:
                                if(booked_date_date==2){
                                    day=2;
                                }
                                else{
                                    day=1;
                                }
                                break;
                        }
                    }
                    break;
            }
        }
        return day;
    }
}
