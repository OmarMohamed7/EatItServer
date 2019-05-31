package com.example.rooot.eatit_server;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Common.DirectionJsonParser;
import com.example.rooot.eatit_server.Remote.IGeoCoordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.rooot.eatit_server.Common.CurrentUser.current_Request;
import static com.example.rooot.eatit_server.Common.CurrentUser.current_User;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener{

    private GoogleMap mMap;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private IGeoCoordinates mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        mServices = CurrentUser.getGeoCodeServices();

        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestRuntimePermission();

        }else{

            if(checkPlayServices()){

                buildGoogleClientApi();
                createLocationRequest();
            }
        }

        displayLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void requestRuntimePermission() {

        ActivityCompat.requestPermissions(this , new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        } , LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(checkPlayServices()){
                        buildGoogleClientApi();
                        createLocationRequest();

                        displayLocation();
                    }
                }
                break;
        }
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        //Check if google play service available, If google play service is not available
        // and error is recoverable then open a dialog to resolve an error.

        if(resultCode != ConnectionResult.SUCCESS){
            if(GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)){

                GooglePlayServicesUtil.getErrorDialog(resultCode,this ,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                Toast.makeText(this, "This device not supported !!", Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }

        return true;
    }

    protected synchronized void buildGoogleClientApi() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        /*
        PRIORITY_BALANCED_POWER_ACCURACY - Use this setting to request location precision to within
        a city block, which is an accuracy of approximately 100 meters.
        This is considered a coarse level of accuracy, and is likely to consume less power.
        With this setting, the location services are likely to use WiFi and cell tower positioning.
        Note, however, that the choice of location provider depends on many other factors,
        such as which sources are available.

        PRIORITY_HIGH_ACCURACY - Use this setting to request the most precise location possible.
        With this setting, the location services are more likely to use GPS to determine the location.


        PRIORITY_LOW_POWER - Use this setting to request city-level precision,
        which is an accuracy of approximately 10 kilometers.
        This is considered a coarse level of accuracy, and is likely to consume less power.

        PRIORITY_NO_POWER - Use this setting if you need negligible impact on power consumption,
        but want to receive location updates when available. With this setting,
        your app does not trigger any location updates, but receives locations triggered by other apps.

        */

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void displayLocation() {

        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){


            requestRuntimePermission();

        }else{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){

                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                LatLng myLocation = new LatLng(latitude,longitude);

                mMap.addMarker(new MarkerOptions().position(myLocation).title("Your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

                //Add marker for order address
                drawRoute(myLocation , current_Request.getAddress() , current_Request.getName());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {
        //locationProviderClient = mLastLocation2;
        if(mLastLocation != null && mLastLocation.getLongitude() == location.getLongitude()
                && mLastLocation.getLatitude() == location.getLatitude())
            return;
        else{
            mLastLocation = location;
            displayLocation();
        }
        //displayLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){


            return;

        }

        // Maybe there is an error
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                displayLocation();
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void drawRoute(final LatLng myLocation, String address , final String ID) {

        mServices.getGeoCode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {

                    // sample json result Sudan test
                    // "results":[{"address_components":[{"long_name":"Sudan","short_name":"SD","types":["country","political"]}],"formatted_address":"Sudan","geometry":{"bounds":{"northeast":{"lat":22.224918,"lng":38.69379989999999},"southwest":{"lat":9.3472209,"lng":21.8146345}},"location":{"lat":12.862807,"lng":30.217636},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":22.224918,"lng":38.69379989999999},"southwest":{"lat":9.3472209,"lng":21.8146345}}},"place_id":"ChIJlbFyEMQc2RURNythKkZwv9I","types":["country","political"]}],"status":"OK"}

                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    System.out.println(jsonObject);

                    String lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lat").toString();

                    String lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat) , Double.parseDouble(lng));

                    // we should put an image to use bitmap function

                    mMap.addMarker(new MarkerOptions().position(orderLocation).title("order # "+ ID));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(orderLocation));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

                    // draw

                    mServices.getDirections(myLocation.latitude+","+myLocation.longitude , orderLocation.latitude+","+orderLocation.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    new ParseTask().execute(response.body().toString());

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(TrackingOrder.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){

            mGoogleApiClient.connect();
        }
    }

    private class ParseTask extends AsyncTask<String , Integer , List<List<HashMap<String,String>>>>{

        ProgressDialog mDialog = new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setTitle("Please wait...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try{

                jsonObject = new JSONObject(strings[0]);
                DirectionJsonParser parser = new DirectionJsonParser();

                routes = parser.parse(jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("routes : " , routes.toString());
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points  ;
            PolylineOptions lineOptions = new PolylineOptions();

            for(int i=0; i<lists.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);
                Log.i("Path : " , path.toString());

                for(int j=0; j<path.size(); j++){

                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat , lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

                Log.i("points : " , points.toString());

            }

            mMap.addPolyline(lineOptions);
        }
    }
}
