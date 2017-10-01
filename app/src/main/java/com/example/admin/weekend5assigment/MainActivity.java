package com.example.admin.weekend5assigment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.admin.weekend5assigment.model.GooglePlaces.GoogleResponse;
import com.example.admin.weekend5assigment.model.GooglePlaces.Location;
import com.example.admin.weekend5assigment.model.GooglePlaces.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Adapter.RecyclerViewListner {

    public static final String TAG = "MainActivityTAG";
    FusedLocationProviderClient fusedLocationProviderClient;
    android.location.Location currentLocation;
    private Adapter rvAdapter;
    private String latLong;

    GoogleMap mGoogleMap;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                    getPlaceByName(place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MainActivity.this, "Error to Get Place Name", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+status.toString());
            }
        });

        mapView = findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
        }
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MapsInitializer.initialize(MainActivity.this);
                    mGoogleMap = googleMap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }

            });

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {


                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            205);
                }
            } else {
                getLocation();
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 205: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else {

                    Toast.makeText(this, "Need this location", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            205);
                }

            }

        }

    }

    private void getLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    111);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                currentLocation = location;
                latLong = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                GetNearbyPlaces();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to Get your location", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    public void GetNearbyPlaces() {

        if (currentLocation != null) {

            Call<GoogleResponse> Results = RetrofitHelper.getnearbyPlaces(latLong);
            Results.enqueue(new Callback<GoogleResponse>() {
                @Override
                public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                    ShowRecyclerView(response.body());
                    updateMap(response.body());
                }

                @Override
                public void onFailure(Call<GoogleResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: RestAPI" + t.toString());
                }
            });
        }
    }

    public void getPlaceByName(String placeName)
    {
        Call<GoogleResponse> getResult = RetrofitHelper.getPlaceByName(placeName);
        getResult.enqueue(new Callback<GoogleResponse>() {
            @Override
            public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                rvAdapter.UpdateRVLIST(response.body());
                updateMap(response.body());
            }

            @Override
            public void onFailure(Call<GoogleResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPlaceByCategory(String Category)
    {
        Call<GoogleResponse> getResults = RetrofitHelper.getnearbyPlacesByType(latLong, Category);
        getResults.enqueue(new Callback<GoogleResponse>() {
            @Override
            public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                rvAdapter.UpdateRVLIST(response.body());
                updateMap(response.body());
            }

            @Override
            public void onFailure(Call<GoogleResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    public void ShowRecyclerView(GoogleResponse googleResponse) {

        RecyclerView RVList = (RecyclerView) findViewById(R.id.RVList);
        rvAdapter = new Adapter(this, googleResponse, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        RVList.setAdapter(rvAdapter);
        RVList.setLayoutManager(layoutManager);
        RVList.setItemAnimator(itemAnimator);

    }

    @Override
    public void itemClicked(String placeID) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra("PLACEID",placeID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String find = null;
        switch (id) {
            case R.id.findHospital:
                find = "hospital";
                break;
            case R.id.findBank:
                find = "bank";
                break;
            case R.id.findRestaurant:
                find = "restaurant";
                break;
            case R.id.findCafe:
                find = "cafe";
                break;
            case R.id.findBar:
                find = "bar";
                break;
        }

        if (find != null) {
           getPlaceByCategory(find);
        }
        return true;

    }

    public void updateMap(GoogleResponse response)
    {
        double MinS = response.getResults().get(0).getGeometry().getLocation().getLat();
        double MinW = response.getResults().get(0).getGeometry().getLocation().getLng();
        double MaxN = MinS;
        double MaxE = MinW;

        for(Result r: response.getResults())
        {
            double lat = r.getGeometry().getLocation().getLat();
            double lng = r.getGeometry().getLocation().getLng();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lng))
                    .title(r.getName()));
            if(MinS>lat)
            {
                MinS = lat;
            }
            if(MinW>lng)
            {
                MinW = lng;
            }
            if(MaxN<lat)
            {
                MaxN = lat;
            }
            if(MaxE<lng)
            {
                MaxE = lng;
            }
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(MinS, MinW), new LatLng(MaxN, MaxE));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
    }
}

