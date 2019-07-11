package com.example.androidjs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.androidjs.NFCUtil.stringUtils;
public class Main2Activity extends AppCompatActivity {
private Button jump;
private EditText url;
private SharedPreferences sp;
private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        jump=findViewById(R.id.jump);
        url=findViewById(R.id.urlAddress);
        sp=getSharedPreferences("address",MODE_PRIVATE);
        String adr=sp.getString("adr","");
        abc();
        if (adr==""){
            jump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stringUtils.isHttpUrl(url.getText().toString())==false){
                        Toast.makeText(Main2Activity.this,"请输入正确网址",Toast.LENGTH_LONG).show();
                        return; }
                    editor=sp.edit();
                    editor.putString("adr",url.getText().toString());
                    editor.putBoolean("permission",true);
                    editor.commit();
                    Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
        }else {
            Intent intent=new Intent(Main2Activity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        /**test*/
        byte[] bt={(byte)0x5f,(byte)0x5f,(byte)0x5f,(byte)0x00};
        Log.d("abcd字节是======》",""+ stringUtils.bytesToTen(bt));
        //Toast.makeText(Main2Activity.this,"16进制变整数值是====》"+stringUtils.bytesToTen(bt),Toast.LENGTH_LONG).show();
        String str = ",,,小学";
        String[] buff = str.split(",");
        //Toast.makeText(Main2Activity.this,"字符是====》"+buff[0]+"长度是===》"+buff.length+"gggg"+Integer.toHexString(555),Toast.LENGTH_LONG).show();
        Log.d("abcd字节是======》",""+ Integer.toHexString(555));
        /***/
    }
    private void abc(){
        Boolean status=sp.getBoolean("permission",true);
        if (status==false){
            Toast.makeText(Main2Activity.this,"网络故障！！！",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
