package com.dubico.lifesaver;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUp extends AppCompatActivity {
    String USERNAME;
    RadioGroup type;
    ServerRequest serverRequest;
    private static int RESULT_LOAD_IMAGE = 1;
    Button choose_image;
    Bitmap bitmap;
    EditText username;
    EditText password;
    String user_type;
    String encodedImage = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsSAAALEgHS3X78AAABuUlEQVR42u2bvUoEQRCEZ8XcSANBxdjoQDA6fAHBzBe50NhQEHwS4TAXI8H0YkHBQCOf4Iy7V6at3XFL6PqyudsZapqunp+968r/Yw0+340Zb4M9WzYKAFsAm8g/U2A8ul4Ggs/AwZf1/ukzQAFgC2AzRQ2orsM9z88vrcCtqwL19xPs1wwz5/QZoACwBbDZbDAG5nGP83x5BD0P9vekzwAFgC2AzZAaAO3dex71RJ7/wvpH674nfQYoAGwBbH5zFqh6frY4MO393W3TPtzbqQ5+czvDFMeeh0ifAQoAWwCbn2pA1fPn18emHXn85e2j+r3v36sJgefRs4HuBB0KAFsAm66Ano/wnn54Wpk2vE+4uLcfgGcL3QkGKABsAWzg+wB03T89OYKe743f2POe9BmgALAFsIFrALquo2cBP34p2NmggO8702eAAsAWwGb0u8HX90/TjvYJaI1o7XlP+gxQANgC2DS/Eww9HXC3eLYCG3vekz4DFAC2ADaj3w36muD3BR5/dpja8570GaAAsAWwGeIv7DdCkYCJPe9JnwEKAFsAmxZ+Q//n9xcaBpM+AxQAtgAhhGDyDambbz+Tl7XQAAAAAElFTkSuQmCC";
   ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        choose_image = (Button) findViewById(R.id.select_image);
         username = (EditText) findViewById(R.id.username);
         password = (EditText) findViewById(R.id.password);
        Button signup = (Button) findViewById(R.id.sign_up);
        type = (RadioGroup) findViewById(R.id.type);
         imageView = (ImageView) findViewById(R.id.profile_image);
        byte[] decodedString = Base64.decode(encodedImage, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);

        serverRequest = new ServerRequest();
        username.setHint("username");
        password.setHint("password");
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton type_selected = (RadioButton) findViewById(type.getCheckedRadioButtonId());
                if(type_selected.getText().toString().equals("Patient")){
                    user_type = "p";
                }else{
                    user_type = "d";
                }
                thread.start();
                Intent intent = new Intent(SignUp.this, PatientPick.class);
                intent.putExtra("userID", USERNAME);

                startActivity(intent);
            }
        });
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            // String picturePath contains the path of selected Image
        }
    }
    Thread thread = new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            try {
                Log.d("BITMAP with toString()", bitmap.toString());
                Log.d("BITMAP", BitMapToString(bitmap));
                Log.d("Server Responce", serverRequest.user_sign_up(user_type, username.getText().toString(),password.getText().toString(), BitMapToString(bitmap)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.URL_SAFE);
        return temp;
    }
}
