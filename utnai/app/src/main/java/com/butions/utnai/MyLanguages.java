package com.butions.utnai;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MyLanguages extends AppCompatActivity implements MyLanguageFirebase.LanguageCallbacks, View.OnClickListener {

    private String TAG = "MyLanguages";
    private ListView my_list_languages;
    private CustomListViewLanguages adapter;
    private DatabaseReference mRootRef;
    private ArrayList<String> code = new ArrayList<>();
    private ArrayList<String>  name = new ArrayList<>();
    private ArrayList<String>  id = new ArrayList<>();
    private ImageView btnBack;
    private TextView textLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_languages);

        LanguageCodeListener listenerLanguageCode = new LanguageCodeListener();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("LanguagesCode").addListenerForSingleValueEvent(listenerLanguageCode);

        textLanguages = (TextView) findViewById(R.id.textLanguages);
        textLanguages.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        adapter = new CustomListViewLanguages(this);
        my_list_languages = (ListView) findViewById(R.id.my_list_languages);
        my_list_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectLanguages(id.get(i), code.get(i));
            }
        });
    }

    private void selectLanguages(String key, String code) {
        MyLanguageFirebase.addLanguageListener(key, code, this, this);
    }

    @Override
    public void onLanguageChange() {
        setFonts();
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == btnBack){
            finish();
        }
    }

    private class CustomListViewLanguages extends BaseAdapter {

        public Context mContext;
        public LayoutInflater mInflater;
        public CustomListViewLanguages(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return code.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listview_language_layout, null);
                holder = new ViewHolder();

                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.code_name = (TextView) convertView.findViewById(R.id.code);


                holder.text_name.setTypeface(Config.getInstance().getDefaultFont(mContext), Typeface.BOLD);
                holder.code_name.setTypeface(Config.getInstance().getDefaultFont(mContext));

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text_name.setText(name.get(i));
            holder.code_name.setText(code.get(i));


            return convertView;
        }
    }
    private class ViewHolder{
        TextView text_name;
        TextView code_name;
    }

    private class LanguageCodeListener implements ValueEventListener{

        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
            for (com.google.firebase.database.DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                Map<String, Object> mapObjLang = (Map<String, Object>) objDataSnapshot.getValue();
                code.add(mapObjLang.get("Code").toString());
                name.add(mapObjLang.get("Name").toString());
                id.add(mapObjLang.get("ID").toString());
            }

            adapter.notifyDataSetChanged();
            my_list_languages.setAdapter(adapter);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "*** OnResume ***");
        Translater.getInstance().setLanguages(this);
        setFonts();
    }

    private void setFonts() {
        textLanguages.setText(StringManager.getsInstance().getString("Languages"));
    }
}
