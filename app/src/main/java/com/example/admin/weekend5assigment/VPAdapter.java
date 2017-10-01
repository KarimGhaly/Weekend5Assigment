package com.example.admin.weekend5assigment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.admin.weekend5assigment.model.PlaceDetails.PlaceResponse;

/**
 * Created by Admin on 9/30/2017.
 */

public class VPAdapter extends PagerAdapter {
    public static final String TAG = "AdapterTAG";
    PlaceResponse placeResponse;
    Context context;
    public static final String APIKEY = "AIzaSyBVSeJsuWIzBKOjBhyfgzBVW6cn2jkYSbI";

    public VPAdapter(PlaceResponse placeResponse, Context context) {
        this.placeResponse = placeResponse;
        this.context = context;
    }

    @Override
    public int getCount() {
        return placeResponse.getResult().getPhotos().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object) ;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_layout,container,false);
        ImageView imgView = (ImageView) view.findViewById(R.id.photoImgView);
        String ImageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=250&photoreference="+placeResponse.getResult().getPhotos().get(position).getPhotoReference()+"&key="+APIKEY;
        Glide.with(context).load(ImageURL).into(imgView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
