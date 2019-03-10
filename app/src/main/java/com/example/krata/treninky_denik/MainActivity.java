package com.example.krata.treninky_denik;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetLessonsCallback;
import com.example.krata.treninky_denik.Fragments.ChatFrg;
import com.example.krata.treninky_denik.Fragments.LoginFrg;
import com.example.krata.treninky_denik.Fragments.ManageFrg;
import com.example.krata.treninky_denik.Fragments.NewsFrg;
import com.example.krata.treninky_denik.Fragments.EditFrg;
import com.example.krata.treninky_denik.Fragments.SearchFrg;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private static final int PICK_IMAGE = 100;
    private Uri imageUri;

    UserLocalStore userLocalStore;

    TextView head_nick, head_mail;
    CircularImageView head_pic;

    View head_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        head_view = navigationView.getHeaderView(0);
        head_nick = (TextView) head_view.findViewById(R.id.head_nickName);
        head_mail = (TextView) head_view.findViewById(R.id.head_mail);

        head_pic = (CircularImageView) head_view.findViewById(R.id.head_profilePic);
        head_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        userLocalStore = new UserLocalStore(this);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (savedInstanceState == null)
//        {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new NewsFrg()).commit();
//            navigationView.setCheckedItem(R.id.nav_news);
//            //return;
//        }

        if(authenticate())
            displayUserDetails();
        getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new NewsFrg()).commit();
        navigationView.setCheckedItem(R.id.nav_news);
        fetchLessons();
    }

    private void fetchLessons()
    {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchLessonsInBackground(userLocalStore.getLoggedUser(), new GetLessonsCallback() {
            @Override
            public void done(ArrayList<Lesson> lessons) {
                userLocalStore.getLoggedUser().setLessons(lessons);
                Data.lessons = lessons;
            }
        });
    }

    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            head_pic.setImageURI(imageUri);
            User user = userLocalStore.getLoggedUser();
            user.profilePic = ((BitmapDrawable)head_pic.getDrawable()).getBitmap();
            ServerRequests serverRequests = new ServerRequests(this);
            serverRequests.storePictureInBackground(user.profilePic, user.nick);
        }
    }

    private String encodeImage(Bitmap image)
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        return Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);
    }

    private void displayUserDetails()
    {
        User user = userLocalStore.getLoggedUser();
        head_nick.setText(user.nick);
        head_mail.setText(user.mail);
        if (user.profilePic != null)
            head_pic.setImageBitmap(user.profilePic);
        else
            head_pic.setImageResource(R.mipmap.ic_launcher);
    }

    private boolean authenticate()
    {
        return userLocalStore.isUserLoggedIn();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();
                break;
            case R.id.nav_news:
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new NewsFrg()).commit();
                break;
            case R.id.nav_cal:
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new CalFrg()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new SearchFrg()).commit();
                break;
            case R.id.nav_lessons:
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new ManageFrg()).commit();
//                if (userLocalStore.isUserLoggedIn() && userLocalStore.getLoggedUser().trainer)
//                else
//                    Toast.makeText(this, "Do téhle sekce nemáte přístup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_signout:
                userLocalStore.clearUserData();
                displayUserDetails();
                Toast.makeText(this, "Uživatel odhlášen", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new NewsFrg()).commit();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Data.fragments.empty() || Data.fragments.size() == 1)
        {
            super.onBackPressed(); //close the app
        }
        else
        {
            Data.fragments.pop();
            String className = Data.fragments.pop();
            Object instanceOfMyClass = null;
            try {
                Class myClass = Class.forName(className);

                Class[] types = {Double.TYPE, this.getClass()};
                Constructor constructor = myClass.getConstructor(types);

                instanceOfMyClass = constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, (Fragment)instanceOfMyClass).commit();
            Toast.makeText(this, className, Toast.LENGTH_SHORT).show();
        }
    }
}
