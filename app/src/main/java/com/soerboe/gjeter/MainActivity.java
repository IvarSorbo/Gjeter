package com.soerboe.gjeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.wms.WMSEndpoint;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MenuItem downloadMapMenuItem;

    private MapView mapView;
    private WMSEndpoint wmsEndpoint;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Create the map (Don't use findViewById before the content view is set)
        setContentView(R.layout.activity_main);

        // Initialize the navigation bar
        NavBarSetup();

        mapView = findViewById(R.id.map);

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
            // Do stuff
            ZXYsetup();
        } else {
            // request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
            // The reponse to this is handled by onRequestPermissionsResult
        }

        /*
        // Doesn't display anything...
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WMSSetup();
            }
        });
        thread.start();*/
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

    private void moveMapTo(GeoPoint point){
        IMapController mapController = mapView.getController();
        mapController.setZoom(9);
        mapController.setCenter(point);
    }

    private void WMSSetup(){
        HttpURLConnection c;
        InputStream is;
        try{
            c = (HttpURLConnection) new URL("https://openwms.statkart.no/skwms1/wms.topo4?request=GetCapabilities&service=WMS").openConnection();
            is = c.getInputStream();

            wmsEndpoint = WMSParser.parse(is);

            is.close();
            c.disconnect();

            WMSTileSource source = WMSTileSource.createFrom(wmsEndpoint, wmsEndpoint.getLayers().get(201));
            //for (int i = 0; i < wmsEndpoint.getLayers().size(); i++){
            //    if(Objects.equals(wmsEndpoint.getLayers().get(i).getName(), "topo4_WMS")){
            //        Log.d(TAG, "\nidx: " + i);
            //    }
            // }
            //topo4_WMS


            // Add default zoom buttons and ability to zoom with 2 fingers.
            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);

            // Move the map to the starting position.
            GeoPoint startPoint = new GeoPoint(63.419780, 10.401765);

            mapView.setTileSource(source);
            moveMapTo(startPoint);

            Log.d(TAG, "\nSuccess");
            Log.d(TAG, mapView.getTileProvider().getTileSource().name());
            Log.d(TAG, "Storage location: " + Configuration.getInstance().getOsmdroidBasePath().getAbsolutePath());
        } catch (Exception e){
            Log.d(TAG, "\nMessage: " + e.getMessage());
            Log.d(TAG, "Exception class:" + e.getClass().toString());
            e.printStackTrace();
        }
        // https://openwms.statkart.no/skwms1/wms.toporaster3?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&LAYERS=toporaster&CRS=EPSG%3A25833&STYLES=&WIDTH=1362&HEIGHT=1040&BBOX=220000.582811029%2C6976748.1392506445%2C335178.592811029%2C7064746.291750644
    }

    private void ZXYsetup(){
        //http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers=sjokartraster&zoom=1&x=1&y=1
        // That works, Google Map's API also returns 404 if I change zoom level on some of the (x,y) pairs, so that is probably intended.

        // Zoom level 17 and 18 uses the black and white map. Level 16 should be detailed enough.
        String layer = "toporaster3";// evt "topo4"
        mapView.setTileSource(new OnlineTileSourceBase("Kartverket", 0, 16, 150 , "png",
                new String[] { "http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer,
                        "http://opencache2.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer,
                        "http://opencache3.statkart.no/gatekeeper/gk/gk.open_gmaps?layers="+layer}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                String result = getBaseUrl()
                        + "&zoom=" + MapTileIndex.getZoom(pMapTileIndex)
                        + "&x=" + MapTileIndex.getX(pMapTileIndex)
                        + "&y=" + MapTileIndex.getY(pMapTileIndex);
                //+ mImageFilenameEnding;
                Log.d(TAG, "\ngetTileURLString returns: " + result);
                return result;
            }
        });

        // Add default zoom buttons and ability to zoom with 2 fingers.
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        //TODO: start at current GPS position, "else if" start at last GPS position, "else" start at this location...
        // Move the map to the starting position.
        GeoPoint startPoint = new GeoPoint(63.419780, 10.401765);

        //mapView.setTileSource(source);
        moveMapTo(startPoint);
    }

    /**
     * Handle the permissions request response.
     * TODO: make the permission process prettier
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ZXYsetup();
        } else {
            // report to user that permission was denied
            Toast.makeText(MainActivity.this,
                    "Write permission denied",//getResources().getString(R.string.raster_write_permission_denied),
                    Toast.LENGTH_SHORT).show();

            Toast.makeText(MainActivity.this,
                    "Location permission denied",//getResources().getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void NavBarSetup(){
        // Setup the navigation menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        // Handling item clicks
        switch (menuItem.getItemId()) {
            case R.id.download_map: {
                DownloadArea();
                break;
            }
            case R.id.menu_item2:{
                Log.d(TAG, "Menu item 2 was clicked");
                break;
            }
            case R.id.info_item:{
                Log.d(TAG, "Info item was clicked");
                break;
            }

        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Cache the current area
     */
    private void DownloadArea(){
        Log.d(TAG, "the button was clicked");
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