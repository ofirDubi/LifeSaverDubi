package com.dubico.lifesaver;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Dubi on 16/08/2016.
 */
public class ServerRequest {

    String SERVER =  "https://209c77db.ngrok.io/";
    String value = "";
    Boolean check_request = false;
    Patient[] patient_list;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    public String user_sign_up( String type, String username, String password, String bitmap) throws IOException {
        RequestBody body =  new FormBody.Builder()
                .add("t", type)
                .add("u", username)
                .add("p", password)
                .add("b", bitmap)
                .build();
        Request request = new Request.Builder()
                .url(SERVER +"signup")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String get_chat(String username) throws IOException {

        Request request = new Request.Builder()
                .url(SERVER + "chat/"+username)
                .build();

        Response response = client.newCall(request).execute();
        Log.d("RAW_RESPONCE" , response.toString());
        return response.body().string();
    }


    public String add_to_chat( String du, String pu) throws IOException {
        RequestBody body =  new FormBody.Builder()

                .add("du", du)
                .add("pu", pu)
                .build();
        Request request = new Request.Builder()
                .url(SERVER + "chat")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    OkHttpClient client = new OkHttpClient();

   public void doctor_view_setup() {
            try
            {
                value = req( SERVER + "general");

                Log.d("RESPONSE", "this is the responce in json from server: " + value);
                JSONtoPatient(value);
            }
            catch (Exception e)
            {


                check_request =true;
                Log.d("server requests error", e.toString());
                e.printStackTrace();
            }
        }

    String req(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        Log.d("RAW_RESPONCE" , response.toString());
        return response.body().string();
    }


    private Patient[] JSONtoPatient(String message){
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        try {

            patient_list = mapper.readValue(message, Patient[].class);
            Log.d("Patient image", patient_list[0].getImageBitmap());
            Log.d("JSON convertion", " ################ success ###########");
            return patient_list;
        } catch(JsonGenerationException e){
            Log.d("JSON convertion error", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e){
            Log.d("JSON convertion error", e.toString());
            e.printStackTrace();
        } catch (IOException e){
            Log.d("JSON convertion error", e.toString());
            e.printStackTrace();
        }
        return null;
    }

}
