package com.example.androidjs.NFCUtil;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFCUtil {
    private Tag tag;
    private static NFCUtil helper;
    private byte[] bytes = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
    private NFCUtil.NFCCallback callback;
    public NFCUtil(Intent intent) {
        this.tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
    /**
     * 单例初始化
     *
     * @param intent
     * @return
     */
    public static NFCUtil getInstence(Intent intent) {
        if (helper == null) {
            helper = new NFCUtil(intent);
        }
        return helper;
    }
    /**
     * 写卡
     *
     * @param str      书写内容，16个字节
     * @param a        书写的扇区 (从0开始数)
     * @param b        书写的块(从0开始数)
     * @param callback 返回监听
     */
    public void writeblock(byte[] str, int a, int b, NFCUtil.NFCCallback callback) {
        MifareClassic mfc = MifareClassic.get(tag);
        if (null != mfc) {
            try {
                //连接NFC
                mfc.connect();
                //范围外不可写
                if ((a==0&&b==0)||(b > 3 || b < 0)||(a > 15 || a < 0)){
                    callback.WriteSuccess(false);
                    return;
                }
                //验证扇区密码
                boolean isOpen = mfc.authenticateSectorWithKeyA(a, bytes);
                if (isOpen) {
                    int bIndex = mfc.sectorToBlock(a);
                    //写卡,str.length=16
                    mfc.writeBlock(bIndex + b, str);
                }
                callback.WriteSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                callback.WriteSuccess(false);
            } finally {
                try {
                    mfc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param str 长度必须为96
     * @param callback
     */
    public void writeSixblock(byte[] str, NFCUtil.NFCCallback callback) {
        MifareClassic mfc = MifareClassic.get(tag);
        if (null != mfc) {
            try {
                //连接NFC
                mfc.connect();
                    int y=0;
                    for (int j=0;j<2;j++){
                        //验证扇区密码
                        boolean isOpen = mfc.authenticateSectorWithKeyA(4+j, bytes);
                        if (isOpen) {
                        for (int i=0;i<3;i++){
                            int bIndex = mfc.sectorToBlock(4+j);
                            mfc.writeBlock(bIndex + i, Arrays.copyOfRange(str,y*16,y*16+16));
                            y++;
                        }
                        }
                    }
                callback.WriteSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                callback.WriteSuccess(false);
            } finally {
                try {
                    mfc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**20190701
     * @param str
     * @param sector 扇区号
     * @param block 块号
     * @param start
     * @param end
     * @param callback
     */
    public void writeBytes(final byte[] str, final int sector, final int block, final int start, final int end, final NFCUtil.NFCCallback callback){
        if (str.length!=(end-start+1)){
            callback.WriteSuccess(false);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                MifareClassic mfc = MifareClassic.get(tag);
                if (null != mfc) {
                    try {
                        mfc.connect();
                        if ((sector < 0 || sector > 15)||(block < 0 || block > 3)) {
                            callback.error();
                            return;
                        }
                        boolean isOpen = mfc.authenticateSectorWithKeyA(sector, bytes);
                        if (isOpen) {
                            int bIndex = mfc.sectorToBlock(sector);
                            byte[] data = mfc.readBlock(bIndex + block);
                            int j=0;
                            for (int i=start;i<end+1;i++){
                                data[i]=str[j++];
                            }
                            //写卡,data.length=16
                            mfc.writeBlock(bIndex + block, data);
                            callback.WriteSuccess(true);
                        }
                    } catch (Exception e) {
                        callback.error();
                        callback.WriteSuccess(false);
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    /**
     * 修改密码
     *
     * @param password 书写密码，16个字节
     * @param a        书写的扇区
     * @param callback 返回监听
     */
    public void changePasword(byte[] password, int a, final NFCUtil.NFCCallback callback) {
        MifareClassic mfc = MifareClassic.get(tag);
        byte[] data = new byte[16];
        if (null != mfc) {
            try {
                mfc.connect();
                if ((password.length != 6)||(a > 15 || a < 0)) {
                    callback.WriteSuccess(false);
                    return;
                }
                //将密码转换为keyA//将密码转换为KeyB
                for (int i = 0; i < 6; i++) {
                    data[i] = password[i];
                    data[i + 10] = password[i];
                }
                //输入控制位
                data[6] = (byte) 0xff;
                data[7] = (byte) 0x07;
                data[8] = (byte) 0x80;
                data[9] = (byte) 0x69;
                //验证密码
                boolean isOpen = mfc.authenticateSectorWithKeyA(a, bytes);
                if (isOpen) {
                    int bIndex = mfc.sectorToBlock(a);
                    //写到扇区的最后一个块
                    mfc.writeBlock(bIndex + 3, data);
                }
                callback.WriteSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
                callback.WriteSuccess(false);
            } finally {
                try {
                    mfc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 读取NFC卡的全部信息
     *
     * @param callback
     */
    public void readAll(final NFCCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<String>> map = new HashMap<>();
                MifareClassic mfc = MifareClassic.get(tag);
                if (null != mfc) {
                    try {
                        //链接NFC
                        mfc.connect();
                        //用于判断时候有内容读取出来
                        boolean flag = false;
                        for (int i = 0; i <16; i++) {
                            List<String> list = new ArrayList<>();
                            //验证扇区密码，否则会报错（链接失败错误）
                            boolean isOpen = mfc.authenticateSectorWithKeyA(i, bytes);
                            if (isOpen) {
                                //获取扇区第一个块对应芯片存储器的位置（我是这样理解的，因为第0扇区的这个值是4而不是0）
                                int bIndex = mfc.sectorToBlock(i);
                                //String data1 = "";
                                for (int j = 0; j < 4; j++) {
                                    //读取数据
                                    byte[] data = mfc.readBlock(bIndex);
                                    bIndex++;
                                    list.add(stringUtils.bytesToString(data));
                                }
                                flag = true;
                            }
                            map.put(i + "", list);
                        }
                        if (flag) {
                            callback.callBack(map);
                        } else {
                            callback.error();
                        }
                    } catch (Exception e) {
                        callback.error();
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    /**
     * 读取NFC卡的特定扇区信息
     *
     * @param a        扇区
     * @param b        块
     * @param callback
     */
    public void readblock(final int a, final int b, final NFCCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MifareClassic mfc = MifareClassic.get(tag);
                if (null != mfc) {
                    try {
                        mfc.connect();
                        if ((a < 0 || a > 15)||(b < 0 || b > 3)) {
                            callback.error();
                            return;
                        }
                        boolean isOpen = mfc.authenticateSectorWithKeyA(a, bytes);
                        if (isOpen) {
                            int bIndex = mfc.sectorToBlock(a);
                            byte[] data = mfc.readBlock(bIndex + b);
                            callback.callBack(data);
                        } else {
                            callback.error();
                        }
                    } catch (Exception e) {
                        callback.error();
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void readSixblock(final NFCCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MifareClassic mfc = MifareClassic.get(tag);
                if (null != mfc) {
                    try {
                        mfc.connect();
                        byte[] backByte=new byte[96];
                        int n=0;
                        for (int j=0;j<2;j++){
                            boolean isOpen = mfc.authenticateSectorWithKeyA(4+j, bytes);
                            if (isOpen) {
                            for (int i=0;i<3;i++){
                                int bIndex = mfc.sectorToBlock(4+j);
                                byte[] data = mfc.readBlock(bIndex + i);
                                System.arraycopy(data,0,backByte,n*16,16);
                                n++;
                            }
                            }else {
                                callback.error();
                            }
                        }
                        callback.callBack(backByte);
                    } catch (Exception e) {
                        callback.error();
                        e.printStackTrace();
                    } finally {
                        try {
                            mfc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public interface NFCCallback {
        /**
         * 返回是否成功
         *
         * @param flag
         */
        void WriteSuccess(boolean flag);
        void callBack(Map<String, List<String>> data);

        void callBack(byte[] data);

        void error();

    }

}

