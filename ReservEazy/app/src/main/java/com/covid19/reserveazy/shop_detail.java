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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

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


public class shop_detail extends AppCompatActivity {
    TextView shop_name;
    ImageView shop_pic;
    TextView shop_area;
    TextView shop_phone;
    TextView shop_email;
    TextView shop_category;
    String user_key;
    Spinner spinner;
    DatabaseReference databaseReference;
    DatabaseReference ref;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageView map;
    String map_link;
    ImageView whatsapp_open;
    String phone;
    Button submit;
    DatePicker datePicker;
    Spinner slot;
    String datepicker_date;
    String slots;
    int day_ofset;
    String today;
    String customer_key;
    String slots_available;
    ArrayList<String> list;
    ArrayAdapter<String> dataAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_detail);

        shop_name=(TextView) findViewById(R.id.shop_detail_name);
        shop_pic = (ImageView) findViewById(R.id.shop_detail_pic);
        shop_area = (TextView) findViewById(R.id.shop_d_area);
        shop_email = (TextView) findViewById(R.id.s_d_email);
        shop_phone = (TextView) findViewById(R.id.shop_d_phone);
        shop_category = (TextView) findViewById(R.id.s_d_category);
        map = (ImageView) findViewById(R.id.s_d_map);
        whatsapp_open = (ImageView) findViewById(R.id.s_d_whatsapp);
        submit = (Button) findViewById(R.id.s_d_submit);
        datePicker = (DatePicker)findViewById(R.id.s_d_datepicker);
        slot = (Spinner) findViewById(R.id.s_d_slotpicker);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(map_link);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        whatsapp_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri whatsapp_uri = Uri.parse("https://api.whatsapp.com/send?phone=+91"+phone);
                Intent whatsapp_intent = new Intent(Intent.ACTION_VIEW,whatsapp_uri);
                if(whatsapp_intent.resolveActivity(getPackageManager())!=null){
                    startActivity(whatsapp_intent);
                }
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Merchants");
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("merchant_pic");
        Intent intent = getIntent();

        progressDialog = new ProgressDialog(this);
        spinner = (Spinner) findViewById(R.id.s_d_slotpicker);


        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePicker.setMaxDate(System.currentTimeMillis() + 2*24*60*60*1000);

        final LocalDate today_date = LocalDate.now();
        today = today_date.toString();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker_date = datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
                day_ofset = getDay(today,datepicker_date);
                slots = slot.getSelectedItem().toString();
                setUserDetails(day_ofset,slot,datepicker_date);
            }
        });

            if(intent.getExtras()!=null) {
                 user_key = (String)intent.getSerializableExtra("data");
                 customer_key = (String)intent.getSerializableExtra("customer_key");
                 databaseReference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         progressDialog.setMessage("Loading....");
                         progressDialog.show();
                         Map<String, Map<String,String>> details = (Map<String,Map<String,String>>) dataSnapshot.getValue();
                         final Map<String,String> merchant = details.get(user_key);
                         list = getSlots(merchant.get("start"),merchant.get("end"));
                         dataAdapter = new ArrayAdapter<String>(shop_detail.this,android.R.layout.simple_spinner_item, list);
                         dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                         spinner.setAdapter(dataAdapter);
                         if(merchant.get("profile").equals("true")) {
                             storageReference.child(user_key + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                 @Override
                                 public void onSuccess(byte[] bytes) {
                                     Bitmap d = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                     shop_pic.setImageBitmap(d);
                                     shop_name.setText(merchant.get("name"));
                                     shop_area.setText(merchant.get("area"));
                                     shop_phone.setText(merchant.get("phone"));
                                     shop_email.setText(merchant.get("email"));
                                     shop_category.setText(merchant.get("category"));
                                     map_link = merchant.get("location");
                                     phone = merchant.get("whatsapp");
                                     slots_available = merchant.get("slots");
                                     progressDialog.dismiss();
                                 }
                             });
                         }else{
                             shop_name.setText(merchant.get("name"));
                             shop_area.setText(merchant.get("area"));
                             shop_phone.setText(merchant.get("phone"));
                             shop_email.setText(merchant.get("email"));
                             shop_category.setText(merchant.get("category"));
                             map_link = merchant.get("location");
                             phone = merchant.get("whatsapp");
                             slots_available = merchant.get("slots");
                             progressDialog.dismiss();
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
            }
    }

    private void setUserDetails(final int day_ofset, final Spinner slot, final String date) {
        ref.child("merchant_user_detail").child(user_key).child("day_"+day_ofset).child(slot.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> details = (Map<String, String>) dataSnapshot.getValue();
                if (Integer.parseInt(details.get("count")) < Integer.parseInt(slots_available)) {
                    final int count = Integer.parseInt(details.get("count"));
                    ref.child("customer_bookings").child(customer_key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(user_key)) {
                                ref.child("customer_bookings").child(customer_key).child(user_key).child("date").setValue(date);
                                ref.child("customer_bookings").child(customer_key).child(user_key).child("slot").setValue(slot.getSelectedItem().toString());
                                ref.child("merchant_user_detail").child(user_key).child("day_" + day_ofset).child(slot.getSelectedItem().toString()).child("cust_" + Integer.toString(count)).setValue(customer_key);
                                ref.child("merchant_user_detail").child(user_key).child("day_" + day_ofset).child(slot.getSelectedItem().toString()).child("count").setValue(Integer.toString(count + 1));
                                startActivity(new Intent(shop_detail.this, slot_booked.class).putExtra("key", customer_key));
                            }else{
                                Toast.makeText(shop_detail.this,"Booking Limit Reached for this Merchant",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(shop_detail.this, "Slot is full! Please select a different slot", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private ArrayList<String> getSlots(String start_time,String end_time){
        ArrayList<String> slots = new ArrayList<>();
        String start_m = start_time.substring(start_time.length()-2);
        int start_h = Integer.parseInt(start_time.substring(0,start_time.indexOf(":")));
        int end_h = Integer.parseInt(end_time.substring(0,end_time.indexOf(":")));
        String end_m = end_time.substring(end_time.length()-2);

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
        }
        else{
            int end_hour = end_h+12;
            for(int i=0;i<end_hour-start_h;i++){
                int hour = start_h+i;
                if(hour<12){
                    slots.add(Integer.toString(hour)+":00"+start_m);
                    slots.add(Integer.toString(hour)+":30"+start_m);
                }else if(hour==12) {
                    slots.add(Integer.toString(hour)+":00pm");
                    slots.add(Integer.toString(hour)+":30pm");
                }else
                {
                    slots.add(Integer.toString(hour-12)+":00"+end_m);
                    slots.add(Integer.toString(hour-12)+":30"+end_m);
                }
            }
        }
        return slots;
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
