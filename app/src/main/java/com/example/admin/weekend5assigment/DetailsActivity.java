package com.example.admin.weekend5assigment;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.admin.weekend5assigment.model.PlaceDetails.PlaceResponse;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    public static final String TAG = "DetailsTAG";
    private ViewPager viewPager;
    TextView txtName;
    TextView txtAddress;
    TextView txtPhone;
    TextView txtWebsite;
    TextView txtOpenNow;
    TextView txtPriceLevel;

    double lat;
    double lng;
    String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        String placeID = intent.getStringExtra("PLACEID");
        getPlace(placeID);
        SetBingers();

    }
    public void getPlace(String placeID)
    {
        Call<PlaceResponse> getResponse = RetrofitHelper.getPlaceDetails(placeID);
        getResponse.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                UpdateUI(response.body());
                VPAdapter vpAdapter = new VPAdapter(response.body(),DetailsActivity.this);
                viewPager.setAdapter(vpAdapter);
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }
    public void UpdateUI(PlaceResponse placeResponse)
    {
        txtName.setText(placeResponse.getResult().getName());
        txtAddress.setText("Address: "+placeResponse.getResult().getFormattedAddress());
        txtPhone.setText("Phone: "+placeResponse.getResult().getFormattedPhoneNumber());
        txtWebsite.setText("Website: "+ placeResponse.getResult().getWebsite());
        txtPriceLevel.setText("Price Level: "+placeResponse.getResult().getPriceLevel());
        String Open="";
        if(placeResponse.getResult().getOpeningHours().getOpenNow())
        {
            Open = "Open Now";
        }
        else
        {
            Open = "Closed";
        }
        txtOpenNow.setText(Open);
        lat= placeResponse.getResult().getGeometry().getLocation().getLat();
        lng = placeResponse.getResult().getGeometry().getLocation().getLng();
        mTitle = placeResponse.getResult().getName();
    }
    public void SetBingers()
    {
        txtName = findViewById(R.id.tv_name);
        txtAddress = findViewById(R.id.tv_formatted_address);
        txtOpenNow = findViewById(R.id.tv_open_now);
        txtPhone = findViewById(R.id.tv_formatted_phone_number);
        txtWebsite = findViewById(R.id.tv_website);
        txtPriceLevel = findViewById(R.id.tv_price_level);
        viewPager = (ViewPager) findViewById(R.id.viewPager1ager1);

    }

    public void OpenGoogleMaps(View view) {
        String uri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + mTitle + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

}
