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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class edit_merchant extends Activity {

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
    ImageButton cus;
    Button edit_store;
    Button edit_map;
    Button edit_area;
    Button edit_start;
    Button edit_stop;
    Button edit_slot;
    TextView area;
    TextView slot;
    TextView map;
    TextView start;
    TextView stop;
    TextView store;
    Button edit_whatsapp;
    TextView whatsapp;
    RelativeLayout spin_dialog;
    ArrayList<String> slots;
    Spinner spin_time;
    Button submit3;
    TextView text2;
    String password;
    Pattern p;
    Pattern q;
    Pattern r;

    int CAPTURECODE;
    String merchant_key;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_merchants);

        edit_email = (Button) findViewById(R.id.e_m_edit_email);
        edit_phone = (Button) findViewById(R.id.e_m_edit_phone);
        edit_pic = (Button) findViewById(R.id.e_m_edit_pic);
        profile = (ImageView) findViewById(R.id.e_m_pic);
        email = (TextView) findViewById(R.id.e_m_email);
        phone = (TextView) findViewById(R.id.e_m_phone);
        value = (EditText) findViewById(R.id.e_m_changed);
        submit1 = (Button) findViewById(R.id.e_m_submit1);
        submit2 = (Button) findViewById(R.id.e_m_submit2);
        dialog = (RelativeLayout) findViewById(R.id.e_m_dialog);
        text = (TextView) findViewById(R.id.e_m_text);
        name = (TextView) findViewById(R.id.e_m_name);
        edit_area = (Button) findViewById(R.id.e_m_edit_area);
        edit_store = (Button) findViewById(R.id.e_m_edit_store);
        edit_slot = (Button) findViewById(R.id.e_m_edit_slot);
        edit_start = (Button) findViewById(R.id.e_m_edit_start);
        edit_stop = (Button) findViewById(R.id.e_m_edit_stop);
        edit_map = (Button) findViewById(R.id.e_m_edit_map);
        store = (TextView) findViewById(R.id.e_m_store_name);
        area = (TextView) findViewById(R.id.e_m_area);
        start = (TextView) findViewById(R.id.e_m_start);
        stop = (TextView) findViewById(R.id.e_m_stop);
        map = (TextView) findViewById(R.id.e_m_map);
        slot = (TextView) findViewById(R.id.e_m_slot);
        edit_whatsapp = (Button) findViewById(R.id.e_m_edit_whatsapp);
        whatsapp = (TextView) findViewById(R.id.e_m_whatsapp);
        spin_dialog = (RelativeLayout) findViewById(R.id.e_m_spin_dialog);
        spin_time = (Spinner) findViewById(R.id.e_m_spin);
        submit3 = (Button) findViewById(R.id.e_m_submit3);
        text2 = (TextView) findViewById(R.id.e_m_time);

        p=Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        q=Pattern.compile("\\d");
        r=Pattern.compile("\\d\\d");
        slots = new ArrayList<>();
        slots.add("End Time");
        slots.add("8:00am");
        slots.add("9:00am");
        slots.add("10:00am");
        slots.add("11:00am");
        slots.add("12:00pm");
        slots.add("1:00pm");
        slots.add("2:00pm");
        slots.add("3:00pm");
        slots.add("4:00pm");
        slots.add("5:00pm");
        slots.add("6:00pm");
        slots.add("7:00pm");
        slots.add("8:00pm");
        slots.add("9:00pm");
        slots.add("10:00pm");
        ArrayAdapter<String> end_Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, slots);
        end_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_time.setAdapter(end_Adapter);

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Merchants");
        storageReference = FirebaseStorage.getInstance().getReference().child("merchant_pic");
        auth = FirebaseAuth.getInstance();

        home = (ImageButton) findViewById(R.id.e_m_home);
        cus = (ImageButton) findViewById(R.id.e_m_cus);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_merchant.this,merchant_home.class).putExtra("key",merchant_key));
            }
        });

        cus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_merchant.this,merchant_user_detail.class).putExtra("key",merchant_key));
            }
        });


        Intent intent2 = getIntent();
        if(intent2.getExtras()!=null){
            progressDialog.setMessage("Please Wait");
            password=null;
            progressDialog.show();
            merchant_key =(String) intent2.getSerializableExtra("key");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Map<String,String>> detail = (Map<String,Map<String,String>>) dataSnapshot.getValue();
                    Map<String,String> user_detail = detail.get(merchant_key);
                    name.setText(user_detail.get("owner"));
                    phone.setText(user_detail.get("phone"));
                    email.setText(user_detail.get("email"));
                    area.setText(user_detail.get("area"));
                    store.setText(user_detail.get("name"));
                    slot.setText(user_detail.get("slots"));
                    start.setText(user_detail.get("start"));
                    stop.setText(user_detail.get("end"));
                    map.setText(user_detail.get("location"));
                    whatsapp.setText(user_detail.get("whatsapp"));
                    if(user_detail.get("profile").equals("true")){
                        storageReference.child(merchant_key+".jpg").getBytes(4*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                text.setText("Enter New Phone");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(p.matcher(value.getText().toString()).find()) {
                            phone.setText(value.getText());
                            dialog.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(edit_merchant.this,"Invalid Phone Number",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        edit_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Area");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        area.setText(value.getText());
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        });

        edit_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Whatsapp Number");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(p.matcher(value.getText().toString()).find()) {
                            whatsapp.setText(value.getText());
                            dialog.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(edit_merchant.this,"Invalid Whatsapp Number",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edit_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Store Name");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        store.setText(value.getText());
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        });

        edit_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Location");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        map.setText(value.getText());
                        dialog.setVisibility(View.GONE);
                    }
                });
            }
        });

        edit_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Slot");
                dialog.setVisibility(View.VISIBLE);
                value.setText("");
                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(q.matcher(value.getText().toString()).find() || r.matcher(value.getText().toString()).find()) {
                            slot.setText(value.getText());
                            dialog.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(edit_merchant.this,"Invalid slot number",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("Enter New Email");
                dialog.setVisibility(View.VISIBLE);
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
                if(ActivityCompat.checkSelfPermission(edit_merchant.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(edit_merchant.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,CAPTURECODE);
                databaseReference.child(merchant_key).child("profile").setValue("true");
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
                                progressDialog.setMessage("Please wait!!");
                                progressDialog.show();
                                databaseReference.child(merchant_key).child("email").setValue(email.getText().toString());
                                databaseReference.child(merchant_key).child("phone").setValue(phone.getText().toString());
                                databaseReference.child(merchant_key).child("name").setValue(store.getText().toString());
                                databaseReference.child(merchant_key).child("start").setValue(start.getText().toString());
                                databaseReference.child(merchant_key).child("end").setValue(stop.getText().toString());
                                databaseReference.child(merchant_key).child("location").setValue(map.getText().toString());
                                databaseReference.child(merchant_key).child("slots").setValue(slot.getText().toString());
                                databaseReference.child(merchant_key).child("area").setValue(area.getText().toString());
                                databaseReference.child(merchant_key).child("whatsapp").setValue(whatsapp.getText().toString());
                                if(!auth.getCurrentUser().getEmail().equals(email.getText().toString())) {
                                    auth.getCurrentUser().updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            Toast.makeText(edit_merchant.this,"Profile Edited Successfully",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(edit_merchant.this,merchant_home.class).putExtra("key",merchant_key));
                                        }
                                    });
                                }
                                progressDialog.dismiss();
                                Toast.makeText(edit_merchant.this,"Profile Edited Successfully",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(edit_merchant.this,merchant_home.class).putExtra("key",merchant_key));
                            }
                        });
                    }
                });
            }

        });

        edit_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text2.setText("Enter Start Time");
                spin_dialog.setVisibility(View.VISIBLE);
                submit3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         start.setText(spin_time.getSelectedItem().toString());
                         spin_dialog.setVisibility(View.GONE);
                    }
                });
            }
        });

        edit_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text2.setText("Enter Stop Time");
                spin_dialog.setVisibility(View.VISIBLE);
                submit3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stop.setText(spin_time.getSelectedItem().toString());
                        spin_dialog.setVisibility(View.GONE);
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
            storageReference.child(merchant_key+".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(merchant_key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(edit_merchant.this).load(uri).into(profile);
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }
    }
}
