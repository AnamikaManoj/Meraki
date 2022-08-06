package com.example.meraki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meraki.adapter.MonetizeAdapter;
import com.example.meraki.model.Monetize;
import com.example.meraki.model.Post;
import com.example.meraki.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MonetizeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MonetizeAdapter monetizeAdapter;
    private List<Monetize> monetizeList;
    private EditText addComment;
    private CircleImageView imageProfile;
    private CircleImageView imageProfilePublisher;
    private TextView post;
    private TextView amt;
    private String postId;
    private String authorId;
    private String startAmt;

    FirebaseUser fUser;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monetize);



        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");
        amt=findViewById(R.id.amt);
        imageProfilePublisher=findViewById(R.id.image_profile_publisher);

        ref=FirebaseDatabase.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();




        ref.child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                startAmt = post.getStartAmt();
                amt.setText(startAmt + " is the starting amount set by the artist");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(authorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    imageProfilePublisher.setImageResource(R.mipmap.ic_profilepic);
                } else {
                    Picasso.get().load(user.getImageurl()).into(imageProfilePublisher);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bid for this beautiful work");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        monetizeList = new ArrayList<>();
        monetizeAdapter = new MonetizeAdapter(this, monetizeList, postId,authorId);

        recyclerView.setAdapter(monetizeAdapter);

        addComment = findViewById(R.id.add_comment);
        imageProfile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);




        getUserImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addComment.getText().toString())) {
                    Toast.makeText(MonetizeActivity.this, "No offer made!!", Toast.LENGTH_SHORT).show();
                } else {
                    putOffer();
                }
            }
        });

        getOffer();
    }




    private void getOffer() {

        FirebaseDatabase.getInstance().getReference().child("Monetize").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                monetizeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Monetize monetize = snapshot.getValue(Monetize.class);
                    monetizeList.add(monetize);
                }

                monetizeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void putOffer() {

        HashMap<String, Object> map = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Monetize").child(postId);

        String id = ref.push().getKey();

        map.put("id", id);
        map.put("offer", addComment.getText().toString());
        map.put("admirer", fUser.getUid());



        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MonetizeActivity.this, "Offer sent!", Toast.LENGTH_SHORT).show();
                    addNotification(postId,authorId,addComment.getText().toString() );
                } else {
                    Toast.makeText(MonetizeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        addComment.setText("");

    }

    private void addNotification(String postId, String publisherId,String amt) {

        System.out.println("Called");
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "has made an offer for your work.");
        map.put("postid", postId);
        map.put("isPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId).push().setValue(map);
    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    imageProfile.setImageResource(R.mipmap.ic_profilepic);
                } else {
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}


