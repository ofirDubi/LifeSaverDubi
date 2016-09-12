package com.dubico.lifesaver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.ErrorType;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;

public class PatientChat extends AppCompatActivity {
    String USER_ID = "";
    String RECEIVER_ID ="";
    String MESSAGE_CONTENT = "";
    EditText message_content;
    ListView message_list;
    MessageClient messageClient;
    Button send;
    MessageAdapter adapter;
    ArrayList<Message> sent_messages;
    int message_counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            RECEIVER_ID = extras.getString("receiverID");
            USER_ID = extras.getString("userID");
            //The key argument here must match that used in the other activity
        }
        Toast.makeText(PatientChat.this, "Started",
                Toast.LENGTH_LONG).show();
        sent_messages = new ArrayList<Message>();
        message_list = (ListView) findViewById(R.id.listMessages);
        adapter = new MessageAdapter(this, R.layout.message_left, sent_messages);
        message_list.setAdapter(adapter);

        message_content = (EditText) findViewById(R.id.messageBodyField);
        send = (Button) findViewById(R.id.sendButton);

        message_content.setHint("to: " + RECEIVER_ID);
        INIT_CHAT();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MESSAGE_CONTENT = message_content.getText().toString();
                send_message();
            }
        });

    }
    public void INIT_CHAT(){

        // Instantiate a SinchClient using the SinchClientBuilder.
        android.content.Context context = this.getApplicationContext();
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("cce930a4-fa03-4e06-afc2-62d6873816dd")
                .applicationSecret("dBVprZo5o0+zAftQjwbQUA==")
                .environmentHost("sandbox.sinch.com")
                .userId(USER_ID)
                .build();



        // Specify the client capabilities.
        // At least one of the messaging or calling capabilities should be enabled.
        sinchClient.setSupportMessaging(true);

       // sinchClient.setSupportManagedPush(true);
        // or
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.addSinchClientListener(new SinchClientListener() {
            public void onClientStarted(SinchClient client) { }
            public void onClientStopped(SinchClient client) { }
            public void onClientFailed(SinchClient client, SinchError error) { }
            public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) { }
            public void onLogMessage(int level, String area, String message) { }
        });
        sinchClient.start();

        MessageClient messageClient = sinchClient.getMessageClient();
        MessageClientListener messageClientListener = new MessageClientListener() {
            @Override
            public void onIncomingMessage(MessageClient messageClient, Message message) {
                // put message on screen
                Toast.makeText(PatientChat.this,message.toString(),
                        Toast.LENGTH_LONG).show();
                add_message(message);
            }

            @Override
            public void onMessageSent(MessageClient messageClient, Message message, String s) {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(PatientChat.this, "Message sent",
                              Toast.LENGTH_LONG).show();
                  }
              });
                add_message(message);

            }

            @Override
            public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
                Log.d("Message Failed", "Failed to send to user: "+ messageFailureInfo.getRecipientId()
                        +" because: "+messageFailureInfo.getSinchError().getMessage());
                add_message(message);
                if(messageFailureInfo.getSinchError().getErrorType().equals(ErrorType.NETWORK)){

                    WritableMessage remessage = new WritableMessage(
                            messageFailureInfo.getRecipientId(),
                            message.getTextBody());
                    // Send it
                    messageClient.send(remessage);
                }
            }

            @Override
            public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
                Log.d("MessageDelivered", "The message with id "+messageDeliveryInfo.getMessageId()
                        +" was delivered to the recipient with id"+ messageDeliveryInfo.getRecipientId());

            }

            @Override
            public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

            }
        };
        messageClient.addMessageClientListener(messageClientListener);
        this.messageClient = messageClient;
    }

    public void add_message(Message message){
            //TODO:
         //chage the text in listView
        adapter.add(message);

    }



    public void send_message(){
        // Create a WritableMessage

        WritableMessage message = new WritableMessage(
                RECEIVER_ID,
                MESSAGE_CONTENT);
        // Send it

        messageClient.send(message);


    }
}
