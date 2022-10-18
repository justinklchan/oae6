package com.example.oae;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oae.AudioSpeaker;
import com.example.oae.Chirp;
import com.example.oae.CountUpTimer;
import com.example.oae.FileOperations;
import com.example.oae.OfflineRecorder;
import com.example.oae.R;
import com.example.oae.SignalGenerator;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    Button b1;
    EditText f1Text;
    EditText f2Text;
    EditText primevolText;
    EditText lengthText;
    EditText trackTone;
    EditText trackTone2;
    EditText vol1Text;
    EditText vol2Text;
    Switch recordSwitch;
    Switch agcSwitch;
    Switch singleToneSwitch;
    Switch stereoSwitch;
    TextView fname;
    TextView statView;
    TextView statView2;
    TextView tv11;
    int bottomspeaker = AudioManager.STREAM_SYSTEM;
    String filePrefix;
    String filename;
    private int requestCode;
    private int grantResults[];
    String OAE = "TE";
    Spinner spinner;
    boolean chirp=false;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        b1 = (Button) findViewById(R.id.button);
        fname = (TextView) findViewById(R.id.textView5);
        tv11 = (TextView) findViewById(R.id.textView11);
        f1Text = (EditText) findViewById(R.id.f1);
        f2Text = (EditText) findViewById(R.id.f2);
        primevolText = (EditText) findViewById(R.id.volText);
        vol1Text = (EditText) findViewById(R.id.vol1text);
        vol2Text = (EditText) findViewById(R.id.vol2text);
        statView = (TextView) findViewById(R.id.statView);
        statView2 = (TextView) findViewById(R.id.statView2);
        lengthText = (EditText) findViewById(R.id.lengthText);
        trackTone = (EditText) findViewById(R.id.trackTone);
        trackTone2 = (EditText) findViewById(R.id.trackTone2);
        recordSwitch = (Switch)findViewById(R.id.switch1);
        singleToneSwitch = (Switch)findViewById(R.id.switch2);
        stereoSwitch = (Switch)findViewById(R.id.switch3);
        agcSwitch = (Switch)findViewById(R.id.switch4);
        spinner = (Spinner)findViewById(R.id.spinner);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        Log.e("asdf",manufacturer+","+model);
        if (model.equals("ONEPLUS A3000")) {
            bottomspeaker = AudioManager.STREAM_MUSIC;
        }
        else if (model.equals("Infinix X5515")) {
            bottomspeaker=AudioManager.STREAM_MUSIC;
        }

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && !notificationManager.isNotificationPolicyAccessGranted()) {
//
//            Intent intent = new Intent(
//                    android.provider.Settings
//                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//
//            startActivity(intent);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CONTACTS)
//                == PackageManager.PERMISSION_DENIED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{ Manifest.permission.READ_CONTACTS },
//                    0);
//        }
//        final int[] oae=new int[]{640,960,1280,1920,2560,3840,5120};
//        final int[] f1vals= new int[]{820,1230,1640,2460,3280,4920,6560};
//        final int[] f2vals=new int[]{1000,1500,2000,3000,4000,6000,8000};

//        final int[] oae=new int[]{480,645,891,1266,1829,2578,3610,5110};
//        final int[] f1vals= new int[]{615,838,1172,1641,2344,3281,4641,6563};
//        final int[] f2vals=new int[]{750,1031,1453,2016,2859,3984,5672,8016};
//        final int[] f1vals=new int[]{844,1022,1378,2092,2807,4183,6562};
//        final int[] f2vals= new int[]{1031,1327,1786,2653,3572,5357,7969};
//        final int[] oae=new int[]{657,1632,2194,3214,4337,6531,5155};
//        final int[] f1vals=new int[]{918,1327,1786,2653,3572,5358,7143};
//        final int[] f2vals= new int[]{1123,1633,2194,3215,4337,6531,8725};
//        final int[] oae=new int[]{713,1021,1378,2091,2807,4185,5561};

//        final int[] f1vals=new int[]{844,1219,1640,2438,3282,4078,4922,6563};
//        final int[] f2vals= new int[]{1031,1500,2016,2953,3985,4969,6000,8016};
//        final int[] oae=new int[]{657,938,1264,1923,2579,3187,3844,5110};
//        final int[] oae2=new int[]{1218,1781,2392,3468,4688,5860,7078,9469};
        final int[] f1vals=new int[]{1640,2438,3282,4078};
        final int[] f2vals= new int[]{2016,2953,3985,4969};
        final int[] oae=new int[]{1264,1923,2579,3187};
        final int[] oae2=new int[]{2392,3468,4688,5860};

        final double[] primevols;
        final double[] v1;
        final double[] v2;

        String phone="sch";
        if (phone.equals("sch")) {
//            primevols=new double[]{.45, .5, .6, .55, .7, .95, .6, .95};
//            v1=new double[]{.22,.4,.9,1,.35,.5,.8,.55};
//            v2=new double[]{.27,.65,.85,.7,.95,.55,.35,.3};
//            primevols=new double[]{.6, .55, .7, .95};
//            primevols=new double[]{.7,.7,.7,.7};
//            v1=new double[]{.9,1,1,.5};
//            v2=new double[]{.85,.7,.8,.55};

//            primevols=new double[]{.8,.8,.8,.75};
//            v1=new double[]{1,1,1,1};
//            v2=new double[]{.12,.45,.2,.04};
//            primevols=new double[]{.55,.58,.6,.52};
//            v1=new double[]{1,1,1,1};
//            v2=new double[]{.15,.6,.2,.05};
//            primevols=new double[]{.35,.35,.35,.25};
//            v1=new double[]{1,1,1,1};
//            v2=new double[]{.13,.65,.25,.05};
            primevols=new double[]{0,0,0,0};
            v1=new double[]{0,0,0,0};
            v2=new double[]{0,0,0,0};
        }
        else if (phone.equals("sch2")) {
//            primevols=new double[]{.45, .5, .6, .55, .7, .95, .6, .95};
//            v1=new double[]{.22,.4,.9,1,.35,.5,.8,.55};
//            v2=new double[]{.27,.65,.85,.7,.95,.55,.35,.3};
            primevols=new double[]{.6, .6, .7, .95};
            v1=new double[]{.65,.9,.4,.2};
            v2=new double[]{.35,.12,.28,.15};
        }
        else if (phone.equals("lab")) {
            primevols=new double[]{.45, .5, .6, .55, .9, 1., .6, .95};
            v1=new double[]{.22,.4,.4,.52,.25,.35,.8,.55};
            v2=new double[]{.27,.65,.45,.55,.71,.55,.35,.3};
        }
        else if (phone.equals("lab_htc")) {
            primevols=new double[]{.45, .5, .8, .55, .9, 1., .6, .95};
            v1=new double[]{.22,.4,.16,.17,.14,.16,.8,.55};
            v2=new double[]{.27,.65,.03,.1,.05,.07,.35,.3};
        }
        else if (phone.equals("lab_pixel")) {
            primevols=new double[]{.45, .5, .8, .55, .9, 1., .6, .95};
            v1=new double[]{.22,.4,.8,.72,.7,.68,.8,.55};
            v2=new double[]{.27,.65,.105,.52,.32,.32,.35,.3};
        }
        else if (phone.equals("lab_lg")) {
            primevols=new double[]{.45, .5, .6, .55, .9, 1., .6, .95};
            v1=new double[]{.22,.4,.8,.8,.23,.15,.8,.55};
            v2=new double[]{.27,.65,.11,.53,.11,.06,.35,.3};
        }
        else if (phone.equals("lab_s9")) {
            primevols=new double[]{.45, .5, .6, .55, .9, .8, .6, .95};
            v1=new double[]{.22,.4,.1,.08,.025,.045,.8,.55};
            v2=new double[]{.27,.65,.014,.06,.011,.02,.35,.3};
        }
        else if (phone.equals("lab_s9_25")) {
            primevols=new double[]{.45, .5, .4, .55, .7, .6, .6, .95};
            v1=new double[]{.22,.4,.25,.12,.11,.24,.8,.55};
            v2=new double[]{.27,.65,.05,.12,.07,.18,.35,.3};
        }
        else if (phone.equals("kenya_a")) {
            primevols=new double[]{.45, .5,.9,.7,.75,.9, .6, .95};
            v1=new double[]{.22,.4,.5,1,1,1,.8,.55};
            v2=new double[]{.27,.65,.1,.7,.5,.45,.35,.3};
        }
        else if (phone.equals("kenya_b")) {
            primevols=new double[]{.45, .5,.9,.7,.75,1, .6, .95};
            v1=new double[]{.22,.4,.65,1,1,1,.8,.55};
            v2=new double[]{.27,.65,.3,1,.8,.3,.35,.3};
        }
        else if (phone.equals("kenya_c")) {
            primevols=new double[]{.45, .5,.9,.75,.9,1, .6, .95};
            v1=new double[]{.22,.4,.65,1,1,1,.8,.55};
            v2=new double[]{.27,.65,.3,.7,.15,.2,.35,.3};
        }
        else {
            primevols=new double[]{.45, .73, .6, .55, .9, 1., .6, .95};
            v1=new double[]{.22,.43,.9,1,.35,.5,.8,.55};
            v2=new double[]{.27,.23,.85,.7,.95,.55,.35,.3};
        }
        tv11.setText(phone);
//        final double[] primevols=new double[]{.3,.35,.3,.6,.6,.5,.7,1};
//        final double[] v1=new double[]{.8,.8,.9,.5,.9,1,.3,.15};
//        final double[] v2=new double[]{.4,.3,.4,1,.1,.8,1,.6};
//        final int[] oae=new int[]{468,657,891,1266,1829,2578,3610,5110,960,1280,1920,2560,3840,5120};
//        final int[] f1vals= new int[]{609,844,1172,1641,2344,3281,4641,6563,1230,1640,2460,3280,4920,6560};
//        final int[] f2vals=new int[]{750,1031,1453,2016,2859,3984,5672,8016,1500,2000,3000,4000,6000,8000};

        List<String> spinnerArray =  new ArrayList<String>();
        for (Integer i : f2vals) {
            spinnerArray.add(i+"");
        }
        spinnerArray.add("chirp");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        stereoSwitch.setChecked(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==4) {
                    chirp=true;
                    stereoSwitch.setChecked(false);
                }
                else {
                    chirp=false;
//                    stereoSwitch.setChecked(true);
                    Log.e("asdf", position + "," + f1vals[position] + "," + f2vals[position] + "," + oae[position]);
                    f1Text.setText(f1vals[position] + "");
                    f2Text.setText(f2vals[position] + "");
                    trackTone.setText(oae[position] + "");
                    trackTone2.setText(oae2[position] + "");
                    primevolText.setText(primevols[position] + "");
                    vol1Text.setText(v1[position] + "");
                    vol2Text.setText(v2[position] + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        agcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Constants.AGC = b;
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},requestCode);
        onRequestPermissionsResult(requestCode,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},grantResults);
    }

    @Override // android recommended class to handle permissions
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted");
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
                return;
            }
        }
    }

    public void onClick(View v) {
        try {
            closeKeyboard();
            pulsetest(bottomspeaker);
        } catch (Exception e) {

        }
    }
    @Override
    public void onBackPressed() {
    }

    AudioSpeaker sp1;
    OfflineRecorder rec;
    String time;
    SendChirp sc;
    CountDownTimer timer;

    public void pulsetest(final int speakerType) {
        sc = new SendChirp(this);
        sc.execute(speakerType);
        filename = System.currentTimeMillis()+"";
//        fname.setText(filename.substring(0,filename.length()-4)+"-"+filename.substring(filename.length()-4,filename.length()));
        fname.setText(filename.substring(filename.length()-4,filename.length()));
    }

    public void stopit(View v) {
        if (sp1 != null) {
            sp1.stopit();
            sp1 = null;
        }
        if (rec != null) {
            rec.stopit();
            rec = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        Button b1 = (Button) findViewById(R.id.button);
        b1.setEnabled(true);
        if (sc != null) {
            sc.cancel(true);
            sc = null;
        }
    }

    private class SendChirp extends AsyncTask<Integer,Void,Void> {
        Activity context;
        public SendChirp(Activity context) {
            this.context=context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Button b1 = (Button) findViewById(R.id.button);
            b1.setEnabled(false);
        }

        protected void onPostExecute (Void result) {
            super.onPostExecute(result);
//            Log.e("hello4","onpostexecute");
            Button b1 = (Button) findViewById(R.id.button);
            b1.setEnabled(true);
        }
        public Void doInBackground(Integer... params) {
            int speakerType=params[0];

            int samplingFreq = 48000;

            double recordDurationInSeconds = Double.parseDouble(lengthText.getText().toString());
            double f1 = Double.parseDouble(f1Text.getText().toString());
            double f2 = Double.parseDouble(f2Text.getText().toString());
            short[] pulse = null;
            boolean stereo = stereoSwitch.isChecked();
            Constants.AGC = agcSwitch.isChecked();

            int numreps = 1000;
            int sleep=1;
            if (OAE.equals("TE")) {
//                pulse = SignalGenerator.custompulse(48,4800*5);
                pulse = SignalGenerator.custompulse2(context);
//                pulse = SignalGenerator.SineWaveSpeaker(samplingFreq,f1,0,samplingFreq);
//                recordDurationInSeconds = ((pulse.length*tePulseRepetitions)/samplingFreq)+1;
                recordDurationInSeconds=Math.ceil((numreps*pulse.length)/48e3)+sleep;
                Log.e("asdf","record duration "+recordDurationInSeconds);
                sp1 = new AudioSpeaker(context, pulse, samplingFreq, speakerType, stereo, .3);
            }
            else {
                if (singleToneSwitch.isChecked()) {
//                    pulse = SignalGenerator.SineWaveSpeaker(samplingFreq,f1,0,samplingFreq);
//                    Log.e("asdf", pulse.length + "");
//                    pulse= Chirp.continuouspulse(500,6000,4800,12000, 48e3,0);
//                    pulse= Chirp.continuouspulse(1800,4400,4800,12000, 48e3,0);
//                    pulse = SignalGenerator.continuouspulse( // Generate pulse to send into the ear to test for fluid
//                            1, 1500, 13000,
//                            48000, 0.25, 1);

                    double v1 = Double.parseDouble(vol1Text.getText().toString());
                    short[] pulse1 = SignalGenerator.SineWaveSpeaker(samplingFreq,f1,0,samplingFreq,.05);
                    short[] pulse2 = SignalGenerator.SineWaveSpeaker(samplingFreq,f1,0,samplingFreq,1);
                    pulse=Utils.concat(pulse1,pulse2);
//                    FileOperations.writetofile(context,pulse,"input-"+filename+".txt");
                    sp1 = new AudioSpeaker(context, pulse, samplingFreq, speakerType, stereo, Double.parseDouble(primevolText.getText().toString()));
                } else {
                    if (chirp) {
                        pulse=FileOperations.readrawasset_binary(context, R.raw.ofdm);
                        sp1 = new AudioSpeaker(context, pulse, samplingFreq, speakerType, stereo, Double.parseDouble(primevolText.getText().toString()));
                    }
                    else {
                        if (stereo) {
                            double v1 = Double.parseDouble(vol1Text.getText().toString());
                            double v2 = Double.parseDouble(vol2Text.getText().toString());
                            pulse = SignalGenerator.sine2speaker(f1, f2, samplingFreq, v1, v2);
                        } else {
                            pulse = SignalGenerator.combinedsine(f1, f2, samplingFreq);
                        }
                        sp1 = new AudioSpeaker(context, pulse, samplingFreq, speakerType, stereo, Double.parseDouble(primevolText.getText().toString()));
                    }
                }
            }

            int micType;
//            //        do this is regular record
//            if (Constants.AGC) {
//                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                if (audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) != null) {
//                    micType = (MediaRecorder.AudioSource.UNPROCESSED);
//                } else {
//                    micType = (MediaRecorder.AudioSource.VOICE_RECOGNITION);
//                }
//            }
//            else {
//                micType = MediaRecorder.AudioSource.DEFAULT;
//            }

            if (recordSwitch.isChecked()) {
                rec = new OfflineRecorder(Integer.parseInt(trackTone.getText().toString()),
                        Integer.parseInt(trackTone2.getText().toString()),
                        statView, statView2, context, samplingFreq, recordDurationInSeconds,
                        0, filename + ".txt", "data4", false);
            }

            try {
                Log.e("hello4","start to sleep");
                Log.e("hello4","slept");
//                Thread.sleep(1500);
                if (recordSwitch.isChecked()) {
                    Log.e("hello4","rec start");
                    rec.start();
                }

                Thread.sleep(sleep*1000);
                Log.e("hello4", "play");
                if (singleToneSwitch.isChecked()) {
                    sp1.play((int)numreps-1,30);
                }
                else {
                    sp1.play((int)numreps-1,30);
                }

                final double finalRecordDurationInSeconds = recordDurationInSeconds;
                context.runOnUiThread(new Runnable() {
                public void run() {
                    timer = new CountUpTimer((long) finalRecordDurationInSeconds * 1000) {
                    public void onTick(int second) {
                        TextView tv9 = findViewById(R.id.textView7);
                        int raw = Integer.parseInt(String.valueOf(second));
                        int min = raw / 60;
                        int sec = raw % 60;
                        if (sec < 10) {
                            tv9.setText(min + ":0" + sec + "/0:" + (int)finalRecordDurationInSeconds);
                        } else {
                            tv9.setText(min + ":" + sec + "/0:" + (int)finalRecordDurationInSeconds);
                        }
                    }
                };
                timer.start();}});

                Log.e("hello4","sleep");
//                if (Build.MODEL.equals("ONEPLUS A3000")) {
//                    recordDurationInSeconds += 5;
//                }
                Thread.sleep((long)(recordDurationInSeconds * 1000));
                Log.e("hello4","sleep done");

                sp1 = null;

                Log.e("hello4","write input");
//                FileOperations.writetofile(context,pulse,"input-"+filename+".txt");
                Log.e("hello4","write input done");

            } catch (Exception e) {
                Log.e("hello4",e.getMessage());
            }

            return null;
        }
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getTime() {
        return (System.currentTimeMillis() / 1000L)+"";
    }

//    public static native double[] fftnative(double[] data, int N);
//    public static native double[] fftnativetrim(double[] data, int N, int outcropstart, int outcropend);
//    public static native double[][] fftcomplexoutnative(double[] data, int N);
//    public static native double[] ifftnative(double[][] data);
//    public static native void conjnative(double[][] data);
//    public static native double[][] timesnative(double[][] data1,double[][] data2);

}
