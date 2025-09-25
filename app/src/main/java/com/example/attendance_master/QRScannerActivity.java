package com.example.attendance_master;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.attendance_master.Util.ApiUtil;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class QRScannerActivity extends AppCompatActivity {
    private static final String TAG = "QRScannerActivity";
    String username, date, time, userId;
    //camera permission launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startQRScanner();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan QR codes",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            });
    // QR Scanner launcher
    private final ActivityResultLauncher<ScanOptions> qrScannerLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String qrData = result.getContents();
                    Log.d(TAG, "QR Code scanned: " + qrData);
                    handleQRResult(qrData);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        username = getIntent().getStringExtra("username");
        userId = getIntent().getExtras().getString("userId");
        time = getIntent().getExtras().getString("time");
        date = getIntent().getExtras().getString("date");
        // Check camera permission
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startQRScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);

        }
    }

    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan  QR Code for Attendance");
        options.setCameraId(0);  // Use rear camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setTimeout(30000);
        qrScannerLauncher.launch(options);
    }
    private void handleQRResult(String qrData) {
        try {
            JSONObject qrJson = new JSONObject(qrData);

            // Validate required fields
            String[] requiredFields = {"TimeOn", "Date"};
            for (String field : requiredFields) {
                if (!qrJson.has(field)) {
                    Toast.makeText(this, "Invalid QR code: missing " + field, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }

            Toast.makeText(this, "QR Code scanned successfully", Toast.LENGTH_SHORT).show();

            // Mark attendance with parsed data
            markAttendanceData(qrJson.toString());

        } catch (JSONException e) {
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void markAttendanceData(String qrData) {
        // Show loading message
        Toast.makeText(this, "Marking attendance...", Toast.LENGTH_SHORT).show();

        // Call your API to mark attendance
        ApiUtil.markAttendance(this, username, userId, time, date, qrData,
                response -> {
                    Log.d(TAG, "Attendance response: " + response.toString());

                    if ("success".equals(response.optString("status"))) {
                        // Get class info from response for a more personalized message
                        String message = response.optString("message", "Thank you for attending!");

                        Toast.makeText(this, "✅ " + message, Toast.LENGTH_LONG).show();

                        // Return to Home activity with success result
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("attendance_status", "success");
                        resultIntent.putExtra("message", message);


                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        String errorMessage = response.optString("message", "Attendance failed");
                        Toast.makeText(this, "❌ " + errorMessage, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                error -> {
                    Log.e(TAG, "Attendance error: " + error.getMessage());
                    String errorMsg = "Network error occurred";

                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error response: " + new String(error.networkResponse.data));

                        // Try to parse error response
                        try {
                            JSONObject errorResponse = new JSONObject(new String(error.networkResponse.data));
                            errorMsg = errorResponse.optString("message", errorMsg);
                        } catch (JSONException e) {
                            Log.e(TAG, "Could not parse error response");
                        }
                    }

                    Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                    finish();
                }
        );
    }
}