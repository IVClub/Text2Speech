package com.example.text2speech;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    private TextToSpeech mTTs;
    private String TextSrc;
    private TextView mEditText;
    private ImageButton Speak;
    private ImageButton Load_Text;
    private LinkedList<String> sentenceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Speak = findViewById(R.id.Speak);
        Load_Text = findViewById(R.id.Load);

        sentenceList = new LinkedList<String>();

        try {
            ReadTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTTs = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                System.out.println(i);
                if (i == TextToSpeech.SUCCESS) {
                    int result = mTTs.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        Speak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mEditText = findViewById(R.id.edit_text);



        Speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Succeed");
                speak();
            }
        });
        Load_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEditText.setText(TextSrc);
            }
        });
    }
    public void play_background(){
        if (player==null){
            player = MediaPlayer.create(this, R.raw.white_noise);//create player
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (player!=null){
                        player.release();
                        player=null;
                    }
                }
            });
        }

        player.start();//start
    }

    private void speak() {
        String text = mEditText.getText().toString();
        float pitch = (float) 0.5;
        float speed = (float) 0.5;
        mTTs.setPitch(pitch);
        mTTs.setSpeechRate(speed);
        mTTs.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (mTTs != null) {
            mTTs.stop();
            mTTs.shutdown();
        }
        super.onDestroy();
    }
    public String Pop() {
        try {
            return sentenceList.removeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Text Loaded";
    }

    public void ReadTextFile() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        while((line = reader.readLine()) !=null){//Add each line to the sentences list
            String[] current_line = line.split(",");
            sentenceList.addAll(Arrays.asList(current_line));
            TextSrc+=line;
        }
    }


}
