package com.example.attendance_master;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Calendar;


public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";
Button btnScanAdmin;
String username, date, time;
    private final ActivityResultLauncher<Intent> qrScannerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String status = result.getData().getStringExtra("attendance_status");
                    String message = result.getData().getStringExtra("message");



                    Log.d(TAG, "Attendance result - Status: " + status + ", Message: " + message);

                    if ("success".equals(status)) {
                        showAttendanceSuccessDialog(message, username, time, date);
                        Toast.makeText(this, "✅ " + message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "❌ Attendance failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

                View view =inflater.inflate(R.layout.fragment_admin, container, false);
                btnScanAdmin = view.findViewById(R.id.btn_scan_qrcode01);
                username = getArguments().getString("username");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
         date = String.format("%02d_%02d_%d", day, month, year); // e.g., "20_09_2025"
         time = String.format("%02d:%02d", hour, minute); // e.g., "22:35"

                btnScanAdmin.setOnClickListener(view1 -> StartQRScanner());
                
        return view;
    }

    private void StartQRScanner() {

        Intent intent = new Intent(getActivity(), QRScannerActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        qrScannerLauncher.launch(intent);
    }
}