package com.gaurav.whatsappstatus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.List;
import java.util.Objects;

public class StatusActivity extends AppCompatActivity {

  String users_images;
  private RecyclerView statusList;
  public static final int GALLERY_REQUEST = 1;
  private DatabaseReference mDatabase;
  public StorageReference storageReference;
  public StorageReference profileReference;
  private DatabaseReference mDatabaseUser;
  private ProgressBar statusUpdateproBar;
  private DatabaseReference databaseReference;
  private StorageReference userStorageRef;
  private FirebaseAuth mAuth;
  private Button updateStatusButton;
  private FirebaseUser mCurrentUser;
  private Uri uri;
  private String uid;
  private Uri profileUri;
  ImageView allUserImage;

  private ImageView userPic;
  private int n = 0;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status);



    statusList = findViewById(R.id.status_list);
    statusList.setHasFixedSize(true);
    statusList.setLayoutManager(new GridLayoutManager(this,3));

    // ----------------------------------- Defining database ------------------------------------------------------
    mAuth = FirebaseAuth.getInstance();
    mCurrentUser = mAuth.getCurrentUser();
    storageReference = FirebaseStorage.getInstance().getReference();

    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users_status_images");
    String key = databaseReference.getKey();
    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users_status_images").child("Images");
// ----------------------------------------------------------------------------------------------------------

  }
  //---------------------------To get status images uploaded by all users ------------------------------------


  @Override
  protected void onStart() {
    super.onStart();

    FirebaseRecyclerAdapter<Statuslist, StatusActivity.StatusListViewHolder> FBRA = new FirebaseRecyclerAdapter<Statuslist, StatusActivity.StatusListViewHolder>(
            Statuslist.class,
            R.layout.useruploads,
            StatusListViewHolder.class,
            mDatabase
    ) {

      @Override
      protected void populateViewHolder(StatusListViewHolder viewHolder, Statuslist model, int position) {
        viewHolder.setUsers_images(getBaseContext(),model.getUsers_images());
      }



    };
    FBRA.startListening();
    statusList.setAdapter(FBRA);
    FBRA.notifyDataSetChanged();
  }

  public static class StatusListViewHolder extends RecyclerView.ViewHolder {
    String users_images;
    public String uid;
    ImageView allUserImage;


    public StatusListViewHolder(View itemView) {
      super(itemView);
      allUserImage = itemView.findViewById(R.id.all_users_status);

    }


    void setUsers_images(Context c, String users_images) {
      this.users_images = users_images;
        Picasso.get().load(users_images).into(allUserImage);

    }
  }






  //--------------------------------------------- For Menu -------------------------------------------------------
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu,menu);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();
    if (id == R.id.user_account) {
      Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
      startActivity(intent);
    }
    if (id == R.id.signOut){
      FirebaseAuth.getInstance().signOut();
      Intent toLogIn = new Intent(getApplicationContext(),LogInActivity.class);
      startActivity(toLogIn);
    }
    if (id == R.id.add_status){
      Intent toAddStatus = new Intent(getApplicationContext(),AddStatusActivity.class);
      startActivity(toAddStatus);
    }

    return super.onOptionsItemSelected(item);
  }

//--------------------------------------------------------------------------------------------------------------------------

  //------------------------------------ On back button press exit ----------------------------------------------------------
  private Boolean exit = false;
  @Override
  public void onBackPressed() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
    System.exit(0);

  }

  //-------------------------------------------------------------------------------------------------------------------------
}
