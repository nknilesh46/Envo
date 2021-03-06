/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.academy.envy.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.academy.envy.Model.UserNaNo;
import com.academy.envy.R;
import com.academy.envy.Utils.DefaultImage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<UserNaNo> {
    TextView NameDisplay;
    DefaultImage defaultImage;



    /** Resource ID for the background color for this list of words */

    public UserListAdapter(Context context, ArrayList<UserNaNo> words) {
        super(context, 0, words);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_user, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        final UserNaNo currentWord = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID miwok_text_view.
        NameDisplay = listItemView.findViewById(R.id.nameTV);
        NameDisplay.setText(currentWord.getUserName());

        // Find the TextView in the list_item.xml layout with the ID default_text_view.
//        TextView NumberDisplay = listItemView.findViewById(R.id.default_text_view);
//        NumberDisplay.setText("");

        // Find the ImageView in the list_item.xml layout with the ID image.
        final ImageView imageView = listItemView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.default_avata);

        defaultImage = new DefaultImage(currentWord.getUserName());
        imageView.setImageResource(defaultImage.getDefaultImage());

        //check & load if image present in shared pref
//        try{
//            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getContext());
//            String uri = shre.getString("imagepath"+currentWord,"");
//            Glide.with(getContext()).load(uri).into(imageView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profileImages")
                    .child(currentWord.getuID()+ ".jpeg");

            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(uri).into(imageView);
//                            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getContext());
//                            SharedPreferences.Editor edit=shre.edit();
//                            edit.putString("imagepath"+currentWord,uri.toString());
//                            edit.commit();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        } catch (Exception e) {
            //e.printStackTrace();
        }


        ImageView flag = listItemView.findViewById(R.id.flag);

              switch(currentWord.getState()){
                  case 1: flag.setImageResource(R.drawable.green_flag); break;
                  case 2: flag.setImageResource(R.drawable.yellow_flag); break;
                  case 3: flag.setImageResource(R.drawable.red_flag); break;
              }

        return listItemView;
    }



}