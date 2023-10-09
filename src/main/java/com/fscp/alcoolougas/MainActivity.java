package com.fscp.alcoolougas;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    TextView txtResult, txtAlcool, txtGas,lastTxt;
    SeekBar sldrAl, sldrGas;
    Float alcool = new Float(1),gas= new Float(1);
    int lastA = 0, lastG=0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private FirebaseAnalytics mFirebaseAnalytics;

    private String u="0",d="0",c="0",m="0",last="-",combustivel="-";
    private final String start="Último abst. :";
    NumberPicker npU,npD,npC,npM;
    private SharedPreferences prefs;
    private Button btnRegistrar;
    private RadioButton radioGas;
    private RadioButton radioAlcool;
    private String lastDate= "";
    private Set<String> set;
    //    com.google.android.gms.ads.AdView adView;
    LinkedList<String> sum_set = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtAlcool = (TextView) findViewById(R.id.txtAl);
        txtGas = (TextView) findViewById(R.id.txtGas);
        sldrAl = (SeekBar) findViewById(R.id.sldrAl);
        sldrGas = (SeekBar) findViewById(R.id.sldrGas);

        npU = (NumberPicker) findViewById(R.id.npunidade);
        npD = (NumberPicker) findViewById(R.id.npdez);
        npC = (NumberPicker) findViewById(R.id.npcent);
        npM = (NumberPicker) findViewById(R.id.npM);

        npU.setMinValue(0);
        npU.setMaxValue(9);
        npU.setWrapSelectorWheel(true);
        npU.setValue(0);
        npU.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npD.setMinValue(0);
        npD.setMaxValue(9);
        npD.setWrapSelectorWheel(true);
        npD.setValue(0);
        npD.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npC.setMinValue(0);
        npC.setMaxValue(9);
        npC.setWrapSelectorWheel(true);
        npC.setValue(0);
        npC.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npM.setMinValue(0);
        npM.setMaxValue(9);
        npM.setWrapSelectorWheel(true);
        npM.setValue(0);
        npM.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        NumberPicker.OnValueChangeListener vcl = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(picker == npU)
                    u=new Integer(newVal).toString();
                if(picker == npD)
                    d=new Integer(newVal).toString();
                if(picker == npC)
                    c=new Integer(newVal).toString();
                if(picker == npM)
                    m=new Integer(newVal).toString();

                calcular(getNPFvalue());
            }
        };

        npU.setOnValueChangedListener(vcl);
        npD.setOnValueChangedListener(vcl);
        npC.setOnValueChangedListener(vcl);
        npM.setOnValueChangedListener(vcl);

        txtAlcool.setText("Preço do Alcool");
        txtGas.setText("Preço da Gasolina");

        lastTxt = (TextView) findViewById(R.id.lastTxt);
        btnRegistrar  =(Button) findViewById(R.id.registrar);
        radioAlcool = (RadioButton) findViewById(R.id.alcool);
        radioGas = (RadioButton) findViewById(R.id.gasolina);


        MobileAds.initialize(getApplicationContext(),"ca-app-pub-1158950443496217/3122890284");

        com.google.android.gms.ads.AdView adView = (com.google.android.gms.ads.AdView) findViewById(R.id.adView);
//        adView.setAdUnitId("ca-app-pub-1158950443496217/3122890284");
//        adView.setAdSize(AdSize.BANNER);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //teste para commit
//        adView.setAdSize(AdSize.SMART_BANNER);

        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "click");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "click ad");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "ad");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            }
        });

        radioGas.setChecked(true);//caso nao haja nenhum valor setado
        loadPreferences();

        if(!last.equalsIgnoreCase("-")){
            if(combustivel.trim().equalsIgnoreCase("Gasolina")) {
                setNpsValues(last);
                radioGas.setChecked(true);
            }
            else {
                if (combustivel.trim().equalsIgnoreCase("Alcool")) {
                    String palcool = last.substring(1);
                    Float valcool = new Float(palcool.charAt(0) + "." +
                            palcool.charAt(2) +
                            palcool.charAt(3) +
                            palcool.charAt(4)
                    );
                    String pgas = new Float(valcool - 0.001 / .7).toString();
                    setNpsValues(pgas);
                    radioAlcool.setChecked(true);

                }
            }
        }

        lastTxt.setText(start+combustivel+" a "+last);



        View.OnClickListener click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==radioAlcool){
                    radioGas.setChecked(false);
                }
                if(v==radioGas){
                    radioAlcool.setChecked(false);
                }
                if(v==btnRegistrar){
                    last=getNPvalue();

                    if(radioAlcool.isChecked()){
                        last= calcular(getNPFvalue());
                        combustivel="Alcool";
                    }
                    else{
                        combustivel="Gasolina";
                    }
                    Date now = java.util.Calendar.getInstance().getTime();
                    String dt  = (String) android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss",now);
                    Log.d("DEBUG", "onClick: "+now);
                    savePreferencesApp(last,combustivel,dt);
                    lastTxt.setText(start+combustivel+" a:"+last);
                    fireEvent(last,combustivel,dt);

                }

            }
        };
        radioAlcool.setOnClickListener(click);
        radioGas.setOnClickListener(click);
        btnRegistrar.setOnClickListener(click);




        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "start");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "appstart");
        mFirebaseAnalytics.logEvent("REGISTRO", bundle);

        System.out.print(true);


    }

    private String getNPvalue(){
        return ""+npU.getValue()+","+npD.getValue()+npC.getValue()+npM.getValue();
    }

    private Float getNPFvalue(){
        return new Float(npU.getValue()+"."+npD.getValue()+npC.getValue()+npM.getValue());
    }
    private void savePreferencesApp(String last, String combustivel, String s) {
        SharedPreferences.Editor ed = prefs.edit();

        if(last=="0,000")//nao deve registrar
            return;

        String newValue = s+"."+last+"."+combustivel;

        // set.forEach(sum_set::add); 1.8
        for(String stt :set){
            sum_set.add(stt);//para no persistente ir todos
        }
        sum_set.push(newValue);

        String lastabast = "lastabst";
        ed.putStringSet(lastabast,new TreeSet(sum_set));

        ed.commit();
    }

    private void fireEvent(String value,String combustivel,String date) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "reg");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Registro");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "number");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "" + value);
        mFirebaseAnalytics.logEvent("REGISTRO", bundle);
        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "cb");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "combustive");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "string");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "" + combustivel);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "dt");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Data");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "date");
        bundle.putString(FirebaseAnalytics.Param.VALUE, "" + date);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void loadPreferences() {
        prefs= this.getSharedPreferences("preferences.xml",Context.MODE_PRIVATE);
        set = prefs.getStringSet("lastabst",new TreeSet<String>());
        if(set.size()!=0) {
            TreeMap<String,String[]> map = new TreeMap<String,String[]>();
            for(String str : set){
                String[] vs = str.split("\\.");
                String[] v = {vs[1],vs[2]};
                map.put(vs[0],v);
            }

            lastDate = map.lastKey();
            last = map.get(lastDate)[0];
            combustivel = map.get(lastDate)[1];
            }
        else
            Log.d("DEBUG", "loadPreferences: no prefs");
    }

    private void setNpsValues(String last) {
        npU.setValue(new Integer(""+last.charAt(0)));
        npD.setValue(new Integer(""+last.charAt(2)));
        npC.setValue(new Integer(""+last.charAt(3)));
        npM.setValue(new Integer(""+last.charAt(4)));
        calcular(new Float(""+last.charAt(0)+"."+
                              last.charAt(2)+
                              last.charAt(3)+
                              last.charAt(4)
                        )
        );
    }

    private void updateView(View view){
        if(view instanceof EditText){
            ((EditText)view).setTextSize(15);
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

//        System.out.println(i);

        if (seekBar == sldrAl) {
            alcool = (float)i/100;
            txtAlcool.setText(String.format("%.2f",alcool));
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "sl_gas");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "slider_alcool");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "number");
            bundle.putString(FirebaseAnalytics.Param.VALUE,""+alcool );
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
        if (seekBar == sldrGas) {
            gas = (float)i/100;
            txtGas.setText(String.format("%.2f",gas));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "sl_gas");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "slider_gas");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "number");
            bundle.putString(FirebaseAnalytics.Param.VALUE,""+gas );
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        }
        calcular();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private String calcular(float gas){
        float precoVantAlcool = gas*(float).7+(float)0.001;
        last = ""+gas;
        last=last.replace('.',',');

        if(gas==0.000)
            txtResult.setText("Gasolina Gratis?");
        else {
            Locale pt_br = new Locale("pt","BR");
            String formatted = String.format(pt_br, "> %.3f", precoVantAlcool);
            System.out.println(formatted);
            txtResult.setText(formatted);
            return formatted;
        }
        return "";

    }

    private void calcular() {

        float pGas, pAlcool;
        pGas = this.gas;
        pAlcool = this.alcool;

        if (pGas * 0.7 >= pAlcool) {
            txtResult.setText("Abasteça com alcool");

        } else {
            txtResult.setText("Abasteça com Gasolina");
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
