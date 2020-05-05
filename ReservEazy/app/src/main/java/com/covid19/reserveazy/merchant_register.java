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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class merchant_register extends Activity {

    TextView name;
    TextView email;
    TextView owner;
    TextView license;
    TextView phone;
    TextView whatsapp;
    TextView area;
    TextView city;
    TextView map;
    TextView slots;
    TextView password;
    TextView confirm;
    Spinner category;
    Button submit;
    Spinner start_time;
    Spinner end_time;
    CheckBox check;
    Pattern p;

    Pattern q;
    Pattern r;

    FirebaseAuth auth;
    DatabaseReference ref;
    StorageReference storageReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_register);
        name = (TextView) findViewById(R.id.store_name);
        email = (TextView) findViewById(R.id.m_r_Email_id);
        owner = (TextView) findViewById(R.id.m_r_Owner_Name);
        password = (TextView) findViewById(R.id.m_r_password);
        confirm = (TextView) findViewById(R.id.m_r_confirm);
        license = (TextView) findViewById(R.id.m_r_license_number);
        phone = (TextView) findViewById(R.id.m_r_Phone_Number);
        whatsapp = (TextView) findViewById(R.id.m_r_Whatsapp);
        area = (TextView) findViewById(R.id.m_r_Area);
        city = (TextView) findViewById(R.id.m_r_city);
        map = (TextView) findViewById(R.id.m_r_location);
        slots = (TextView) findViewById(R.id.m_r_slots);
        submit = (Button) findViewById(R.id.m_r_submit);
        start_time = (Spinner) findViewById(R.id.m_r_start);
        end_time = (Spinner) findViewById((R.id.m_r_stop));
        check = (CheckBox) findViewById(R.id.m_r_check);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        q = Pattern.compile("\\d");
        r = Pattern.compile("\\d\\d");
        category = (Spinner) findViewById(R.id.m_r_category);
        ArrayList<String> list = new ArrayList<String>();
        list.add("diary");
        list.add("provision");
        list.add("meat");
        list.add("vegetables/fruits");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);

        final ArrayList<String> start = new ArrayList<String>();
        start.add("Start Time");
        start.add("8:00am");
        start.add("9:00am");
        start.add("10:00am");
        start.add("11:00am");
        start.add("12:00pm");
        start.add("1:00pm");
        start.add("2:00pm");
        start.add("3:00pm");
        start.add("4:00pm");
        start.add("5:00pm");
        start.add("6:00pm");
        start.add("7:00pm");
        start.add("8:00pm");
        start.add("9:00pm");
        start.add("10:00pm");
        ArrayAdapter<String> start_Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, start);
        start_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        start_time.setAdapter(start_Adapter);

        ArrayList<String> end = new ArrayList<>();
        end.add("End Time");
        end.add("8:00am");
        end.add("9:00am");
        end.add("10:00am");
        end.add("11:00am");
        end.add("12:00pm");
        end.add("1:00pm");
        end.add("2:00pm");
        end.add("3:00pm");
        end.add("4:00pm");
        end.add("5:00pm");
        end.add("6:00pm");
        end.add("7:00pm");
        end.add("8:00pm");
        end.add("9:00pm");
        end.add("10:00pm");
        ArrayAdapter<String> end_Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, end);
        end_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        end_time.setAdapter(end_Adapter);

        progressDialog = new ProgressDialog(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirm.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(owner.getText().toString()) || TextUtils.isEmpty(license.getText().toString()) || TextUtils.isEmpty(area.getText().toString()) || TextUtils.isEmpty(city.getText().toString()) || TextUtils.isEmpty(map.getText().toString()) || TextUtils.isEmpty(slots.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(whatsapp.getText().toString())){
                    Toast.makeText(merchant_register.this,"Fields are empty",Toast.LENGTH_LONG).show();
                }
                else if(!check.isChecked()){
                    Toast.makeText(merchant_register.this,"Please Accept Terms and Condition",Toast.LENGTH_LONG).show();
                }
                else if(!p.matcher(phone.getText().toString()).find()){
                    Toast.makeText(merchant_register.this,"Invalid Phone Number",Toast.LENGTH_LONG).show();
                }
                else if(!p.matcher(whatsapp.getText().toString()).find()){
                    Toast.makeText(merchant_register.this,"Invalid Whatsapp Number",Toast.LENGTH_LONG).show();
                }
                else if(!q.matcher(slots.getText().toString()).find() && !r.matcher(slots.getText().toString()).find()){
                    Toast.makeText(merchant_register.this,"Invalid Slots",Toast.LENGTH_LONG).show();
                }
                else if(validate_slot(start_time.getSelectedItem().toString(),end_time.getSelectedItem().toString())){
                    if(password.getText().toString().equals(confirm.getText().toString())){
                        progressDialog.setMessage("Creating account");
                        progressDialog.show();
                        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(merchant_register.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i("a",email.getText().toString());
                                    Log.i("b",password.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("name").setValue(name.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("owner").setValue(owner.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("phone").setValue(phone.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("whatsapp").setValue(whatsapp.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("license").setValue(license.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("area").setValue(area.getText().toString() + "," + city.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("timings").setValue(start_time.getSelectedItem().toString() + "-" + end_time.getSelectedItem().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("start").setValue(start_time.getSelectedItem().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("location").setValue(map.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("end").setValue(end_time.getSelectedItem().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("slots").setValue(slots.getText().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("category").setValue(category.getSelectedItem().toString());
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("profile").setValue("false");
                                    ref.child("Merchants").child(task.getResult().getUser().getUid()).child("email").setValue(task.getResult().getUser().getEmail());
                                    ArrayList<String> slot_value = getSlots(start_time.getSelectedItem().toString(), end_time.getSelectedItem().toString());
                                    for (int i = 0; i < 3; i++) {
                                        for (String x : slot_value) {
                                            ref.child("merchant_user_detail").child(task.getResult().getUser().getUid()).child("day_" + Integer.toString(i)).child(x).child("count").setValue("0");
                                        }
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(merchant_register.this, "Account created successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(merchant_register.this, Front_Activity.class));
                                }else{
                                    Toast.makeText(merchant_register.this,"Something went Wrong",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(merchant_register.this,"Password and Confirm Password did not match!!",Toast.LENGTH_LONG);
                    }
                }
            }
        });
    }

    private boolean validate_slot(String start_time,String end_time){
        String start_m = start_time.substring(start_time.length()-2);
        int start_h = Integer.parseInt(start_time.substring(0,start_time.indexOf(":")));
        int end_h = Integer.parseInt(end_time.substring(0,end_time.indexOf(":")));
        String end_m = end_time.substring(end_time.length()-2);
        if(start_m.equals(end_m)){
            if(start_h!=12) {
                if (end_h <= start_h) {
                    Toast.makeText(merchant_register.this, "Open time cannot be less than Close time", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        else if(start_m.equals("pm") && end_m.equals("am")){
                Toast.makeText(merchant_register.this, "Open time cannot be less than Close time", Toast.LENGTH_LONG).show();
                return false;
        }
        else if(start_time.equals("Start Time") || end_time.equals("End Time")){
            Toast.makeText(merchant_register.this,"Start Time or End Time is invalid",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
}
