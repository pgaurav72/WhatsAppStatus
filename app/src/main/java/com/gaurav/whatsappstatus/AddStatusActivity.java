package com.gaurav.whatsappstatus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.xml.sax.DTDHandler;

import java.util.Objects;

public class AddStatusActivity extends AppCompatActivity {

  private RecyclerView statusList;
  private ImageView statusImage;
  private ImageView updateLogo;
  public static final int GALLERY_REQUEST = 1;
  private DatabaseReference mDatabase;
  public StorageReference storageReference;
  public StorageReference profileReference;
  private DatabaseReference mDatabaseUser;
  private ProgressBar statusUpdateproBar;
  private DatabaseReference databaseReference;
  private DatabaseReference forPrivateImages;
  private StorageReference userStorageRef;
  private FirebaseAuth mAuth;
  private FirebaseUser mCurrentUser;
  private Uri uri;
  private String uid;
  private Uri profileUri;

  private ImageView userPic;
  private ImageView usersStatusList;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_status);

    updateLogo = findViewById(R.id.update_status_image);
    statusList = findViewById(R.id.status_list);
    statusList.setHasFixedSize(true);
    statusList.setLayoutManager(new GridLayoutManager(this,2));

    // ----------------------------------- Defining database -----------------------------------------------------
    mAuth = FirebaseAuth.getInstance();
    mCurrentUser = mAuth.getCurrentUser();
    storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    assert currentFirebaseUser != null;
    uid = currentFirebaseUser.getUid();
    mDatabase = FirebaseDatabase.getInstance().getReference().child("Single_users_images").child(uid);
    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users_status_images").child("Images");
    forPrivateImages = FirebaseDatabase.getInstance().getReference().child("Single_users_images");
    String key = databaseReference.getKey();

    // -------------------------------------------------------------------------------------------------------------------

  }

  // -------------------------------------To get image from phone ----------------------------------------------------
  public void statusImageFunction(View view) {
    Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
    gallaryIntent.setType("image/*");
    startActivityForResult(gallaryIntent,GALLERY_REQUEST);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK ){
      uri = data.getData();
      statusImage= findViewById(R.id.status_image);
      statusImage.setImageURI(uri);

    }
  }

  // ----------------- On UPDATE button click upload picture to firebase database ----------------------------------

  public void updateStatusOfUser(View view){
    if (uri == null){
      new AlertDialog.Builder(AddStatusActivity.this)
              .setTitle("Select Image")
              .setNeutralButton("Ok",null)
              .show();
    }
    else {
      FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
      assert currentFirebaseUser != null;
      uid = currentFirebaseUser.getUid();

      final StorageReference filepath = storageReference.child("Status_images").child(uri.getLastPathSegment());
      filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          final Uri downloadUri = taskSnapshot.getDownloadUrl();
          final DatabaseReference newPost = databaseReference.push();
          final DatabaseReference privateImages = forPrivateImages.child(uid).push();

          databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              FirebaseUser firebaseUser = mAuth.getCurrentUser();
              assert firebaseUser != null;
              String uid = firebaseUser.getUid();

              newPost.child("users_images").setValue(Objects.requireNonNull(downloadUri).toString());
              privateImages.child("private_images").setValue(downloadUri.toString());
            } @Override
            public void onCancelled(DatabaseError databaseError) {

            }
          });
        }
      })
              .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                  updateLogo.setClickable(false);
                  Toast.makeText(AddStatusActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                  statusUpdateproBar = findViewById(R.id.status_update_probar);
                  statusUpdateproBar.setVisibility(View.VISIBLE);
                  double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                  int currentProgress = (int) progress;
                  statusUpdateproBar.setProgress(currentProgress);
                }
              }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
          statusUpdateproBar.setVisibility(View.INVISIBLE);
          updateLogo.setClickable(true);
          Toast.makeText(AddStatusActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
        }
      });
    }


  }

  //---------------------------------------------------------------------------------------------------------------------
  //----------------------------- To get the user uploaded containt from firebase database ------------------------------


  @Override
  protected void onStart() {
    super.onStart();
    FirebaseRecyclerAdapter<Statuslist, statusViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Statuslist, statusViewHolder>( Statuslist.class,
            R.layout.useruploads,
            statusViewHolder.class,
            mDatabase) {


      @NonNull
      @Override
      public statusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_list, parent, false);
        return new statusViewHolder(view);
      }

      @Override
      protected void populateViewHolder(statusViewHolder viewHolder, Statuslist model, int position) {
       viewHolder.setPrivate_images(getApplicationContext(),model.getPrivate_images());
      }

    };
    firebaseRecyclerAdapter.startListening();
    statusList.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();
  }

  public static class statusViewHolder extends RecyclerView.ViewHolder {
    String private_images;
    public String uid;

    ImageView usersStatusList;

    public statusViewHolder(View itemView) {
      super(itemView);
      usersStatusList = itemView.findViewById(R.id.user_status_id);
    }


      public void setPrivate_images(Context context, String private_images){
        this.private_images = private_images;
          Picasso.get().load(private_images).into(usersStatusList);

      }

  }

}

