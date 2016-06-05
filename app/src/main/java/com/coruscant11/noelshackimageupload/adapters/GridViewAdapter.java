package com.coruscant11.noelshackimageupload.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.coruscant11.noelshackimageupload.Noelshack;
import com.coruscant11.noelshackimageupload.R;
import com.coruscant11.noelshackimageupload.datas.UploadSave;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {

    private Activity m_Context;
    private int m_LayoutResourceId;
    private ArrayList m_Data = new ArrayList();

    public GridViewAdapter(Activity context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);

        this.m_LayoutResourceId = layoutResourceId;
        this.m_Context = context;
        if(data.isEmpty() || data == null) {
            this.m_Data = new ArrayList();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = m_Context.getLayoutInflater();
            row = inflater.inflate(m_LayoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        UploadSave uploadSaveItem = (UploadSave) this.m_Data.get(position);
        Picasso.with(this.m_Context).load(new File(Uri.parse(uploadSaveItem.getImage().path).toString())).fit().centerCrop().into(holder.image);
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
