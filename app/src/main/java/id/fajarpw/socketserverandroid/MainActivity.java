package id.fajarpw.socketserverandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //Step 1
    ServerSocket serverSocket;
    Thread thread = null;

    TextView tvServerStatus, tvMessageReceived;
    EditText etIPAddress, etPortNumber,etMessageSent;
    Button btnStartListen, btnStopListen, btnSend;

    public static String SERVER_IP = "";
    public static int SERVER_PORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvServerStatus = findViewById(R.id.textViewServerStatus);
        tvMessageReceived = findViewById(R.id.textViewMessageReceived);
        etIPAddress = findViewById(R.id.editTextIPAddress);
        etPortNumber = findViewById(R.id.editTextTextPortNumber);
        btnStartListen = findViewById(R.id.buttonStartListen);
        btnStopListen = findViewById(R.id.buttonStopListen);
        btnSend = findViewById(R.id.buttonSend);

        //Step 1
        thread = new Thread(new Thread1());
        thread.start();
    }

    private PrintWriter output;
    private BufferedReader input;

    class Thread1 implements Runnable{

        @Override
        public void run() {
            Socket socket;
            try {
                SERVER_IP = etIPAddress.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPortNumber.getText().toString().trim());
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvServerStatus.setText("Not Connected!");
                    }
                });
                try {
                    socket = serverSocket.accept();
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvServerStatus.setText("Connected\n");
                        }
                    });
                    new Thread(new Thread2()).start();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class Thread2 implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                   final String message =  input.readLine();
                   if (message != null){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               tvMessageReceived.append("Client: " + message + "\n");
                           }
                       });
                   }else {
                       thread = new Thread(new Thread1());
                       thread.start();
                       return;
                   }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable{
        private String message;
        Thread3(String message){
            this.message=message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessageReceived.append("Server: " + message + "\n");
                    etMessageSent.setText("");
                }
            });
        }
    }
}