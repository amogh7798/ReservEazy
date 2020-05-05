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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.regex.Pattern;

public class edit_customer extends Activity {

    Button edit_email;
    Button edit_phone;
    Button edit_pic;
    TextView email;
    TextView phone;
    EditText value;
    ImageView profile;
    Button submit1;
    Button submit2;
    RelativeLayout dialog;
    TextView text;
    TextView name;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    ImageButton home;
    ImageButton shop;
    ImageButton bookings;
    String password;
    Pattern p;

    int CAPTURECODE;
    String customer_key;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_customer);

        edit_email = (Button) findViewById(R.id.e_p_c_edit_email);
        edit_phone = (Button) findViewById(R.id.e_p_c_edit_phone);
        edit_pic = (Button) findViewById(R.id.e_p_c_edit_pic);
        profile = (ImageView) findViewById(R.id.e_p_c_pic);
        email = (TextView) findViewById(R.id.e_p_c_email);
        phone = (TextView) findViewById(R.id.e_p_c_phone);
        value = (EditText) findViewById(R.id.e_p_c_changed);
        submit1 = (Button) findViewById(R.id.e_p_c_submit1);
        submit2 = (Button) findViewById(R.id.e_p_c_submit2);
        dialog = (RelativeLayout) findViewById(R.id.e_p_c_dialog);
        text = (TextView) findViewById(R.id.e_p_c_text);
        name = (TextView) findViewById(R.id.e_p_c_name);

        p=Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customers");
        storageReference = FirebaseStorage.getInstance().getReference().child("customer");
        auth = FirebaseAuth.getInstance();

        home = (ImageButton) findViewById(R.id.e_p_c_home_button);
        shop = (ImageButton) findViewById(R.id.e_p_c_shops);
        bookings = (ImageButton) findViewById(R.id.e_p_c_bookings);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_customer.this,customer_home.class).putExtra("key",customer_key));
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_customer.this,MainActivity.class).putExtra("key",customer_key));
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_customer.this,mybookings.class).putExtra("key",customer_key));
            }
        });

        Intent intent2 = getIntent();
        if(intent2.getExtras()!=null){
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            password=null;
            customer_key =(String) intent2.getSerializableExtra("key");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,Map<String,String>> detail = (Map<String,Map<String,String>>) dataSnapshot.getValue();
                    Map<String,String> user_detail = detail.get(customer_key);
                    name.setText(user_detail.get("name"));
                    phone.setText(user_detail.get("phone"));
                    email.setText(user_detail.get("email"));
                    if(user_detail.get("google").equals("true")){
                        edit_email.setVisibility(View.GONE);
                    }
                    if(user_detail.get("profile").equals("true")){
                        storageReference.child(customer_key+".jpg").getBytes(4*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap d = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                profile.setImageBitmap(d);
                                progressDialog.dismiss();
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                text.setText("Enter New Phone");
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(p.matcher(value.getText().toString()).find()) {
                            phone.setText(value.getText());
                            dialog.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(edit_customer.this,"Invalid phone Number",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                text.setText("Enter New Email");
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        email.setText(value.getText());
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        });

        edit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(edit_customer.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(edit_customer.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,CAPTURECODE);
                databaseReference.child(customer_key).child("profile").setValue("true");
            }
        });

        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setVisibility(View.VISIBLE);
                text.setText("Enter Password");
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(auth.getCurrentUser().getEmail(), value.getText().toString());

                        auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.setMessage("Please Wait!!");
                                databaseReference.child(customer_key).child("email").setValue(email.getText().toString());
                                databaseReference.child(customer_key).child("phone").setValue(phone.getText().toString());

                                if(!auth.getCurrentUser().getEmail().equals(email.getText().toString())) {
                                    auth.getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            Toast.makeText(edit_customer.this,"Profile Edited Successfully",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(edit_customer.this,customer_home.class).putExtra("key",customer_key));
                                        }
                                    });
                                }
                                progressDialog.dismiss();
                                Toast.makeText(edit_customer.this,"Profile Edited Successfully",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(edit_customer.this,customer_home.class).putExtra("key",customer_key));
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURECODE && resultCode == RESULT_OK){
            progressDialog.setMessage("Uploading");
            progressDialog.show();
            Uri uri = data.getData();
            storageReference.child(customer_key+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       storageReference.child(customer_key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               Picasso.with(edit_customer.this).load(uri).into(profile);
                               progressDialog.dismiss();
                           }
                       });
                }
            });
        }
    }
}
