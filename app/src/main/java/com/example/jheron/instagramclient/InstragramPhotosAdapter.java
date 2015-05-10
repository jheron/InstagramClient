package com.example.jheron.instagramclient;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by jheron on 5/9/15.
 */
public class InstragramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    // what data do we need from the activity
    // Context, data Source
    public InstragramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
    // what out item looks like
    // use the template to display each photo
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        InstagramPhoto photo = getItem(position);
        // check if we are using a recyled view, if not we need to inflate
        if (convertView == null) {
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }
        // Lookup the views for populating the data (image, caption)
        ImageView ivUserPhotoURL = (ImageView) convertView.findViewById(R.id.ivUserImage);
        TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);

        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
        // Insert the model data into each of the view items


        // insert image using picasso
        ivUserPhotoURL.setImageResource(0);
        Picasso.with(getContext()).load(photo.userPhotoURL).transform(new RoundedTransformation(100,20)).placeholder(R.mipmap.ic_launcher).into(ivUserPhotoURL);
        String formattedUsername = "<b>" + photo.username + "</b>";
        tvUser.setText(Html.fromHtml(formattedUsername));
        Date now = new java.util.Date();
        String then = DateUtils.getRelativeTimeSpanString(
                photo.datetime*1000,
                now.getTime(),
                DateUtils.SECOND_IN_MILLIS).toString();
        //Log.d("InstagramPhotosAdapter", String.valueOf(now.getTime()) + " " + then );
        tvDate.setText(then);

        // insert image using picasso
        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageURL).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(ivPhoto);

        tvCaption.setText(photo.caption);

        String formattedComment = "<b>" + photo.commenter + "</b> " + photo.comment;
        tvComment.setText(Html.fromHtml(formattedComment));

        // return create item as a view

        return convertView;
    }
}
