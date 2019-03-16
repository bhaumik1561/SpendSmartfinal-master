package com.example.bhaumik.spendsmart.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bhaumik.spendsmart.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static final String TAG = "EventAdapter";
    private ArrayList<EventDbFormat> mEvents;
    private Context mContext;
    ImageView eventImage;

    public EventAdapter(Context context, ArrayList<EventDbFormat> events) {
        mContext = context;
        mEvents = events;
    }


    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View view = inflater.inflate(R.layout.activity_list_item_event, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        eventImage = view.findViewById(R.id.event_img);
        //return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: getItemCount" +getItemCount());
        if(getItemCount() < 1){
            holder.message.setText("There is no data to display");

        }
        else{
            Log.d(TAG, "onBindViewHolder: "+mEvents.get(position).getMessage() );
            holder.message.setText(mEvents.get(position).getMessage());
            String StringGenerated="";
            if(mEvents.get(position).getMessage().contains("friend")){
                StringGenerated = "friendship";
            }
            else{
                StringGenerated = "discount";
            }
            //add category image
            int id = mContext.getResources().getIdentifier("com.example.minalshettigar.splashscreen:drawable/" + StringGenerated, null, null);
            eventImage.setImageResource(id);

        }

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.event_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: selected event: " + mEvents.get(getAdapterPosition()));


                }
            });
        }
    }
}
