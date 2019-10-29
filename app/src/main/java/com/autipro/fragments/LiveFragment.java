package com.autipro.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autipro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LiveFragment extends Fragment {

    private TextView textViewData;
    private ProgressDialog progressDialog;
    private final String TAG = "LiveFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        textViewData = view.findViewById(R.id.textViewData);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        fetchData();
    }

    private void fetchData() {
        progressDialog.show();
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference();
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                StringBuilder s = new StringBuilder();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getValue().getClass() == String.class) {
                        String data = ds.getValue(String.class);
                        if (data != null) {
                            s.append(data).append("\n");
                            Log.d(TAG, data);
                        }
                    }
                }
                textViewData.setText(s.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}
