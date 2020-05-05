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
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class customer_home extends Activity {

    customer current_customer;
    private String user_key;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ImageView imageView;
    TextView textView;
    Button logout;
    ProgressDialog progressDialog;
    ImageButton current;
    ImageButton shops;
    ImageButton bookings;
    ImageButton profile;
    ViewPager viewPager;
    viewPagerAdapter viewPagerAdapter;
    ArrayList<Bitmap> Images;
    StorageReference ads;
    DatabaseReference ads_count;
    int count=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_home);

        viewPager = (ViewPager) findViewById(R.id.c_h_v_p);

        Images = new ArrayList<Bitmap>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers");
        ads_count = FirebaseDatabase.getInstance().getReference().child("ads").child("count");
        storageReference = FirebaseStorage.getInstance().getReference().child("customer");
        ads = FirebaseStorage.getInstance().getReference().child("ads");
        textView = (TextView) findViewById(R.id.c_h_user_name);
        imageView = (ImageView) findViewById(R.id.c_h_user_pic);
        Intent intent = getIntent();
        logout = (Button) findViewById(R.id.customer_logout);

        current = (ImageButton) findViewById(R.id.c_h_home_button);
        shops = (ImageButton) findViewById(R.id.c_h_shops);
        bookings = (ImageButton) findViewById(R.id.c_h_bookings);
        profile = (ImageButton) findViewById(R.id.c_h_profile_button);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(customer_home.this,edit_customer.class).putExtra("key",user_key));
            }
        });


        current.setPressed(true);

        shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(customer_home.this, MainActivity.class).putExtra("key",user_key));
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(customer_home.this, mybookings.class).putExtra("key", user_key));
            }
        });
        progressDialog = new ProgressDialog(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(customer_home.this, "Logged Out Successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(customer_home.this, Front_Activity.class));
            }
        });


        if (intent.getExtras() != null) {
            user_key = (String) intent.getSerializableExtra("key");
            databaseReference.child(user_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.setMessage("Loading!!");
                    progressDialog.show();
                    final Map<String, String> details = (Map<String, String>) dataSnapshot.getValue();
                    ads_count.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final int count = Integer.parseInt(dataSnapshot.getValue(String.class));
                            for (int i = 1; i <= count; i++) {
                                final int finalI = i;
                                ads.child(Integer.toString(i) + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap e = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Images.add(e);
                                        if (finalI == count) {
                                            viewPagerAdapter = new viewPagerAdapter(customer_home.this, Images);
                                            viewPager.setAdapter(viewPagerAdapter);
                                        }
                                    }
                                });
                            }
                            if(details.get("profile").equals("true")) {
                                storageReference.child(user_key + ".jpg").getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap d = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        current_customer = new customer(user_key,details.get("name"),details.get("email"),details.get("phone"),d);
                                        imageView.setImageBitmap(current_customer.getCustomer_pic());
                                        textView.setText(current_customer.getCustomer_name());
                                        progressDialog.dismiss();
                                    }
                                });
                            }else{
                                current_customer = new customer(user_key,details.get("name"),details.get("email"),details.get("phone"),null);
                                textView.setText(current_customer.getCustomer_name());
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
