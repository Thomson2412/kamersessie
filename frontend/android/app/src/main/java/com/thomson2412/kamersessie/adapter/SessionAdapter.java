package com.thomson2412.kamersessie.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thomson2412.kamersessie.R;

import java.util.ArrayList;

import com.thomson2412.kamersessie.activity.SessionInfoActivity;
import com.thomson2412.kamersessie.dataObject.SessionData;

/**
 * Created by Thomas on 3-10-2015.
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder>  {
    private final ArrayList<SessionData> mDataset;
    private final Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public View rootView;

        public ViewHolder(View v) {
            super(v);
            rootView = v.getRootView();
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SessionAdapter(ArrayList<SessionData> myDataset, Activity a) {
        activity = a;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SessionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /*public void add(int position, String item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }*/

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtHeader.setText(mDataset.get(position).getLocation());
        holder.txtFooter.setText(mDataset.get(position).getSessionCreator());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,SessionInfoActivity.class);
                intent.putExtra("sessiondata", mDataset.get(position));
                activity.startActivity(intent);
            }
        });;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}