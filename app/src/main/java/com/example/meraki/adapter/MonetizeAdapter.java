package com.example.meraki.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meraki.HomeActivity;
import com.example.meraki.MonetizeActivity;
import com.example.meraki.R;
import com.example.meraki.model.Comment;
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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MonetizeAdapter extends RecyclerView.Adapter<MonetizeAdapter.ViewHolder> {

    private Context mContext;
    private List<Monetize> mMonetize;

    String postId;
    String authorId;

    private FirebaseUser fUser;

    public MonetizeAdapter(Context mContext, List<Monetize> mMonetize , String postId,String authorId) {
        this.mContext = mContext;
        this.mMonetize = mMonetize;
        this.postId = postId;
        this.authorId=authorId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.monetize_item, parent, false);
        return new MonetizeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final Monetize monetize = mMonetize.get(position);

        holder.comment.setText("I would like to offer "+monetize.getOffer());

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(monetize.getAdmirer()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                holder.username.setText(user.getUsername());
                if (user.getImageurl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_profilepic);
                } else {
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", monetize.getAdmirer());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", monetize.getAdmirer());
                mContext.startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                if(!post.getPublisher().equals(fUser.getUid())){
                    holder.accept.setVisibility(View.GONE);
                    holder.reject.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Confirm the offer");
                            alertDialog.setMessage("All the other offers will be rejected. Are you sure about this offer?");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("offerAccepted", monetize.getAdmirer().toString());
                                    map.put("monetize", false);

                                    FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).updateChildren(map);
                                    addNotification(postId, monetize.getAdmirer());

                                            FirebaseDatabase.getInstance().getReference().child("Monetize").child(postId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(mContext, "Offer accepted!!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();

                                                    }
                                                }
                                            });
                                }
                });
                alertDialog.show();
            }
        });


        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Monetize").child(postId).child(monetize.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Offer rejected!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                addNotification2(postId,monetize.getAdmirer());
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (monetize.getAdmirer().endsWith(fUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Monetize")
                                    .child(postId).child(monetize.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Offer deleted successfully!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }

                return true;
            }
        });

    }



    private void addNotification2(String postId, String admirer) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "rejected your offer.");
        map.put("postid", postId);
        map.put("isPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(admirer).push().setValue(map);
    }

    @Override
    public int getItemCount() {
        return mMonetize.size();
    }

    private void addNotification(String postId, String publisherId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "accepted your offer!!!");
        map.put("postid", postId);
        map.put("isPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId).push().setValue(map);

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public TextView comment;
        public ImageView accept;
        public ImageView reject;
        public RelativeLayout bottom;
        public TextView post;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            accept= itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            bottom=itemView.findViewById(R.id.bottom);
            post=itemView.findViewById(R.id.post);


        }
    }

}
