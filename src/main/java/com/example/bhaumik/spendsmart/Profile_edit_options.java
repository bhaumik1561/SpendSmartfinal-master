package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Profile_edit_options extends AppCompatActivity {
    private static final String TAG = "Profile_edit_options";

    EditText Name, Email, Conatct;
    Button Edit;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userId;
    List<users> registeredUserList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_options);

        Log.d("Hello","Hello");

        Edit = (Button) findViewById(R.id.EditInfo);
        Name = (EditText) findViewById(R.id.Name1);
        Email = (EditText) findViewById(R.id.Email1);
        Conatct = (EditText) findViewById(R.id.Contact1);

        mAuth = FirebaseAuth.getInstance();
        database  = FirebaseDatabase.getInstance();
        myRef  = database.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();




        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName, newEmail, newContact;
                newName= Name.getText().toString();
                newEmail = Email.getText().toString();
                newContact = Conatct.getText().toString();
                if(newName.equals("")){
                    toastMessage("Enter Name");

                }
                else if(newEmail.equals("")){
                    toastMessage("Enter Email");


                }
                else if(newContact.equals("")){
                    toastMessage("Enter Contact");

                }else {
                    myRef.child(userId).child("contact").setValue(newContact);
                    myRef.child(userId).child("email").setValue(newEmail);
                    myRef.child(userId).child("name").setValue(newName);
                    toastMessage("Data Updated");
                }



            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

//                String contact=dataSnapshot.child(userId).getValue().toString();
//                String email = dataSnapshot.child(userId).getValue().toString();
//                String name = dataSnapshot.child(userId).getValue().toString();
//                Name.setText(name);
//                Email.setText(email);
//                Conatct.setText(contact);
//                for (DataSnapshot frndSnap : dataSnapshot.getChildren()) {
//                    users udm = frndSnap.getValue(users.class);
//
//                    if (udm.getEmail() != null && !udm.getEmail().equalsIgnoreCase(currentUserId))
//                    {
//                        users udm1 = new users();
//                        udm1.setContact(udm.getContact());
//                        udm1.setEmail(udm.getEmail());
//                        udm1.setName(udm.getName());
//
//
//                        registeredUserList.add(udm1);
//                    }
//                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.action_dashboard:
                        Intent intent0 = new Intent(Profile_edit_options.this, Dashboard.class);
                        intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent0);
                        finish();
                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(Profile_edit_options.this, AddFriends.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.action_addexpenses:
                        Intent intent4 = new Intent(Profile_edit_options.this, AddExpenses.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent4);
                        finish();
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(Profile_edit_options.this, ActivityList.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.action_settings:

                        break;
                }


                return false;
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void toastMessage(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
