package com.example.admin.weekend5assigment;

import com.example.admin.weekend5assigment.model.GooglePlaces.GoogleResponse;
import com.example.admin.weekend5assigment.model.PlaceDetails.PlaceResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Admin on 9/30/2017.
 */

public class RetrofitHelper {
    public static final String Base_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final String APIKey = "AIzaSyCcPmL9pb7thWDvrdMYLfznW5yOiKhJ2mM";
    public static final int Raduis = 10000;

    public static Retrofit create(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;

    }

    public static Call<GoogleResponse> getnearbyPlaces (String LatLong)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getnearbyPlaces(LatLong,Raduis,APIKey);
    }

    public static Call<GoogleResponse> getnearbyPlacesByType (String LatLong, String Category)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getnearbyPlacesByType(LatLong,Raduis,Category,APIKey);
    }
    public static Call<GoogleResponse> getPlaceByName(String placeName)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getPlacebyName(placeName,APIKey);
    }
    public static Call<PlaceResponse> getPlaceDetails(String placeID)
    {
        Retrofit retrofit = create();
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getPlaceDetails(placeID,APIKey);
    }

    interface APIService{
        @GET("nearbysearch/json")
        Call<GoogleResponse> getnearbyPlaces(@Query("location") String Location, @Query("radius") int Radius, @Query("key") String ApiKey);

        @GET("nearbysearch/json")
        Call<GoogleResponse> getnearbyPlacesByType(@Query("location") String Location, @Query("radius") int Radius, @Query("type") String Type, @Query("key") String ApiKey);

        @GET ("textsearch/json")
        Call<GoogleResponse> getPlacebyName(@Query("query") String Query ,@Query("key") String Key);

        @GET("details/json")
        Call<PlaceResponse> getPlaceDetails(@Query("placeid") String placeID, @Query("key") String Key);
    }
}
