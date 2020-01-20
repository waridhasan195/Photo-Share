package com.example.user.photobloging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PhotoBlog extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private BottomNavigationView mainbottomNav;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private FloatingActionButton floatingActionButton;
    private HomeFragment homeFragment;
    private NotiFicationFragment notiFicationFragment;
    private AccountFragment accountFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeFragment=new HomeFragment();
        notiFicationFragment= new NotiFicationFragment();
        accountFragment=new AccountFragment();

        setContentView(R.layout.activity_photo_blog);
        mainbottomNav=(BottomNavigationView)findViewById(R.id.mainBottomNav);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        floatingActionButton=(FloatingActionButton) findViewById(R.id.flotting);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhotoBlog.this, NewPost.class);
                startActivity(intent);
                finish();
            }
        });



        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment);
                        return true;

                    case R.id.bottom_action_account:
                        replaceFragment(accountFragment);
                        return true;


                    case R.id.bottom_action_notification:
                        replaceFragment(notiFicationFragment);
                        return true;

                        default:
                            return false;


                }




            }
        });



    }
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            sendtologin();
        }
        else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent setupIntent=new Intent(PhotoBlog.this, ProfileSetting.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    }
                    else {
                        String errorMessage =task.getException().getMessage();
                        Toast.makeText(PhotoBlog.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.action_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.account_setting){
            Toast.makeText(PhotoBlog.this,"Account Setting is Complete", Toast.LENGTH_LONG).show();
        }
        if(item.getItemId()==R.id.log_out){
            logout();
            Toast.makeText(PhotoBlog.this,"You are Log Out", Toast.LENGTH_LONG).show();
        }
        if(item.getItemId()==R.id.theme_change){
            Toast.makeText(PhotoBlog.this,"Theme change Complete", Toast.LENGTH_LONG).show();
        }
        if(item.getItemId()==R.id.profile_setting){

            Intent intent=new Intent(PhotoBlog.this,ProfileSetting.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        mAuth.signOut();
        sendtologin();
    }
    private void sendtologin() {
        Intent intent=new Intent(PhotoBlog.this,MainActivity.class);
        startActivity(intent);
        finish();
    }




    private void replaceFragment(Fragment fragment){


        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();


    }




}