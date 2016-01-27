package net.nikonorov.advancedmessenger.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vitaly on 27.01.16.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.CardViewHolder> {

    ArrayList<JSONObject> data = null;

    public ContactListAdapter(ArrayList<JSONObject> data){
        this.data = data;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_contact_list, parent, false);
        CardViewHolder holder = new CardViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        try {
            holder.userUid = data.get(position).getString("uid");
            Utils.setPhoto(data.get(position).getString("picture"), holder.avatar);
            holder.nick.setText(data.get(position).getString("nick"));
            holder.phone.setText(data.get(position).getString("phone"));
            holder.email.setText(data.get(position).getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        String userUid;
        CardView cv;
        ImageView avatar;
        TextView nick;
        TextView email;
        TextView phone;

        public CardViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            cv = (CardView) itemView.findViewById(R.id.contact_list_cv);
            avatar = (ImageView) itemView.findViewById(R.id.contact_list_cv_ava);
            nick = (TextView) itemView.findViewById(R.id.contact_list_cv_nick);
            email = (TextView) itemView.findViewById(R.id.contact_list_cv_email);
            phone = (TextView) itemView.findViewById(R.id.contact_list_cv_phone);
        }

        @Override
        public void onClick(View v) {
            Log.i("ContactListAdapter", "onClick RecyclerView "+userUid);
        }
    }


}
