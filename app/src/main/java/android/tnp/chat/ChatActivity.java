package android.tnp.chat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.g38.tnp.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.tnp.DAO.CreateDB;
import android.tnp.services.GmailSendService;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private CreateDB createDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        createDB = new CreateDB(this);

//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setMessage("testing");
//        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//        chatMessage.setMe(false);
//        createDB.insertChatData(chatMessage);
        initControls();
    }
    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        loadChatHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");
                createDB.insertChatData(chatMessage);
                displayMessage(chatMessage);
                sendMail(messageText);
            }
        });


    }


    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadChatHistory(){
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        final Cursor cursor=createDB.getChatData();
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            do{
                ChatMessage chatMessage = new ChatMessage();
                if(cursor.getString(cursor.getColumnIndex(CreateDB.ISME)).equals("0"))
                    chatMessage.setMe(false);
                else
                    chatMessage.setMe(true);

                chatMessage.setMessage(cursor.getString(cursor.getColumnIndex(CreateDB.MESSAGE)));
                chatMessage.setDate(cursor.getString(cursor.getColumnIndex(CreateDB.DATE)));
                displayMessage(chatMessage);
            }while (cursor.moveToNext());
        }


    }

    private void sendMail(String body){
        if(isNetworkAvailable()) {
            Intent intent = new Intent(ChatActivity.this, GmailSendService.class);
            intent.putExtra("body", body);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Long time = new GregorianCalendar().getTimeInMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(ChatActivity.this, 1,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        else
            Toast.makeText(ChatActivity.this,"Query not send! Check internet connection", Toast.LENGTH_LONG).show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ChatActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}

