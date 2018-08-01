package com.butions.utnai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyNewFeeds extends Fragment implements EmojiFirebase.EmojiCallbacks, MyPostsNewFeedsFirebase.PostsNewFeedsCallbacks {

    private String TAG = "MyFeedFriend";
    private String mFacebookID;
    private String mName;
    private String mPicture;
    private View v;
    private Context context;
    private String myOnMap;
    private double mLat;
    private double mLng;
    private RecyclerView listViewFeed;
    private RecyclerViewAdapterMyFeed adapter;
    private DatabaseReference mRootRef;
    private ArrayList<String> mPostId;
    private Map<String, ArrayList<String>> mPostMessage = new HashMap<>();
    private Map<String, ArrayList<String>> mPostFeeling = new HashMap<>();
    private Map<String, ArrayList<String>> mPostImage = new HashMap<>();
    private Map<String, ArrayList<String>> mPostVideo = new HashMap<>();
    private Map<String, ArrayList<String>> mPostCheckIn = new HashMap<>();
    private Map<String, ArrayList<String>> mPostLng = new HashMap<>();
    private Map<String, ArrayList<String>> mPostLat = new HashMap<>();
    private Map<String, ArrayList<String>> mPostDate = new HashMap<>();
    private Map<String, ArrayList<Integer>> mPostCountLike = new HashMap<>();
    private Map<String, ArrayList<String>> mPostLike = new HashMap<>();
    private Map<String, ArrayList<Integer>> mPostComment = new HashMap<>();

    private ArrayList<String> EmojiImage = new ArrayList<String>();
    private ArrayList<String> EmojiName = new ArrayList<String>();
    private ArrayList<String> EmojiKey = new ArrayList<String>();

    private ArrayList<String>  NameFriend = new ArrayList<String> ();
    private ArrayList<String>  FBIDFriend = new ArrayList<String> ();
    private ArrayList<String>  PictureFriend = new ArrayList<String> ();
    private EmojiFirebase.EmojiListener emojiListener;
    private MyPostsNewFeedsFirebase.PostsNewFeedsListener postsNewFeedsListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_feed_friend, container, false);
        context = getActivity();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mFacebookID = getArguments().getString("MyFacebookID");
        mName = getArguments().getString("MyName");
        mPicture = getArguments().getString("MyPicture");
        myOnMap = getArguments().getString("MyOnMap");
        mLat = getArguments().getDouble("MyLatitude");
        mLng = getArguments().getDouble("MyLongitude");
        NameFriend.clear();
        FBIDFriend.clear();
        PictureFriend.clear();
        NameFriend = getArguments().getStringArrayList("NameFriend");
        FBIDFriend = getArguments().getStringArrayList("FBIDFriend");
        PictureFriend = getArguments().getStringArrayList("PictureFriend");

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, context);

        //call posts new feeds
        postsNewFeedsListener = MyPostsNewFeedsFirebase.addPostsNewFeedsListener(mFacebookID, FBIDFriend, this);

        listViewFeed = (RecyclerView) v.findViewById(R.id.listViewFeed);
        adapter = new RecyclerViewAdapterMyFeed();
        listViewFeed.setFocusable(false);


        return v;
    }

    @Override
    public void onEmojiChange(EmojiValue emojiValue) {
        if(emojiValue != null) {
            EmojiImage = emojiValue.getEmojiImage();
            EmojiName = emojiValue.getEmojiName();
            EmojiKey = emojiValue.getEmojiKey();
        }
        EmojiFirebase.stop(emojiListener);
    }

    @Override
    public void onPostsNewFeedsChange(PostsNewFeedsValue postsNewFeedsValue) {
        MyPostsNewFeedsFirebase.stop(postsNewFeedsListener);
        if(postsNewFeedsValue != null) {
            mPostCheckIn = postsNewFeedsValue.getCheckIn();
            mPostDate = postsNewFeedsValue.getTime();
            mPostId = postsNewFeedsValue.getId();
            mPostMessage = postsNewFeedsValue.getMessage();
            mPostFeeling = postsNewFeedsValue.getFeeling();
            mPostImage = postsNewFeedsValue.getImage();
            mPostLike = postsNewFeedsValue.getLikes();
            mPostCountLike = postsNewFeedsValue.getCountLike();
            mPostVideo = postsNewFeedsValue.getVideo();
            mPostComment = postsNewFeedsValue.getCountComment();

            adapter.notifyDataSetChanged();
            listViewFeed.setAdapter(adapter);

        }else{
            Log.d(TAG, "*** postsValue == null***");
        }
    }

    private class RecyclerViewAdapterMyFeed extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_post_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int i) {
            final String position = mPostId.get(i);
            Log.d(TAG, "PostId :" + position);
            final int index = checkIndex(position);
            Picasso.with(context)
                    .load(PictureFriend.get(index))
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.bg_circle_white)
                    .transform(new CircleTransform())
                    .into(holder.imageProfile);

            holder.name.setText(NameFriend.get(index));
            holder.date.setText(CalendarTime.getInstance().getTimeAgo(mPostDate.get(position).get(i)));

            if (mPostMessage.get(position) != null) {
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(mPostMessage.get(position).get(i));
            } else {
                holder.text.setVisibility(View.GONE);
            }

            if(mPostFeeling.get(position) != null && mPostFeeling.get(position).get(i) != null && EmojiKey != null){
                holder.text_emoji.setVisibility(View.VISIBLE);
                int index_emoji = checkImageEmoji(mPostFeeling.get(position).get(i));
                if (index_emoji != -1) {
                    holder.image_emoji.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(EmojiImage.get(index_emoji))
                            .centerCrop()
                            .fit()
                            .noFade()
                            .placeholder(R.color.white)
                            .error(R.drawable.ic_emotion_mini)
                            .into(holder.image_emoji);
                    if(EmojiName != null)
                        holder.text_emoji.setText(String.valueOf(StringManager.getsInstance().getString("IFeeling") + " " + EmojiName.get(index_emoji)));
                } else {
                    holder.image_emoji.setVisibility(View.GONE);
                    holder.text_emoji.setText(String.valueOf(StringManager.getsInstance().getString("IFeeling") + " " + mPostFeeling.get(position)));
                }
            }else{
                holder.image_emoji.setVisibility(View.GONE);
                holder.text_emoji.setVisibility(View.GONE);
            }

            if (mPostImage.get(position).get(i) != null) {
                holder.space_image.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(mPostImage.get(position).get(i))
                        .fit()
                        .centerInside()
                        .placeholder(R.color.black)
                        .error(R.color.white)
                        .into(holder.image);
            } else {
                holder.space_image.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
            }

            if (mPostCheckIn.get(position).get(i) != null) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(mPostCheckIn.get(position).get(i));
            } else {
                holder.location.setVisibility(View.GONE);
            }

            if (mPostVideo.get(position).get(i) != null) {
                holder.iconPlay.setVisibility(View.VISIBLE);
            } else {
                holder.iconPlay.setVisibility(View.GONE);
                holder.video.setVisibility(View.GONE);
            }

            holder.icon_delect.setVisibility(View.GONE);

            final ViewHolder finalHolder = holder;
            holder.iconPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalHolder.iconPlay.setVisibility(View.GONE);
                    finalHolder.image.setVisibility(View.GONE);
                    finalHolder.video.setVisibility(View.VISIBLE);
                    finalHolder.video.setVideoPath(mPostVideo.get(position).get(i));
                    finalHolder.video.start();
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deviceName = Config.getInstance().getDeviceName();
                    String isPostMessage = deviceName + "\n" + mPostMessage.get(position);
                    String message = isPostMessage + "/&" + mPostDate.get(position) + "/&" + mPostFeeling.get(position) + "/&" + mPostImage.get(position) + "/&" + mPostVideo.get(position) + "/&" + mPostCheckIn.get(position) + "/&" + mPostId.get(i);
                    Intent intent = new Intent(context, MyPostDetail.class);
                    intent.putExtra("mUserID", FBIDFriend.get(index));
                    intent.putExtra("mPostMessage", message);
                    intent.putExtra("MyFacebookID", mFacebookID);
                    intent.putExtra("MyName", mName);
                    intent.putExtra("MyPicture", mPicture);
                    startActivity(intent);
                }
            });

            holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MyFriendFeeds.class);
                    intent.putExtra("MyFacebookID", mFacebookID);
                    intent.putExtra("mFriendFacebookID", FBIDFriend.get(index));
                    intent.putExtra("mFriendName", NameFriend.get(index));
                    intent.putExtra("mName", mName);
                    intent.putExtra("mPicture", mPicture);
                    startActivity(intent);
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowFullImage.class);
                    intent.putExtra("mPicture", mPostImage.get(position));
                    startActivity(intent);
                }
            });

            if (mPostCountLike.get(position).get(i) != null) {
                if (mPostCountLike.get(position).get(i) > 0) {
                    holder.count_like.setText(mPostCountLike.get(position).toString());
                } else {
                    holder.count_like.setText("");
                }
            } else {
                holder.count_like.setText("");
            }


            if (mPostLike.get(position).get(i) != null) {
                holder.like.setImageResource(R.drawable.ic_heart_full);
            } else {
                holder.like.setImageResource(R.drawable.ic_heart);
            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPostLike.get(position) != null) {
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mFacebookID).child(mPostId.get(i)).child("Likes");
                        mUsersRef.child(mFacebookID).setValue(null);
                    } else {
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mFacebookID).child(mPostId.get(i)).child("Likes").child(mFacebookID);
                        mUsersRef.child("ID").setValue(mFacebookID);
                        mUsersRef.child("Name").setValue(mName);
                        mUsersRef.child("Picture").setValue(mPicture);
                    }
                }
            });

            if (mPostComment.get(position).get(i) != null) {
                if (mPostComment.get(position).get(i) > 0) {
                    holder.count_comment.setText(mPostComment.get(position).toString());
                } else {
                    holder.count_comment.setText("");
                }
            } else {
                holder.count_comment.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return mPostId.size();
        }
    }

    private int checkIndex(String position) {
        for(int i=0; i<FBIDFriend.size(); i++){
            if(FBIDFriend.get(i).equals(position)) {
                return i;
            }
        }
        return -1;
    }


    private class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        TextView location;
        TextView text_emoji;
        ImageView icon_delect;
        ImageView iconPlay;
        VideoView video;
        ImageView imageProfile;
        TextView name;
        TextView date;
        ImageView comment;
        RelativeLayout space_image;
        ImageView like;
        TextView count_like;
        TextView count_comment;
        ImageView image_emoji;

        public ViewHolder(View itemView) {
            super(itemView);
            imageProfile = (ImageView) itemView.findViewById(R.id.imageProfile);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            image = (ImageView) itemView.findViewById(R.id.image);
            space_image = (RelativeLayout) itemView.findViewById(R.id.space_image);
            text = (TextView) itemView.findViewById(R.id.text);
            location = (TextView) itemView.findViewById(R.id.location);
            icon_delect = (ImageView) itemView.findViewById(R.id.icon_delect);
            text_emoji = (TextView) itemView.findViewById(R.id.text_emoji);
            image_emoji = (ImageView) itemView.findViewById(R.id.image_emoji);
            iconPlay = (ImageView) itemView.findViewById(R.id.iconPlay);
            video = (VideoView) itemView.findViewById(R.id.video);
            comment = (ImageView) itemView.findViewById(R.id.ic_comment);
            like = (ImageView) itemView.findViewById(R.id.ic_like);
            count_like = (TextView) itemView.findViewById(R.id.count_like);
            count_comment = (TextView) itemView.findViewById(R.id.count_comment);
        }
    }

    private int checkImageEmoji(String key) {
        int emojiIndex = -1;
        if(key != null && EmojiKey != null) {
            for (int i = 0; i < EmojiKey.size(); i++) {
                if (EmojiKey.get(i).toLowerCase().contains(key.toLowerCase())) {
                    emojiIndex = i;
                }
            }
        }
        return emojiIndex;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        MyPostsNewFeedsFirebase.stop(postsNewFeedsListener);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
