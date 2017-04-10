package stone.philosophers.com.driversed;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected List<LatLng> mCapturedLocations;
    protected LocationRequest mLocationRequest;
    protected Button tripButton;
    protected TextView distanceText;
    protected boolean locating;
    public static long mStartTime;
    public static long mEndTime;
    public static float milesFromMap;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locating = false;

        mCapturedLocations = new ArrayList<LatLng>();

        setContentView(R.layout.activity_maps);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        createLocationRequest();
        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distanceText = (TextView) findViewById(R.id.distanceText);
        tripButton = (Button) findViewById(R.id.tripButton);

        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tripButton.getText().toString() == "End Trip"){
                    Intent intent = new Intent(MapsActivity.this, UploadDrive.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                if(!locating) {
                    locating = true;
                    tripButton.setText("End Trip");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapsActivity.this);
                } else {
                    locating = false;
                    tripButton.setText(("Start Trip"));
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MapsActivity.this);
                    distanceText.setText("Miles: " + Float.toString(distanceCalc()));
                    mEndTime = mLastLocation.getTime();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(mCapturedLocations.get(mCapturedLocations.size()-1).latitude, mCapturedLocations.get(mCapturedLocations.size()-1).longitude)).title("End Point"));
                    mCapturedLocations.clear();
                }
            }
        });
    }

    protected float distanceCalc() {
        if(mCapturedLocations.size()<2)
            return 0;

        Location from = new Location("");
        float distance = 0;
        Location to = new Location("");
        for(int i = 1; i < mCapturedLocations.size(); i++) {
            from.setLatitude(mCapturedLocations.get(i-1).latitude);
            from.setLongitude(mCapturedLocations.get(i-1).longitude);
            to.setLatitude(mCapturedLocations.get(i).latitude);
            to.setLongitude(mCapturedLocations.get(i).longitude);
            distance += from.distanceTo(to);
        }
        distance *= 0.000621371;
        milesFromMap = distance;
        return distance;
    }

    /**
     * Creates the location request object, which sets settings about locationupdates
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(1000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng loc;
        if (mLastLocation != null) {
            loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
          //  Toast.makeText(this, "location detected", Toast.LENGTH_LONG).show();

        } else
            loc = new LatLng(0, -0);
        mMap.addMarker(new MarkerOptions().position(loc).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    200);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        startLocating();
    }

    public void startLocating() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng loc;
            if (mMap != null) {
                mCapturedLocations.clear();
                mCapturedLocations.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
              //  Toast.makeText(this, "location detected", Toast.LENGTH_LONG).show();
            }
            else
                loc = new LatLng(0, 0);
            mMap.addMarker(new MarkerOptions().position(loc).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            //Toast.makeText(this, "no location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocating();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("MapsActivity", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("MapsActivity", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng prevLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mLastLocation = location;

        if(mCapturedLocations.size()==0)
            mStartTime = location.getTime();
        mCapturedLocations.add(new LatLng(location.getLatitude(), location.getLongitude()));
        LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        LatLng[] mapPoints = new LatLng[mCapturedLocations.size()];
        int i = 0;
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (LatLng point : mCapturedLocations) {
            mapPoints[i] = point;
            bounds.include(mapPoints[i]);
            i += 1;
        }

        mMap.clear();
        mMap.addPolyline(new PolylineOptions()
                .add(mapPoints)
                .width(20).color(Color.BLUE).geodesic(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.addMarker(new MarkerOptions().position(mapPoints[0]).title("Start Point"));
    }
}
