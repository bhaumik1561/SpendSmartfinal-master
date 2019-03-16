package com.example.bhaumik.spendsmart;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public interface ActivityCallback {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    void googleSignOut();
    void googleSignIn();
    void revokeAccess();
    void changeStatusText();
    void googleSignInVisibility();
    void changeStatusSuccess();
    void changeDetailText();
    void changeStatusFail();
    void changeDetailnull();
    void logout();

}
