package com.butions.utnai;
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
public class MyPostsFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyPostsFirebase";
    private static String MY_FACEBOOK_ID;
    private static String MY_POST;


    public static PostsListener addPostsListener(String mFacebookID, final PostsCallbacks callbacks){
        MY_FACEBOOK_ID = mFacebookID;
        PostsListener listener = new PostsListener(callbacks);
        MY_POST = "Posts/" + mFacebookID;
        sRef.child(MY_POST).limitToLast(25).addValueEventListener(listener);
        return listener;
    }


    public static void stop(PostsListener listener){
        sRef.child(MY_POST).removeEventListener(listener);
    }


    public static class PostsListener implements ValueEventListener {
        private PostsCallbacks callbacks;
        private ArrayList<String> mPostID = new ArrayList<>();
        private Map<Integer, String> mPostMessage = new HashMap<>();
        private Map<Integer, String> mPostFeeling = new HashMap<>();
        private Map<Integer, String> mPostImage = new HashMap<>();
        private Map<Integer, String> mPostVideo = new HashMap<>();
        private Map<Integer, String> mPostCheckIn = new HashMap<>();
        private Map<Integer, String> mPostLng = new HashMap<>();
        private Map<Integer, String> mPostLat = new HashMap<>();
        private Map<Integer, String> mPostDate = new HashMap<>();
        private Map<Integer, Integer> mPostCountLike = new HashMap<>();
        private Map<Integer, String> mPostLike = new HashMap<>();
        private Map<Integer, Integer> mPostCountComment = new HashMap<>();

        PostsListener(PostsCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0) {
                clearAll();
                int count = -1;
                for (DataSnapshot objDataSnapshotPost : dataSnapshot.getChildren()) {
                    count++;
                    mPostID.add(objDataSnapshotPost.getKey());

                    Map<String, Object> mapObjPost = (Map<String, Object>) objDataSnapshotPost.getValue();
                    if (mapObjPost.containsKey("Message")) {
                        mPostMessage.put(count, mapObjPost.get("Message").toString());
                    }

                    if (mapObjPost.containsKey("Feeling")) {
                        mPostFeeling.put(count, mapObjPost.get("Feeling").toString());
                    }

                    if (mapObjPost.containsKey("Image")) {
                        mPostImage.put(count, mapObjPost.get("Image").toString());
                    }

                    if (mapObjPost.containsKey("Video")) {
                        mPostVideo.put(count, mapObjPost.get("Video").toString());
                    }

                    if (mapObjPost.containsKey("CheckInName")) {
                        mPostCheckIn.put(count, mapObjPost.get("CheckInName").toString());
                    }

                    if(mapObjPost.containsKey("LatLng")){
                        String latlng = mapObjPost.get("LatLng").toString();
                        String[] location = latlng.split(",");
                        mPostLat.put(count, location[0]);
                        mPostLng.put(count, location[1]);
                    }

                    if (mapObjPost.containsKey("Created_time")) {
                        mPostDate.put(count, mapObjPost.get("Created_time").toString());
                    }

                    if (mapObjPost.containsKey("Likes")) {
                        Map<String, Object> objLikes = (Map<String, Object>) mapObjPost.get("Likes");
                        mPostCountLike.put(count, objLikes.size());
                        if (objLikes.containsKey(MY_FACEBOOK_ID)) {
                            mPostLike.put(count, MY_FACEBOOK_ID);
                        }
                    } else {
                        mPostLike.put(count, null);
                        mPostCountLike.put(count, 0);
                    }

                    if (mapObjPost.containsKey("Comments")) {
                        Map<String, Object> objLikes = (Map<String, Object>) mapObjPost.get("Comments");
                        int countComment = objLikes.size();
                        mPostCountComment.put(count, countComment);
                    } else {
                        mPostCountComment.put(count, 0);
                    }
                }

                PostsValue posts = new PostsValue();
                posts.setCheckIn(mPostCheckIn);
                posts.setTime(mPostDate);
                posts.setId(mPostID);
                posts.setLatitude(mPostLat);
                posts.setLongitude(mPostLng);
                posts.setMessage(mPostMessage);
                posts.setFeeling(mPostFeeling);
                posts.setImage(mPostImage);
                posts.setLikes(mPostLike);
                posts.setCountLike(mPostCountLike);
                posts.setVideo(mPostVideo);
                posts.setComment(mPostCountComment);

                if (callbacks != null) {
                    callbacks.onPostsChange(posts);
                }
            }else{
                if (callbacks != null) {
                    callbacks.onPostsChange(null);
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

    public interface PostsCallbacks{
        public void onPostsChange(PostsValue postsValue);
    }

}
