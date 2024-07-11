package com.proyecto.agroinsight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private ModeloChatbot chatbotModel;
    private ListView chatListView;
    private EditText userInput;
    private Button sendButton;
    private ImageButton backButton;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_layout);

        try {
            chatbotModel = new ModeloChatbot(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        chatListView = findViewById(R.id.chat_list_view);
        userInput = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);

        chatMessages = new ArrayList<>();
        adapter = new ChatAdapter(this, chatMessages);
        chatListView.setAdapter(adapter);

        // Obtener el mensaje inicial del Intent
        Intent intent = getIntent();
        String initialMessage = intent.getStringExtra("user_message");
        if (initialMessage != null) {
            chatMessages.add(new ChatMessage("Tú: " + initialMessage, true));
            String response = chatbotModel.predict(initialMessage);
            chatMessages.add(new ChatMessage("AgroInsight: " + response, false));
            adapter.notifyDataSetChanged();
            chatListView.smoothScrollToPosition(chatMessages.size() - 1);
        }

        sendButton.setOnClickListener(v -> {
            String inputText = userInput.getText().toString().trim();
            if (!inputText.isEmpty()) {
                chatMessages.add(new ChatMessage("Tú: " + inputText, true));
                String response = chatbotModel.predict(inputText);
                chatMessages.add(new ChatMessage("AgroInsight: " + response, false));
                userInput.setText("");
                adapter.notifyDataSetChanged();
                chatListView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        });

        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ChatbotActivity.this, MainActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

}

