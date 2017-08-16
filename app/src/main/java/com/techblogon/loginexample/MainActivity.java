package com.techblogon.loginexample;

/**
 * Created by Dell on 18-11-2016.
 */

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class MainActivity extends HomeActivity {

    private TextView txtSpeechInput;
    Handler bluetoothIn;
   // final int handlerState = 0;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ImageButton On, Off, Discnt, Abt;
    TextView txtArduino;
    boolean alreadyRun=false;
    private Button b1,b2;


    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    String phoneNumber = null;
    //TextView  testView1, txtString;

    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    InputStream is;
    TextView mTextView;
    //String readMessage = new String();
    private ConnectedThread mConnectedThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();



        new ConnectBT().execute();










        address = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
      //  txtString = (TextView) findViewById(R.id.txtString);
        //testView1 = (TextView) findViewById(R.id.testView1);
        final TextView txtArduino = (TextView) findViewById(R.id.txtArduino);
        mTextView=(TextView)findViewById(R.id.textView5);
        /*mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write(phoneNumber);	// Send "0" via Bluetooth*/


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                    // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                    // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);                // extract string
                            sb.delete(0, sb.length());                                        // and clear
                            txtArduino.setText(sbprint);
                            // update TextView

                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            }

            ;
        };

        // hide the action bar
      //  getActionBar().hide();



        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(alreadyRun==false) {


                    try {
                        mTextView.setText(getIntent().getStringExtra("myText"));
                        phoneNumber=getIntent().getStringExtra("myText");
                        btSocket.getOutputStream().write(phoneNumber.getBytes());
                        if (phoneNumber.equalsIgnoreCase("")) {
                            alreadyRun = false;
                        }
                        else
                        {
                         alreadyRun = true;
                        }

                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));

                    if ((btSocket!=null)&&(((result.get(0)).equalsIgnoreCase("fan on"))||((result.get(0)).equalsIgnoreCase("fun on"))||((result.get(0)).equalsIgnoreCase("find on"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("1".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&((((result.get(0)).equalsIgnoreCase("fan off"))||((result.get(0)).equalsIgnoreCase("fan of")))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("2".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&((((result.get(0)).equalsIgnoreCase("kitchen")))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("3".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("kitchen off"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("4".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("hall"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("5".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("hall off"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("6".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("bedroom"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("7".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();

                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("bedroom off"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("8".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("fire"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("9".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equals("fire off"))||((result.get(0)).equals("fire of"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("10".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&((((result.get(0)).equals("PIR On")))||(((result.get(0)).equals("Pir on")))||(((result.get(0)).equals("pir on")))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("11".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&((((result.get(0)).equalsIgnoreCase("PIR off"))||((result.get(0)).equalsIgnoreCase("pir of"))||((result.get(0)).equalsIgnoreCase("Pir off"))||((result.get(0)).equalsIgnoreCase("PIR of")))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("12".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equalsIgnoreCase("guest"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("13".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equalsIgnoreCase("open"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("14".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equalsIgnoreCase("all on"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("15".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                    else if((btSocket!=null)&&(((result.get(0)).equalsIgnoreCase("all off"))))
                    {
                        try
                        {
                            btSocket.getOutputStream().write("16".getBytes());
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();


                        }
                        catch (IOException e)
                        {
                            msg("Error");
                        }
                    }
                }
                break;
            }

        }
    }

    private void msg(String error) {

        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

*/
    /*public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            *//*case R.id.bluetooth: {
                Intent bluetoothIntent = new Intent(MainActivity.this, ledControl.class);

                startActivity(bluetoothIntent);
            }*//*
            default:
                return super.onOptionsItemSelected(item);
        }

    }*/
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    bluetoothIn.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();        // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String message) {
            //Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                //Log.d(TAG, "...Error data send: " + e.getMessage() + "...");

            }
        }
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private final InputStream mmInStream;
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        private ConnectBT() {
            mmInStream = null;
        }

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }


        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection


                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }



        private void Disconnect()
        {
            if (btSocket!=null) //If the btSocket is busy
            {
                try
                {
                    btSocket.close(); //close connection
                }
                catch (IOException e)
                { msg("Error");}
            }
            finish(); //return to the first layout

        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}