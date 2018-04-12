package com.gaurav.whatsappstatus;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

  private Handler mHandler;
  private long delay = 2000;
  private int i = 0;
  private TextView welcomeTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);

    welcomeTextView = findViewById(R.id.welcomeTextView);
    Timer timer = new Timer();
    timer.schedule(task, delay);
  }

  TimerTask task = new TimerTask() {
    @Override
    public void run() {
      Intent toLogInActivity = new Intent(getApplicationContext(),LogInActivity.class);
      startActivity(toLogInActivity);
      finish();
    }
  };
}
