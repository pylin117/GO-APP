package com.go.go;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class Register_Activity extends AppCompatActivity {
    TextView mexistingAccount;
    Button mlogin_signup;
    EditText edtemail;
    EditText edtpassword;
    EditText edtconfirmPassword;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
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
        setContentView(R.layout.activity_register);
        mexistingAccount=findViewById(R.id.existingAccount);
        mlogin_signup = findViewById(R.id.login_signup);

        edtemail=findViewById(R.id.accountRigester);
        edtpassword=findViewById(R.id.passwordRigester);
        edtconfirmPassword = findViewById(R.id.confirmPasswordRigester);

        mAuth = FirebaseAuth.getInstance();

        mexistingAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Register_Activity.this , Login_Activity.class);
                startActivity(intent);
            }
        });

        mlogin_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(edtemail.getText().toString().isEmpty() || edtpassword.getText().toString().isEmpty() || edtconfirmPassword.getText().toString().isEmpty()){
                    Log.d("TAG", "信箱、密碼和確認密碼不能為空白!");
                    new AlertDialog.Builder(Register_Activity.this)
                            .setMessage("信箱、密碼和確認密碼不能為空白!")
                            .setPositiveButton("OK",null)
                            .show();
                }
                else {
                    if (edtpassword.getText().length() < 6) {
                        Log.d("TAG", "密碼長度不可小於六!");
                        new AlertDialog.Builder(Register_Activity.this)
                                .setMessage("密碼長度不可小於六!")
                                .setPositiveButton("OK",null)
                                .show();
                        edtpassword.setText("");
                        edtconfirmPassword.setText("");
                    } else {
                        final String email = edtemail.getText().toString();
                        final String password = edtpassword.getText().toString();
                        final String confirmpassword = edtconfirmPassword.getText().toString();
                        int ifequal = password.compareTo(confirmpassword);
                        if (ifequal == 0) { //密碼跟確認密碼相同
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            final String message;
                                            if (task.isSuccessful()){
                                                Log.d("TAG", "成功!");
                                                message = "註冊成功";

                                             }
                                            else{
                                                Log.d("TAG", "失敗!");
                                                message = "註冊失敗";
                                            }
                                            new AlertDialog.Builder(Register_Activity.this)
                                                    .setMessage(message)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if("註冊成功" == message){
                                                                Intent toMainPage=new Intent();
                                                                toMainPage.setClass(Register_Activity.this, HomeActivity.class);
                                                                startActivity(toMainPage);
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                        }
                        else{
                            Log.d("TAG", password);
                            Log.d("TAG", confirmpassword);
                            String message = "密碼與確認密碼不相同，請重新輸入!";
                            AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                            builder.setMessage(message);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    edtpassword.setText("");
                                    edtconfirmPassword.setText("");
                                }
                            });
                            builder.show();
                        }
                    }
                }

            }
        });
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Intent searchIntent = new Intent();
                        searchIntent.setClass(Register_Activity.this, HomeActivity.class);
                        searchIntent.putExtra("id", 1);
                        startActivity(searchIntent);
                        break;
                    case R.id.action_recommend:
                        Intent recommendIntent = new Intent();
                        recommendIntent.setClass(Register_Activity.this, HomeActivity.class);
                        recommendIntent.putExtra("id", 2);
                        startActivity(recommendIntent);
                        break;
                    case R.id.action_tour:
                        Intent tourIntent = new Intent();
                        tourIntent.setClass(Register_Activity.this, HomeActivity.class);
                        tourIntent.putExtra("id", 3);
                        startActivity(tourIntent);
                        break;
                    case R.id.action_more:
                        Intent moreIntent = new Intent();
                        moreIntent.setClass(Register_Activity.this, HomeActivity.class);
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
