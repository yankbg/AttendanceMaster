package com.example.attendance_master;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Dashboard extends AppCompatActivity {
    AdminFragment adminFragment;
    WorkerFragment workerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        adminFragment = new AdminFragment();
        workerFragment = new WorkerFragment();

        String role = getIntent().getExtras().getString("checkUser");
        if("Worker".equals(role)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,workerFragment).commit();
        }else if("Admin".equals(role)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,adminFragment).commit();
        }
    }
}