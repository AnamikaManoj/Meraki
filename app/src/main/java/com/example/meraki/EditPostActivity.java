package com.example.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meraki.fragments.Profile;
import com.example.meraki.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class EditPostActivity extends AppCompatActivity {

    private String imageUrl;

    private ImageView close;
    private ImageView imageAdded;
    private TextView postbutton;
    SocialAutoCompleteTextView description;
    private CheckBox money;
    private Boolean monetize;
    private String postID;
    private MaterialEditText startAmt;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        money=findViewById(R.id.checkBox);
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_added);
        postbutton = findViewById(R.id.post);
        description = findViewById(R.id.description);
        startAmt=findViewById(R.id.startamt);


        monetize=money.isChecked();




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        postID=intent.getStringExtra("postId");

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post =snapshot.getValue(Post.class);
                description.setText(post.getDescription());
                Picasso.get().load(post.getImageurl()).into(imageAdded);
                startAmt.setText(post.getStartAmt());
                monetize=post.getMonetize();
                money.setChecked(monetize);
                if(monetize)
                    startAmt.setVisibility(View.VISIBLE);
                else
                    startAmt.setVisibility(View.GONE);
                System.out.println(monetize);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monetize=money.isChecked();
                if(monetize)
                    startAmt.setVisibility(View.VISIBLE);
                else
                    startAmt.setVisibility(View.GONE);
            }
        });

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePost();

            }
        });

    }
    private void updatePost() {
        if(startAmt.getText().equals("")&&money.isChecked()){
            Toast.makeText(EditPostActivity.this, "Please enter a starting amount!", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("description", description.getText().toString());
            map.put("monetize", money.isChecked());
            map.put("startAmt", startAmt.getText().toString());

            FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).updateChildren(map);
            startActivity(new Intent(EditPostActivity.this, Profile.class));
        }
    }
}