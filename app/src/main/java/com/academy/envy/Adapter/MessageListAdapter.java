package com.academy.envy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.academy.envy.ChatActivity;
import com.academy.envy.Model.MessageDetails;
import com.academy.envy.R;
import com.academy.envy.Utils.DefaultImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.CheesyAdapterViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<MessageDetails> messages;
    private String currentUser;
    Context context;


    private long timeRemaining = 0;
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d");


    public MessageListAdapter(List<MessageDetails> messages, String currentUser){
        this.messages=messages;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public CheesyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        context = parent.getContext();

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.list_item_group_chat_user_me, parent, false);
            return new MessageListAdapter.CheesyAdapterViewHolder(view);
        }
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.list_item_group_chat_user_other, parent, false);
            return new MessageListAdapter.CheesyAdapterViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CheesyAdapterViewHolder holder, int position) {

        MessageDetails message = messages.get(position);
        boolean isContinuous = false;
        boolean isNewDay = false;

        // If there is at least one item preceding the current one, check the previous message.
        if (position>0 && position <= messages.size() - 1) {
            MessageDetails prevMessage = messages.get(position - 1);

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            try {
                if (dateFormatter.format(message.getDateSent()).equals(dateFormatter.format(prevMessage.getDateSent()))) {
                    if (message.getSenderID().equals(prevMessage.getSenderID())) {
                        isContinuous = true;
                    } else {
                        isContinuous = false;
                    }
                } else {
                    isNewDay = true;
                    isContinuous = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
        if (position == 0) {
            isNewDay = true;
        }

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                holder.bindViewSnt(messages.get(position), isContinuous, isNewDay);

                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                holder.bindViewRcv(messages.get(position),isContinuous, isNewDay);
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class CheesyAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView text_group_chat_nickname,text_group_chat_message, text_message_name,
                text_group_chat_date,text_message_body,text_group_chat_time,timertv;
        View padding,line;
        CircleImageView image_group_chat_profile;
        public CheesyAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            //rcv
            text_group_chat_nickname = itemView.findViewById(R.id.text_group_chat_nickname);
            text_group_chat_message = itemView.findViewById(R.id.text_group_chat_message);
            image_group_chat_profile = itemView.findViewById(R.id.image_group_chat_profile);
//            text_message_name = itemView.findViewById(R.id.text_message_name);
            text_message_body = itemView.findViewById(R.id.text_message_body);
            text_group_chat_time = itemView.findViewById(R.id.text_group_chat_time);
            text_group_chat_date = itemView.findViewById(R.id.text_group_chat_date);
            timertv = itemView.findViewById(R.id.timer);
            padding = itemView.findViewById(R.id.view_group_chat_padding);
            line = itemView.findViewById(R.id.line);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                            .child("BS2/Users/"+messages.get(getAdapterPosition()).getSenderID()+"/number");

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                String url = "https://api.whatsapp.com/send?phone=+91"+snapshot.getValue().toString();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                context.startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context,"Number not found!",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    return false;
                }
            });
        }

        public void bindViewRcv(MessageDetails message, boolean isContinuous, boolean isNewDay){

            DefaultImage defaultImage = new DefaultImage(message.getSenderName());
            image_group_chat_profile.setImageResource(defaultImage.getDefaultImage());


            if (isContinuous) {
                image_group_chat_profile.setVisibility(View.INVISIBLE);
                text_group_chat_nickname.setVisibility(View.GONE);
            }
            else {
                text_group_chat_nickname.setText(message.getSenderName());
                image_group_chat_profile.setVisibility(View.VISIBLE);
                text_group_chat_nickname.setVisibility(View.VISIBLE);
            }
            // If the message is sent on a different date than the previous one, display the date.
            if (isNewDay) {
                text_group_chat_date.setText(dateFormatter.format(message.getDateSent()));
                text_group_chat_date.setVisibility(View.VISIBLE);
            }
            else {
                text_group_chat_date.setVisibility(View.GONE);
            }

            text_group_chat_message.setText(message.getMsg());
            text_group_chat_time.setText(timeFormatter.format(message.getDateSent()));

            long currTime = new Date().getTime();
            long msgTime;
            try {
                    msgTime = message.getDateSent().getTime();
            } catch (Exception e) {
                    e.printStackTrace();
                    msgTime = currTime;
            }
            long diffTime = currTime-msgTime;

            CountDownTimer timer;
            long millisInFuture = 120000-diffTime; //120 seconds - timeleft
            long countDownInterval = 1000; //1 second
                //Initialize a new CountDownTimer instance
                timer = new CountDownTimer(millisInFuture,countDownInterval){

                    public void onTick(long millisUntilFinished){
                        //Display the remaining seconds to app interface
                        //1 second = 1000 milliseconds
                        timertv.setText(""+millisUntilFinished / 1000);
                        //Put count down timer remaining time in a variable
                        timeRemaining = millisUntilFinished;
                    }
                    @Override
                    public void onFinish() {
                        timertv.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }
                };
                if(diffTime<=120000 && diffTime>-3000 && isSubstring(message.getMsg().toLowerCase(),"increasing")){
                    timertv.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    timer.start();
                }
        }


        public void bindViewSnt(final MessageDetails message, boolean isContinuous, boolean isNewDay){

            if (isContinuous) {
                padding.setVisibility(View.GONE);
            }
            else {
                padding.setVisibility(View.VISIBLE);
            }
            // If the message is sent on a different date than the previous one, display the date.
            if (isNewDay) {
                text_group_chat_date.setText(dateFormatter.format(message.getDateSent()));
                text_group_chat_date.setVisibility(View.VISIBLE);
            }
            else {
                text_group_chat_date.setVisibility(View.GONE);
            }

            text_message_body.setText(message.getMsg());
            text_group_chat_time.setText(timeFormatter.format(message.getDateSent()));


            if(message.getDateSent()!=null){

                long currTime = new Date().getTime();
                long msgTime;
                try {
                    msgTime = message.getDateSent().getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                    msgTime = currTime;
                }
                long diffTime = currTime-msgTime;

                CountDownTimer timer;
                long millisInFuture = 120000-diffTime; //120 seconds - timeleft
                long countDownInterval = 1000; //1 second

                //Initialize a new CountDownTimer instance
                timer = new CountDownTimer(millisInFuture,countDownInterval){

                    public void onTick(long millisUntilFinished){
                            //Display the remaining seconds to app interface
                            //1 second = 1000 milliseconds
                        timertv.setText(""+millisUntilFinished / 1000);
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                        }
                    @Override
                    public void onFinish() {
                        timertv.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }
                };
                if(diffTime<=120000 && diffTime>-3000 && isSubstring(message.getMsg().toLowerCase(),"increasing")){
                    timertv.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                timer.start();
                }
            }
            else{
                 text_group_chat_time.setText("Sending...");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).getSenderID().equals(currentUser)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    private boolean isSubstring(String msg, String substr) {
        int M = substr.length();
        int N = msg.length();

        /* A loop to slide pat[] one by one */
        for (int i = 0; i <= N - M; i++) {
            int j;

            /* For current index i, check for
            pattern match */
            for (j = 0; j < M; j++)
                if (msg.charAt(i + j)
                        != substr.charAt(j))
                    break;

            if (j == M)
                return true;
        }

        return false;
    }
}
