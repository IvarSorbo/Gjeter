package com.soerboe.gjeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private WMSEndpoint wmsEndpoint;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Create the map
        setContentView(R.layout.activity_main);

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

        //mapView.setTileSource(TileSourceFactory.MAPNIK); // The original... from OSM

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
        mapView.setTileSource(new OnlineTileSourceBase("Kartverket", 0, 16, 150 , "png",
                new String[] { "http://opencache.statkart.no/gatekeeper/gk/gk.open_gmaps?layers=toporaster3" }) {
            // Evt. "?layers=topo4"
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

        // Move the map to the starting position.
        GeoPoint startPoint = new GeoPoint(63.419780, 10.401765);

        //mapView.setTileSource(source);
        moveMapTo(startPoint);
    }

    /**
     * Handle the permissions request response.
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
}
