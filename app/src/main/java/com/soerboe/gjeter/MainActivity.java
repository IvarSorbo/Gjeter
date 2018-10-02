package com.soerboe.gjeter;

import android.Manifest;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Navigation menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    // Map
    private MapView mapView;
    private MyLocationNewOverlay myLocationNewOverlay;
    private CompassOverlay compassOverlay;
    private ScaleBarOverlay scaleBarOverlay;
    private Polyline trackOverlay;

    // Offline caching
    CacheManager cacheManager;
    SqliteArchiveTileWriter sqliteArchiveTileWriter;
    AlertDialog alertDialog;

    // Tag used in debug messages
    private static final String TAG = MainActivity.class.getSimpleName();

    // Location
    private LocationManager locationManager; //Accesses location services
    private LocationListener locationListener; //Listens for location changes
    private ArrayList<Waypoint> track = new ArrayList<>(); //Stores the track

    private ImageButton newObservation;
    private View confirm_cancel_buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // (Don't use findViewById before the content view is set)
        setContentView(R.layout.activity_main);

        // Initialize the navigation bar
        NavBarSetup();

        // Setup location listener
        SetupLocationListener();

        mapView = findViewById(R.id.map);
        newObservation = findViewById(R.id.new_observation);
        confirm_cancel_buttons = findViewById(R.id.confirm_cancel_buttons);

        //TODO Handle permissions (location access is only needed if GPS is used. Write access is needed for all use of the app)

        String[] reqPermissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        int requestCode = 2;

        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermissions[2]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermissions[3]) == PackageManager.PERMISSION_GRANTED
                ) {
            // Setup the connection to Kartverket's API.
            SetupMap();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
            // The response to this is handled by onRequestPermissionsResult
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
    }

    /**
     * Center the map
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

        // Add default zoom buttons and ability to zoom with 2 fingers.
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Add a MyLocation overlay
        this.myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),mapView);
        this.myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(this.myLocationNewOverlay);//TODO: different marker?

        // Add a compass overlay
        this.compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        this.compassOverlay.enableCompass();
        mapView.getOverlays().add(this.compassOverlay);

        // Add map scale bar
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        // These values changes the location on the screen
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(this.scaleBarOverlay);

        // Limit the zoom levels
        mapView.setMaxZoomLevel(16.0); // (Kartverket level 17+ uses the black and white map).
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
        //TODO: start at current GPS position, "else" start at last position, "else" start at some default location
        GeoPoint startPoint = new GeoPoint(63.419780, 10.401765);
        moveMapTo(startPoint, 12.0);
    }

    /**
     * Setup the connection to Kartverket's API
     */
    private void SetupKartverketZXY(){
        String layer = "toporaster3";// evt "topo4"

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
     * TODO: make the permission process prettier
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SetupMap();
        } else {
            //TODO: make this report more useful
            // report to user that permission was denied
            Toast.makeText(MainActivity.this,
                    "One or more permissions were denied",//getResources().getString(R.string.something),
                    Toast.LENGTH_SHORT).show();
        }
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
            case R.id.menu_item2:{//TODO
                Toast.makeText(MainActivity.this,"Menu item 2 was clicked", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.info_item:{//TODO
                Toast.makeText(MainActivity.this,"Info item was clicked", Toast.LENGTH_SHORT).show();
                break;
            }

        }
        // Close navigation drawer.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Cache the current area
     */
    private void showDownloadDialog(){
        // Build the dialog.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.download_alert_title);
        alertDialogBuilder.setItems(new CharSequence[]{
                        getResources().getString(R.string.show_cache_info),
                        getResources().getString(R.string.cache_download),
                        getResources().getString(R.string.cancel)
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                cacheManager = new CacheManager(mapView);
                                showCurrentCacheInfo();
                                break;
                            case 1:
                                downloadJobAlert();
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                }
        );

        // Create and show it
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Show information about the cache
     */
    private void showCurrentCacheInfo() {
        //TODO
        Toast.makeText(this, "TODO: show info here", Toast.LENGTH_SHORT).show();
    }

    /**
     * Start downloading the tiles while showing a progression bar
     */
    private void downloadJobAlert() {
        try {
            int zoom_max = (int) Math.floor(mapView.getZoomLevelDouble());
            int zoom_min = (int) Math.floor(mapView.getMinZoomLevel());
            BoundingBox bb = mapView.getBoundingBox();
            int tilecount = cacheManager.possibleTilesInArea(bb, zoom_min, zoom_max);

            //TODO: change filename? (Do I need a unique filename for each output?)
            String outputName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "osmdroid" + File.separator + "Kartverket";
            sqliteArchiveTileWriter=new SqliteArchiveTileWriter(outputName);
            cacheManager = new CacheManager(mapView, sqliteArchiveTileWriter);

            Log.d(TAG, "\nDownloading tiles:");
            Log.d(TAG, "Output: " + outputName);
            Log.d(TAG, tilecount + " tiles");

            if (true) {
                //this triggers the download
                cacheManager.downloadAreaAsync(this, bb, zoom_min, zoom_max, new CacheManager.CacheManagerCallback() {
                    @Override
                    public void onTaskComplete() {
                        Toast.makeText(MainActivity.this, "Download complete!", Toast.LENGTH_LONG).show();
                        if (sqliteArchiveTileWriter!=null)
                            sqliteArchiveTileWriter.onDetach();
                    }

                    @Override
                    public void onTaskFailed(int errors) {
                        Toast.makeText(MainActivity.this, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();
                        if (sqliteArchiveTileWriter!=null)
                            sqliteArchiveTileWriter.onDetach();
                    }

                    @Override
                    public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                        //NOOP since we are using the build in UI
                    }

                    @Override
                    public void downloadStarted() {
                        //NOOP since we are using the build in UI
                    }

                    @Override
                    public void setPossibleTilesInArea(int total) {
                        //NOOP since we are using the build in UI
                    }
                });
            }
        }catch (Exception ex){
            //TODO
            ex.printStackTrace();
        }

    }

    /**
     * Setup a location listener
     */
    private void SetupLocationListener(){
        //TODO: what happens if user does not grant permission initially, but wants to do it later?

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
                track.add(waypoint);
                Log.d(TAG, "New waypoint: " + waypoint.toGeoJSONFeature());
                // TODO: should the track(waypoints) also be written to a permanent file? (so that not all data is lost if the app is shut down for some reason)

                // Update track on map
                trackOverlay.addPoint(new GeoPoint(location));
                mapView.invalidate();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

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

    private void startObservationDialog(){
        // This is called when the "+" button is clicked

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
                // Mark the selected spot as an observation
                markObservation();
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

    private void markObservation(){
        // This is called when the observation is confirmed

        // Find the coordinates of the middle of the screen
        Waypoint observation_point = new Waypoint(
                new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude()),
                new Date(System.currentTimeMillis())
        );

        // TODO: Store the observation somewhere
        Observation observation = new Observation(observation_point, 0);

        // TODO: Make a marker on the map
        

        // TODO: Draw a Polyline between the current position and the observed position

        // TODO: Query the user for more information about the observation

    }

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
}

/*
* NOTE;
* see: https://stackoverflow.com/questions/14060389/osmdroid-displays-an-empty-grid
* for a possible solution for why Kartverket doesn't work in the emulator.
* I have to either get some external storage for the emulator or I have to change
* where OSMdroid stores the cached files (see bottom answer).
* OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());
*
 */