package com.go.go;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class Login_Activity extends AppCompatActivity {
    Button registerButton;
    Button signinButton;
    Button forgetButton;
    EditText edtemail;
    EditText edtpassword;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener = new AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) { // User is signed in
                Log.d("TAG",user.getUid());
            } else {  // User is signed out
                Log.d("Tag","user ==null");
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgetButton = findViewById(R.id.forgetPassword);
        registerButton = findViewById(R.id.login_register);
        signinButton = findViewById(R.id.login_signin);
        edtemail = findViewById(R.id.accountLogin);
        edtpassword = findViewById(R.id.passwordLogin);
        mAuth = getInstance();
        //bottom_navigation
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Intent searchIntent = new Intent();
                        searchIntent.setClass(Login_Activity.this, HomeActivity.class);
                        searchIntent.putExtra("id", 1);
                        startActivity(searchIntent);
                        break;
                    case R.id.action_recommend:
                        Intent recommendIntent = new Intent();
                        recommendIntent.setClass(Login_Activity.this, HomeActivity.class);
                        recommendIntent.putExtra("id", 2);
                        startActivity(recommendIntent);
                        break;
                    case R.id.action_tour:
                        Intent tourIntent = new Intent();
                        tourIntent.setClass(Login_Activity.this, HomeActivity.class);
                        tourIntent.putExtra("id", 3);
                        startActivity(tourIntent);
                        break;
                    case R.id.action_more:
                        Intent moreIntent = new Intent();
                        moreIntent.setClass(Login_Activity.this, HomeActivity.class);
                        moreIntent.putExtra("id", 4);
                        startActivity(moreIntent);
                        break;
                }
                return false;
            }
        });
        forgetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login_Activity.this , Forgetpw_Activity.class);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login_Activity.this , Register_Activity.class);
                startActivity(intent);
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtemail.getText().toString().isEmpty() || edtpassword.getText().toString().isEmpty()){
                    Log.d("TAG", "信箱和密碼不能為空白!");
                    new AlertDialog.Builder(Login_Activity.this)
                            .setMessage("信箱和密碼不能為空白!")
                            .setPositiveButton("OK",null)
                            .show();
                }
                else {
                    final String email = edtemail.getText().toString();
                    final String password = edtpassword.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "登入成功!");
                                        Toast.makeText(Login_Activity.this, email + "登入成功 ", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent();
                                        intent.setClass(Login_Activity.this, HomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.d("TAG", "此信箱未註冊!");
                                        new AlertDialog.Builder(Login_Activity.this)
                                                .setMessage("此信箱未註冊!!")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }
                            });

            }
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


