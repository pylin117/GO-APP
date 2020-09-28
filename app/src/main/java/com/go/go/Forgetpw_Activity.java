package com.go.go;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class Forgetpw_Activity extends AppCompatActivity {
    Button forgetconfirmButton;
    Button go_signinButton;
    EditText maccountForget;
    BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener = new AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) { // User is signed in
                Log.d("TAG", user.getUid());
            } else {  // User is signed out
                Log.d("Tag", "user ==null");
            }
        }
    };
    private String userUID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw);
        forgetconfirmButton = findViewById(R.id.forgetconfirm);
        go_signinButton = findViewById(R.id.go_signin);
        maccountForget = findViewById(R.id.accountForget);

        mAuth = getInstance();

        forgetconfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maccountForget.getText().toString().isEmpty()) {
                    Log.d("TAG", "信箱不能為空白!");
                    new AlertDialog.Builder(Forgetpw_Activity.this)
                            .setMessage("信箱不能為空白!")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    final String email = maccountForget.getText().toString();
                    Log.d("TAG", email);
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Email sent.");
                                        new AlertDialog.Builder(Forgetpw_Activity.this)
                                                .setMessage("請到信箱收取信件!")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                    else {
                                        Log.d("TAG", "此信箱未註冊!");
                                        new AlertDialog.Builder(Forgetpw_Activity.this)
                                                .setMessage("此信箱未註冊!!")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }

                                }
                            });

                }
            }
        });
        go_signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Forgetpw_Activity.this, Login_Activity.class);
                startActivity(intent);
            }
        });
        //bottom_navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Intent searchIntent = new Intent();
                        searchIntent.setClass(Forgetpw_Activity.this, HomeActivity.class);
                        searchIntent.putExtra("id", 1);
                        startActivity(searchIntent);
                        break;
                    case R.id.action_recommend:
                        Intent recommendIntent = new Intent();
                        recommendIntent.setClass(Forgetpw_Activity.this, HomeActivity.class);
                        recommendIntent.putExtra("id", 2);
                        startActivity(recommendIntent);
                        break;
                    case R.id.action_tour:
                        Intent tourIntent = new Intent();
                        tourIntent.setClass(Forgetpw_Activity.this, HomeActivity.class);
                        tourIntent.putExtra("id", 3);
                        startActivity(tourIntent);
                        break;
                    case R.id.action_more:
                        Intent moreIntent = new Intent();
                        moreIntent.setClass(Forgetpw_Activity.this, HomeActivity.class);
                        moreIntent.putExtra("id", 4);
                        startActivity(moreIntent);
                        break;
                }
                return false;
            }
        });
    }


    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


