package com.example.bhaumik.spendsmart.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bhaumik.spendsmart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<UsersDataModel> usersDataModelList = null;
    private ArrayList<UsersDataModel> arrayListUsersDataModel;

    public FriendListViewAdapter(Context context, List<UsersDataModel> usersDataModelList) {
        mContext = context;
        this.usersDataModelList = usersDataModelList;
        inflater = LayoutInflater.from(mContext);
        this.arrayListUsersDataModel = new ArrayList<UsersDataModel>();
        this.arrayListUsersDataModel.addAll(usersDataModelList);
    }

    public class ViewHolder {
        TextView name;
        TextView email;
    }

    @Override
    public int getCount() {
        return usersDataModelList.size();
    }

    @Override
    public UsersDataModel getItem(int position) {
        return usersDataModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.friend_list_layout, null);
            holder.name = (TextView) view.findViewById(R.id.textViewName);
            holder.email = (TextView) view.findViewById(R.id.textViewEmail);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(usersDataModelList.get(position).getFrndName());
        holder.email.setText(usersDataModelList.get(position).getFriendId());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        usersDataModelList.clear();
        if (charText.length() == 0) {
            usersDataModelList.addAll(arrayListUsersDataModel);
        } else {
            for (UsersDataModel wp : arrayListUsersDataModel) {
                if (wp.getFrndName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    usersDataModelList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
