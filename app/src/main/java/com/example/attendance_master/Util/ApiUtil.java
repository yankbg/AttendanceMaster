package com.example.attendance_master.Util;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtil {
    public static final String BASE_URL = "http://172.20.10.5/attendance_system/";
    private static final String TAG = "ApiUtil";

    public static void markAttendance(Context context, String username, String userId, String time, String date, String qrData,
                                      Response.Listener<JSONObject> success,
                                      Response.ErrorListener error) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                BASE_URL + "mark_attendance.php",
                response -> {
                    Log.d(TAG, "Attendance response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "Attendance error: " + volleyError.getMessage());
                    error.onErrorResponse(volleyError);
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", username);
                    jsonBody.put("userId", userId);
                    jsonBody.put("time", time);
                    jsonBody.put("date", date);
                    jsonBody.put("qr_data", qrData);
                    jsonBody.put("timestamp", System.currentTimeMillis() / 1000);

                    String body = jsonBody.toString();
                    Log.d(TAG, "Attendance request: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating attendance request: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }
    public static void getuserId(Context context, String password, String username,
                                      Response.Listener<JSONObject> success,
                                      Response.ErrorListener error) {

        String url = BASE_URL + "check_student";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "GetStudentInfo Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "GetStudentInfo error: " + volleyError.getMessage());
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Status code: " + volleyError.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(volleyError.networkResponse.data));
                    }
                    error.onErrorResponse(volleyError);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("password", password);
                    jsonBody.put("username", username);

                    String body = jsonBody.toString();
                    Log.d(TAG, "GetStudentInfo request body: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating GetStudentInfo request body: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }
    public static void getStudentInfo(Context context, String id, String name,
                                      Response.Listener<JSONObject> success,
                                      Response.ErrorListener error) {

        String url = BASE_URL + "check_student";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "GetStudentInfo Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        success.onResponse(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        error.onErrorResponse(new VolleyError("Invalid JSON response: " + response));
                    }
                },
                volleyError -> {
                    Log.e(TAG, "GetStudentInfo error: " + volleyError.getMessage());
                    if (volleyError.networkResponse != null) {
                        Log.e(TAG, "Status code: " + volleyError.networkResponse.statusCode);
                        Log.e(TAG, "Response data: " + new String(volleyError.networkResponse.data));
                    }
                    error.onErrorResponse(volleyError);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("studentId", id);
                    jsonBody.put("fullname", name);

                    String body = jsonBody.toString();
                    Log.d(TAG, "GetStudentInfo request body: " + body);
                    return body.getBytes("utf-8");
                } catch (Exception e) {
                    Log.e(TAG, "Error creating GetStudentInfo request body: " + e.getMessage());
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }

}
