package com.butions.utnai;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MessageFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MessageFirebase";
    private static final String COLUMN_TEXT = "Text";
    private static final String COLUMN_SENDER = "SenderId";
    private static final String COLUMN_CREATED ="Created";
    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_VIDEO = "Video";
    private static final String COLUMN_READ = "Read";
    private static String PATH_CHILD;


    public static void saveMessage(Message message){

        Calendar calendar = Calendar.getInstance();

        HashMap<String, String> msg = new HashMap<>();
        msg.put(COLUMN_CREATED, String.valueOf(calendar.getTimeInMillis()));
        msg.put(COLUMN_TEXT, message.getText());
        msg.put(COLUMN_SENDER,message.getSender());
        msg.put(COLUMN_TYPE, message.getType());
        msg.put(COLUMN_READ, message.getRead());
        if(message.getVideo() != null){
            msg.put(COLUMN_VIDEO, message.getVideo());
        }

        sRef.child(PATH_CHILD).push().setValue(msg);
    }

    public static MessagesListener addMessagesListener(String chatsKey, final MessagesCallbacks callbacks){
        MessagesListener listener = new MessagesListener(callbacks);
        PATH_CHILD = "Chats/" + chatsKey;
        sRef.child(PATH_CHILD).addChildEventListener(listener);
        return listener;
    }

    public static void stop(MessagesListener listener){
        sRef.child(PATH_CHILD).removeEventListener(listener);
    }

    public static class MessagesListener implements ChildEventListener {
        private MessagesCallbacks callbacks;
        MessagesListener(MessagesCallbacks callbacks){
            this.callbacks = callbacks;
        }
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap<String,String> msg = (HashMap)dataSnapshot.getValue();
            Message message = new Message();
            message.setSender(msg.get(COLUMN_SENDER));
            message.setText(msg.get(COLUMN_TEXT));
            message.setType(msg.get(COLUMN_TYPE));
            message.setDate(msg.get(COLUMN_CREATED));
            message.setRead(String.valueOf(msg.get(COLUMN_READ)));
            if(msg.containsKey(COLUMN_VIDEO)){
                message.setVideo(msg.get(COLUMN_VIDEO));
            }
            if(callbacks != null){
                callbacks.onMessageAdded(message);
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    public interface MessagesCallbacks{
        public void onMessageAdded(Message message);
    }

}
