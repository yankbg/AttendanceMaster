package com.example.attendance_master;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendance_master.Util.ApiUtil;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Calendar;


public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";
Button btnScanAdmin;
String username, date, time, userId;
    private final ActivityResultLauncher<Intent> qrScannerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String status = result.getData().getStringExtra("attendance_status");
                    String message = result.getData().getStringExtra("message");



                    Log.d(TAG, "Attendance result - Status: " + status + ", Message: " + message);

                    if ("success".equals(status)) {
                        showAttendanceSuccessDialog(message, username, userId, time, date);
                        Toast.makeText(Dashboard.this, "✅ " + message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Dashboard.this, "❌ Attendance failed", Toast.LENGTH_LONG).show();
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
                userId = getArguments().getString("userId");

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
        intent.putExtra("userId",userId);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        qrScannerLauncher.launch(intent);
    }

    private void showAttendanceSuccessDialog(String message, String username, String userId, String time, String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Attendance Marked Successfully!");

        // Inflate a custom layout containing TextViews and an ImageView
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_attendance_success, null);

        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvId = dialogView.findViewById(R.id.tvId);
        TextView tvtime = dialogView.findViewById(R.id.tvTime);
        TextView tvdate = dialogView.findViewById(R.id.tvDate);
        ImageView ivStudentImage = dialogView.findViewById(R.id.ivStudentImage);

        tvMessage.setText(message);
        tvName.setText("Username: " + username);
        tvId.setText("User ID: " + userId);
        tvdate.setText("Date: " +date);
        tvtime.setText("Time: " +time);


        ApiUtil.getStudentInfo(this, userId, username,
                response -> {
                    String imageUrl = null;
                    if ("success".equals(response.optString("status"))) {
                        imageUrl = ApiUtil.BASE_URL + response.optString("image_path");

                    }
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error)
                                .into(ivStudentImage);
                    } else {
                        ivStudentImage.setImageResource(R.drawable.download);
                    }

                    builder.setView(dialogView);
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.show();
                },
                error -> {
                    // Fallback: Show dialog without image if image fetch fails
                    String urlerror = "C:\\Users\\HP\\AndroidStudioProjects\\MyApplication2\\app\\src\\main\\res\\drawable\\error2.jpeg";

                    if (urlerror != null && !urlerror.isEmpty()) {
                        Glide.with(this)
                                .load(urlerror)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error)
                                .into(ivStudentImage);
                    } else {
                        ivStudentImage.setImageResource(R.drawable.download);
                    }

                    builder.setView(dialogView);
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.show();
                }
        );


    }
}