package com.example.bhaumik.spendsmart;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.FriendsExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class friends_detail extends AppCompatActivity {

    TextView frndName;
    TextView frndEmail;

    TextView OwesYou;
    private FirebaseAuth mAuth;
    String currentUserId;
    DatabaseReference dbFriendsRef;
    String amount;
    Button settleUpButton;
    EditText amountToBeSettled;
    double totalAmount=0;
    double AmntforValidation=0;
    String oweDisplayText;
    String recievedemail;
    double amountFrmCurrUser=0;
    double amountFrmFrnd=0;

    String   currentUserIdWithoutDot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_row_item);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_dashboard:
                        Intent intent0 = new Intent(friends_detail.this, Dashboard.class);
                        startActivity(intent0);
                        break;

                    case R.id.action_friends:

                        break;

                    case R.id.action_addexpenses:
                        Intent intent2 = new Intent(friends_detail.this, AddExpenses.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(friends_detail.this, ActivityList.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_settings:
                        Intent intent4 = new Intent(friends_detail.this, UserSettings.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });

        Intent intent = getIntent();
        String receivedName=intent.getStringExtra("frndname");
         recievedemail=intent.getStringExtra("frndemail");

        System.out.println("receivedName"+receivedName+"recievedemail"+recievedemail);

        frndName=(TextView)findViewById(R.id.PersonName);
        frndEmail=(TextView)findViewById(R.id.PersonEmail);
        OwesYou=(TextView)findViewById(R.id.owesYou) ;
        settleUpButton=(Button)findViewById(R.id.settleUp);
        amountToBeSettled=(EditText)findViewById(R.id.amountToBeSettled);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUserIdWithoutDot=currentUserId.replace(".","");

        frndName.setText(receivedName);
        frndEmail.setText(recievedemail);

        //DatabaseReference update=FirebaseDatabase.getInstance().getReference("friendships");

        settleUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(amountToBeSettled.getText().toString().isEmpty() ||amountToBeSettled.getText().toString()==null )
               {
                   Toast.makeText(getApplicationContext(), "Settle Amount cannot be Empty! Please Enter some amount", Toast.LENGTH_LONG).show();
               }
        else {
                   double SettlevalEntered = Double.parseDouble(amountToBeSettled.getText().toString());
                   if (SettlevalEntered > AmntforValidation)
                   {
                       Toast.makeText(getApplicationContext(), "Amount to be settle cannot be greater than Owed/Owes Amount", Toast.LENGTH_LONG).show();
                   }
                   else if (SettlevalEntered != 0 && SettlevalEntered == Double.parseDouble(amount)) {
                       DatabaseReference updateExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                               child(currentUserIdWithoutDot).child("friends");


                       updateExpenseValue.child(recievedemail.replace(".", "")).setValue("0");
                      // System.out.println(updateExpenseValue.getKey());

                       DatabaseReference updateFrndExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                               child(recievedemail.replace(".", "")).child("friends");

                       updateFrndExpenseValue.child(currentUserIdWithoutDot).setValue("0");

                       Toast.makeText(getApplicationContext(), "Amount has been settled", Toast.LENGTH_LONG).show();
                   }

                   else
                   {

                       String oweVal=OwesYou.getText().toString();
                       if(oweVal.contains("OWES YOU"))
                       {
                           String amtToBeUpdated=Double.toString(amountFrmCurrUser-SettlevalEntered);

                           DatabaseReference updateExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                                   child(currentUserIdWithoutDot).child("friends");


                           updateExpenseValue.child(recievedemail.replace(".", "")).setValue(amtToBeUpdated);
                       }
                       else if (oweVal.contains("YOU OWE"))
                       {
                           String amtToBeUpdated=Double.toString(amountFrmFrnd-SettlevalEntered);
                           DatabaseReference updateFrndExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                                   child(recievedemail.replace(".", "")).child("friends");

                           updateFrndExpenseValue.child(currentUserIdWithoutDot).setValue(amtToBeUpdated);

                       }


                       Toast.makeText(getApplicationContext(), "Amount has been settled by $"+SettlevalEntered, Toast.LENGTH_LONG).show();
                   }

               }

                amountToBeSettled.setText(null);
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        dbFriendsRef= FirebaseDatabase.getInstance().getReference("user_friends");

        dbFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            try{
                for (DataSnapshot frndSnap : dataSnapshot.getChildren()) {

                    if (frndSnap.getKey().equalsIgnoreCase(currentUserId.replace(".", ""))) {
                        FriendsExpense udm = frndSnap.getValue(FriendsExpense.class);

                        //System.out.println("gugigigigigi "+udm.getFriends().entrySet().size());

                        for (Map.Entry<String, String> entry : udm.getFriends().entrySet()) {
                            if (entry.getKey().equalsIgnoreCase(recievedemail.replace(".", ""))) {
                                // System.out.println("entry.getValue()******"+entry.getValue());
                                amountFrmCurrUser = Double.parseDouble(entry.getValue());
                            }
                        }
                    } else if (frndSnap.getKey().equalsIgnoreCase(recievedemail.toString().replace(".", ""))) {
                        FriendsExpense udm = frndSnap.getValue(FriendsExpense.class);

                        for (Map.Entry<String, String> entry : udm.getFriends().entrySet()) {
                            if (entry.getKey().equalsIgnoreCase(currentUserId.replace(".", ""))) {
                                 //System.out.println("entry.getValue()******"+entry.getValue());
                                amountFrmFrnd = Double.parseDouble(entry.getValue());
                            }
                        }
                    }


                }

                System.out.println("amountFrmFrnd" + amountFrmFrnd + "amountFrmCurrUser" + amountFrmCurrUser);

                totalAmount = amountFrmCurrUser - amountFrmFrnd;

                if (amountFrmCurrUser > amountFrmFrnd) {
                    AmntforValidation = amountFrmCurrUser - amountFrmFrnd;
                    // totalAmount=amountFrmCurrUser-amountFrmFrnd;
                    amount = Double.toString(amountFrmCurrUser - amountFrmFrnd);

                    OwesYou.setText("OWES YOU $" + amount);
                } else if (amountFrmCurrUser < amountFrmFrnd) {
                    AmntforValidation = amountFrmFrnd - amountFrmCurrUser;
                    //totalAmount=amountFrmCurrUser-amountFrmFrnd;
                    amount = Double.toString(amountFrmFrnd - amountFrmCurrUser);

                    OwesYou.setText("YOU OWE $" + amount);
                } else {

                    OwesYou.setText("SETTLED UP");
                }

            }
            catch(Exception e)
            {

            }
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                Intent intent7 = new Intent(friends_detail.this, LoginActivity.class);
                startActivity(intent7);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
