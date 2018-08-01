package com.butions.utnai;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MyPostsNewFeedsFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyPostsNewFeedsFirebase";
    private static String MY_USER_ID;
    private static String MY_POST;
    private static ArrayList<String> ListFriend;


    public static PostsNewFeedsListener addPostsNewFeedsListener(String myUserID, ArrayList<String> listFriend , final PostsNewFeedsCallbacks callbacks){
        MY_USER_ID = myUserID;
        ListFriend = listFriend;
        PostsNewFeedsListener listener = new PostsNewFeedsListener(callbacks);
        MY_POST = "Posts";
        sRef.child(MY_POST).addValueEventListener(listener);
        return listener;
    }


    public static void stop(PostsNewFeedsListener listener){
        sRef.child(MY_POST).removeEventListener(listener);
    }


    public static class PostsNewFeedsListener implements ValueEventListener {
        private PostsNewFeedsCallbacks callbacks;
        private ArrayList<String> mPostID = new ArrayList<>();
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
        private Map<String, ArrayList<Integer>> mPostCountComment = new HashMap<>();

        private ArrayList<String> objectMessage = new ArrayList<>();
        private ArrayList<String> objectFeeling = new ArrayList<>();
        private ArrayList<String> objectImage = new ArrayList<>();
        private ArrayList<String> objectVideo = new ArrayList<>();
        private ArrayList<String> objectCheckIn = new ArrayList<>();
        private ArrayList<String> objectLng = new ArrayList<>();
        private ArrayList<String> objectLat = new ArrayList<>();
        private ArrayList<String>objectDate = new ArrayList<>();
        private ArrayList<Integer> objectCountLike = new ArrayList<>();
        private ArrayList<String> objectLike = new ArrayList<>();
        private ArrayList<Integer> objectCountComment = new ArrayList<>();

        private int count;
        private long lastPosts;

        PostsNewFeedsListener(PostsNewFeedsCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0) {
                clearAll();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    String key = objDataSnapshot.getKey();
                    if(ListFriend.contains(key)) {
                        count = -1;
                        lastPosts = objDataSnapshot.getChildrenCount();
                        Log.d(TAG, "last posts : " + lastPosts);
                        for(DataSnapshot objDataSnapshotPost :  objDataSnapshot.getChildren()) {
                            count ++;
                            Map<String, Object> mapObjPost = (Map<String, Object>) objDataSnapshotPost.getValue();

                            for(int i=5; i>=0; i--) {
                                if ((lastPosts - i) == count) {
                                    if (mapObjPost.containsKey("Message")) {
                                        objectMessage.add(mapObjPost.get("Message").toString());
                                    } else {
                                        objectMessage.add(null);
                                    }

                                    if (mapObjPost.containsKey("Feeling")) {
                                        objectFeeling.add(mapObjPost.get("Feeling").toString());
                                    } else {
                                        objectFeeling.add(null);
                                    }

                                    if (mapObjPost.containsKey("Image")) {
                                        objectImage.add(mapObjPost.get("Image").toString());
                                    } else {
                                        objectImage.add(null);
                                    }

                                    if (mapObjPost.containsKey("Video")) {
                                        objectVideo.add(mapObjPost.get("Video").toString());
                                    } else {
                                        objectVideo.add(null);
                                    }

                                    if (mapObjPost.containsKey("CheckInName")) {
                                        objectCheckIn.add(mapObjPost.get("CheckInName").toString());
                                    } else {
                                        objectCheckIn.add(null);
                                    }

                                    if(mapObjPost.containsKey("LatLng")){
                                        String latlng = mapObjPost.get("LatLng").toString();
                                        String[] location = latlng.split(",");
                                        objectLat.add(location[0]);
                                        objectLng.add(location[1]);
                                    }else{
                                        objectLat.add(null);
                                        objectLng.add(null);
                                    }

                                    if (mapObjPost.containsKey("Created_time")) {
                                        mPostID.add(key);
                                        objectDate.add(mapObjPost.get("Created_time").toString());
                                    } else {
                                        objectDate.add(null);
                                    }

                                    if (mapObjPost.containsKey("Likes")) {
                                        Map<String, Object> objLikes = (Map<String, Object>) mapObjPost.get("Likes");
                                        objectCountLike.add(objLikes.size());
                                        if (objLikes.containsKey(MY_USER_ID)) {
                                            objectLike.add(MY_USER_ID);
                                        } else {
                                            objectLike.add(null);
                                        }
                                    } else {
                                        objectLike.add(null);
                                        objectCountLike.add(0);
                                    }

                                    if (mapObjPost.containsKey("Comments")) {
                                        Map<String, Object> objLikes = (Map<String, Object>) mapObjPost.get("Comments");
                                        int countComment = objLikes.size();
                                        objectCountComment.add(countComment);
                                    } else {
                                        objectCountComment.add(0);
                                    }
                                }
                            }
                        }

                        mPostMessage.put(key, objectMessage);
                        mPostFeeling.put(key, objectFeeling);
                        mPostImage.put(key, objectImage);
                        mPostVideo.put(key, objectVideo);
                        mPostCheckIn.put(key, objectCheckIn);
                        mPostLat.put(key, objectLng);
                        mPostLng.put(key, objectLat);
                        mPostDate.put(key, objectDate);
                        mPostCountLike.put(key, objectCountLike);
                        mPostLike.put(key, objectLike);
                        mPostCountComment.put(key, objectCountComment);

                    }
                }

                PostsNewFeedsValue postsNewFeeds = new PostsNewFeedsValue();
                postsNewFeeds.setCheckIn(mPostCheckIn);
                postsNewFeeds.setTime(mPostDate);
                postsNewFeeds.setId(mPostID);
                postsNewFeeds.setLatitude(mPostLat);
                postsNewFeeds.setLongitude(mPostLng);
                postsNewFeeds.setMessage(mPostMessage);
                postsNewFeeds.setFeeling(mPostFeeling);
                postsNewFeeds.setImage(mPostImage);
                postsNewFeeds.setLikes(mPostLike);
                postsNewFeeds.setCountLike(mPostCountLike);
                postsNewFeeds.setVideo(mPostVideo);
                postsNewFeeds.setCountComment(mPostCountComment);

                if (callbacks != null) {
                    callbacks.onPostsNewFeedsChange(postsNewFeeds);
                }
            }else{
                if (callbacks != null) {
                    callbacks.onPostsNewFeedsChange(null);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        private void clearAll() {
            mPostID.clear();
            mPostMessage.clear();
            mPostFeeling.clear();
            mPostImage.clear();
            mPostVideo.clear();
            mPostCheckIn.clear();
            mPostLng.clear();
            mPostLat.clear();
            mPostDate.clear();
            mPostCountLike.clear();
            mPostLike.clear();
            mPostCountComment.clear();
        }
    }

    public interface PostsNewFeedsCallbacks{
        public void onPostsNewFeedsChange(PostsNewFeedsValue postsNewFeedsValue);
    }

}
