package com.example.parseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;
    Intent intent;
    ArrayList<String> users = new ArrayList<>();

    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedUri = data.getData();

        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedUri);

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

                byte[] byteArray = out.toByteArray();

                ParseFile file = new ParseFile("image.png",byteArray);

                ParseObject object = new ParseObject("Image");
                object.put("image",file);
                object.put("username",ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null)
                            Toast.makeText(getApplicationContext(),"Image Shared Successfully!!!",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"There has been an issue uploading the Image:(",Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.share)
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                getPhoto();
            }
        }
        else if(item.getItemId()==R.id.logout)
        {
            ParseUser.logOut();
            intent = new Intent(getApplicationContext(),MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        setTitle("List of Users");

        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.usersListListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,users);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseUser user:objects)
                        {
                            users.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                }
                else
                    e.printStackTrace();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),UsersFeed.class);
                intent.putExtra("username",users.get(i));
                startActivity(intent);
            }
        });
    }
/*
    @Override
    public void onBackPressed() {
        if(ParseUser.getCurrentUser()!=null)
        {
            finishAndRemoveTask();
        }
        else
            super.onBackPressed();
    }
 */
}