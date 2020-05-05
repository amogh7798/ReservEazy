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

public class merchant_home extends Activity {
    Shop shop;
    private String user_key;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ImageView imageView;
    TextView textView;
    Button logout;
    ProgressDialog progressDialog;
    ImageButton current;
    ImageButton user_detail;
    ImageButton profile;
    ViewPager viewPager;
    viewPagerAdapter viewPagerAdapter;
    ArrayList<Bitmap> Images;
    StorageReference ads;
    DatabaseReference ads_count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_login);

        Images = new ArrayList<Bitmap>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("merchant_pic");
        ads_count = FirebaseDatabase.getInstance().getReference().child("ads").child("count");
        ads = FirebaseStorage.getInstance().getReference().child("ads");
        imageView = (ImageView) findViewById(R.id.user_pic);
        textView = (TextView) findViewById(R.id.user_name);
        Intent intent = getIntent();
        logout = (Button) findViewById(R.id.merchant_logout);
        viewPager = (ViewPager) findViewById(R.id.h_l_v_p);


        current = (ImageButton) findViewById(R.id.home_button);
        user_detail = (ImageButton) findViewById(R.id.user_detail_button);
        profile = (ImageButton) findViewById(R.id.profile_button);

        current.setPressed(true);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(merchant_home.this,edit_merchant.class).putExtra("key",user_key));
            }
        });
        user_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(merchant_home.this, merchant_user_detail.class).putExtra("key", user_key));
            }
        });
        progressDialog = new ProgressDialog(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(merchant_home.this, "Logged Out Successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(merchant_home.this, Front_Activity.class));
            }
        });

        if (intent.getExtras() != null) {
            user_key = intent.getSerializableExtra("key").toString();
            DatabaseReference ref = databaseReference.child("Merchants").child(user_key);
            ref.addValueEventListener(new ValueEventListener() {
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
                                        if(finalI ==count){
                                            viewPagerAdapter = new viewPagerAdapter(merchant_home.this, Images);
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
                                        shop = new Shop(user_key, d, details.get("name"), details.get("owner"), details.get("license"), details.get("area"), details.get("category"), details.get("phone"), details.get("timings"), details.get("location"), details.get("whatsapp"), details.get("slot"));
                                        imageView.setImageBitmap(shop.getShop_pic());
                                        textView.setText(shop.getOwner_name());
                                        progressDialog.dismiss();
                                    }
                                });
                            }else{
                                shop = new Shop(user_key, null, details.get("name"), details.get("owner"), details.get("license"), details.get("area"), details.get("category"), details.get("phone"), details.get("timings"), details.get("location"), details.get("whatsapp"), details.get("slot"));
                                textView.setText(shop.getOwner_name());
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
