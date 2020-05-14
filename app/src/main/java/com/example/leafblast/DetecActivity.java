package com.example.leafblast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetecActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button chooseImage;
    private Button detec;
    private EditText fileName;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseLocation;
    private StorageTask uploadTask;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detec);

        chooseImage = findViewById(R.id.choose);
        detec = findViewById(R.id.detec);
        fileName = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        databaseLocation = FirebaseDatabase.getInstance().getReference("googleMap");



        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        detec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(DetecActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        backButton();

        databaseLocation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String date = dataSnapshot.child("date").getValue(String.class);
                String latitude = dataSnapshot.child("latitude").getValue(String.class);
                String longitude = dataSnapshot.child("longitude").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                Log.e("value", "onChildAdded: "+latitude+","+longitude+","+date+","+status);
                id++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void fetchLastLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]
//                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
//            return;
//        }
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null){
//                    currentLocation = location;
//                }
//            }
//        });
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case REQUEST_CODE:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    fetchLastLocation();
//                }
//                break;
//        }
//    }


    public void uploadLocation(final String status){
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    LocalDate date = LocalDate.now();
                    LongLa longLa = new LongLa(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()),String.valueOf(date),String.valueOf(status));
//                    String longLaID = databaseLocation.push().getKey();
                    databaseLocation.child(String.valueOf(id)).setValue(longLa);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    public String encodeBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        Log.i("base64", Base64.encodeToString(imageBytes, Base64.DEFAULT));
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(DetecActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();

                            ImageView imageView = findViewById(R.id.imageView);
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                            final String imageString = encodeBase64(bitmap);
                            Log.i("tum","img base64: "+imageString);
                            RequestQueue queue = Volley.newRequestQueue(DetecActivity.this);
                            String url ="https://us-central1-light-willow-261108.cloudfunctions.net/leafBlastFunc";

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data",imageString );
//                            Log.i("params", String.valueOf(params));
                            JSONObject param = new JSONObject(params);
                            final ProgressDialog progressDialog = new ProgressDialog(DetecActivity.this);

                            JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                                    new Response.Listener<JSONObject>()
                                    {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.i("tum","response :"+response);
                                            try {
                                                String prediction = String.valueOf(response.get("predictions"));
                                                Log.i("tum","prediction :"+prediction);
                                                // for stop progress dialog
                                                progressDialog.dismiss();
                                                if(prediction.equals("Healthy")){
                                                    AlertDialog alertDialog = new AlertDialog.Builder(DetecActivity.this).create();
                                                    alertDialog.setTitle("เป็นโรคหรือไม่");
                                                    alertDialog.setMessage("ไม่เป็น");
                                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Log.d("ii", "no: ");
                                                                    dialog.dismiss();
                                                                    View decorView = getWindow().getDecorView();
                                                                    decorView.setSystemUiVisibility(
                                                                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                                    );
                                                                }
                                                            });
                                                    alertDialog.show();
                                                    uploadLocation("0");
                                                }else{
                                                    AlertDialog alertDialog = new AlertDialog.Builder(DetecActivity.this).create();
                                                    alertDialog.setTitle("เป็นโรคหรือไม่");
                                                    alertDialog.setMessage("เป็น\nคำแนะนำเบื้องต้น : \n -ควรแบ่งแปลงให้มีพื้นที่ถ่ายเทอากาศได้ดี ไม่อับลม\n -พ่นสารป้องกันกำจัดโรคพืช คาซูกาไมซิน อีดิเฟนฟอส ไตรไซคลาโซล");
                                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Log.d("ii", "no: ");
                                                                    dialog.dismiss();
                                                                    View decorView = getWindow().getDecorView();
                                                                    decorView.setSystemUiVisibility(
                                                                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                                    );
                                                                }
                                                            });
                                                    alertDialog.show();
                                                    uploadLocation("1");
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.i("tum","error :"+error.toString());
                                        }
                                    });
                            strRequest.setRetryPolicy(new RetryPolicy() {
                                @Override
                                public int getCurrentTimeout() {
                                    return 50000;
                                }
                                @Override
                                public int getCurrentRetryCount() {
                                    return 50000;
                                }
                                @Override
                                public void retry(VolleyError error) throws VolleyError {

                                }
                            });
                            queue.add(strRequest);
                            // for start progress dialog
                            progressDialog.setMessage("loading");
                            progressDialog.show();

                            Detection detection = new Detection(fileName.getText().toString().trim(),
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId = databaseRef.push().getKey();
                            databaseRef.child(uploadId).setValue(detection);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetecActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void backButton() {
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
