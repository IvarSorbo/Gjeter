package com.soerboe.gjeter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Navigation menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    // Map
    private MapView mapView;
    private Polyline trackOverlay;

    // Offline caching
    CacheManager cacheManager;
    SqliteArchiveTileWriter sqliteArchiveTileWriter;
    AlertDialog alertDialog;

    // Tag used in debug messages
    private static final String TAG = MainActivity.class.getSimpleName();

    // Stores a waypointlist and a list of observations for the current trip
    private Trip trip;

    // The "+" button
    private ImageButton newObservation;


    private View confirm_cancel_buttons;

    private int obs_activity_request_code = 100;

    private static final String locationFilename = "location";

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        trip = new Trip();

        NavBarSetup();

        SetupLocationListener();

        mapView = findViewById(R.id.map);
        newObservation = findViewById(R.id.new_observation);
        confirm_cancel_buttons = findViewById(R.id.confirm_cancel_buttons);

        cacheManager = new CacheManager(mapView);

        // Checking and requesting permissions:
        String[] reqPermissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        int requestCode = 2;

        // For API level 23+ request permission at runtime
        if (permissionsGranted(reqPermissions)) {
            // Setup the connection to Kartverket's API.
            SetupMap();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
        // Save the state
        saveState();
    }

    /**
     * Center the map to a specified point and zoom level
     */
    private void moveMapTo(GeoPoint point, Double zoom){
        IMapController mapController = mapView.getController();
        mapController.setZoom(zoom);
        mapController.setCenter(point);
    }

    /**
     * Setup the map
     */
    private void SetupMap(){
        // Connection to Kartverket's API.
        SetupKartverketZXY();

        // Add the ability to zoom with 2 fingers.
        mapView.setMultiTouchControls(true);

        // Add a MyLocation overlay (shows the user's position on the map)
        MyLocationNewOverlay myLocationNewOverlay;
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),mapView);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);

        // Add a compass overlay
        CompassOverlay compassOverlay;
        compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Add map scale bar
        ScaleBarOverlay scaleBarOverlay;
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);// These values changes the location on the screen
        mapView.getOverlays().add(scaleBarOverlay);

        // Limit the zoom levels
        mapView.setMaxZoomLevel(16.0); // (toporaster3 level 17+ uses the black and white map).
        mapView.setMinZoomLevel(6.0); // No point in being able to see more than Norway.

        // Setup the overlay that will display the track
        trackOverlay = new Polyline(mapView);
        trackOverlay.setColor(0xFFCC14C6);//AlphaRGB
        trackOverlay.setWidth(10f);//width in pixels
        mapView.getOverlays().add(trackOverlay);

        // Setup button that can be clicked to add an observation.
        newObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "New observation (+) button clicked ");
                startObservationDialog();
            }
        });

        // Move the map to the starting position.
        moveMapToStartingPosition();
    }

    /**
     * Setup the connection to Kartverket's API
     */
    private void SetupKartverketZXY(){
        String layer = "toporaster3";// evt "topo4" / "norgeskart_bakgrunn" (this works for higher zoom level)

        mapView.setTileSource(new OnlineTileSourceBase("Kartverket", 0, 20, 256 , "png",
                new String[] { "http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer,
                        "http://opencache2.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer,
                        "http://opencache3.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                // Creating the url string
                String result = getBaseUrl()
                        + "&zoom=" + MapTileIndex.getZoom(pMapTileIndex)
                        + "&x=" + MapTileIndex.getX(pMapTileIndex)
                        + "&y=" + MapTileIndex.getY(pMapTileIndex);
                Log.d(TAG, "\nQuerying Kartverket's API at: " + result);
                return result;
            }
        });
    }

    /**
     * Handle the permissions request response.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(checkGrantResults(grantResults)){
            SetupMap();
        } else {
            Toast.makeText(MainActivity.this,
                    R.string.permissions_denied,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if all the required permissions are granted
     */
    private boolean permissionsGranted(@NonNull String[] reqPermissions){
        for (String permission : reqPermissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private boolean checkGrantResults(@NonNull int[] grantResults){
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Setup the navigation bar/menu
     */
    private void NavBarSetup(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Toggle to open/close the navigation bar
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup listeners for navigation menu items.
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handling navigation item click.
        switch (menuItem.getItemId()) {
            case R.id.download_map: {
                showDownloadDialog();
                break;
            }
            case R.id.info_item:{
                showCurrentCacheInfo();
                break;
            }
        }
        // Close navigation drawer.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Show dialog where user can choose to download the current area.
     */
    private void showDownloadDialog(){
        // Build the dialog.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.download_alert_title);

        // Add option to download/cancel
        alertDialogBuilder.setItems(new CharSequence[]{
                        getResources().getString(R.string.cache_download),
                        getResources().getString(R.string.cancel)
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                downloadJobAlert();
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                }
        );

        // Create and show the dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Show information about the cache
     */
    private void showCurrentCacheInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this );

                alertDialogBuilder.setTitle("Cache")
                        .setMessage("Tilgjengelig cache-kapasitet: " +
                                cacheManager.cacheCapacity()/(1024*1024) +
                                " MB\nBrukt cache-kapasitet: " +
                                cacheManager.currentCacheUsage()/(1024*1024) + " MB");

                alertDialogBuilder.setItems(new CharSequence[]{
                                getResources().getString(R.string.cancel)
                        }, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

                // Create alert dialog and show it.
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
        }).start();
    }

    /**
     * Start downloading the tiles while showing a progression bar.
     */
    private void downloadJobAlert() {
        try {
            int zoom_max = (int) Math.floor(mapView.getZoomLevelDouble());
            int zoom_min = (int) Math.floor(mapView.getMinZoomLevel());
            BoundingBox bb = mapView.getBoundingBox();

            int tilecount = cacheManager.possibleTilesInArea(bb, zoom_min, zoom_max);

            String outputName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "osmdroid" + File.separator + "Kartverket";
            sqliteArchiveTileWriter=new SqliteArchiveTileWriter(outputName);
            cacheManager = new CacheManager(mapView, sqliteArchiveTileWriter);

            //Log.d(TAG, "\nDownloading tiles:");
            //Log.d(TAG, "Output: " + outputName);
            //Log.d(TAG, tilecount + " tiles");

            // Download
            cacheManager.downloadAreaAsync(this, bb, zoom_min, zoom_max, new CacheManager.CacheManagerCallback() {
                @Override
                public void onTaskComplete() {
                    Toast.makeText(MainActivity.this, R.string.done, Toast.LENGTH_LONG).show();
                    if (sqliteArchiveTileWriter!=null)
                        sqliteArchiveTileWriter.onDetach();
                }

                @Override
                public void onTaskFailed(int errors) {
                    Toast.makeText(MainActivity.this, "Nedlastning ferdig, men ga " + errors + " feilmeldinger", Toast.LENGTH_LONG).show();
                    if (sqliteArchiveTileWriter!=null)
                        sqliteArchiveTileWriter.onDetach();
                }

                @Override
                public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                    //NOOP since we are using the build in UI
                }

                @Override
                public void downloadStarted() { }

                @Override
                public void setPossibleTilesInArea(int total) { }
            });

        }catch (Exception ex){
            ex.printStackTrace();
            Log.d(TAG, "Something went wrong with the downlaod");
        }
    }

    /**
     * Setup a location listener
     */
    private void SetupLocationListener(){
        LocationManager locationManager; //Accesses location services
        LocationListener locationListener; //Listens for location changes

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}
                        ,10);
            }
            return;
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called whenever the location is updated
                // Store the new location in a list of waypoints.
                Waypoint waypoint = new Waypoint(
                        new GeoPoint(location.getLatitude(), location.getLongitude()),
                        new Date(System.currentTimeMillis()));
                trip.addWaypoint(waypoint);
                Log.d(TAG, "New waypoint: " + gson.toJson(waypoint));

                // Update track on map
                trackOverlay.addPoint(new GeoPoint(location));
                mapView.invalidate();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) {
                // Checks weather the GPS is turned off
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        locationManager.requestLocationUpdates(
                "gps",
                5000,//how often it should check (ms)
                1,//min changed distance for it to count as an update
                locationListener);
    }

    /**
     * Starts the process of adding an observation.
     * This is called when the "+" button is clicked.
     */
    private void startObservationDialog(){
        // Show a crosshair in the middle of the screen
        showCrosshair(true);

        // Remove the "+" button
        newObservation.setVisibility(View.INVISIBLE);

        // Show the confirm/cancel button at the bottom of the screen
        confirm_cancel_buttons.setVisibility(View.VISIBLE);

        // When "confirm" is clicked; markObservation() is called
        Button bt_confirm = findViewById(R.id.bt_confirm);
        Button bt_cancel = findViewById(R.id.bt_cancel);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanupObservationDialog();
                startObservationActivity();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanupObservationDialog();
            }
        });
    }

    /**
     * Cleanup the view after the startObservationDialog is done
     */
    private void cleanupObservationDialog(){
        // Remove crosshair
        showCrosshair(false);
        // Remove cancel/confirm buttons
        confirm_cancel_buttons.setVisibility(View.INVISIBLE);
        // Show the "+" button
        newObservation.setVisibility(View.VISIBLE);
    }

    /**
     * Marks the observation on the map and draws a line between the current position and the
     * observed position.
     */
    private void markObservation(String observation_json){
        // Get user's position and observation's position:
        Observation obs = gson.fromJson(observation_json, Observation.class);
        GeoPoint my_pos = obs.getMyPosition();
        GeoPoint obs_pos = obs.getObsPosition();

        // Make a marker on the map
        Marker observationMarker = new Marker(mapView);
        observationMarker.setPosition(obs_pos);
        observationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        observationMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_place_24px));
        observationMarker.setTitle(obs.getObservationType().toString());
        mapView.getOverlays().add(observationMarker);

        // Draw a Polyline between the current position and the observed position
        if(my_pos.getLatitude() != 0 && my_pos.getLongitude() != 0){
            Polyline line = new Polyline(mapView);
            line.addPoint(my_pos);
            line.addPoint(obs_pos);
            line.setColor(0xFF000000);//AlphaRGB
            line.setWidth(7f);//width in pixels
            mapView.getOverlays().add(line);
            mapView.invalidate();
        }
    }

    /**
     * Turn on/off the visibility of the crosshair in the middle of the map.
     */
    private void showCrosshair(boolean show){
        View horizontal = findViewById(R.id.crosshair_horizontal);
        View vertical = findViewById(R.id.crosshair_vertical);

        if (show) {
            horizontal.setVisibility(View.VISIBLE);
            vertical.setVisibility(View.VISIBLE);
        } else{
            horizontal.setVisibility(View.INVISIBLE);
            vertical.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Put observed position and my position onto an ObservationActivity and start the activity.
     */
    private void startObservationActivity(){
        // Find the user's position
        GeoPoint my_pos = trip.getCurrentGeoPoint();
        if(my_pos == null){
            my_pos = new GeoPoint(0.0, 0.0);
            Toast.makeText(MainActivity.this,
                    "Unable to find current GPS location.",
                    Toast.LENGTH_SHORT).show();
        }

        // Find the observation's position
        GeoPoint obs_pos = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());

        Intent obs_activity = new Intent(this, ObservationActivity.class);
        obs_activity.putExtra(Constants.OBS_LAT, obs_pos.getLatitude());
        obs_activity.putExtra(Constants.OBS_LNG, obs_pos.getLongitude());
        obs_activity.putExtra(Constants.MY_LAT, my_pos.getLatitude());
        obs_activity.putExtra(Constants.MY_LNG, my_pos.getLongitude());

        startActivityForResult(obs_activity, obs_activity_request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == obs_activity_request_code){
            switch (resultCode){
                case Activity.RESULT_OK: {
                    String result = data.getExtras().get("result").toString();
                    Log.d(TAG, "\nResult from the Observation Activity:\n" + result);
                    markObservation(result);
                    trip.addObservation(result);
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Log.d(TAG, "\nThe observation activity was canceled");

                    break;
                }
                default:{
                    Log.d(TAG, "\nThe observation activity failed");
                }
            }
        }
    }

    /**
     * Save some stuff before the app closes completely
     */
    private void saveState(){
        // Save the last position
        GeoPoint myPosition = trip.getCurrentGeoPoint();
        if (myPosition != null) {
            String content = gson.toJson(myPosition);

            saveToInternalFile(content, locationFilename);
        }
        // Save the trip object
        String filename = trip.getFilename();
        trip.finish();
        String tripJSON = gson.toJson(trip);
        saveToInternalFile(tripJSON, filename);
    }

    private void saveToInternalFile(String content, String filename){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            Log.d(TAG, "Successfully saved internal storage");
        } catch (Exception e) {
            Log.d(TAG, "Failed to save. Message: " + e.getMessage());
        }
    }

    /**
     * Move the map to the starting position.
     * First try to get the current position, if this fails; try to get a position from the
     * internal storage, if this fails; use a default location and zoom out such that all of
     * Norway is shown.
     */
    private void moveMapToStartingPosition(){
        GeoPoint startPoint = new GeoPoint(63.419780, 10.401765);
        double zoom = 7.0;

        // Try to find the current location
        if(trip.getCurrentGeoPoint() != null){
            startPoint = trip.getCurrentGeoPoint();
            zoom = 12.0;
        }
        else{
            // Try to find the last known location from internal memory
            try{
                String fileContent = stringFromInternalFile(locationFilename);
                // parse the GeoJSON into a GeoPoint object
                GeoPoint locationFromFile = gson.fromJson(fileContent, GeoPoint.class);
                if (locationFromFile != null){
                    startPoint = locationFromFile;
                    zoom = 12.0;
                }
            }
            catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
        }

        // No saved location is found, so zoom out so that most of Norway is shown.
        moveMapTo(startPoint, zoom);
    }

    /**
     * Reads the specified filename from internal storage and returns the content as a String.
     * @param filename filename
     * @return The String found in the file
     */
    private String stringFromInternalFile(String filename){
        String result;
        try{
            FileInputStream fileInputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            result = stringBuilder.toString();
        }catch (Exception e){

            result = "";
        }
        return result;
    }
}
