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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import static java.nio.file.Paths.get;

public class merchant_user_detail extends Activity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatePicker datePicker;
    Spinner spinner;
    Button submit;
    String today;
    String datepicker_date;
    int day_ofset;
    DatabaseReference ref;
    String slot;
    String user_key;
    DatabaseReference customer_database;
    StorageReference storageReference;
    DatabaseReference merchant_database;
    ArrayList<String> list;
    ProgressDialog progressDialog;
    ArrayAdapter<String> dataAdapter;
    ImageButton home;
    ImageButton profile;
    ArrayList<customer> customers;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_user_view);

        customers = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        LocalDate today_date = LocalDate.now();
        today = today_date.toString();
        recyclerView = (RecyclerView) findViewById(R.id.shop_list_recycler);
        submit = (Button) findViewById(R.id.user_detail_submit);
        home = (ImageButton) findViewById(R.id.m_u_d_home_button);
        profile = (ImageButton) findViewById(R.id.m_u_d_profile_button);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(merchant_user_detail.this,edit_merchant.class).putExtra("key",user_key));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(merchant_user_detail.this,merchant_home.class).putExtra("key",user_key));
            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child("merchant_user_detail");
        customer_database = FirebaseDatabase.getInstance().getReference().child("Customers");
        merchant_database = FirebaseDatabase.getInstance().getReference().child("Merchants");
        storageReference = FirebaseStorage.getInstance().getReference().child("customer");

        spinner = (Spinner) findViewById(R.id.slotpicker);

        datePicker = (DatePicker) findViewById(R.id.datepicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setMaxDate(System.currentTimeMillis() + 2*24*60*60*1000);
        Intent intent = getIntent();

        if(intent.getExtras()!=null){
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            user_key = (String) intent.getSerializableExtra("key");
            merchant_database.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,String> details = (Map<String,String>) dataSnapshot.getValue();
                    list = getSlots(details.get("start"),details.get("end"));
                    dataAdapter = new ArrayAdapter<String>(merchant_user_detail.this,android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker_date = datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
                day_ofset = getDay(today,datepicker_date);
                slot = spinner.getSelectedItem().toString();
                Log.d("c",user_key);
                getUserDetails(day_ofset,slot);
            }
        });
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
    }

    private void getUserDetails(final int day_ofset, final String slot) {
        customers.clear();
        ref.child(user_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.setMessage("Please wait....");
                progressDialog.show();
                Map<String, Map<String,Map<String,String>>> details = (Map<String, Map<String,Map<String,String>>>) dataSnapshot.getValue();
                final Map<String,String> users =(Map<String,String>) details.get("day_"+Integer.toString(day_ofset)).get(slot);
                customer_database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            Map<String, Map<String, String>> User_detail = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                            for (final String key : users.keySet()) {
                                if(key.equals("count")){
                                    if(users.get(key).equals("0")){
                                        adapter = new customer_adapter(customers);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                                if (!key.equals("count")) {
                                    final Map<String, String> current_user = User_detail.get(users.get(key));
                                    if (current_user.get("profile").equals("true")) {
                                        storageReference.child(users.get(key) + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap d = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                customers.add(new customer(users.get(key), current_user.get("name"), current_user.get("email"), current_user.get("phone"), d));
                                                adapter = new customer_adapter(customers);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        });
                                    }else{
                                        customers.add(new customer(users.get(key), current_user.get("name"), current_user.get("email"), current_user.get("phone"), null));
                                        adapter = new customer_adapter(customers);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }
                            progressDialog.dismiss();

                        }
                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){

                            }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> getSlots(String start_time,String end_time) {
        ArrayList<String> slots = new ArrayList<>();
        String start_m = start_time.substring(start_time.length() - 2);
        int start_h = Integer.parseInt(start_time.substring(0, start_time.indexOf(":")));
        int end_h = Integer.parseInt(end_time.substring(0, end_time.indexOf(":")));
        String end_m = end_time.substring(end_time.length() - 2);

        if(start_m.equals(end_m)) {
            if (start_h == 12) {
                start_h -= 12;
            }
            for (int i = 0; i < end_h - start_h; i++) {
                int hour = start_h+i;
                if(hour==0){
                    hour=12;
                }
                slots.add(Integer.toString(hour) + ":00" + start_m);
                slots.add(Integer.toString(hour) + ":30" + start_m);
            }
        } else {
            int end_hour = end_h + 12;
            for (int i = 0; i < end_hour - start_h; i++) {
                int hour = start_h + i;
                if (hour < 12) {
                    slots.add(Integer.toString(hour) + ":00" + start_m);
                    slots.add(Integer.toString(hour) + ":30" + start_m);
                } else if (hour == 12) {
                    slots.add(Integer.toString(hour) + ":00pm");
                    slots.add(Integer.toString(hour) + ":30pm");
                } else {
                    slots.add(Integer.toString(hour - 12) + ":00" + end_m);
                    slots.add(Integer.toString(hour - 12) + ":30" + end_m);
                }
            }
        }
        return slots;
    }

    private int getDay(String today,String booked_date){
        int day=0;
        int today_year = Integer.parseInt(today.substring(0,today.indexOf("-")));
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
