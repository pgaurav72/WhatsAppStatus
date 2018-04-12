package com.gaurav.whatsappstatus;

import android.content.Context;
import android.content.Intent;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {


  public static final int GALLERY_REQUEST = 2;
  private Uri photoUri;
  private ImageView profileImageView;
  private ProgressBar profiePicProBar;
  private DatabaseReference mDatabaseUser;
  private Button profileUploadBtn;

  public StorageReference storageReference;
  private FirebaseDatabase database;
  private DatabaseReference databaseReference;
  private FirebaseAuth mAuth;
  private FirebaseUser mCurrentUser;
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  String uid = user.getUid();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);

    profileImageView = findViewById(R.id.user_profile_image);
    profiePicProBar = findViewById(R.id.profile_pic_pro_bar);
    profileUploadBtn = findViewById(R.id.profile_upload_btn);
    mAuth = FirebaseAuth.getInstance();
    mCurrentUser = mAuth.getCurrentUser();
    mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

    StorageReference profileReference = FirebaseStorage.getInstance().getReference().child(uid).child("users_profile_images");
    profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri profileUri) {
      Picasso.get().load(profileUri).into(profileImageView);
     }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(ProfileActivity.this, "Upload a profile picture", Toast.LENGTH_SHORT).show();
      }
    });




    storageReference = FirebaseStorage.getInstance().getReference();
    databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersProfile");

  }

  public void imageChooserFunction(View view){
    Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
    gallaryIntent.setType("image/*");
    startActivityForResult(gallaryIntent,GALLERY_REQUEST);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK ){

      photoUri = data.getData();
      profileImageView = findViewById(R.id.user_profile_image);
      profileImageView.setImageURI(photoUri);

    }
  }


  public void uploadProfilePic(View view){

    if (photoUri == null){
      new AlertDialog.Builder(ProfileActivity.this)
              .setTitle("Select a profile picture")
              .setNeutralButton("Ok", null)
              .show();
    }
    else {

      StorageReference filepath = storageReference.child(uid).child("users_profile_images");
      filepath.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
           Uri downloadUri = taskSnapshot.getDownloadUrl();
           DatabaseReference newPost = databaseReference;
          assert downloadUri != null;
          newPost.child(uid).child("profile_image").setValue(downloadUri.toString());
          newPost.child(uid).child("uid").setValue(uid);

        }
      }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
          profileUploadBtn.setClickable(false);
          double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
          int currentProgress = (int) progress;
          profiePicProBar.setProgress(currentProgress);

        }
      }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
          profileUploadBtn.setClickable(true);
          profiePicProBar.setVisibility(View.INVISIBLE);
          Toast.makeText(ProfileActivity.this, "Profile pic uploaded!", Toast.LENGTH_SHORT).show();
        }
      });

    }
  }


}
