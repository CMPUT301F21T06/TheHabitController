package com.example.thehabitcontroller_project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;

    public interface UserAuthListener {
        /**
         * Triggered when the user login completes
         * @param u User object of the user logged in
         */
        public void onAuthComplete(User u);
    }

    public interface UserSearchListener {
        /**
         * Triggered when the userSearch() operation completes
         * @param result a list of matching users
         */
        public void onSearchComplete(ArrayList<Map<String,Object>> result);
    }

    /**
     * Verify if current user has logged in. If not it throws a SecurityException
     */
    private void assertAuth() {
        if (mAuth.getCurrentUser()==null) {
            throw new SecurityException("Authentication Required.");
        }
    }

    /**
     * Login a user with email and password and create the User object
     * @param email the email used to login
     * @param password the password used to login
     */
    public User(String email, String password, UserAuthListener listener){
        Log.d("User-Login", "start sign in");
        this.mAuth = FirebaseAuth.getInstance();
        Log.d("User-Login", "got instance");
        this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("User-Login", "task complete");
                if (task.isSuccessful()){
                    Log.d("User-Login", "signInWithEmail:success");
                    User.this.fbUser=User.this.mAuth.getCurrentUser();
                    listener.onAuthComplete(User.this);
                } else {
                    Log.w("User-Login", "signInWithEmail:failure", task.getException());
                    throw new SecurityException("Sign-in Failed.");
                }
            }
        });
    }

    /**
     * Create empty User object, not logged in.
     */
    public User(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register a new user with email, username and password and get the User object
     * @param email    email of the new user
     * @param username username of the new user
     * @param password password of the new user
     * @return a User object of the new user
     * @throws SecurityException on failure
     */
    public static User Register(String email, String username, String password){
        User newUser = new User();
        newUser.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("User-Signup", "createUserWithEmail:success");
                    newUser.fbUser = newUser.mAuth.getCurrentUser();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", username);
                    user.put("id", newUser.fbUser.getUid());
                    newUser.setUserName(username);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("User-Signup", "User added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("User-Signup", "Error adding document", e);
                                    throw new SecurityException("Sign-up Failed.");
                                }
                            });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("User-Signup", "createUserWithEmail:failure", task.getException());
                    throw new SecurityException("Sign-up Failed.");
                }
            }
        });
        return newUser;
    };

    /**
     * Search users using a keyword
     * @param keyword the keyword used to search for users
     * @param listener a UserSearchListener to be triggered when search operation completes
     */
    public static void searchUser(String keyword, UserSearchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Map<String,Object>> result= new ArrayList<>();;
        db.collection("users")
                .whereGreaterThanOrEqualTo("name", keyword)
                .whereLessThanOrEqualTo("name", keyword+ '\uf8ff')
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc:task.getResult()){
                                result.add(doc.getData());
                            }
                            listener.onSearchComplete(result);
                        }else{
                            throw new RuntimeException("Search User Failed.");
                        }
                    }
                });
    }

    /**
     * Get the (display) name of current user
     * @return the display name of current user
     */
    public String getUserName() {
        assertAuth();
        return fbUser.getDisplayName();
    }

    /**
     * Updates the (display) name of current user
     * @param newName the new (display) name of current user
     */
    public void setUserName(String newName) {
        assertAuth();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        fbUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("User-Profile","Profile updated.");
            }
        });
    }

    /** follow a specific user by uid
     * @param userId uid of the user to follow
     */
    public void follow(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("UserFollow","Start "+getUserName());
        db.collection("users").whereEqualTo("name", getUserName())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("UserFollow","Complete fetching user data");
                    Log.d("UserFollow", String.valueOf(task.getResult().getDocuments().size()));
                    Map<String,Object> data = task.getResult().getDocuments().get(0).getData();
                    List<String> fList= new ArrayList<>();
                    if (data.containsKey("follow")) {
                        fList.addAll((ArrayList<String>) data.get("follow"));
                    }
                    if (!fList.contains(userId)){
                        fList.add(userId);
                    }
                    Log.d("UserFollow","Data put"+fList.toString());
                    data.put("follow",fList);
                }else{
                    throw new RuntimeException("Failed to follow user "+userId);
                }
            }
        });
        ArrayList<String> fList= new ArrayList<>();
    }

    /**
     * Sign out the current user
     */
    public void signOut(){
        mAuth.signOut();
    }
}
