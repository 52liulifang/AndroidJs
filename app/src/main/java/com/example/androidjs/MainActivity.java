package com.example.androidjs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.androidjs.NFCUtil.Employee;
import com.example.androidjs.NFCUtil.NFCUtil;
import com.example.androidjs.NFCUtil.Producing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.androidjs.NFCUtil.stringUtils.Authoriz;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout mRefresh;
private WebView h5;
private byte[] byteTowrite;//待写入数据
private Map<String, List<byte[]>> map = new HashMap<>();
private List<byte[]> list = new ArrayList<>();
private int flag=4;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                Toast.makeText(MainActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                    result(msg.getData().getString("data"));//此行会导致跳转到空白页面土司
            }else if (msg.what == 3){
                Toast.makeText(MainActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                //拼接字串，分行显示
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Authoriz(MainActivity.this);
        SharedPreferences sp1=getSharedPreferences("address",MODE_PRIVATE);
        final SharedPreferences.Editor ed=sp1.edit();
        String uri=sp1.getString("adr","www.baidu.com");
        if (hasNfc(this)==false){
            Toast.makeText(MainActivity.this, "本手机不支持NFC功能，否则请打开NFC！", Toast.LENGTH_SHORT).show();
        }
        mRefresh=findViewById(R.id.sRefresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ed.putString("adr","");
                ed.commit();
                Toast.makeText(MainActivity.this, "清除已配置网址！重启后重试。", Toast.LENGTH_SHORT).show();
                mRefresh.setRefreshing(false);
                finish();
            }
        });
        h5=findViewById(R.id.h5);
        WebSettings webSettings = h5.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);//没有这个会导致诸如淘宝，知乎等网页显示不全
        webSettings.setBlockNetworkImage(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        h5.setWebViewClient(new WebViewClient());
        h5.loadUrl(uri);
        h5.addJavascriptInterface(this,"justTest");
        h5.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                result("I AM READY!");
             }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
                //Toast.makeText(MainActivity.this, "加载网页异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (flag){
            case 0:
                write(intent);
                break;//写员工卡
            case 1:
                write(intent);
                break;//写生产卡
            case 2:
                read(intent);

                break;//读员工卡
            case 3:
                read(intent);
                break;//读生产卡
        }

    }

    /**
     * @param msg 接受待写入信息
     * @param card 卡片类型
     */
            @JavascriptInterface
            public void writeNfc(String msg,String card) {
                 //Toast.makeText(this, msg+card, Toast.LENGTH_SHORT).show();
                String str=msg.substring(0,msg.length()-1);
                String[] strArray=str.split(",");
                int length=strArray.length;
                 if (card.equals("员工卡")){
                         byteTowrite=new Employee(Integer.parseInt(strArray[0]),Integer.parseInt(strArray[1]),strArray[2]).getBytes();
                     Toast.makeText(this, "待写入数据长度"+byteTowrite.length, Toast.LENGTH_SHORT).show();
                         flag=0;
                 }else {
                     byteTowrite=new Producing(Integer.parseInt(strArray[0]),Integer.parseInt(strArray[1]),Integer.parseInt(strArray[2]),
                             Integer.parseInt(strArray[3]),Integer.parseInt(strArray[4]),Integer.parseInt(strArray[5]),strArray[6],
                             strArray[7],strArray[8],Integer.parseInt(strArray[9]),strArray[10]).getBytes();

                         Toast.makeText(this, "待写入数据长度"+byteTowrite.length, Toast.LENGTH_SHORT).show();
                     flag=1;
                         return;
                 }
             }

    /**
     * @param card 卡片类型
     */
            @JavascriptInterface
            public void readNfc(String card) {
                 Toast.makeText(this,"读"+card+",请把卡片靠近手机", Toast.LENGTH_SHORT).show();
                 if (card.equals("员工卡")){
                     flag=2;
                 }else {
                     flag=3;
                 }
             }

    /**返回
     * @param data
     */
            @SuppressLint("SetJavaScriptEnabled")
            public void result(String data) {
                 h5.loadUrl("javascript:backdata(\""+data+"\")");
             }
             /**CORE*/
    private void write(Intent intent){
        Message message = new Message();
        final Bundle bundle = new Bundle();
                if (flag==0){
                    NFCUtil.getInstence(intent).writeblock(byteTowrite,4,0,new NFCUtil.NFCCallback(){
                        @Override
                        public void callBack(Map<String, List<String>> data) {

                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void callBack(byte[] data) {

                        }

                        @Override
                        public void WriteSuccess(boolean flag) {

                            if (flag) {
                                bundle.putString("data", "写入成功");
                            } else {
                                bundle.putString("data", "写入失败");
                            }
                        }
                    });
                }else if (flag==1){
                    NFCUtil.getInstence(intent).writeSixblock(byteTowrite, new NFCUtil.NFCCallback() {
                        @Override
                        public void WriteSuccess(boolean flag) {
                            if (flag) {
                                bundle.putString("data", "写入成功");
                            } else {
                                bundle.putString("data", "写入失败");
                            }
                        }

                        @Override
                        public void callBack(Map<String, List<String>> data) {

                        }

                        @Override
                        public void callBack(byte[] data) {

                        }

                        @Override
                        public void error() {

                        }
                    });
                }
        message.setData(bundle);
        message.what = 1;
        handler.sendMessage(message);
    }

    private void read(Intent intent){
        final Bundle bundle = new Bundle();
        if (flag==2){
            NFCUtil.getInstence(intent).readblock(4, 0, new NFCUtil.NFCCallback() {
                @Override
                public void WriteSuccess(boolean flag) {

                }

                @Override
                public void callBack(Map<String, List<String>> data) {

                }

                @Override
                public void callBack(byte[] data) {
                    Message message = new Message();
                    Employee employee=Employee.getInstance(data);
                    bundle.putString("data", "卡类型是："+ employee.getType()+"；员工编号是:"+employee.getNumber()+"；员工姓名是:"+employee.getName());
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                }

                @Override
                public void error() {
                    Message message = new Message();
                    bundle.putString("data", "读取失败");
                    message.setData(bundle);
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });

        }else if (flag==3){
            NFCUtil.getInstence(intent).readSixblock(new NFCUtil.NFCCallback() {
                @Override
                public void WriteSuccess(boolean flag) {

                }

                @Override
                public void callBack(Map<String, List<String>> data) {

                }

                @Override
                public void callBack(byte[] data) {
                    Producing proCard=Producing.getInstance(data);
                    bundle.putString("data", "卡类型是："+proCard.getType()+";ID是："+proCard.getID()+";总经长是："+proCard.getZjc()+";经密是："
                            +proCard.getJm()+";纬密是："+proCard.getWm()+";织缩是："+proCard.getZs()+";产品编号是："+proCard.getProductNo()
                            +";扣号是："+proCard.getQQ()+";经轴编号是："+proCard.getJzbh()+";批号是："+proCard.getBatch()+";备注是："+proCard.getInfo());
                    Message message = new Message();
                    message.setData(bundle);
                    message.what = 2;
                    handler.sendMessage(message);
                }

                @Override
                public void error() {
                    Message message = new Message();
                    bundle.putString("data", "读取失败");
                    message.setData(bundle);
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });
        }

    }
    public static boolean hasNfc(Context context){
        boolean bRet=false;
        if(context==null)
            return bRet;
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter存在，能启用
            bRet=true;
        }
        return bRet;
    }

}
