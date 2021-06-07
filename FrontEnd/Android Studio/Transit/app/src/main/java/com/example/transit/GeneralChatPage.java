package com.example.transit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;


public class GeneralChatPage extends AppCompatActivity {

private WebSocketClient connection;

    private String userName, type;
    private EditText messagetext;
    private Button send;
    private ArrayList<JSONObject> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_chat_page);

        userName = getIntent().getStringExtra("userName");
        type = getIntent().getStringExtra("type");

        messages = new ArrayList<>();
        messagetext = findViewById(R.id.chatpageMessage);
        send = findViewById(R.id.chatpageSend);

        Draft[] d = {new Draft_6455()
        };

        String str = "ws://"+getString(R.string.ip_address)+":8080/chat/help/" + userName +"/" + type ;

        try {
            Log.d("Socket:", "Trying to conncet");
            connection = new WebSocketClient(new URI(str), (Draft) d[0]) {
                @Override
                public void onMessage(final String message) {
                    final String s =message;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONObject toRender = new JSONObject((String) s);
                                renderMessages(toRender);
                                messagetext.setText("");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            toaster("Connection Established");
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            toaster("Connection is Closed");
                        }
                    });

                }
                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            toaster("Exception Error");
                        }
                    });
                }
            };
        } catch (URISyntaxException e) {
            Log.d("Exception:", e.getMessage().toString());
            e.printStackTrace();
        }
        connection.connect();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.send(messagetext.getText().toString());
            }
        });

        messagetext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    connection.send(messagetext.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void renderMessages(JSONObject toRender) throws JSONException {
        messages.add(toRender);

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.scroll_chat);
        int count = tableLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        TextView textView = findViewById(R.id.wtftest1);
        for(int i=0;i<messages.size();i++){

            JSONObject cur = messages.get(i);

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            // if from(self)
            //      color is yellow
            // else
            //      color is white
            //      if usertype exists
            //          add the user as a message detail

            TextView text1 = new TextView(this);
            if(cur.get("type").equals("from")){
                text1.setGravity(Gravity.LEFT);
                text1.setBackgroundColor(Color.parseColor("#ffffcc"));
                text1.setText(cur.get("user") +":"+ "\n"
                + cur.get("message"));
            }
            else{
                text1.setGravity(Gravity.RIGHT);
                text1.setBackgroundColor(Color.parseColor("#c9c9c1"));
                if(cur.has("userType")){
                    text1.setText(cur.get("user") + " | Type: " + cur.get("userType") + " |" + "\n"
                            + cur.get("message"));
                }
                else{
                    text1.setText(cur.get("user") +":"+ "\n"
                            + cur.get("message"));
                }
            }
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            text1.setLayoutParams(params);
            tableRow.addView(text1, 0);
            tableLayout.addView(tableRow);
        }
    }

    private void toaster(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}

