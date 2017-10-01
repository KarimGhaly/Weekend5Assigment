package com.example.admin.weekend5assigment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.weekend5assigment.model.GooglePlaces.GoogleResponse;
import com.example.admin.weekend5assigment.model.GooglePlaces.Result;

/**
 * Created by Admin on 9/30/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public static final String TAG = "AdapterTAG";
    Context context;
    GoogleResponse googleResponse;
    RecyclerViewListner mListner;

    public Adapter(Context context, GoogleResponse googleResponse, RecyclerViewListner mListner) {
        this.context = context;
        this.googleResponse = googleResponse;
        this.mListner = mListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Result result = googleResponse.getResults().get(position);
        Glide.with(context).load(result.getIcon()).into(holder.imgView);
        holder.txtName.setText(result.getName());
        if(result.getVicinity() != null) {
            holder.txtAddress.setText(result.getVicinity());
        }
        else if(result.getFormattedaddress()!= null)
        {
            holder.txtAddress.setText(result.getFormattedaddress());
        }
        String OpenStatus="";
        if(result.getOpeningHours()!= null) {
            if (result.getOpeningHours().getOpenNow()!=null) {
                if (result.getOpeningHours().getOpenNow()) {
                    OpenStatus = "Open Now";
                } else {
                    OpenStatus = "Closed";
                }
            }
        }
        holder.txtOpen.setText(OpenStatus);
        String Category=result.getTypes().get(0);

        for (int i = 1; i < result.getTypes().size(); i++) {
            Category+=", "+result.getTypes().get(i);
        }
        holder.txtCategory.setText(Category);
    }
    public void UpdateRVLIST(GoogleResponse newResponse)
    {
        googleResponse = newResponse;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return googleResponse.getResults().size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView txtName;
        TextView txtCategory;
        TextView txtOpen;
        TextView txtAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.itemImgView);
            txtName = itemView.findViewById(R.id.itemTxtName);
            txtCategory = itemView.findViewById(R.id.itemTxtCategory);
            txtOpen = itemView.findViewById(R.id.itemTxtOpen);
            txtAddress = itemView.findViewById(R.id.itemTxtAddress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListner.itemClicked(googleResponse.getResults().get(getLayoutPosition()).getPlaceId());
                }
            });
        }
    }
    interface RecyclerViewListner
    {
        void itemClicked(String placeID);
    }
}
