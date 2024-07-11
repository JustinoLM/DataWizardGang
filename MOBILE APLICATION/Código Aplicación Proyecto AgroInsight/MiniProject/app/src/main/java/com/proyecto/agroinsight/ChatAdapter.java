package com.proyecto.agroinsight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private final int userMessageLayout = R.layout.chat_item;
    private final int botMessageLayout = R.layout.chat_item_agro;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        super(context, 0, chatMessages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);
        if (convertView == null) {
            if (chatMessage.isUserMessage()) {
                convertView = LayoutInflater.from(getContext()).inflate(userMessageLayout, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(botMessageLayout, parent, false);
            }
        }

        TextView messageTextView = convertView.findViewById(R.id.chat_message);
        messageTextView.setText(chatMessage.getMessage());

        return convertView;
    }
}
