package com.example.thehabitcontroller_project;

import static org.junit.Assert.assertEquals;
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
                Log.d("TestLogin","Username: "+u.getUserName());
                assertEquals("Test",u.getUserName());
                return;
            }
        });
        Thread.sleep(2000);
        loginUser.signOut();
        Thread.sleep(1000);
    }

    //@Test
    public void testRegister() throws InterruptedException {
        String username=String.valueOf(Instant.now().getEpochSecond());
        User newUser = User.Register(username+"@test.com",username, "123456");
        Thread.sleep(4000);
    }

    public void testRegisterTU() throws InterruptedException {
        String username=String.valueOf(Instant.now().getEpochSecond());
        User newUser = User.Register("test@test.com","Test", "123456");
        Thread.sleep(4000);
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
        ArrayList<User> gres=new ArrayList<>();
        User.searchUser("Te", new User.UserSearchListener() {
            @Override
            public void onSearchComplete(ArrayList<User> result) {
                gres.addAll(result);
            }
        });

        Thread.sleep(3000);
        boolean flag=false;
        for (User u:gres){
            Log.d("UserSearchTest",u.toString());
            if (u.getUserName().compareTo("Test")==0) {
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
                u.follow("DhrqWWLnbxNxGhGc7PTEcthcigw1");
            }
        });
        Thread.sleep(5000);
    }
}
