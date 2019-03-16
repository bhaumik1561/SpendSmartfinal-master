package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Intent.*;

public class Invite_new_people extends AppCompatActivity {


    EditText Name, Email, Contact;
    Button invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_new_people);

        Name = (EditText) findViewById(R.id.Name);
        Email = (EditText) findViewById(R.id.Email2);
        Contact = (EditText) findViewById(R.id.ContactNo2);
        invite = (Button) findViewById(R.id.invite);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, contactNo;

                if(Name.getText().equals(null) || Email.getText().equals(null)||Contact.getText().equals(null)){
                    showToast("Enter all 3 fields");
                }else{
                    email = Email.getText().toString();
                    Intent intent =new Intent(ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL,email);
                    //intent.putExtra(Intent.EXTRA_EMAIL,email);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"App Invitation");
                    intent.putExtra(Intent.EXTRA_TEXT,"Enjoy the new world of splitting bills, just by clicking a picture");
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent,"choose an email client"));

                    contactNo = Contact.getText().toString();
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",contactNo,null));
                    intent1.putExtra("sms_body", "Hey there we have develop an amazing app, want to use it ?");
                    startActivity(intent1);
                }
            }
        });


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
                        Intent intent0 = new Intent(Invite_new_people.this, Dashboard.class);
                        startActivity(intent0);
                        break;

                    case R.id.action_friends:
                        Intent intent1 = new Intent(Invite_new_people.this, AddFriends.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_addexpenses:
                        Intent intent4 = new Intent(Invite_new_people.this, AddExpenses.class);
                        startActivity(intent4);
                        break;

                    case R.id.action_activity:
                        Intent intent3 = new Intent(Invite_new_people.this, ActivityList.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_settings:
                        Intent intent5 = new Intent(Invite_new_people.this, UserSettings.class);
                        startActivity(intent5);
                        break;
                }


                return false;
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
