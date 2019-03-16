package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.Item;
import com.example.bhaumik.spendsmart.helper.UserDbFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    TextView category1,category2,category3,category4,category5,category6,category7,expenses;
    ImageView user_profile,cat1_img,cat2_img,cat3_img,cat4_img,cat5_img,cat6_img,cat7_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton addBtn;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference items;
    Double food=0.0,rent=0.0,shopping=0.0,utility=0.0,misc=0.0,grocery=0.0,travel=0.0;
    ValueEventListener vallist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference(getString(R.string.dbnode_users));
        items = database.getReference(getString(R.string.dbnode_items));
        Intent myintent = getIntent();
       FirebaseDatabase.getInstance().getReference().keepSynced(true);


        //UI Components
        category1 = findViewById(R.id.category1_val);
        cat1_img = findViewById(R.id.category1_img);
        category2 = findViewById(R.id.category2_val);
        cat2_img = findViewById(R.id.category2_img);
        category3 = findViewById(R.id.category3_val);
        cat3_img = findViewById(R.id.category3_img);
        category4 = findViewById(R.id.category4_val);
        cat4_img = findViewById(R.id.category4_img);
        category5 = findViewById(R.id.category5_val);
        cat5_img = findViewById(R.id.category5_img);
        category6 = findViewById(R.id.category6_val);
        cat6_img = findViewById(R.id.category6_img);
        category7 = findViewById(R.id.category7_val);
        cat7_img = findViewById(R.id.category7_img);
        expenses = findViewById(R.id.expenses);
        user_profile = findViewById(R.id.user_profile);
        addBtn = findViewById(R.id.btnAdd);




        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance((R.style.ExpandedAppBar));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance((R.style.CollapsedAppBar));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Dashboard.this, AddExpenses.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        initFCM();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.action_dashboard:

                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(Dashboard.this, AddFriends.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent1);
                        break;

                    case R.id.action_addexpenses:
                        Intent intent2 = new Intent(Dashboard.this, AddExpenses.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(Dashboard.this, ActivityList.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        break;

                    case R.id.action_settings:
                        Intent intent4 = new Intent(Dashboard.this, UserSettings.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });
        getUserDetail(user.getUid());




        vallist=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    shopping=0.0;
                    food=0.0;
                    rent=0.0;
                    grocery=0.0;
                    misc=0.0;
                    utility=0.0;
                    travel=0.0;
                    for (DataSnapshot itemsnap : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: itemsnap"+itemsnap);
                        Item item=itemsnap.getValue(Item.class);


                        if(itemsnap.hasChild("category")){
                            if(item.getCategory().equalsIgnoreCase("Shopping")){
                                shopping = shopping + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Food")){
                                food = food + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Rent")){
                                rent = rent + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Grocery")){
                                grocery = grocery + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Travel")){
                                travel = travel + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Miscellaneous")){
                                misc = misc + Double.parseDouble(item.getItem_price());
                            }
                            else if(item.getCategory().equalsIgnoreCase("Utility")){
                                utility = utility + Double.parseDouble(item.getItem_price());
                            }
                        }

                    }
                    Log.d(TAG, "onDataChange: lets check the value"+ food);
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



    }

    private void getUserDetail(final String userID) {

        Log.d(TAG, "getUserDetail:userID "+ users.child(userID).toString());
        System.out.println(userID + users.child(userID).toString());

        users.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot.hasChild("name")){
                        users.child(userID).removeEventListener(this);
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                        UserDbFormat current_user = dataSnapshot.getValue(UserDbFormat.class);
                        //Set image
                        if(current_user.getPic()!=null){
                            File file = new File(Objects.requireNonNull(current_user).getPic());
                            Picasso.with(getBaseContext()).load(file)
                                    .into(user_profile);
                        }
                        collapsingToolbarLayout.setTitle(current_user.getName());
                        Log.d(TAG, "onDataChange: Name is "+ current_user.getEmail() +" "+ current_user.getPic());
                        System.out.println("name is : "+ current_user.getEmail());
                        expenses.setText("My Total Expenditure: "+ (shopping+food+grocery+travel+misc+utility+rent));
                        category1.setText("Shopping: "+shopping);
                        category2.setText("Food: "+food);
                        category3.setText("Grocery: "+grocery);
                        category4.setText("Travel: "+travel);
                        category5.setText("Miscellaneous: "+misc);
                        category6.setText("Utility: "+utility);
                        category7.setText("Rent: "+rent);
                    }
                }catch (Exception e){

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if(mAuth.getCurrentUser().getEmail()!=null){
            reference.child(getString(R.string.dbnode_notification))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",""))
                    .child(getString(R.string.field_messaging_token))
                    .setValue(token);
            reference.child(getString(R.string.dbnode_notification))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",""))
                    .child(getString(R.string.field_user_name))
                    .setValue(mAuth.getCurrentUser().getDisplayName());
        }
        else{
            Toast.makeText(this, "Event didnt get added", Toast.LENGTH_SHORT).show();
        }

    }


    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);
        sendRegistrationToServer(token);

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
                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
protected void onDestroy() {

    super.onDestroy();
    items.child(user.getEmail().replace(".","")).removeEventListener(vallist);
}
    @Override
    protected void onPause() {
        super.onPause();
        items.child(user.getEmail().replace(".","")).removeEventListener(vallist);
    }

    @Override
    protected void onResume() {
        super.onResume();
       getUserDetail(user.getUid());

     items.child(user.getEmail().replace(".","")).addValueEventListener(vallist);
    }

    @Override
    public void onStart() {
        super.onStart();

        getUserDetail(user.getUid());
        items.child(user.getEmail().replace(".","")).addValueEventListener(vallist);
    }

    public void onBackPressed(){
        //items.child(user.getEmail().replace(".","")).addValueEventListener(vallist);
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



}
