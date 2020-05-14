package com.example.leafblast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    JSONObject data = null;

    Location currenLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference googleMapRef;
    Marker marker;
    List<Marker> list = new ArrayList<>();
    List<Marker> list1 = new ArrayList<>();
    List<Marker> list2 = new ArrayList<>();
    List<Marker> list3 = new ArrayList<>();
    List<Marker> list4 = new ArrayList<>();
    NumberPicker numberPicker;
    GoogleMap googleMap;
    Button button;
    GoogleMap newMap;
    Button newButton;
    TextView textView3;
    TextView tempView;
    // device sensor manager
    private SensorManager SensorManage;
    // define the compass picture that will be use
    private ImageView compassimage;
    // record the angle turned of the compass picture
    private float DegreeStart;
    String tempString;
    TextView windspView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(4);
        numberPicker.setDisplayedValues(new String[] {"3" , "7" ,"15" , "30"});
        googleMapRef = rootRef.child("googleMap");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText("Show Each Month");
        tempView = (TextView) findViewById(R.id.tempView);
        windspView = (TextView) findViewById(R.id.windspView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("nbpk", "onClick: " + numberPicker.getValue());
                // this code delete all marker in map
                if(numberPicker.getValue() == 1){
                    //set all false
                    for (Marker mk: list) {
                        mk.setVisible(false);
                    }
                    //set 1m
                    for (Marker mk: list1){
                        mk.setVisible(true);
                        mk.showInfoWindow();
                    }
                    //set 2m
                    for(Marker mk: list2){
                        mk.setVisible(false);
                    }
                    //set 3m
                    for (Marker mk: list3) {
                        mk.setVisible(false);
                    }
                    //set 30
                    for (Marker mk: list4){
                        mk.setVisible(false);
                    }
                }
                if(numberPicker.getValue() == 2){
                    //set all false
                    for (Marker mk: list) {
                        mk.setVisible(false);
                    }
                    //set 1m
                    for (Marker mk: list1){
                        mk.setVisible(false);
                    }
                    //set 2m
                    for(Marker mk: list2){
                        mk.setVisible(true);
                        mk.showInfoWindow();

                    }
                    //set 3m
                    for (Marker mk: list3) {
                        mk.setVisible(false);
                    }
                    //set 30
                    for (Marker mk: list4){
                        mk.setVisible(false);
                    }
                }
                if(numberPicker.getValue() == 3){
                    //set all false
                    for (Marker mk: list) {
                        mk.setVisible(false);
                    }
                    //set 1m
                    for (Marker mk: list1){
                        mk.setVisible(false);
                    }
                    //set 2m
                    for(Marker mk: list2){
                        mk.setVisible(false);
                    }
                    //set 3m
                    for (Marker mk: list3) {
                        mk.setVisible(true);
                        mk.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mk.showInfoWindow();
                    }
                    //set 30
                    for (Marker mk: list4){
                        mk.setVisible(false);
                    }
                }
                if(numberPicker.getValue() == 4){
                    //set all false
                    for (Marker mk: list) {
                        mk.setVisible(false);
                    }
                    //set 1m
                    for (Marker mk: list1){
                        mk.setVisible(false);
                    }
                    //set 2m
                    for(Marker mk: list2){
                        mk.setVisible(false);
                    }
                    //set 3m
                    for (Marker mk: list3) {
                        mk.setVisible(false);
                    }
                    //set 30
                    for (Marker mk: list4){
                        mk.setVisible(true);
                        mk.showInfoWindow();
                    }
                }

            }
        });



        newButton = findViewById(R.id.button3);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Marker mk: list) {
                    mk.setVisible(true);
                }
                //set 1m
                for (Marker mk: list1){
                    mk.setVisible(false);
                }
                //set 2m
                for(Marker mk: list2){
                    mk.setVisible(false);
                }
                //set 3m
                for (Marker mk: list3) {
                    mk.setVisible(false);
                    mk.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mk.showInfoWindow();
                }
            }
        });

        //
        compassimage = (ImageView) findViewById(R.id.compass_image);
        // TextView that will display the degree
        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);


    }
    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
//        DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassimage.startAnimation(ra);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currenLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    public void find_weather(){
//        String url = "https://api.openweathermap.org/data/2.5/weather?lat=13&lon=100&appid=48ead9e10254133f065e6d40200d2fbf";

        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+currenLocation.getLatitude()+"&lon="+currenLocation.getLongitude()+"&appid=48ead9e10254133f065e6d40200d2fbf";
//        LatLng latLng = new LatLng(currenLocation.getLatitude(),currenLocation.getLongitude());


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    JSONObject wind = response.getJSONObject("wind");


                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");
                    String windSpeed = String.valueOf(wind.getDouble("speed"));
                    String windDeg = String.valueOf(wind.getInt("deg"));

//                    Log.d("ii", "temp: " + temp);
                    Log.d("ii", "description: " + description);
                    Log.d("ii", "city: " + city);
                    Log.d("ii", "windSpeed: " + windSpeed);
                    Log.d("ii", "windDeg: " + windDeg);

                    //set data
                    DegreeStart = Float.parseFloat(windDeg);

                    double temp_int = Double.parseDouble(temp);
                    double centi = temp_int -271;
                    int i = (int)centi;
                    //set temp
                    tempString = String.valueOf(i);
                    Log.d("ii", "tempC: " + tempString);
                    tempView.setText(tempString + "°C");
                    windspView.setText("แรงลม "+windSpeed);



                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );



        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng latLng = new LatLng(currenLocation.getLatitude(),currenLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18.0f));
        googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//        MarkerOptions markerOptions1 = new MarkerOptions().position(new LatLng(13.9500000,100.4000000)).title("2019-12-12");
//        googleMap.addMarker(markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        //call api to find wind direction




        //


        // ปักมาร์คเพิ่มตรงนี้
        ValueEventListener valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String longtitudeDB = ds.child("longitude").getValue(String.class);
                    String latitudeDB = ds.child("latitude").getValue(String.class);
                    String dateDB = ds.child("date").getValue(String.class);
                    Log.d("ii", dateDB + " / " + longtitudeDB  + " / " + latitudeDB);

                    LocalDate dateInDB = LocalDate.parse(dateDB);
                    int minusNum = numberPicker.getValue();
                    LocalDate dateNow = LocalDate.now();
                    // can get date minus
                    LocalDate dateChange1 = dateNow.minusDays(3);
                    LocalDate dateChange2 = dateNow.minusDays(7);
                    LocalDate dateChange3 = dateNow.minusDays(15);
                    LocalDate dateChange4 = dateNow.minusDays(30);

                    Log.d("kk", "dateSet: " + dateInDB.toString());
                    Log.d("kk", "dateChange: " + dateChange1.toString());
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(latitudeDB)
                                    , Double.valueOf(longtitudeDB)))
                            .title(dateDB));
                    list.add(marker);

                    if(dateInDB.isAfter(dateChange1)){
                        Log.d("mm", "list 1 date minusMounths " + dateChange1);
                        Log.d("mm", "list 1is After :  " + dateInDB.toString() );
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(latitudeDB)
                                        , Double.valueOf(longtitudeDB)))
                                .title(dateDB));
                        list1.add(marker);
                    }
                    if(dateInDB.isAfter(dateChange2)){
                        Log.d("mm", "list 2 date minusMounths " + dateChange1);
                        Log.d("mm", "list 2 is After :  " + dateInDB.toString() );
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(latitudeDB)
                                        , Double.valueOf(longtitudeDB)))
                                .title(dateDB));
                        list2.add(marker);
                    }
                    if(dateInDB.isAfter(dateChange3)){
                        Log.d("mm", "list 3 date minusMounths " + dateChange1);
                        Log.d("mm", "list 3 is After :  " + dateInDB.toString() );
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(latitudeDB)
                                        , Double.valueOf(longtitudeDB)))
                                .title(dateDB));
                        list3.add(marker);
                    }
                    if(dateInDB.isAfter(dateChange4)){
                        Log.d("mm", "list 4 date minusMounths " + dateChange1);
                        Log.d("mm", "list 4 is After :  " + dateInDB.toString() );
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(latitudeDB)
                                        , Double.valueOf(longtitudeDB)))
                                .title(dateDB));
                        list4.add(marker);
                    }
                }
                callLocation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        googleMapRef.addListenerForSingleValueEvent(valueEventListener);
        find_weather();

    }
    public void callLocation(){
        for (Marker mk: list) {
            mk.showInfoWindow();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }
}
