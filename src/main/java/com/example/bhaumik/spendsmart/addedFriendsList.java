package com.example.bhaumik.spendsmart;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.example.bhaumik.spendsmart.helper.UsersDataModel;

public class addedFriendsList extends ArrayAdapter<UsersDataModel>
{
private Activity context;
private List<UsersDataModel> friendsList;

public addedFriendsList(Activity context,List<UsersDataModel>friendsList)
{
    super(context,R.layout.friend_list_layout,friendsList);
    this.context=context;
    this.friendsList=friendsList;
}


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View listViewFriends=inflater.inflate(R.layout.friend_list_layout,null,true);

        TextView textViewName=(TextView) listViewFriends.findViewById(R.id.textViewName);
        TextView textViewEmail=(TextView) listViewFriends.findViewById(R.id.textViewEmail);

        UsersDataModel udm=friendsList.get(position);

        textViewName.setText(udm.getFrndName());
        textViewEmail.setText(udm.getFriendId());

        return listViewFriends;
    }
}
