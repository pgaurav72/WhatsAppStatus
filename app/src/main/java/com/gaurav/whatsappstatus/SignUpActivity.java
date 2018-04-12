package com.gaurav.whatsappstatus;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

  private EditText userNameEditText;
  private EditText emailEditText;
  private EditText passEditText;
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;
  private String uid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);

    userNameEditText = findViewById(R.id.NameEditText);
    emailEditText = findViewById(R.id.emailEditText);
    passEditText = findViewById(R.id.passEditText);
    mAuth = FirebaseAuth.getInstance();


  }


  public void toLogInPage(View view) {
    Intent toLogIn = new Intent(getApplicationContext(), LogInActivity.class);
    startActivity(toLogIn);
  }

  public void signUpFunction(View view) {
    final String name = userNameEditText.getText().toString().trim();
    final String email = emailEditText.getText().toString().trim();
    final String password = passEditText.getText().toString().trim();

    if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
      new AlertDialog.Builder(SignUpActivity.this)
              .setTitle("Enter Name,Email & Password")
              .setNeutralButton("Ok", null)
              .show();
    }
    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
      mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()) {

            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String uid = firebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(uid).child("Name");
            DatabaseReference userInfo = mDatabase;
            userInfo.child(uid).setValue(name);

            String text = emailEditText.getText().toString().trim();
            Intent toLogInActivity = new Intent(getApplicationContext(), LogInActivity.class);
            toLogInActivity.putExtra("email", text);
            startActivity(toLogInActivity);

          } else if (!task.isSuccessful()) {
            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
              new AlertDialog.Builder(SignUpActivity.this)
                      .setTitle("User already exits!\nLog in below")
                      .setNeutralButton("Log In", null)
                      .show();
            }
          } else {
            Exception exception = task.getException();
            assert exception != null;
            Log.i("Error", exception.getMessage());

          }
        }
      });
    }

  }

}
