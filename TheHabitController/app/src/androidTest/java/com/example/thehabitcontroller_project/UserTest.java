package com.example.thehabitcontroller_project;

import static org.junit.Assert.fail;

import android.util.Log;

import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class UserTest {
    @Test
    public void testLogin() throws InterruptedException {
        User loginUser = new User("test@test.com", "123456", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                return;
            }
        });
        Thread.sleep(3000);
        loginUser.signOut();
    }

    @Test
    public void testRegister() throws InterruptedException {
        String username=String.valueOf(Instant.now().getEpochSecond());
        User newUser = User.Register(username+"@test.com",username, "123456");
        Thread.sleep(4000);
    }

    @Test
    public void testAuth() throws InterruptedException {
        User loginUser = new User("test@test.com", "123456", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                Log.d("UserTest",u.getUserName());
            }
        });
        Thread.sleep(2000);
        loginUser.signOut();
        Thread.sleep(1000);
    }

    @Test
    public void testSetUserName() throws InterruptedException{
        User loginUser = new User("test@test.com", "123456", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User loginUser) {
                loginUser.setUserName("Test");

                try{
                    Thread.sleep(2000);
                } catch (Exception e){

                }

                loginUser.signOut();
            }
        });
        Thread.sleep(3000);
    }

    @Test
    public void testUserSearch() throws InterruptedException{
        ArrayList<Map<String, Object>> gres=new ArrayList<>();
        User.searchUser("te", new User.UserSearchListener() {
            @Override
            public void onSearchComplete(ArrayList<Map<String, Object>> result) {
                gres.addAll(result);
            }
        });

        Thread.sleep(3000);
        boolean flag=false;
        for (Map<String, Object> u:gres){
            Log.d("UserSearchTest",u.toString());
            if (((String) u.get("name")).compareTo("test")==0) {
                Log.d("UserSearchTest","Found");
                return;
            }
        }
        fail("User 'test' is not found");
    }

    @Test
    public void testUserFollow() throws InterruptedException{
        User tu= new User("test@test.com", "123456", new User.UserAuthListener() {
            @Override
            public void onAuthComplete(User u) {
                u.follow("znxqA2XR5JUyozWcsfhfQmCpSLt1");
            }
        });
        Thread.sleep(5000);
    }
}
