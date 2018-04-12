package com.gaurav.whatsappstatus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity  {

  private EditText logEmailEditText;
  private EditText logPassEditText;
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;


  public void toSignUpActivity(View view){
    Intent tosignUpPage = new Intent(getApplicationContext(),SignUpActivity.class);
    startActivity(tosignUpPage);

  }

  public void logInFunction(View view){
    String logEmail = logEmailEditText.getText().toString().trim();
    String logPass = logPassEditText.getText().toString().trim();
    if (TextUtils.isEmpty(logEmail) && TextUtils.isEmpty(logPass) || TextUtils.isEmpty(logEmail) || TextUtils.isEmpty(logPass)){
      new AlertDialog.Builder(LogInActivity.this)
              .setTitle(" Enter Email & Password! ")
              .setNeutralButton("Ok",null)
              .show();
    }
    else if (!TextUtils.isEmpty(logEmail) && !TextUtils.isEmpty(logPass)){
      mAuth.signInWithEmailAndPassword(logEmail,logPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()){
            checkUserExists();
          }
          else if (!task.isSuccessful()){
            new AlertDialog.Builder(LogInActivity.this)
                    .setTitle("Log in failed!\nEmail or Password incorrect. ")
                    .setNeutralButton("Retry", null)
                    .show();
          }
        }
      });

    }
  }

  public void checkUserExists(){
    final String userId = mAuth.getCurrentUser().getUid();
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild(userId)){
          Intent toStatusActivity= new Intent(getApplicationContext(),StatusActivity.class);
          startActivity(toStatusActivity);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log_in);



    logEmailEditText = findViewById(R.id.logEmailEditText);
    logPassEditText = findViewById(R.id.logPassEditText);


    mAuth = FirebaseAuth.getInstance();
    if (mAuth.getCurrentUser() != null){
      Intent logedIn = new Intent(getApplicationContext(),StatusActivity.class);
      startActivity(logedIn);
    }
    mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");

    Bundle emailFromSignUp = getIntent().getExtras();
    if(emailFromSignUp != null) {
      String text = emailFromSignUp.getString("email");
      logEmailEditText.setText(text);
    }
  }

  //----------------------------------------Authentication------------------------------------------





  //--------------------------------------------------------------------------------------------
  //-------------------------------------------Exit on back start-------------------------------------
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
  //--------------------------------------- Exit on back end -----------------------------------------------




}
