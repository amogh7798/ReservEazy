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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

public class login extends Activity {

    private FirebaseAuth auth;

    Button Loginbutton;
    EditText Username;
    EditText Password;
    ProgressDialog progressDialog;
    DatabaseReference ref;
    Button register;
    Button google_Signin;
    private static int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    RelativeLayout relativeLayout;
    EditText forgot;
    Button submit;
    Button forgot_pass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login);

        auth = FirebaseAuth.getInstance();
        Loginbutton = (Button) findViewById(R.id.customer_login_button);
        Username = (EditText) findViewById(R.id.customer_user);
        Password = (EditText) findViewById(R.id.customer_pass);
        register = (Button) findViewById(R.id.customer_register);
        google_Signin = (Button) findViewById(R.id.google_Signin);
        relativeLayout = (RelativeLayout) findViewById(R.id.c_r_dialog);
        forgot = (EditText) findViewById(R.id.c_r_forgotemail);
        submit = (Button) findViewById(R.id.c_r_dialog_submit);
        forgot_pass = (Button) findViewById(R.id.c_l_forgot_password);

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
                                  Toast.makeText(login.this,"Password Reset link sent to Your Mail",Toast.LENGTH_LONG).show();
                            }
                        });
                        relativeLayout.setVisibility(View.GONE);
                    }
                });
            }
        });
        google_Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, customer_register.class));
            }
        });

        progressDialog = new ProgressDialog(this);

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference().child("Customers");
        Username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Username.setHint("");
                return false;
            }
        });

        Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Password.setHint("");
                return false;
            }
        });
    }

    private void SignIn() {
        final String[] temp = new String[1];
        if (TextUtils.isEmpty(Username.getText().toString()) || TextUtils.isEmpty(Password.getText().toString())) {
            Toast.makeText(login.this, "Username or Password Field is empty", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setMessage("Signing In");
            progressDialog.show();
            auth.signInWithEmailAndPassword(Username.getText().toString(), Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(login.this, "Username or Password Invalid!", Toast.LENGTH_LONG).show();
                    }
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Map<String, String>> detail = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                            if (detail.get(task.getResult().getUser().getUid()) != null) {
                                ref.child(task.getResult().getUser().getUid()).child("notification").setValue(FirebaseInstanceId.getInstance().getToken());
                                startActivity(new Intent(login.this, customer_home.class).putExtra("key", task.getResult().getUser().getUid()));
                            } else {
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("a", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        final AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(login.this);
                 ref.child(task.getResult().getUser().getUid()).child("name").setValue(googleSignInAccount.getDisplayName());
                 ref.child(task.getResult().getUser().getUid()).child("email").setValue(googleSignInAccount.getEmail());
                 ref.child(task.getResult().getUser().getUid()).child("phone").setValue(null);
                 ref.child(task.getResult().getUser().getUid()).child("Notification").setValue(FirebaseInstanceId.getInstance().getToken());
                 ref.child(task.getResult().getUser().getUid()).child("google").setValue("true");
                 ref.child(task.getResult().getUser().getUid()).child("profile").setValue("false");
                 progressDialog.dismiss();
                 startActivity(new Intent(login.this,Front_Activity.class));
            }
        });
    }

}


