package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import android.util.Log;

import com.example.thehabitcontroller_project.Community.User;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;

public class UserTest {
    @Test
    public void testLogin() throws InterruptedException {
        User.login("test@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                Log.d("TestLogin","Username: "+u.getUserName());
                assertEquals("FTest LTest",u.getUserName());
                return;
            }
        });
        Thread.sleep(2000);
        User.signOut();
        Thread.sleep(1000);
    }

    @Ignore
    public void testRegister() throws InterruptedException {
        String username=String.valueOf(Instant.now().getEpochSecond());
        User.Register(username+"@test.com",username, "123456", u -> {
            if (u!=null){
                return;
            }
        });
        Thread.sleep(4000);
    }

    @Test
    public void testSetUserName() throws InterruptedException{
        User.login("test3@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User loginUser) {
                User.setUserName("Mary Brown");

                try{
                    Thread.sleep(8000);
                } catch (Exception e){

                }

                User.signOut();
            }
        });
        Thread.sleep(10000);
    }

    @Test
    public void testUserSearch() throws InterruptedException{
        ArrayList<User> gres=new ArrayList<>();
        User.searchUser("FTe", new User.UserSearchListener() {
            @Override
            public void onSearchComplete(ArrayList<User> result) {
                gres.addAll(result);
            }
        });

        Thread.sleep(3000);
        boolean flag=false;
        for (User u:gres){
            Log.d("UserSearchTest",u.toString());
            if (u.getUserName().compareTo("FTest LTest")==0) {
                return;
            }
        }
        fail("User with 'FTe' in display name is not found");
    }

    @Ignore
    public void testUserFollow() throws InterruptedException{
        User.login("test@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                User.getUserFromId("BowQxXFCfDei5cR35yZ4Obrv2nG2", new User.UserDataListener() {
                    @Override
                    public void onDataChange(User result) {
                        Log.d("TestUserFollow",result.getUserName());
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e){}

                        User.setCurrentUser(u);
                        User.requestFollow(result);
                    }
                });
            }
        });
        Thread.sleep(4000);
    }

    @Ignore
    public void testUserAcceptFollow() throws InterruptedException{
        User.login("test3@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                User.setCurrentUser(u);
                User.getUserFromId("E5zQK50iYiPeBkJR1vxfTe11cgH3", new User.UserDataListener() {
                    @Override
                    public void onDataChange(User result) {
                        Log.d("TestUserAcceptFollow",result.getUserName());
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e){}

                        User.setCurrentUser(u);
                        User.acceptFollow(result);
                    }
                });
            }
        });
        Thread.sleep(4000);
    }

    @Ignore
    public void testUserFollowing() throws InterruptedException{
        ArrayList<User> a = new ArrayList<>();
        User.login("test@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                User.setCurrentUser(u);
                User.getUserFromId("Zq9OMCHJbNbwb4T1wXhuESb9UyE2", new User.UserDataListener() {
                    @Override
                    public void onDataChange(User result) {
                        Log.d("TestUserFollowing",result.getUserName());
                        try {
                            Thread.sleep(2000);
                        }catch (Exception e){}

                        User.setCurrentUser(u);
                        u.getFollowing(new User.UserListDataListener() {
                            @Override
                            public void onDataChange(ArrayList<User> result) {
                                a.clear();
                                a.addAll(result);
                            }
                        });
                    }
                });
            }
        });
        Thread.sleep(4000);
        for (User ui:a){
            Log.d("TestUserFollowing",ui.getEmail());
            if (ui.getEmail().compareTo("test2@test.com")==0){
                Log.d("TestUserFollowing","Match");
                return;
            }
        }
        fail("User not found in the following list.");
    }

    @Ignore
    public void testUserFollowReq() throws InterruptedException{
        ArrayList<User> a = new ArrayList<>();
        User.login("test2@test.com", "asdf1234", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                User.setCurrentUser(u);
                u.getFollowRequests(new User.UserListDataListener() {
                    @Override
                    public void onDataChange(ArrayList<User> result) {
                        a.clear();
                        a.addAll(result);
                    }
                });
            }
        });
        Thread.sleep(3000);
        for (User ui:a){
            Log.d("TestUserFollowReq",ui.getEmail());
            if (ui.getEmail().compareTo("test3@test.com")==0){
                return;
            }
        }
        fail("User not found in follow requests list.");
    }
}
