package info.test.firebase;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created  on 2/11/2017.
 */

public class maptest extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    SupportMapFragment mapFragment;
    boolean vis;
    ImageButton listornot;
    FrameLayout list;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    Boolean click;
    String Client_id = "CPey57Lphu3N8gpYU3M0_MNZ5rh704S0";
    boolean resturent = false;
    private FirebaseAuth auth;
    //Our Map
    private GoogleMap mMap;
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    //Buttons
    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    private ImageButton buttonView;
    private ImageButton buttonViewhus;
    private ImageButton logout, Shopping_mall, Petrol_station, Bus_stop, Cafe;
    private ArrayList countries;
    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private String TAG = "na";
    private ArrayList<Double> laton;
    private ArrayList<Double> longon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Progress bar Intilization
        progressDialog = new ProgressDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        // recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Initializing views and adding onclick listeners
        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
        list = (FrameLayout) findViewById(R.id.List);

        listornot = (ImageButton) findViewById(R.id.listornot);
        buttonViewhus = (ImageButton) findViewById(R.id.buttonHosp);
        buttonView = (ImageButton) findViewById(R.id.buttonView);
        logout = (ImageButton) findViewById(R.id.logout);
        Shopping_mall = (ImageButton) findViewById(R.id.Shopping_mall);
        Petrol_station = (ImageButton) findViewById(R.id.Petrol_station);
        Bus_stop = (ImageButton) findViewById(R.id.Bus_stop);
        Cafe = (ImageButton) findViewById(R.id.Cafe);
        buttonSave.setOnClickListener(this);
        buttonCurrent.setOnClickListener(this);
        listornot.setOnClickListener(this);
        buttonView.setOnClickListener(this);
        buttonViewhus.setOnClickListener(this);
        logout.setOnClickListener(this);
        Shopping_mall.setOnClickListener(this);
        Petrol_station.setOnClickListener(this);
        Bus_stop.setOnClickListener(this);
        Cafe.setOnClickListener(this);



    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", " + longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonCurrent) {
            click = false;
            listornot.setVisibility(View.GONE);
            getCurrentLocation();
            moveMap();
            list.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.VISIBLE);
        } else if (v == buttonSave) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = "atm";//mPlaceType[selectedPosition];
            click = true;
            resturent = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=500");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask

            progressDialog.show();
            progressDialog.setMessage("Gettting Atm data");
            placesTask.execute(sb.toString());

            initViews();

        } else if (v == listornot) {
            {
                click = false;
                if (vis) {
                    mapFragment.getView().setVisibility(View.GONE);
                    listornot.setImageResource(R.drawable.ic_map);
                    list.setVisibility(View.VISIBLE);
                    vis = false;
                } else {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    listornot.setImageResource(R.drawable.ic_view_list);
                    list.setVisibility(View.GONE);
                    vis = true;
                }
            }
        } else if (v == buttonView) {
            click = false;
            listornot.setVisibility(View.VISIBLE);
            String type = "restaurant";//mPlaceType[selectedPosition];
            resturent = true;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=500");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.i("url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting restaurant data");
            placesTask.execute(sb.toString());

            initViews();
        } else if (v == buttonViewhus) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = "hospital";//mPlaceType[selectedPosition];
            click = false;
            resturent = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=2000");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting Hospital data");
            placesTask.execute(sb.toString());

            initViews();

        } else if (v == logout) {
            auth.signOut();
            Log.i("logout", "out");
            Intent intent = new Intent(maptest.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else if (v == Shopping_mall) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = "shopping_mall";//mPlaceType[selectedPosition];
            click = false;
            resturent = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=2000");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting shopping mall data");
            placesTask.execute(sb.toString());

            initViews();

        } else if (v == Petrol_station) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = "police";//mPlaceType[selectedPosition];
            click = false;
            resturent = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=3000");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting Police data");
            placesTask.execute(sb.toString());

            initViews();

        } else if (v == Bus_stop) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            resturent = false;
            String type = "bus_station";//mPlaceType[selectedPosition];
            click = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=1000");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting bus stop data");
            placesTask.execute(sb.toString());

            initViews();

        } else if (v == Cafe) {
            listornot.setVisibility(View.VISIBLE);
            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = "cafe";//mPlaceType[selectedPosition];
            click = false;
            resturent = false;
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + latitude + "," + longitude);
            sb.append("&radius=1000");
            sb.append("&types=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyDvAOBuOpPixuOH4aUAjt1AXjWSIaG5fGA");
            Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            Log.println(Log.INFO, "url", String.valueOf(sb));
            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            progressDialog.show();
            progressDialog.setMessage("Gettting cafe data");
            placesTask.execute(sb.toString());

            initViews();

        }
    }

    private void initViews() {

        countries = new ArrayList<>();

        RecyclerView.Adapter adapter = new DataAdapter(countries);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());

                if (child != null && gestureDetector.onTouchEvent(e)) {
                    final int position = rv.getChildAdapterPosition(child);
                    if (resturent) {

                        Intent in = new Intent(getApplicationContext(), Main.class);
                        startActivity(in);

                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(maptest.this);
                        alertDialogBuilder.setTitle("Call UBER");
                        alertDialogBuilder.setMessage("Should i call a UBER ride?");
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    PackageManager pm = getApplicationContext().getPackageManager();
                                    pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                                    String uri =
                                            "uber://?action=setPickup&client_id=CPey57Lphu3N8gpYU3M0_MNZ5rh704S0&pickup=my_location&dropoff[formatted_address]=Bangalore%2C%20Karnataka%2C%20India&&dropoff[latitude]=" + laton.get(position) + "&dropoff[longitude]=" + longon.get(position) + "";
                                    Log.i("loction", "uber://?action=setPickup&client_id=CPey57Lphu3N8gpYU3M0_MNZ5rh704S0&pickup=my_location&dropoff[formatted_address]=Bangalore%2C%20Karnataka%2C%20India&&dropoff[latitude]=" + laton.get(position) + "&dropoff[longitude]=" + longon.get(position) + "");

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(uri));
                                    startActivity(intent);
                                } catch (PackageManager.NameNotFoundException e) {
                                    // No Uber app! Open mobile website.
                                    String url = "https://m.uber.com/sign-up?client_id=CPey57Lphu3N8gpYU3M0_MNZ5rh704S0";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }

                            }
                        });

                        alertDialogBuilder.show();
                        Toast.makeText(getApplicationContext(), countries.get(position).toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), laton.get(position).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("ExceptionDownloadingUrl", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            laton = new ArrayList<>();
            longon = new ArrayList<>();
            int i;
            // Clears all the existing markers
            mMap.clear();

            countries.clear();
            if (!laton.isEmpty())
                laton.clear();
            if (!longon.isEmpty())
                longon.clear();
            for (i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                countries.add(name);
                laton.add(lat);
                longon.add(lng);

                Log.i("loop", "Add");

                // String open = hmPlace.get("open");
                //Log.i("open",open);
                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");


                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name + " : " + vicinity);
                Log.i("h", String.valueOf(i));
                if (click) {
                    if (i == list.size() - 3) {

                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }


                } else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);

                LatLng MOUNTAIN_VIEW = new LatLng(latitude, longitude);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
                        .zoom(16f)                   // Sets the zoom
                        .bearing(2)                // Sets the orientation of the camera to east
                        .tilt(55)              // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                progressDialog.dismiss();
            }
        }
    }


}




