package com.example.bhaumik.spendsmart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.UsersDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddExpenses extends AppCompatActivity {

    private static final String TAG = "AddExpensesActivity";

    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    public static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ViewPager mViewPager;

    List<UsersDataModel> friendList;
    List<UsersDataModel> selectedFriendList;
    ListView listViewSelectedFriends;
    ListView listViewFriends;
    String currentUserId;
    DatabaseReference dbFriendsRef;
    FirebaseAuth mAuth;
    Fragment fragment;

    ArrayList<String>list=new ArrayList<String>();
    HashMap<String,String>map=new HashMap<String,String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expenses_activity);



        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mAuth = FirebaseAuth.getInstance();
        dbFriendsRef= FirebaseDatabase.getInstance().getReference("friendships");

        friendList = new ArrayList<>();
        selectedFriendList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getEmail();



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.action_dashboard:
                        Intent intent0 = new Intent(AddExpenses.this, Dashboard.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        finish();
                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(AddExpenses.this, AddFriends.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.action_addexpenses:

                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(AddExpenses.this, ActivityList.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.action_settings:
                        Intent intent4 = new Intent(AddExpenses.this, UserSettings.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent4);
                        finish();
                        break;
                }


                return false;
            }
        });


        Bundle bundle = new Bundle();
        bundle.putStringArrayList("peopleName", list);
        ManualFragment manuals=new ManualFragment();
        manuals.setArguments(bundle);


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.myexp, manuals)
                .commit();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onBackPressed() {
        Intent i=new Intent(AddExpenses.this,Dashboard.class);
        startActivity(i);

        super.onBackPressed();


        return;
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
                Intent intent5 = new Intent(AddExpenses.this, LoginActivity.class);
                startActivity(intent5);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    public int getTask() {
        return getIntent().getFlags();
    }



    @Override
    protected void onStart() {
        super.onStart();
        dbFriendsRef= FirebaseDatabase.getInstance().getReference("friendships");
        dbFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                //selectedFriendList.clear();
                for(DataSnapshot frndSnap:dataSnapshot.getChildren())
                {
                    UsersDataModel udm=frndSnap.getValue(UsersDataModel.class);

                    if(udm.getUserId()!=null && udm.getUserId().equalsIgnoreCase(currentUserId))
                    {
                        UsersDataModel udm1=new UsersDataModel();
                        udm1.setFriendId(udm.getFriendId());
                        udm1.setUserId(udm.getUserId());
                        udm1.setFrndName(udm.getFrndName());
                        udm1.setPic(udm.getPic());

                        selectedFriendList.add(udm1);
                        friendList.add(udm1);

                    }
                }

                for(UsersDataModel udm:selectedFriendList)
                {

                    //System.out.println("udm.getFrndName()"+udm.getFrndName());
                    list.add(udm.getFrndName());
                    map.put(udm.getFrndName(),udm.getFriendId());



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }







    public void addTransactionToDb(String userId, double amount) {

    }
}
