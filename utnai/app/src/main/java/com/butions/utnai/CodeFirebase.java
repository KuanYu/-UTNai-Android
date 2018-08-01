package com.butions.utnai;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class CodeFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "CodeFirebase";
    private static String CODES ="Codes";

    public static CodesListener addCodesListener(final CodesCallbacks callbacks, String isCode) {
        CodesListener listener = new CodesListener(callbacks);
        sRef.child(CODES).orderByKey().equalTo(isCode).addValueEventListener(listener);
        return listener;
    }

    public static void stop(CodesListener listener){
        sRef.child(CODES).removeEventListener(listener);
    }

    public static class CodesListener implements ValueEventListener {
        private CodesCallbacks callbacks;
        private String CodesID;
        private String CodesLat;
        private String CodesLng;

        CodesListener(CodesCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount() > 0) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> objCode = (Map<String, Object>)objDataSnapshot.getValue();
                    CodesID = objCode.get("ID").toString();
                    String latlng = objCode.get("LatLng").toString();
                    String[] location = latlng.split(",");
                    CodesLat = location[0];
                    CodesLng = location[1];
                }

                CodesValue codesValue = new CodesValue();
                codesValue.setCodes(CodesID);
                codesValue.setCodesLat(CodesLat);
                codesValue.setCodesLng(CodesLng);

                if (callbacks != null) {
                    callbacks.onCodesChange(codesValue);
                }

            }else{
                if (callbacks != null) {
                    callbacks.onCodesChange(null);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public interface CodesCallbacks {
        public void onCodesChange(CodesValue codesValue);
    }

}
