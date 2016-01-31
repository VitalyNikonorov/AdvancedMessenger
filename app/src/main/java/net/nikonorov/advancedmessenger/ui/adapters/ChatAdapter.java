package net.nikonorov.advancedmessenger.ui.adapters;

import android.app.Dialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.nikonorov.advancedmessenger.R;
import net.nikonorov.advancedmessenger.User;
import net.nikonorov.advancedmessenger.ui.ActivityMain;
import net.nikonorov.advancedmessenger.ui.FragmentChat;
import net.nikonorov.advancedmessenger.ui.FragmentSet;
import net.nikonorov.advancedmessenger.utils.TaskType;
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
    public void onBindViewHolder(CardViewHolder holder, final int position) {

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

            if((messages.get(position).getJSONObject("attach").getString("data") != null)
                    && !(messages.get(position).getJSONObject("attach").getString("data").equals(""))
                    && ((messages.get(position).getJSONObject("attach").getString("mime").contains("image")))){

                final ImageView attachedImg = new ImageView(activityMain);
                Utils.setPhoto(messages.get(position).getJSONObject("attach").getString("data"), attachedImg);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.BELOW, R.id.cv_message);

                attachedImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        final Dialog dialog = new Dialog(activityMain);
                        dialog.setContentView(R.layout.popup_attached_view);
                        dialog.setTitle("Attached file");

                        Button btnDismiss = (Button) dialog.findViewById(R.id.attached_view_close);
                        btnDismiss.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        ImageView attachedImage = (ImageView) dialog.findViewById(R.id.attached_img_view);
                        try {
                            Utils.setPhoto(messages.get(position).getJSONObject("attach").getString("data"), attachedImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialog.show();
                    }
                });

                holder.msgLayout.addView(attachedImg, params);
            }

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
        RelativeLayout msgLayout;
        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            messageView = (TextView)itemView.findViewById(R.id.cv_message);
            authorAva = (ImageView)itemView.findViewById(R.id.cv_iv);
            msgLayout = (RelativeLayout) itemView.findViewById(R.id.message_layout);
        }

    }

}
