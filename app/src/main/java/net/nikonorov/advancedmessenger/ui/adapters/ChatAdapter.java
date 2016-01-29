package net.nikonorov.advancedmessenger.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.ui.ActivityMain;
import net.nikonorov.advancedmessenger.ui.FragmentChat;
import net.nikonorov.advancedmessenger.ui.FragmentSet;
import net.nikonorov.advancedmessenger.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by vitaly on 29.01.16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.CardViewHolder> {

    private List<JSONObject> messages;
    private ActivityMain activityMain;

    public ChatAdapter(List<JSONObject> mess, ActivityMain activityMain){
        this.messages = mess;
        this.activityMain = activityMain;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_item, parent, false);
        CardViewHolder pvh = new CardViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {

        StringBuilder sb = new StringBuilder();
        try {
            sb.append(messages.get(position).getString("nick")).append(": ").append(messages.get(position).getString("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.messageView.setText(sb.toString());

        try {
            if (messages.get(position).getString("from").equals(User.getCid())){
                Utils.setPhoto(User.getPicture(), holder.authorAva);
            }else {
                Utils.setPhoto(((FragmentChat)activityMain.fragments[FragmentSet.MAINCHAT]).getUserAva(), holder.authorAva);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Utils.setPhoto(messages.get(position).getJSONObject("attach").getString("data"), holder.msgPhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView messageView;
        ImageView authorAva;
        ImageView   msgPhoto;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            messageView = (TextView)itemView.findViewById(R.id.cv_message);
            authorAva = (ImageView)itemView.findViewById(R.id.cv_iv);

            msgPhoto = (ImageView) itemView.findViewById(R.id.cv_msgphoto);

        }

    }

}
