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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
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
    private PostModel postModel=new PostModel();
    public RecylerAdopter(@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull AdopterViewHolder holder, int position, @NonNull PostModel model) {
        postModel=model;
        holder.name.setText(model.getName().getUserName());
        holder.postTit.setText(model.getPost());
        if(model.getTextColor()!=0){
            holder.postTit.setTextColor(model.getTextColor());
        }
        if(model.getBackgroundColor()!=0){
            holder.postTit.setBackgroundColor(model.getBackgroundColor());
        }
        if(model.getLikes()!=null) {
            holder.count.setText(Integer.toString(model.getLikes().size()));
        }
        else
        {
            holder.count.setText("0");
        }
        if(postModel.getVideo_url()==null){
            holder.exoPlayerView.setVisibility(View.GONE);
        }
        if(postModel.getVideo_url()!=null){
            holder.exoPlayerView.setVisibility(View.VISIBLE);
            BandwidthMeter bandwidthMeter=new DefaultBandwidthMeter();
            TrackSelector trackSelector=new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            holder.simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(holder.created.getContext(),trackSelector);
            Uri videoUrl=Uri.parse(postModel.getVideo_url());
            DefaultHttpDataSourceFactory defaultHttpDataSourceFactory=new DefaultHttpDataSourceFactory("exoplayer_view");
            ExtractorsFactory extractorsFactory=new DefaultExtractorsFactory();
            MediaSource mediaSource=new ExtractorMediaSource(videoUrl,defaultHttpDataSourceFactory,extractorsFactory,null,null);
            holder.exoPlayerView.setPlayer(holder.simpleExoPlayer);
            holder.simpleExoPlayer.prepare(mediaSource);
            holder.exoPlayerView.setMinimumHeight(200);
            holder.simpleExoPlayer.setVolume(50);
            holder.simpleExoPlayer.setPlayWhenReady(true);
        }
                   holder.created.setText(timeutils.getTime(model.getTime()));
                   Picasso.get().load(model.getName().getImageUrl()).into(holder.imageView);
                   if(model.getImage_url()!=null) {
                       Picasso.get().load(model.getImage_url()).into(holder.uploadImage);
                       Log.d("Adopter Link",model.getImage_url());
                   }
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
        ImageView LikeImage,uploadImage;
        SimpleExoPlayerView exoPlayerView;
        SimpleExoPlayer simpleExoPlayer;
        public AdopterViewHolder(@NonNull View itemView) {
            super(itemView);
            postTit=itemView.findViewById(R.id.PostTitle);
            created=itemView.findViewById(R.id.createdTime);
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
            uploadImage=itemView.findViewById(R.id.Upload_image);
            exoPlayerView=itemView.findViewById(R.id.video_player);
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