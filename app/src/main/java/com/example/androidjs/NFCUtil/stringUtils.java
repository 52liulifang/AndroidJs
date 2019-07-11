package com.example.androidjs.NFCUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class stringUtils {
        /**10进制转16进制再变成字节最后逆序排列
         * @param valueTen 欲转换值
         * @param nums 所占byte字节数，nums必须大于1
         * @return 返回多字节
         * */
        public static byte[] wtenToHexToByte(int valueTen,int nums){
            String hex=Integer.toHexString(valueTen);
            String hexstr="";
            String begin="";
            if (hex.length()<nums*2){
                for (int i=0;i<(nums*2-hex.length());i++){
                    begin=begin+"0";
                }
                hexstr=begin+hex;
            }else if (hex.length()==nums*2){
                hexstr=hex;
            }else {
                //大于的时候
                hexstr=hex.substring(0,nums*2);
            }
            byte newHex[]=new byte[nums];
            for (int i=0;i<nums;i++){
                newHex[i]=(byte)Integer.parseInt(hexstr.substring(i*2,i*2+2), 16);
            }
            //逆序
            byte temp[]=new byte[nums];
            for (int i=0;i<nums;i++){
                temp[i]=newHex[nums-1-i];
            }
            return temp;
        }
    /**
     * @param ten
     * @return 返回单字节
     */
        public static byte wtenToByte(int ten){
             String hex=Integer.toHexString(ten);
             String hexByte=hex;
             if (hex.length()<2){
                 hexByte="0"+hex;
             }
             return (byte)Integer.parseInt(hexByte, 16);
        }

    /**
     * @param words 字符
     * @param nums 所占字节数
     * @return 返回编码的字节
     */
        public static byte[] wordsTobyte(String words,int nums){
            byte[] str=new byte[nums];
            try {
                byte[] temp=words.getBytes("GB18030");
                if (temp.length>nums){
                    for (int i=0;i<nums;i++){
                        str[i]=temp[i];
                    }
                }else {
                    for (int i=0;i<temp.length;i++){
                        str[i]=temp[i];
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return str;
        }
    /**单字节直接转整数
     * @param src 单字节
     * @return 返回单字节整数
     */
        public static int byteToTen(byte src){
                StringBuilder stringBuilder = new StringBuilder();
                char[] buffer = new char[2];
                buffer[0] = Character.forDigit((src >>> 4) & 0x0F, 16);
                buffer[1] = Character.forDigit(src & 0x0F, 16);
                stringBuilder.append(buffer);
            return hexStringToAlgorism(stringBuilder.toString());
        }

    /**多字节转整数
     * @param bt 多字节
     * @return 返回整数
     */
        public static int bytesToTen(byte[] bt){
            byte[] temp=new byte[bt.length];
            for (int i=0;i<bt.length;i++){
                temp[i]=bt[bt.length-i-1];
            }
            return hexStringToAlgorism(bytesToString(temp));
        }

    /**
     * @param bt
     * @return 返回汉字
     */
        public static String bytesToWords(byte[] bt){
            String str="";
            try {
                str=new String(bt,"GB18030");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return str;
        }

    /**
     * @param src
     * @return 返回原始字节码String类型
     */
    public static String bytesToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
    /**
     * 十六进制字符串转十进制
     *
     * @param hex
     *            十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }
    /**
     * @param urls
     * @return
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    public static void Authoriz(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http://blog.sina.com.cn/s/blog_a8b130c70102ygik.html")
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    Pattern p=Pattern.compile("AgreeToAuthoriz");
                    Matcher m=p.matcher(responseData);
                    SharedPreferences sp=context.getSharedPreferences("address",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    if (m.find()){
                        editor.putBoolean("permission",true);
                        editor.commit();
                    }else {
                        editor.putBoolean("permission",false);
                        editor.commit();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
