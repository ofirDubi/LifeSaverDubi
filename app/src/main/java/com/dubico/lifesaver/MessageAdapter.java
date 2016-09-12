package com.dubico.lifesaver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sinch.android.rtc.messaging.Message;

import java.util.ArrayList;

/**
 * Created by Dubi on 24/08/2016.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private final  ArrayList<Message> messages;


    public MessageAdapter(Context context, int message_left, ArrayList<Message> messages) {
        super(context,-1, messages);
        this.context = context;
        this.messages = messages;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View left_message = inflater.inflate(R.layout.message_left, parent, false);
        TextView text_sender = (TextView) left_message.findViewById(R.id.textSender);
        TextView text_date = (TextView) left_message.findViewById(R.id.textDate);
        TextView text_content = (TextView) left_message.findViewById(R.id.textMessage);

        text_sender.setText(messages.get(position).getSenderId());
        text_date.setText(messages.get(position).getTimestamp().toString());
        text_content.setText(messages.get(position).getTextBody());



        return left_message;
    }
}
