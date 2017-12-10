package com.example.administrator.callnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;
/**
 * Created by Administrator on 2016/9/8.
 */




public class SelectKeys extends Activity {

    int CNT=0;
    private MyHandler handler = null;

    private MyApp mApp = null;

    private List<Keyword> keywordList = new ArrayList<Keyword>();

    private TextView tv=null;
    private TextView tv2=null;
    private String TotSentence;

    private String KeySentence;
    private String TotWord;
    private String KeyWord;
    private Context mContext = null;

    ISummariser summariser = new SimpleSummariser();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selectkeys);

        mContext = this;
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);
        TotSentence="";
        TotWord="";
          Intent intent = getIntent();
          String result = intent.getStringExtra("result");
        tv.setText(result);

        mApp = (MyApp) getApplication();
        handler = new MyHandler();
        mApp.setHandler(handler);
        //initKeywords();

        KeywordAdapter adapter = new KeywordAdapter(SelectKeys.this, R.layout.keyword_item, keywordList);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Keyword keyword = keywordList.get(i);
                Toast.makeText(SelectKeys.this, keyword.getName(), Toast.LENGTH_SHORT).show();

                showPopupWindow(view);//点击list弹出popupwindow
            }
        });
      //  Toast.makeText(SelectKeys.this,"oncreate", Toast.LENGTH_SHORT).show();
    }

    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.pop_window, null);
        // 设置按钮的点击事件
        Button button = (Button) contentView.findViewById(R.id.button_pop);
        button.setOnClickListener(new View.OnClickListener() {//点击save后的反应，此处应该添加存入内容

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "save is pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug

        popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }


    private void initKeywords(String result)//每次收到service传递过来的参数后用initKeywords函数进行提取关键词和显示
    {
        CNT++;

        CharSequence temp="";
        String tempp="";
      //  Intent intent = getIntent();
      //  String result = intent.getStringExtra("result");
        try {
            JSONObject jsonObject = new JSONObject(result);
            int sn = jsonObject.getInt("sn");
            boolean ls = jsonObject.getBoolean("ls");
            JSONArray ws = jsonObject.getJSONArray("ws");
            for (int i = 0; i < ws.length(); ++i)
            {
                JSONObject word = ws.getJSONObject(i);
                JSONArray cw = word.getJSONArray("cw");
                for (int j = 0; j < cw.length(); ++j)
                {
                    temp=cw.getJSONObject(j).getString("w");
                    tempp=temp.toString();

                    tempp=tempp.replace("。","");
                    tempp=tempp.replace("？","");
                    tempp=tempp.replace("！","");
                    tempp=tempp.replace("，","");

                    TotWord=TotWord+"."+tempp;

                    TotSentence=TotSentence+" "+tempp;
                    TotSentence=TotSentence.replace('。','.');
                    TotSentence=TotSentence.replace('？','.');
                    TotSentence=TotSentence.replace('！','.');
                    TotSentence=TotSentence.replace('，','.');

                    for(int k=1;k<TotWord.length();k++)
                        if(TotWord.charAt(k)=='.'&&TotWord.charAt((k-1))=='.')
                            TotWord=TotWord.substring(0, k)+TotWord.substring(k+1);

                    for(int k=1;k<TotSentence.length();k++)
                        if(TotSentence.charAt(k)=='.'&&TotSentence.charAt((k-1))=='.')
                            TotSentence=TotSentence.substring(0, k)+TotSentence.substring(k+1);

                    KeySentence=summariser.summarise(TotSentence,2);
                    KeyWord=summariser.summarise(TotWord,5);
                    //keywordList.add(new Keyword(cw.getJSONObject(j).getString("w"), R.mipmap.ic_launcher));
                }
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

        String [] strArray = new String [20];
        int start=0;
        int count=0;
        int realstart=0;
        while (KeyWord.indexOf(".", start) >= 0 && start < KeyWord.length()) {
            start = KeyWord.indexOf(".", start) ;
            strArray[count]=KeyWord.substring(realstart,start);
            count++;
            realstart =start+1;
            start=start+1;

        }

        keywordList.clear();
        for(int i=0;i<count;i++)
            keywordList.add(new Keyword(strArray[i], R.mipmap.ic_launcher));


//TotSentence是所有通话的语音识别内容，KeySentence是提取关键词后的语音识别结果,count是识别了多少个关键词
        tv.setText(KeySentence);
        tv2.setText(TotSentence);
    }

//不同activity之间的参数传递
    final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

          //  Toast.makeText(SelectKeys.this, "Myhandler", Toast.LENGTH_SHORT).show();

           // tv.setText(Integer.toString(CNT));
            String result=(String)msg.obj;
            initKeywords(result);

            }
        }

}
