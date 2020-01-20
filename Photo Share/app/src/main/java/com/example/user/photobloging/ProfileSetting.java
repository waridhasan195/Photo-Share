package com.example.user.photobloging;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetting extends AppCompatActivity {
    private CircleImageView setupImage;
    private boolean isChanged = false;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private EditText nametext;

    private String user_Id;
    private Button savebutton;
    private FirebaseFirestore firebaseFirestore;

    private Uri mainImageURI=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting);

        setupImage=findViewById(R.id.circleimage);

        nametext=(EditText)findViewById(R.id.editText);

        savebutton=(Button)findViewById(R.id.button);

        firebaseAuth=FirebaseAuth.getInstance();

        storageReference=FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        user_Id=firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore.collection("Users").document(user_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                       String name =task.getResult().getString("Name");
                       String image =task.getResult().getString("Image");
                       mainImageURI = Uri.parse(image);
                       nametext.setText(name);
                       RequestOptions placeholderRequest = new RequestOptions();
                       placeholderRequest.placeholder(R.drawable.default_image);
                       Glide.with(ProfileSetting.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                    else {

                        Toast.makeText(ProfileSetting.this, "Data Dose not Exists ", Toast.LENGTH_LONG).show();
                    }
                }
                else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileSetting.this, "FIRESTORE Retrieve Error : " +error, Toast.LENGTH_LONG).show();
                }
            }
        });

        Toast.makeText(ProfileSetting.this, "Registered Succesfull", Toast.LENGTH_LONG).show();
        Toast.makeText(ProfileSetting.this, "Please Customize Your Account ", Toast.LENGTH_LONG).show();
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(ProfileSetting.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED ){
                        Toast.makeText(ProfileSetting.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ProfileSetting.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {

                        Toast.makeText(ProfileSetting.this, "Permission Accepted", Toast.LENGTH_LONG).show();
                        BringImagePicker();
                    }
                }
                else {

                    BringImagePicker();
                }
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = nametext.getText().toString();
                if(isChanged) {

                    if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                        user_Id = firebaseAuth.getCurrentUser().getUid();
                        StorageReference image_path = storageReference.child("Profile_Image").child(user_Id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name);
                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(ProfileSetting.this, "Image Error : " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else {

                    storeFirestore(null, user_name);
                }
            }
        });
    }
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {
        Uri download_uri;
        if(task != null){

            download_uri=task.getResult().getUploadSessionUri();
        }
        else {
            download_uri=mainImageURI;
        }
        Toast.makeText(ProfileSetting.this, "The Image is Uploaded", Toast.LENGTH_LONG).show();

        Map<String, String> usermap= new HashMap<>();

        usermap.put("Name", user_name);

        usermap.put("Image", download_uri.toString());

        firebaseFirestore.collection( "Users").document(user_Id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(ProfileSetting.this, "Setting Updated ", Toast.LENGTH_LONG).show();

                    Intent mainintent = new Intent(ProfileSetting.this, PhotoBlog.class);
                    startActivity(mainintent);
                    finish();
                }
                else {
                    String error = task.getException().getMessage();

                    Toast.makeText(ProfileSetting.this, "FIRESTORE  Error : " +error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()

                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfileSetting.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

               mainImageURI = result.getUri();
               setupImage.setImageURI(mainImageURI);
               isChanged =true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
