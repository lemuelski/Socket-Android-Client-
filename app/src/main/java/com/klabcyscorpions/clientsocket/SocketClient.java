package com.klabcyscorpions.clientsocket;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends AppCompatActivity {

    private TextView connection_status, server_messages;
    private Button btn_connect, btn_send;
    private Socket socket = null;
    private boolean is_connected = false;
    private EditText alias;

    private View.OnClickListener btn_connection_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_connect:
                    connection_status.setText("Connecting");
                    Log.i("lem", alias.getText().toString());
                    new MyClientTask("10.3.15.185", 4000).execute(alias.getText().toString());
                    alias.setEnabled(false);
                    btn_connect.setEnabled(false);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client);

        connection_status = (TextView) findViewById(R.id.connection_status);
        server_messages = (TextView) findViewById(R.id.server_messages);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_send = (Button)findViewById(R.id.btn_send);
        alias = (EditText)findViewById(R.id.textview_nickname);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToServer();
            }
        });

        btn_connect.setOnClickListener(btn_connection_listener);
    }

    public class MyClientTask extends AsyncTask<String, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            Log.i("lem", "start");
            try {
                socket = new Socket(dstAddress, dstPort);
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                printStream.println(arg0[0]);

                SocketClient.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connection_status.setText("Connected");
                    }
                });


              /*  ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];*/

                /*int bytesRead;
                InputStream inputStream = socket.getInputStream();*/

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = " ";
                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */

                while((message = in.readLine())!=null){
                    updateMessagesFromClient(message);
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                is_connected = socket != null;
            }
            return null;
        }

    }

    private void sendMessageToServer(){
            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    try {
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        printStream.println(((EditText) findViewById(R.id.edittext_toserver)).getText().toString());
                        ((EditText) findViewById(R.id.edittext_toserver)).setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    }

    private void updateMessagesFromClient(final String message){
        if(message.equals("fart")){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.fart);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mp.start();
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                server_messages.setText(server_messages.getText() + "\n" + message);
            }
        });
    }

}
