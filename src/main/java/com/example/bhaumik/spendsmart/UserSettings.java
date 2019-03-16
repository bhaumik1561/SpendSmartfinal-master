package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class UserSettings extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ListView settingList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        settingList = (ListView)findViewById(R.id.settingList);



        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);


        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.settings, android.R.layout.simple_list_item_1);
        settingList.setAdapter(adapter);
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position ==0){

                    Intent intent = new Intent(UserSettings.this,Dashboard.class);
                    startActivity(intent);

                }else if(position == 1){

                    Intent intent = new Intent(UserSettings.this,Profile_edit_options.class);
                    startActivity(intent);

                }else if(position ==2){

                    Intent intent = new Intent(UserSettings.this,Invite_new_people.class);
                    startActivity(intent);

                }else{

                    mAuth.signOut();
                    Intent intent8 = new Intent(UserSettings.this, LoginActivity.class);
                    startActivity(intent8);
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_dashboard:
                        Intent intent0 = new Intent(UserSettings.this, Dashboard.class);
                        startActivity(intent0);
                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(UserSettings.this, AddFriends.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_addexpenses:
                        Intent intent2 = new Intent(UserSettings.this, AddExpenses.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(UserSettings.this, ActivityList.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_settings:
                        break;
                }


                return false;
            }
        });
    }

    public void onBackPressed() {


        Intent i=new Intent(UserSettings.this,Dashboard.class);
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
                Intent intent8 = new Intent(UserSettings.this, LoginActivity.class);
                startActivity(intent8);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
