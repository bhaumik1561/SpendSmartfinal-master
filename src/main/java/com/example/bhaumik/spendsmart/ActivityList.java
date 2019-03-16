package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.EventAdapter;
import com.example.bhaumik.spendsmart.helper.EventDbFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityList extends AppCompatActivity {

    private static final String TAG = "ActivityList";

    private RecyclerView mRecyclerView;

    //vars
    private ArrayList<EventDbFormat> mEvents;
    private EventAdapter mEventAdapter;
    //Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    boolean doubleBackToExitPressedOnce = false;
    DatabaseReference events;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_activity);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        events = database.getReference(getString(R.string.dbnode_events));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setupEventList();
        getEventList(mAuth.getCurrentUser().getEmail().replace(".",""));

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.action_dashboard:
                        Intent intent0 = new Intent(ActivityList.this, Dashboard.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);

                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(ActivityList.this, AddFriends.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.action_addexpenses:
                        Intent intent2 = new Intent(ActivityList.this, AddExpenses.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.action_activity:

                        break;

                    case R.id.action_settings:
                        Intent intent4 = new Intent(ActivityList.this, UserSettings.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent4);
                        finish();
                        break;
                }


                return false;
            }
        });
    }

    private void setupEventList(){
        mEvents = new ArrayList<>();
        mEventAdapter = new EventAdapter(ActivityList.this, mEvents);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mEventAdapter);
    }


    private void getEventList(String userID) throws NullPointerException{
        Log.d(TAG, "getEventList: getting a list of all events");
        System.out.println(userID + events.child(userID).toString());

        events.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    EventDbFormat event = snapshot.getValue(EventDbFormat.class);
                    mEvents.add(event);
                    Log.d(TAG, "onDataChange: event data"+ snapshot.getValue());

                }
                mEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {


Intent i=new Intent(ActivityList.this,Dashboard.class);
startActivity(i);

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                mAuth.signOut();
                Intent intent6 = new Intent(ActivityList.this, LoginActivity.class);
                startActivity(intent6);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
