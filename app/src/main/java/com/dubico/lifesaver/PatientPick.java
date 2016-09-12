package com.dubico.lifesaver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import at.markushi.ui.CircleButton;

public class PatientPick extends AppCompatActivity {
    ImageView patient_picture;
    TextView patient_name;
    TextView patient_age;
    TextView diagnosis;
    CircleButton accept_patient;
    CircleButton switch_patient;
    Button chat_button;
    int patient_num = 0;
    boolean add_chat = false;

    ServerRequest srequest = new ServerRequest();
    boolean bool = true;
    String USERNAME;
    boolean load_complite = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_pick);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            USERNAME = extras.getString("userID");
            //The key argument here must match that used in the other activity
        }

        thread.start();
        patient_picture = (ImageView) findViewById(R.id.patient_picture);


        patient_name = (TextView) findViewById(R.id.patient_name);
        patient_age= (TextView) findViewById(R.id.patient_age);
        diagnosis = (TextView) findViewById(R.id.diagnosis);

        switch_patient = (CircleButton) findViewById(R.id.switch_patient);
        accept_patient = (CircleButton) findViewById(R.id.accept_patient);
        chat_button = (Button) findViewById(R.id.chat_button);


        while(true){

            if(load_complite){
                setPatient();
            break;
            }

        }

        chat_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                startActivity(new Intent(PatientPick.this, ChatView.class).putExtra("userID", USERNAME));
            }
        });

        switch_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // patient cahnges
                setPatient();

            }
        });

        accept_patient.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

            add_chat = true;


            }
        });
    }
       void setPatient (){
           patient_name.setText("Patient's name: "+ srequest.patient_list[patient_num].getName());
           patient_age.setText("Patient's age: " + srequest.patient_list[patient_num].getAge());
           diagnosis.setText("Diagnosis is :" + srequest.patient_list[patient_num].getDiagnosis());
           if(!srequest.patient_list[patient_num].getImageBitmap().equals(null)){
               byte[] decodedString = Base64.decode(srequest.patient_list[patient_num].getImageBitmap(), Base64.DEFAULT );
               Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
               patient_picture.setImageBitmap(decodedByte);
           }else{
               patient_picture.setImageResource(R.drawable.old_man);

           }
           patient_num = (patient_num+1)%srequest.patient_list.length;

       }

    Thread thread = new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                srequest.doctor_view_setup();
                load_complite = true;
                while(true){
                    if(add_chat){
                        String res = srequest.add_to_chat(  USERNAME, srequest.patient_list[(patient_num-1)%srequest.patient_list.length].getName());
                        Log.d("Add was made", res.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PatientPick.this, "patient: " + srequest.patient_list[(patient_num-1)%srequest.patient_list.length].getName() + "was added to chat",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        });
                        add_chat = false;
                    }
                }

            }

            catch (Exception e)
            {
                return;

            }
        }
    });
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

}



