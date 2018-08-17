package com.ranganesh.mediawiki.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ranganesh.mediawiki.R;
import com.ranganesh.mediawiki.app.AppController;
import com.ranganesh.mediawiki.model.MediaWiki;

public class MediaWikiAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<MediaWiki> mediaWikiItems;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MediaWikiAdapter(Activity activity, List<MediaWiki> movieItems) {
        mInflater = LayoutInflater.from(activity);
        this.mediaWikiItems = movieItems;
    }

    @Override
    public int getCount() {
        return mediaWikiItems.size();
    }

    @Override
    public Object getItem(int location) {
        return mediaWikiItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // getting media wiki data for the row
        MediaWiki mediaWiki = mediaWikiItems.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.media_wiki_list_row, parent, false);
            viewHolder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.descrption = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // title
        viewHolder.title.setText(mediaWiki.getTitle());
        viewHolder.title.setSelected(true);
        //description
        viewHolder.descrption.setText(mediaWiki.getDescription());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        // thumbnail image
        viewHolder.thumbNail.setImageUrl(mediaWiki.getThumbnailUrl(), imageLoader);

        return convertView;
    }

    private class ViewHolder{
        private TextView title, descrption;
        private NetworkImageView thumbNail;
    }

}
