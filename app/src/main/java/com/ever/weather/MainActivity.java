package com.ever.weather;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;


public class MainActivity extends Activity {

    final int FONT_10 = 0X111;
    final int FONT_12 = 0X112;
    final int FONT_14 = 0X113;
    final int FONT_16 = 0X114;
    final int FONT_18 = 0X115;

    static TextView text;
    static ImageView img;
    static OutputStream fos;
    //Thread thread;
    String city_t;
    //static Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        //bar.setDisplayShowHomeEnabled(false);
        text = (TextView)findViewById(R.id.textView);
        text.setMovementMethod(new ScrollingMovementMethod());
        registerForContextMenu(text);
        img = (ImageView)findViewById(R.id.weather_img);
        //img.setMovementMethod(new ScrollingMovementMethod());
        try {
            fos = openFileOutput("weatherImg.png",MODE_PRIVATE);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        //String content = new WebAccessTools(this).getWebContent("http://m.weather.com.cn/mweather/101280601.shtml");
        //((TextView)findViewById(R.id.textView)).setText(content);


//        try{
//        city = URLEncoder.encode("灌阳", "utf-8");
//
//        }
//        catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//        }
        city_t = "深圳";
        new Thread(netWork).start();
        //thread.start();

        findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.city_et);
                String city_et = et.getText().toString().trim();
                if(city_et != ""){
                    city_t = city_et;
                    TextView tv = (TextView)findViewById(R.id.test);
                    tv.setText(city_t);
                    new Thread(netWork).start();
                }
            }
        });
    }

    MyHandler handler = new MyHandler();
    static Bitmap bm = null;
    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("response");
            Long time = data.getLong("time");
//            String path = data.getString("image");
//            Drawable drawable = null;
//            if(path != "")
//                drawable = Drawable.createFromPath("data/data/com.ever.weather/files/weatherImg.png");
//
//            if(drawable != null)
//                img.setImageDrawable(drawable);

            try {
                JSONObject j = new JSONObject(val).getJSONArray("results").getJSONObject(0);
                JSONArray jsonArray = j.getJSONArray("weather_data");
                //Object obj = jsonArray.get(0);
                JSONObject  json = jsonArray.getJSONObject(0);
                String str;// = obj.toString();
                str = j.getString("currentCity") + "\n";
                str += json.getString("date") + "\n";
                str += json.getString("weather") + "\n";
                str += json.getString("wind") + "\n";
                str += json.getString("temperature") + "\n";
                str += String.valueOf(time);
                text.setText(str);


                img.setImageBitmap(bm);
                //img.setText(json.getString("dayPictureUrl"));
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("Log Demo", "发生了异常情况，怎么回事呢?");
                text.setText(val);
            }

            //text.setText(val);
        }

    }

    Runnable netWork = new Runnable(){
        @Override
        public void run(){
            //http://m.weather.com.cn/data/101280601.html   ak = zCuIDiPbcoCcbVZGeqOOwXYh
            String city = "";
            try{
            city = URLEncoder.encode(city_t,"utf-8");

            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            String content = new WebAccessTools(MainActivity.this).getWebContent("http://api.map.baidu.com/telematics/v3/weather?location="+city+"&output=json&ak=zCuIDiPbcoCcbVZGeqOOwXYh"
            +"&mcode=FF:EC:F4:73:D6:5D:D6:11:79:F3:0E:C5:89:12:AD:86:66:8C:5A:5D;com.ever.weather");
            Message msg= new Message();
            Bundle data = new Bundle();
            String r;
            URL imgURL;
            HttpURLConnection conn;
            //if(content == null || content == "")
                //content = "连接有问题";
            data.putString("response", content);
            try {
                JSONObject j = new JSONObject(content).getJSONArray("results").getJSONObject(0);
                JSONArray jsonArray = j.getJSONArray("weather_data");
                //Object obj = jsonArray.get(0);
                JSONObject  json = jsonArray.getJSONObject(0);
                r = json.getString("dayPictureUrl");
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("Log Demo", "发生了异常情况，怎么回事呢?");
                r = "";
            }


            try {
                imgURL =new URL(r);
                conn = (HttpURLConnection)imgURL.openConnection();
                bm = BitmapFactory.decodeStream(conn.getInputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
            //String ret = new WebAccessTools(MainActivity.this).downLoadImg(r, fos);


            //File file = getDir("weatherImg.png", MODE_PRIVATE);
//            String path = "";
//            if(file.exists())
//                path = file.getAbsolutePath();
            Long time = Calendar.getInstance().getTimeInMillis();
            data.putLong("time", time);
            //data.putString("image",ret);
            msg.setData(data);
            handler.sendMessage(msg);
        }

    };

    final int PLAIN_ITEM = 0X11B;
    final int FONT_RED = 0X116;
    final int FONT_BLUE = 0X117;
    final int FONT_GREEN = 0X118;
    final int FONT_BLACK = 0x119;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //MenuItem i = (MenuItem)findViewById(R.id.font_10);
        //i.setChecked(true);

//        SubMenu fontMenu = menu.addSubMenu("字体大小");
//        fontMenu.setIcon(R.drawable.font);
//        fontMenu.setHeaderIcon(R.drawable.font);
//        fontMenu.setHeaderTitle("选择字体大小");
//        fontMenu.add(0, FONT_10, 0, "10号字体");
//        fontMenu.add(0, FONT_12, 0, "12号字体");
//        fontMenu.add(0, FONT_14, 0, "14号字体");
//        fontMenu.add(0, FONT_16, 0, "16号字体");
//        fontMenu.add(0, FONT_18, 0, "18号字体");
//
//        menu.add(0, PLAIN_ITEM, 0, "普通菜单项");
//
//        SubMenu colorMenu = menu.addSubMenu("字体颜色");
//        colorMenu.setIcon(R.drawable.color);
//        colorMenu.setHeaderIcon(R.drawable.color);
//        colorMenu.setHeaderTitle("选择文字颜色");
//        colorMenu.add(0, FONT_RED, 0, "红色");
//        colorMenu.add(0, FONT_BLUE, 0, "蓝色");
//        colorMenu.add(0, FONT_GREEN, 0, "绿色");
//        colorMenu.add(0, FONT_BLACK, 0, "黑色");


        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

//        switch (id){
//            case FONT_10:
//                text.setTextSize(10 * 2);
//                break;
//            case FONT_12:
//                text.setTextSize(12 * 2);
//                break;
//            case FONT_14:
//                text.setTextSize(14 * 2);
//                break;
//            case FONT_16:
//                text.setTextSize(16 * 2);
//                break;
//            case FONT_18:
//                text.setTextSize(18 * 2);
//                break;
//            case FONT_RED:
//                text.setTextColor(Color.RED);
//                break;
//            case FONT_BLUE:
//                text.setTextColor(Color.BLUE);
//                break;
//            case FONT_GREEN:
//                text.setTextColor(Color.GREEN);
//                break;
//            case FONT_BLACK:
//                text.setTextColor(Color.BLACK);
//                break;
//            case PLAIN_ITEM:
//                Toast.makeText(MainActivity.this,"你单击了普通菜单项",
//                        Toast.LENGTH_SHORT).show();
//                break;
//        }

        switch (id){
            case R.id.font_10:
                text.setTextSize(10 * 2);
                item.setChecked(true);
                break;
            case R.id.font_12:
                text.setTextSize(12 * 2);
                item.setChecked(true);
                break;
            case R.id.font_14:
                text.setTextSize(14 * 2);
                item.setChecked(true);
                break;
            case R.id.font_16:
                text.setTextSize(16 * 2);
                item.setChecked(true);
                break;
            case R.id.font_18:
                text.setTextSize(18 * 2);
                item.setChecked(true);
                break;
            case R.id.red_font:
                text.setTextColor(Color.RED);
                break;
            case R.id.blue_font:
                text.setTextColor(Color.BLUE);
                break;
            case R.id.green_font:
                text.setTextColor(Color.GREEN);
                break;
            case R.id.black_font:
                text.setTextColor(Color.BLACK);
                break;
            case R.id.plain_item:
                Toast.makeText(MainActivity.this, "你单击了普通菜单项",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return true; //super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View source,
                                   ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(0,0,0,"刷新");
        menu.setHeaderTitle("Title");
        menu.setHeaderIcon(R.drawable.tools);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menu){
        int id = menu.getItemId();
        if(id == 0){
            new Thread(netWork).start();
        }

        return true;
    }
}
