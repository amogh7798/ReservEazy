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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Front_Activity extends Activity {
    Button button1;
    Button button2;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);

        button1 = (Button) findViewById(R.id.customer_button);
        button2 = (Button) findViewById(R.id.merchant_button);

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                databaseReference.child("Customers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> detail = (Map<String, String>) dataSnapshot.getValue();
                        if (auth.getCurrentUser() != null && detail.get(auth.getCurrentUser().getUid()) != null) {
                            progressDialog.dismiss();
                            startActivity(new Intent(Front_Activity.this, customer_home.class).putExtra("key", auth.getCurrentUser().getUid()));
                        } else {
                            progressDialog.dismiss();
                            startActivity(new Intent(Front_Activity.this, login.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                databaseReference.child("Merchants").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> detail = (Map<String, String>) dataSnapshot.getValue();
                        if (auth.getCurrentUser() != null && detail.get(auth.getCurrentUser().getUid()) != null) {
                            progressDialog.dismiss();
                            startActivity(new Intent(Front_Activity.this, merchant_home.class).putExtra("key", auth.getCurrentUser().getUid()));
                        } else {
                            progressDialog.dismiss();
                            startActivity(new Intent(Front_Activity.this, merchant_login.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
