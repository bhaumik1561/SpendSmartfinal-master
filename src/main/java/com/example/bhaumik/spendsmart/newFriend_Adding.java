package com.example.bhaumik.spendsmart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.MessageModel;
import com.example.bhaumik.spendsmart.helper.UserDbFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import com.example.bhaumik.spendsmart.helper.UsersDataModel;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class newFriend_Adding extends AppCompatActivity
{
    private static final String TAG = "newFriend_Adding";
//    private EditText name;
    private AutoCompleteTextView name;
    private EditText contactNo;
    private EditText Email;
    private Button btnAddFriend;
    private String currentUserId;
    static final int PICK_CONTACT_REQUEST = 1;
    DatabaseReference addfrnddb;
    DatabaseReference adduserfrnddb;
    DatabaseReference userDb;
    private FirebaseAuth mAuth;
    List<users>registeredUserList;
    List<String>list;
    //String personName;
    HashMap<String,String>map;
    DatabaseReference dbusersRef;
    String userId;
    //vars
    private String mUserId;
    private ArrayList<UserDbFormat> mUsers;
    String currentUserName;
    public static String serverkey="AAAAJAdVtV4:APA91bFq48hXi-akRCAIphzS4r0spGS9BKyAb9Q7qs7orGVYBx18-Vw5EVlpC7l6pLM1eS84eS3XgocWeY9r59pxt0UBVdTTIvOfTJkRBH7vXwb_rqahhX2RpMgfR07saFNQYH8x-mKx";
    public static String senderid="154741880158";



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend__adding);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //TextView title = (TextView) findViewById(R.id.activityTitle1);
        //title.setText("This is Add Friends Activity");

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
                        Intent intent0 = new Intent(newFriend_Adding.this, Dashboard.class);
                        startActivity(intent0);
                        finish();
                        break;

                    case R.id.action_friends:

                        break;

                    case R.id.action_addexpenses:
                        Intent intent2 = new Intent(newFriend_Adding.this, AddExpenses.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(newFriend_Adding.this, ActivityList.class);
                        startActivity(intent3);
                        finish();
                        break;

                    case R.id.action_settings:
                        Intent intent4 = new Intent(newFriend_Adding.this, UserSettings.class);
                        startActivity(intent4);
                        finish();
                        break;
                }
                return false;
            }
        });

       // name=(EditText)findViewById(R.id.Name);
        contactNo=(EditText)findViewById(R.id.ContactNo2);
        Email   =(EditText)findViewById(R.id.Email2);
        btnAddFriend=(Button)findViewById(R.id.addFriend);

        addfrnddb=FirebaseDatabase.getInstance().getReference("friendships");
        userDb = FirebaseDatabase.getInstance().getReference("users");
       adduserfrnddb=FirebaseDatabase.getInstance().getReference("user_friends");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUserName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbusersRef= FirebaseDatabase.getInstance().getReference("users");

        list=new ArrayList<String>();
        map=new HashMap<String,String>();
        registeredUserList=new ArrayList<users>();

        ArrayAdapter<String> name_adapter=new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,list);
        name = (AutoCompleteTextView) findViewById(R.id.Name);
        name.setThreshold(1);
        name.setAdapter(name_adapter);


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("log", "before");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                for(Map.Entry<String,String>entry:map.entrySet())
                {

                    if(entry.getKey().equalsIgnoreCase(s.toString()))
                    {

                        Email.setText(entry.getValue());
                    }
                }
            }
        });


        /*findViewById(R.id.Name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(pickContact, PICK_CONTACT_REQUEST);
            }
        });*/



        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

//                Toast.makeText(newFriend_Adding.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

               // System.out.println("Hi");
                addfriendInDB();

                initFCM();


                name.setText(null);
                contactNo.setText(null);
                Email.setText(null);

            }
        });



    }








    private void addfriendInDB()
    {

        String frnd_name=name.getText().toString().trim();
        final String frnd_email=Email.getText().toString().trim();

            //Log.d("inside db Method", "addfriendInDB: ");
        if(!TextUtils.isEmpty(frnd_email))
        {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String strDate = dateFormat.format(date);
            UsersDataModel udm=new UsersDataModel(currentUserId,frnd_email,frnd_name,strDate,strDate);

            String id=addfrnddb.push().getKey();
            Log.d("printing vale of id", "addfriendInDB: "+id);
            addfrnddb.child(id).setValue(udm);
            adduserfrnddb.child(currentUserId.replace(".","")).child("friends").child(frnd_email.replace(".","")).setValue("0");
            adduserfrnddb.child(currentUserId.replace(".","")).child("myValue").setValue("0");
            // for showing current user as friends profile
            UsersDataModel udm1=new UsersDataModel(frnd_email,currentUserId,currentUserName,strDate,strDate);
            String id1=addfrnddb.push().getKey();
            addfrnddb.child(id1).setValue(udm1);
            adduserfrnddb.child(frnd_email.replace(".","")).child("friends").child(currentUserId.replace(".","")).setValue("0");
            adduserfrnddb.child(frnd_email.replace(".","")).child("myValue").setValue("0");



        }

        DatabaseReference msgreference = FirebaseDatabase.getInstance().getReference();

        if(!name.getText().toString().equals("")){

            //Log.d(TAG, "addfriendInDB: get value of friend id"+mUserId);
            //create the new message
            MessageModel message = new MessageModel();
            message.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail().replace(".",""));
            message.setMessage("You are now friends with "+mAuth.getCurrentUser().getDisplayName());
            message.setTimestamp(getTimestamp());

            //insert the new message
            msgreference
                    .child(getString(R.string.dbnode_messages))
                    .child(frnd_email.replace(".",""))
                    .child(Objects.requireNonNull(msgreference.push().getKey()))
                    .setValue(message);

            Toast.makeText(this, "Friend Added Successfully", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "enter a message", Toast.LENGTH_SHORT).show();
        }

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbnode_events));
        if(mAuth.getCurrentUser().getEmail() !=null){
            eventRef
                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail().replace(".",""))
                    .child(Objects.requireNonNull(eventRef.push().getKey()))
                    .child(getString(R.string.field_message))
                    .setValue("You have successfully added "+frnd_name+" as your friend");
            eventRef
                    .child(frnd_email.replace(".",""))
                    .child(Objects.requireNonNull(eventRef.push().getKey()))
                    .child(getString(R.string.field_message))
                    .setValue("You are now friends with "+mAuth.getCurrentUser().getDisplayName());
        }
        else{
            Toast.makeText(this, "Event didnt get added", Toast.LENGTH_SHORT).show();
        }


    }



    protected void onStart() {
        super.onStart();
        dbusersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //friendlist.clear();
                for (DataSnapshot frndSnap : dataSnapshot.getChildren()) {
                    users udm = frndSnap.getValue(users.class);

                    if (udm.getEmail() != null && !udm.getEmail().equalsIgnoreCase(currentUserId))
                    {
                        users udm1 = new users();
                        udm1.setContact(udm.getContact());
                        udm1.setEmail(udm.getEmail());
                        udm1.setName(udm.getName());

                        registeredUserList.add(udm1);

                    }
                }

                for(users udm:registeredUserList)
                {
                    list.add(udm.getName());
                    map.put(udm.getName(),udm.getEmail());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                Intent intent7 = new Intent(newFriend_Adding.this, LoginActivity.class);
                startActivity(intent7);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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

    /**
     * Return the current timestamp in the form of a string
     * @return
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}
