package com.example.bhaumik.spendsmart;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.FriendsExpense;
import com.example.bhaumik.spendsmart.helper.Item;
import com.example.bhaumik.spendsmart.helper.MessageModel;
import com.example.bhaumik.spendsmart.helper.UsersDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.AdapterView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManualFragment extends Fragment {

    EditText inputCategory;
    EditText inputItem;
    EditText inputPrice;
    AutoCompleteTextView searchQuery;

    Button buttonNewItem;
    Button buttonFinish;
    Button buttonRemoveFriend;

    List<UsersDataModel> selectedFriendList = new ArrayList<UsersDataModel>();
    ListView listViewSelectedFriends;
    ListView listViewFriends;
    String currentUserId;
    String itemName;
    double itemPrice;
    MaterialBetterSpinner staticSpinner;
    DatabaseReference addItemToFrnds;
    EditText peopleEmail;
    String splitPeopleEmail;
    DatabaseReference addExpenseValueToFrnds;
    double amountFrmCurrUser;
    double amountFrmFrnd;
    DatabaseReference dbFriendsRef;
    DatabaseReference userDb,myref;
    ArrayList<String>list1=new ArrayList<String>();
    ArrayList<String>list=new ArrayList<String>();

    HashMap<String,String>map=new HashMap<String,String>();
    private FirebaseAuth mAuth;
    View v;
    String dropdownValue;
    String myvalue;
    private static final String TAG = "ManualFragment";
    MaterialBetterSpinner dynamicSpinner;
    String SplitdropdownValue;
    public static String serverkey="AAAAJAdVtV4:APA91bFq48hXi-akRCAIphzS4r0spGS9BKyAb9Q7qs7orGVYBx18-Vw5EVlpC7l6pLM1eS84eS3XgocWeY9r59pxt0UBVdTTIvOfTJkRBH7vXwb_rqahhX2RpMgfR07saFNQYH8x-mKx";
    public static String senderid="154741880158";





    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        View view= inflater.inflate(R.layout.fragment_manual,container,false);
        mAuth = FirebaseAuth.getInstance();
        inputItem = (EditText) view.findViewById(R.id.input_item);
        inputPrice = (EditText) view.findViewById(R.id.input_price);
        //searchQuery = (AutoCompleteTextView) view.findViewById(R.id.input_people);
        peopleEmail=(EditText) view.findViewById(R.id.input_email);
        FirebaseMessaging.getInstance().subscribeToTopic("new");




        buttonFinish = (Button) view.findViewById(R.id.button_finish);
        //buttonRemoveFriend = (Button) view.findViewById(R.id.button_remove_friend);

        staticSpinner = (MaterialBetterSpinner) view.findViewById(R.id.category_manual);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.category_array,
                        android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        staticSpinner.setAdapter(staticAdapter);

        dynamicSpinner = (MaterialBetterSpinner) view.findViewById(R.id.splitBetweenPeople);
        ArrayAdapter<CharSequence> dynamicAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.splitAmount_EquallyOrNot,
                        android.R.layout.simple_spinner_item);

        dynamicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dynamicSpinner.setAdapter(dynamicAdapter);


        addItemToFrnds=FirebaseDatabase.getInstance().getReference("items");
        userDb = FirebaseDatabase.getInstance().getReference(getString(R.string.dbnode_users));
        myref=FirebaseDatabase.getInstance().getReference("notification");
        //dbFriendsRef= FirebaseDatabase.getInstance().getReference("friendships");
        //addExpenseValueToFrnds= FirebaseDatabase.getInstance().getReference("user_friends");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            list1 = bundle.getStringArrayList("peopleName");

        }


        ArrayAdapter<String> name_adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item,list1);
        searchQuery = (AutoCompleteTextView) view.findViewById(R.id.input_people);
        searchQuery.setThreshold(1);
        searchQuery.setAdapter(name_adapter);
        // System.out.println("map.entr----"+map.entrySet().size());


        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
                        peopleEmail.setText(entry.getValue());
                        splitPeopleEmail=new String(entry.getValue());
                        loadData();

                    }
                }
            }
        });

        staticSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dropdownValue = adapterView.getItemAtPosition(position).toString();
                //System.out.println("dropdownValue"+dropdownValue);
                int mSelectedId = position;

            }
        });

        dynamicSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SplitdropdownValue = adapterView.getItemAtPosition(position).toString();
                //System.out.println("dropdownValue"+dropdownValue);
                int mSelectedId = position;

            }
        });





        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save data to db
                itemName = inputItem.getText().toString();
                itemPrice = Double.parseDouble(inputPrice.getText().toString());

                // dropdownValue = staticSpinner.getOnItemSelectedListener().;
                initFCM();

                addItemTofriends(dropdownValue);
                addExpensessToUserFriends();


            /*    Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://spendsmart.firebaseapp.com/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Api api = retrofit.create(Api.class);

                Call<ResponseBody> call = api.sendNotification("frLLuo8eKIE:APA91bF_86da8gvgx_nPLZ5uPkLAnAwOqvFm83pHV2sf1eAKvazkPTJ0alVwGhq1oA8C5aCjJr-dMGANuXA83ueqvXmrJkDGnTYhXh8joyhZPzjtczLgONs8RXrJIJimrBDl4ke43lJh", "FCM event", "FCM body");

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });*/





                // clear form fields
                inputItem.setText(null);
                inputPrice.setText(null);
                searchQuery.setText(null);
                peopleEmail.setText(null);



            }
        });

        return view;
    }



    private void addItemTofriends(final String dropVal)
    {
        try{
            if(!itemName.isEmpty() )
            {
                final int numberOfSplits = 2;
                Item itemObj;
                String id = addItemToFrnds.push().getKey();

                String currentUserIdWithoutDot=currentUserId.replace(".","");
                String username = mAuth.getCurrentUser().getDisplayName();
                //System.out.println("peopleEmail"+peopleEmail.getText().toString());
                String splitPeopleEmailWithoutDot=peopleEmail.getText().toString().replace(".","");
                DatabaseReference msgreference = FirebaseDatabase.getInstance().getReference();
                MessageModel message = new MessageModel();
                message.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail().replace(".",""));


                if(SplitdropdownValue.equalsIgnoreCase("Charge Full Amount to Friend"))
                {
                    itemObj = new Item(itemName,Double.toString(itemPrice),dropVal);
                    //Handling only for bill split among 2 people
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).setValue(itemObj);
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).child("people").child(splitPeopleEmailWithoutDot).setValue("true");
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).child("category").setValue(dropVal);
                    message.setMessage(username + " added " + itemName + " of amount " + Double.toString(itemPrice)+" you owe");
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbnode_events));
                    eventRef
                            .child(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail().replace(".",""))
                            .child(Objects.requireNonNull(eventRef.push().getKey()))
                            .child(getString(R.string.field_message))
                            .setValue("You have added " +itemName+" of amount "+Double.toString(itemPrice)+" owed by your friend");
                    eventRef
                            .child(splitPeopleEmailWithoutDot)
                            .child(Objects.requireNonNull(eventRef.push().getKey()))
                            .child(getString(R.string.field_message))
                            .setValue("You owe "+mAuth.getCurrentUser().getDisplayName()+" added "+itemName+" of amount "+Double.toString(itemPrice));


                }else{
                    itemObj = new Item(itemName,Double.toString(itemPrice/numberOfSplits),dropVal);
                    //Handling only for bill split among 2 people
                    addItemToFrnds.child(currentUserIdWithoutDot).child(id).setValue(itemObj);
                    addItemToFrnds.child(currentUserIdWithoutDot).child(id).child("people").child(splitPeopleEmailWithoutDot).setValue("true");
                    addItemToFrnds.child(currentUserIdWithoutDot).child(id).child("category").setValue(dropVal);
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).setValue(itemObj);
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).child("people").child(splitPeopleEmailWithoutDot).setValue("true");
                    addItemToFrnds.child(splitPeopleEmailWithoutDot).child(id).child("category").setValue(dropVal);
                    message.setMessage(username + " added " + itemName + " of amount " + Double.toString(itemPrice)+" split equally");
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbnode_events));
                    eventRef
                            .child(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail().replace(".",""))
                            .child(Objects.requireNonNull(eventRef.push().getKey()))
                            .child(getString(R.string.field_message))
                            .setValue("You have added " +itemName+" of amount "+Double.toString(itemPrice)+" split equally");
                    eventRef
                            .child(splitPeopleEmailWithoutDot)
                            .child(Objects.requireNonNull(eventRef.push().getKey()))
                            .child(getString(R.string.field_message))
                            .setValue(mAuth.getCurrentUser().getDisplayName()+" added "+itemName+" of amount "+Double.toString(itemPrice)+" split equally");


                }
                message.setTimestamp(getTimestamp());
                //insert the new message
                msgreference
                        .child(getString(R.string.dbnode_messages))
                        .child(splitPeopleEmailWithoutDot)
                        .child(Objects.requireNonNull(msgreference.push().getKey()))
                        .setValue(message);

                Toast.makeText(getContext(), "Item Added Successfully", Toast.LENGTH_SHORT).show();




            }else{
                Toast.makeText(getContext(), "enter a message", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    private void addExpensessToUserFriends()
    {
        double splitItemPrice=0;
        if(SplitdropdownValue.equalsIgnoreCase("Charge Full Amount to Friend"))
        {
            splitItemPrice=itemPrice;

            double addamt=amountFrmCurrUser+splitItemPrice;
            String amtToBeUpdated=Double.toString(addamt);
            String currentUserIdWithoutDot=currentUserId.replace(".","");
            String splitPeopleEmailWithoutDot=peopleEmail.getText().toString().replace(".","");



            DatabaseReference updateExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                    child(currentUserIdWithoutDot);

            updateExpenseValue.child("myValue").setValue(myvalue) ;
            updateExpenseValue.child("friends").child(splitPeopleEmailWithoutDot).setValue(amtToBeUpdated);

        }
        else
        {
            splitItemPrice=itemPrice/2;

            double addamt=amountFrmCurrUser+splitItemPrice;
            String amtToBeUpdated=Double.toString(addamt);



            String currentUserIdWithoutDot=currentUserId.replace(".","");
            String splitPeopleEmailWithoutDot=peopleEmail.getText().toString().replace(".","");


            DatabaseReference updateExpenseValue = FirebaseDatabase.getInstance().getReference("user_friends").
                    child(currentUserIdWithoutDot);

            updateExpenseValue.child("myValue").setValue(myvalue) ;
            updateExpenseValue.child("friends").child(splitPeopleEmailWithoutDot).setValue(amtToBeUpdated);




            double addamtfrndAmt=amountFrmFrnd+splitItemPrice;
            String amtforFriendTobeUpdated=Double.toString(addamtfrndAmt);



        }




    }


    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v=view;
        // init();

        loadData1();
        //loadData();

    }


    public void loadData() {


        addExpenseValueToFrnds = FirebaseDatabase.getInstance().getReference("user_friends");

        //System.out.println("peopleEmail"+peopleEmail.getText().toString());
        final String splitPeopleEmailWithoutDot = peopleEmail.getText().toString().replace(".", "");
        //System.out.println("splitPeopleEmailWithoutDot"+peopleEmail.getText().toString().replace(".", ""));
        addExpenseValueToFrnds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot frndSnap : dataSnapshot.getChildren()) {

                    if (frndSnap.getKey().equalsIgnoreCase(currentUserId.replace(".", ""))) {
                        FriendsExpense udm = frndSnap.getValue(FriendsExpense.class);

                        myvalue=udm.getMyValue();
                        // System.out.println("gugigigigigi "+udm.getFriends().entrySet().size());

                        for (Map.Entry<String, String> entry : udm.getFriends().entrySet()) {
                            if (entry.getKey().equalsIgnoreCase(splitPeopleEmailWithoutDot)) {
                                // System.out.println("entry.getValue()******"+entry.getValue());
                                amountFrmCurrUser = Double.parseDouble(entry.getValue());

                            }
                        }
                    }

                    else if (frndSnap.getKey().equalsIgnoreCase(splitPeopleEmailWithoutDot.toString().replace(".", ""))) {
                        FriendsExpense udm = frndSnap.getValue(FriendsExpense.class);

                        for (Map.Entry<String, String> entry : udm.getFriends().entrySet()) {
                            if (entry.getKey().equalsIgnoreCase(currentUserId.replace(".", ""))) {
                                //System.out.println("entry.getValue()******"+entry.getValue());
                                amountFrmFrnd = Double.parseDouble(entry.getValue());
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadData1() {
        dbFriendsRef = FirebaseDatabase.getInstance().getReference("friendships");
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

                    }
                }

                for(UsersDataModel udm:selectedFriendList)
                {

                    list.add(udm.getFrndName());
                    map.put(udm.getFrndName(),udm.getFriendId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        try {
            reference.child(getString(R.string.dbnode_notification))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ""))
                    .child(getString(R.string.field_messaging_token))
                    .setValue(token);
            reference.child(getString(R.string.dbnode_notification))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ""))
                    .child(getString(R.string.field_user_name))
                    .setValue(mAuth.getCurrentUser().getDisplayName());
        }catch (Exception e){

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
