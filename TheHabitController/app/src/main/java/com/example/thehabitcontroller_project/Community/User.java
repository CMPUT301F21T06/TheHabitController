package com.example.thehabitcontroller_project.Community;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The User class stores local user information and handles user operations.
 *
 * @author Marcus
 * @version 1.0.0
 */

public class User implements Parcelable, Comparable<User> {
    private static FirebaseAuth mAuth;
    private static FirebaseUser fbUser;
    private static User currentUser=null;
    private String email, name, userId;

    /**
     * Parcel creator
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
        public void onSearchComplete(ArrayList<User> result);
    }

    public interface UserDataListener {
        public void onDataChange(User result);
    }

    public interface UserListDataListener {
        public void onDataChange(ArrayList<User> result);
    }

    /**
     * Check if current user has logged in.
     */
    private static boolean isAuth() {
        if (currentUser==null) {
            return false;
        }
        return true;
    }

    /**
     * Check if current user has logged in. If not, throw SecurityException.
     */
    private static void assertAuth() {
        if (!isAuth()) {
            throw new SecurityException("User not logged in");
        }
    }

    /**
     * Update user object variables with the Map
     * @param map the Map object to apply update with
     */
    private void updateWithMap(Map<String,Object> map) {
        if (map.containsKey("id")) {
            userId=(String) map.get("id");
        }
        if (map.containsKey("name")) {
            name=(String) map.get("name");
        }
        if (map.containsKey("email")) {
            email=(String) map.get("email");
        }
    }

    /**
     * Login a user with email and password and create the User object
     * @param email the email used to login
     * @param password the password used to login
     */
    public static void login(String email, String password, UserAuthListener listener){
        Log.d("User-Login", "start sign in");
        User.mAuth = FirebaseAuth.getInstance();
        Log.d("User-Login", "got instance");
        User.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("User-Login", "task complete");
                if (task.isSuccessful()){
                    Log.d("User-Login", "signInWithEmail:success");
                    User.fbUser=User.mAuth.getCurrentUser();
                    User.setCurrentUser(new User(User.fbUser.getEmail(),
                            User.fbUser.getDisplayName(),
                            User.fbUser.getUid()));
                    listener.onAuthComplete(User.getCurrentUser());
                } else {
                    Log.w("User-Login", "signInWithEmail:failure", task.getException());
                    listener.onAuthComplete(null);
                }
            }
        });
    }

    /**
     * Create empty User object.
     */
    public User(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Create User object, non-local users.
     */
    public User(String email, String name, String userId) {
        this.email=email;
        this.name=name;
        this.userId=userId;
    }

    /**
     * Create User object from Parcel
     * @param parcel
     */
    protected User(Parcel parcel){
        email=parcel.readString();
        name=parcel.readString();
        userId=parcel.readString();
    }

    /**
     * Create a User object with FirebaseUser
     * @param fUser the FirebaseUser to create User object with
     */
    public User(FirebaseUser fUser) {
        this.mAuth=FirebaseAuth.getInstance();
        this.fbUser=fUser;
        this.email=fUser.getEmail();
        this.name=fUser.getDisplayName();
        this.userId=fUser.getUid();
    }

    /**
     * Register a new user with email, username and password and get the User object
     * @param email    email of the new user
     * @param username username of the new user
     * @param password password of the new user
     * @param listener listener when the task completes
     * @return a User object of the new user
     * @throws SecurityException on failure
     */
    public static void Register(String email, String username, String password, UserAuthListener listener){
        final String TAG = "User-Signup";
        Log.d(TAG,"Start user signup: "+email+" "+username);
        User newUser = new User();
        newUser.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    newUser.fbUser = newUser.mAuth.getCurrentUser();
                    newUser.userId = newUser.fbUser.getUid();
                    newUser.name = username;
                    newUser.email = email;
                    User.setCurrentUser(newUser);
                    firstLogin(u -> listener.onAuthComplete(u));
                    User.setUserName(username);
                } else {
                    // If sign up fails, reset currentUser
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    User.setCurrentUser(null);
                }
            }
        });
    };

    /**
     * Get a user object given user id
     * @param userId the user id
     * @param listener the listener for OnDataChange event
     */
    public static void getUserFromId(String userId, UserDataListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User returnUser = new User("","","");

        Task task = db.collection("users").whereEqualTo("id",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Map<String,Object> data=task.getResult().getDocuments().get(0).getData();
                    returnUser.updateWithMap(data);
                    listener.onDataChange(returnUser);
                }
            }
        });
    }

    /**
     * Process first login (new user) routines (create a new document in database to store extra user info)
     */
    public static void firstLogin(UserAuthListener listener){
        Log.d("UserFirstLogin","First Login Event "+currentUser.getUserId());
        // new user
        Map<String, Object> user = new HashMap<>();
        user.put("email",currentUser.getEmail());
        user.put("name", currentUser.getUserName());
        user.put("id", currentUser.getUserId());
        user.put("follower", Arrays.asList());
        user.put("followReq", Arrays.asList());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUserId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("UserFisrtLogin", "User: " + currentUser.getUserId());
                        listener.onAuthComplete(User.getCurrentUser());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserFisrtLogin", "Error adding document", e);
                        listener.onAuthComplete(null);
                    }
                });
    }

    /**
     * Set the current user
     * @param u User to be set as current user
     */
    public static void setCurrentUser(User u){
        currentUser = u;
    }

    /**
     * Get the User object of current user
     * @return the User object
     */
    public static User getCurrentUser(){
        return currentUser;
    }

    /**
     * Search users using a keyword
     * @param keyword the keyword used to search for users
     * @param listener a UserSearchListener to be triggered when search operation completes
     */
    public static void searchUser(String keyword, UserSearchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> result= new ArrayList<>();
        db.collection("users")
                .whereGreaterThanOrEqualTo("name", keyword)
                .whereLessThanOrEqualTo("name", keyword+ '\uf8ff')
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc:task.getResult()){
                                Map<String,Object> dat=doc.getData();
                                result.add(new User((String) dat.get("email"),
                                        (String) dat.get("name"),
                                        (String) dat.get("id")));
                            }
                            listener.onSearchComplete(result);
                        }else{
                            throw new RuntimeException("Search User Failed.");
                        }
                    }
                });
    }

    /**
     * Updates the (display) name of current user
     * @param newName the new (display) name of current user
     */
    public static void setUserName(String newName) {
        assertAuth();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        currentUser.fbUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("User-Profile","Profile updated.");
                currentUser.name = newName;
                Map<String, Object> upd=new HashMap<>();
                upd.put("name", newName);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("id",currentUser.getUserId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d("User-Profile","User profile fetched.");
                            task.getResult().getDocuments().get(0).getReference()
                                    .update(upd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("User-Profile","Username updated in database.");
                                }
                            });
                        } else
                        {
                            Log.d("User-Profile","Fail updating the database.");
                        }
                    }
                });
            }
        });
    }

    /**
     * Send a follow request to target user
     * @param target the target user
     */
    public static void requestFollow(User target) throws RuntimeException{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("RequestFollow",target.getUserId());
        db.collection("users").whereEqualTo("id",target.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("UserAcceptFollow","Complete fetching target user data");
                    DocumentReference d = task.getResult().getDocuments().get(0).getReference();
                    Map<String,Object> upd= new HashMap<>();
                    upd.put("followReq",FieldValue.arrayUnion(currentUser.userId));
                    d.update(upd);
                } else {
                    throw new RuntimeException("Failed to send follow request to user "+target.userId);
                }
            }
        });
    }

    /**
     * Unfollow target user
     * @param target the target user
     */
    public static void unfollow(User target) throws RuntimeException{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Unfollow",target.getUserId());
        db.collection("users").whereEqualTo("id",target.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("UserUnfollow","Complete fetching target user data");
                    DocumentReference d = task.getResult().getDocuments().get(0).getReference();
                    Map<String,Object> upd= new HashMap<>();
                    upd.put("follower",FieldValue.arrayRemove(currentUser.userId));
                    d.update(upd);
                } else {
                    throw new RuntimeException("Failed to unfollow user "+target.userId);
                }
            }
        });
    }



    /** Accept a follow request from user
     * @param user user to add into follower list
     */
    public static void acceptFollow(User user) throws RuntimeException{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("id", currentUser.userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("UserAcceptFollow","Complete fetching current user data");
                    DocumentReference d = task.getResult().getDocuments().get(0).getReference();
                    Map<String,Object> upd= new HashMap<>();
                    upd.put("follower",FieldValue.arrayUnion(user.userId));
                    upd.put("followReq",FieldValue.arrayRemove(user.userId));
                    d.update(upd);
                }else{
                    throw new RuntimeException("Failed to accept user "+user.userId);
                }
            }
        });
    }

    /** Reject a follow request from user
     * @param user user to remove from follow requests list
     */
    public static void rejectFollow(User user) throws RuntimeException{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUser.userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("UserRejectFollow","Complete fetching current user data");
                    DocumentReference d = task.getResult().getReference();
                    Map<String,Object> upd= new HashMap<>();
                    upd.put("followReq",FieldValue.arrayRemove(user.userId));
                    d.update(upd);
                }else{
                    throw new RuntimeException("Failed to reject user "+user.userId);
                }
            }
        });
    }

    /**
     * Get the list of users requesting to follow current user
     * @param dataListener the listener for onDataChange event
     */
    public void getFollowRequests(UserListDataListener dataListener){
        ArrayList<User> fList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task t = db.collection("users").whereEqualTo("id",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<String> rs = (ArrayList<String>) task.getResult().getDocuments().get(0).get("followReq");
                    if (rs!=null){
                        for (String item:rs){
                            Log.d("FRQ",item);
                            User.getUserFromId(item, new UserDataListener() {
                                @Override
                                public void onDataChange(User result) {
                                    fList.add(result);
                                    dataListener.onDataChange(fList);
                                }
                            });
                        }
                    }
                }else{
                    Log.d("UserGetFollowReq","Fail getting the follow request list");
                }
            }
        });
    }

    /**
     * Get the list of users following current user
     * @param dataListener the listener for onDataChange event
     */
    public void getFollowers(UserListDataListener dataListener){
        ArrayList<User> fList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task t = db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<String> rs = (ArrayList<String>) task.getResult().get("follow");
                    if (rs!=null){
                        for (String item:rs){
                            User.getUserFromId(item, new UserDataListener() {
                                @Override
                                public void onDataChange(User result) {
                                    fList.add(result);
                                    dataListener.onDataChange(fList);
                                }
                            });
                        }
                    }
                }else{
                    Log.d("UserGetFollowers","Fail getting the followers list");
                }
            }
        });
    }

    /**
     * Get the list of users the current user is following
     * @param dataListener the listener for onDataChange event
     */
    public void getFollowing(UserListDataListener dataListener){
        ArrayList<User> fList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task t = db.collection("users").whereArrayContains("follower",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot doc:task.getResult()){
                            User au =new User();
                            au.updateWithMap(doc.getData());
                            fList.add(au);
                        }
                    }
                    dataListener.onDataChange(fList);
                }else{
                    Log.d("UserGetFollowing","Fail getting the follow list");
                }
            }
        });
    }

    /**
     * Get the (display) name of a user
     * @return the display name of a user
     */
    public String getUserName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Sign out the user
     */
    public static void signOut(){
        if (currentUser!=null) {
            currentUser.mAuth.signOut();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(userId);
    }

    @Override
    public int compareTo(User user) {
        return userId.compareTo(user.getUserId());
    }
}
