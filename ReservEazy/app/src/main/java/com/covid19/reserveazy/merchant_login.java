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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import java.util.List;
import java.util.Map;

public class merchant_login extends Activity {

    Button register;
    EditText username;
    EditText password;
    Button signin;
    FirebaseAuth auth;
    DatabaseReference ref;
    String user_key;
    ProgressDialog progressDialog;
    EditText forgot;
    Button submit;
    RelativeLayout relativeLayout;
    Button forgot_pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_login);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Merchants");
        username = (EditText) findViewById(R.id.merchant_user);
        password = (EditText) findViewById(R.id.merchant_pass);
        signin = (Button) findViewById(R.id.merchant_login_button);
        forgot_pass = (Button) findViewById(R.id.m_l_forgot_password);
        submit = (Button) findViewById(R.id.m_r_submit2);
        relativeLayout = (RelativeLayout) findViewById(R.id.m_r_dialog);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        user_key=null;
        register = (Button) findViewById(R.id.merchant_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(merchant_login.this,merchant_register.class));
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auth.sendPasswordResetEmail(forgot.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(merchant_login.this,"Password Reset link sent to Your Mail",Toast.LENGTH_LONG).show();
                            }
                        });
                        relativeLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

    }


    private void signIn() {
        final boolean[] flag = {false};
        if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(merchant_login.this, "Fields are empty", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setMessage("Signing In.....");
            progressDialog.show();
            auth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(merchant_login.this, "Username or Password Invalid!", Toast.LENGTH_LONG).show();
                    }
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String,Map<String,String>> detail = (Map<String,Map<String,String>>)dataSnapshot.getValue();
                            if(detail.get(task.getResult().getUser().getUid())!=null){
                                startActivity(new Intent(merchant_login.this,merchant_home.class).putExtra("key",task.getResult().getUser().getUid()));
                            }else{
                                auth.signOut();
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
}
