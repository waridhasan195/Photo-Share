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

public class CreateAccount extends AppCompatActivity {

    private Button register,signin;
    private EditText remail,rpasswod,rconfirmpassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_account);
        mAuth=FirebaseAuth.getInstance();

        register=(Button) findViewById(R.id.rbtnid);
        signin=(Button) findViewById(R.id.rsigninbtnid);

        remail=(EditText) findViewById(R.id.remailid);
        rpasswod=(EditText) findViewById(R.id.rpasswordid);
        rconfirmpassword=(EditText)findViewById(R.id.rconfirmpasswordid);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usercreate();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreateAccount.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    public void usercreate(){
        String email=remail.getText().toString();
        String password=rpasswod.getText().toString();

        String confirmpassword=rconfirmpassword.getText().toString();

        if(password.equals(confirmpassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(CreateAccount.this, ProfileSetting.class);
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(CreateAccount.this, "Registered Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{

            Toast.makeText(CreateAccount.this, "Password Don't Match ", Toast.LENGTH_LONG).show();
        }

    }
}
