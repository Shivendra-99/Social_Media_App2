package com.example.socialmediaapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Dao.PostDao;
import com.example.socialmediaapp.model.PostModel;
import com.example.socialmediaapp.model.user;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class RecylerAdopter extends FirestoreRecyclerAdapter<PostModel,RecylerAdopter.AdopterViewHolder> {
    timeutils timeutils=new timeutils();
    private OnItemClickListener listener;
    public RecylerAdopter(@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull AdopterViewHolder holder, int position, @NonNull PostModel model) {
        holder.name.setText(model.getName().getUserName());
        holder.postTit.setText(model.getPost());
        if(model.getLikes()!=null) {
            holder.count.setText(Integer.toString(model.getLikes().size()));
        }
        else
        {
            holder.count.setText("0");
        }
        holder.created.setText(timeutils.getTime(model.getTime()));
        Picasso.get().load(model.getName().getImageUrl()).into(holder.imageView);
                   String authProvider= FirebaseAuth.getInstance().getCurrentUser().getUid();
                   Log.d("My user id",authProvider);
                   if(model.getLikes()!=null) {
                       boolean isLikes = model.getLikes().contains(authProvider);
                       if (isLikes) {
                           holder.LikeImage.setImageDrawable(ContextCompat.getDrawable(holder.imageView.getContext(), R.drawable.liked_done));
                       } else {
                           holder.LikeImage.setImageDrawable(ContextCompat.getDrawable(holder.imageView.getContext(), R.drawable.ic_linked));
                       }
                   }
                   else {
                       holder.LikeImage.setImageDrawable(ContextCompat.getDrawable(holder.imageView.getContext(), R.drawable.ic_linked));
                   }
    }
    @NonNull
    @Override
    public AdopterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
        return new AdopterViewHolder(view);
    }
    public void deleteItem(int position)
    {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    public class AdopterViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView postTit,created,count,name;
        ImageView LikeImage;
        public AdopterViewHolder(@NonNull View itemView) {
            super(itemView);
            postTit=itemView.findViewById(R.id.PostTitle);
            created=itemView.findViewById(R.id.createdTime);
          //  Linkify.addLinks(created,Linkify.ALL);
            BetterLinkMovementMethod.linkify(Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES | Linkify.MAP_ADDRESSES, (Activity) created.getContext()).setOnLinkClickListener((created, url)->
            {
                CustomTabsIntent.Builder builder=new CustomTabsIntent.Builder();
                CustomTabsIntent intent=builder.build();
                intent.launchUrl(created.getContext(),Uri.parse(url));
                return true;
            }).setOnLinkLongClickListener(((created, url) -> {
                Toast.makeText(created.getContext(),"Please Click once to Link",Toast.LENGTH_LONG).show();
                return true;
            }));
            name=itemView.findViewById(R.id.userName);
            count=itemView.findViewById(R.id.LikeCount);
            LikeImage=itemView.findViewById(R.id.LikeImage);
            imageView=itemView.findViewById(R.id.profileImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnitemClickListener(OnItemClickListener listener)
    {
        this.listener=listener;
    }

}