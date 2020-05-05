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

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class customer_register extends Activity {

    EditText name;
    EditText phone;
    EditText email;
    EditText password;
    EditText conpass;
    Button submit;
    int GALARY_INTENT = 2;
    CheckBox check;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth mauth;
    Pattern p;
    Matcher m;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_register);

        name = (EditText) findViewById(R.id.c_r_name);
        phone = (EditText) findViewById(R.id.c_r_phone);
        email = (EditText) findViewById(R.id.c_r_email);
        password = (EditText) findViewById(R.id.c_r_pass);
        conpass = (EditText) findViewById(R.id.c_r_conpass);
        submit = (Button) findViewById(R.id.c_r_submit);
        check = (CheckBox) findViewById(R.id.c_r_check);

        p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");

        mauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers");
        storageReference = FirebaseStorage.getInstance().getReference().child("customer");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check.isChecked()) {
                    Toast.makeText(customer_register.this, "Please Agree to Terms and Conditions", Toast.LENGTH_LONG).show();
                } else {
                    if (TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(conpass.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString())) {
                        Toast.makeText(customer_register.this, "Fields are empty", Toast.LENGTH_LONG).show();
                    } else {
                        m = p.matcher(phone.getText().toString());
                        if (m.find()) {
                            if (password.getText().toString().equals(conpass.getText().toString())) {
                                progressDialog.setMessage("Creating account");
                                progressDialog.show();
                                mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(customer_register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            databaseReference.child(task.getResult().getUser().getUid()).child("name").setValue(name.getText().toString());
                                            databaseReference.child(task.getResult().getUser().getUid()).child("email").setValue(email.getText().toString());
                                            databaseReference.child(task.getResult().getUser().getUid()).child("phone").setValue(phone.getText().toString());
                                            databaseReference.child(task.getResult().getUser().getUid()).child("notification").setValue(FirebaseInstanceId.getInstance().getToken());
                                            databaseReference.child(task.getResult().getUser().getUid()).child("profile").setValue("false");
                                            databaseReference.child(task.getResult().getUser().getUid()).child("google").setValue("false");
                                            progressDialog.dismiss();
                                            Toast.makeText(customer_register.this, "Account Created Successfully!!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(customer_register.this, Front_Activity.class));
                                        } else {
                                            Toast.makeText(customer_register.this, "Sign Up Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(customer_register.this, "Password and Confirm password do not match", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(customer_register.this,"Invalid Phone Number",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
}
