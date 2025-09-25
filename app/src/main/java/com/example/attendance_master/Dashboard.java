package com.example.attendance_master;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.attendance_master.Util.ApiUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class Dashboard extends AppCompatActivity {
    AdminFragment adminFragment;
    WorkerFragment workerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        adminFragment = new AdminFragment();
        workerFragment = new WorkerFragment();

        String username = getIntent().getExtras().getString("checkUser");
        String password = getIntent().getExtras().getString("password");
        if("Worker".equals(username)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,workerFragment).commit();
        }else if("Admin".equals(username)){


            ApiUtil.getuserId(this,password, username, response -> {
                        String userId = null;
                        try {
                            if (response != null && "success".equals(response.optString("status"))) {
                                userId = ApiUtil.BASE_URL + response.optString("userId");
                                Bundle arg = new Bundle();
                                arg.putString("userId", userId);
                                arg.putString("username", username);
                                adminFragment.setArguments(arg);
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,adminFragment).commit();
                            } else {
                                Toast.makeText(Dashboard.this, "User ID not found or status not success", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error processing response", Toast.LENGTH_SHORT).show();
                        }


                    },
                    error -> {
                        Toast.makeText(Dashboard.this, "user ID is not found", Toast.LENGTH_SHORT).show();

                    });

        }
    }
}