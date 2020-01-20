package com.example.user.photobloging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login,createaccount;
    private FirebaseAuth auth;
    private DatabaseReference database ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=(EditText) findViewById(R.id.emailid);

        password=(EditText) findViewById(R.id.passwordid);
        login=(Button) findViewById(R.id.loginid);
        createaccount=(Button) findViewById(R.id.createaccountid);
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance().getReference("hello");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
                finish();
            }
        });
    }

/*
    public void createUser(){
        auth.createUserWithEmailAndPassword(email.getText().toString() , password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

*/

    public void loginUser(){
        auth.signInWithEmailAndPassword(email.getText().toString() , password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser userInfo = auth.getCurrentUser();
                            String UID = userInfo.getUid();
                            database.setValue(UID);

                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(MainActivity.this, PhotoBlog.class);

                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
