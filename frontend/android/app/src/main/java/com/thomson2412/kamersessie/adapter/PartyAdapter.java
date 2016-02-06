package com.thomson2412.kamersessie.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thomson2412.kamersessie.activity.NewSessionActivity;
import com.thomson2412.kamersessie.activity.PartyInfoActivity;
import com.thomson2412.kamersessie.R;

import java.util.ArrayList;

import com.thomson2412.kamersessie.dataObject.PartyData;

/**
 * Created by Thomas on 3-10-2015.
 */
public class PartyAdapter extends RecyclerView.Adapter<PartyAdapter.ViewHolder> {
    private ArrayList<PartyData> mDataset;
    private final Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public View rootView;

        public ViewHolder(View v) {
            super(v);
            rootView = v.getRootView();
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PartyAdapter(ArrayList<PartyData> myDataset, Activity a) {
        activity = a;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PartyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_party_row, parent, false);
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
        holder.txtHeader.setText(mDataset.get(position).getPartyname());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NewSessionActivity.class);
                intent.putExtra("partyData", mDataset.get(position));
                activity.startActivity(intent);
            }
        });
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(activity, PartyInfoActivity.class);
                intent.putExtra("partyData", mDataset.get(position));
                activity.startActivity(intent);
                return true;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}