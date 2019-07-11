package com.example.androidjs.NFCUtil;

import static com.example.androidjs.NFCUtil.stringUtils.byteToTen;
import static com.example.androidjs.NFCUtil.stringUtils.bytesToTen;
import static com.example.androidjs.NFCUtil.stringUtils.bytesToWords;
import static com.example.androidjs.NFCUtil.stringUtils.wordsTobyte;
import static com.example.androidjs.NFCUtil.stringUtils.wtenToByte;
import static com.example.androidjs.NFCUtil.stringUtils.wtenToHexToByte;

public class Producing {
    public byte[] getBytes() {
        return bytes;
    }
    private static byte[] bytes = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00,(byte) 0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte) 0x00,
            (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte) 0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte) 0x00, (byte) 0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00,(byte) 0x00, (byte) 0x00, (byte)0x00, (byte) 0x00};
    private int type;
    private int ID;
    private int zjc;//总经长
    private int jm;//经密
    private int wm;//纬密
    private int zs;//织缩
    private String productNo;
    private String QQ;
    private String jzbh;//经轴编号
    private int batch;//批号
    private String info;
    public static Producing proCard;
    private Producing(){}
    public Producing(int type,int id,int zjc,int jm,int wm,int zs,String proNo,String qq,String jzbh,int batch,String info){
        bytes[0]=wtenToByte(type);
        for (int i=0;i<4;i++){
            bytes[i+2]=wtenToHexToByte(id,4)[i];
            bytes[i+6]=wtenToHexToByte(zjc,4)[i];
        }
        for (int i=0;i<2;i++){
            bytes[i+10]=wtenToHexToByte(jm,2)[i];
            bytes[i+12]=wtenToHexToByte(wm,2)[i];
            bytes[i+14]=wtenToHexToByte(zs,2)[i];
            bytes[i+64]=wtenToHexToByte(batch,2)[i];
        }
        byte[] prono=wordsTobyte(proNo,24);
        for (int i=0;i<24;i++){
            bytes[i+16]=prono[i];
        }
        byte[] tempqq=wordsTobyte(qq,6);
        for (int i=0;i<6;i++){
            bytes[i+40]=tempqq[i];
        }
        byte[] tempjzbh=wordsTobyte(jzbh,16);
        for (int i=0;i<16;i++){
            bytes[i+48]=tempjzbh[i];
        }
        byte[] tempinfo=wordsTobyte(info,30);
        for (int i=0;i<30;i++){
            bytes[i+66]=tempinfo[i];
        }
    }
    public static Producing getInstance(byte[] byt){
        bytes=byt;
        if (proCard==null){
            proCard=new Producing();
        }
        return proCard;
    }
    public int getType() {
        type=byteToTen(bytes[0]);
        return type;
    }

    public int getID() {
        byte[] idbyte=new byte[4];
        for (int i=0;i<4;i++){
            idbyte[i]=bytes[i+2];
        }
        return bytesToTen(idbyte);
    }

    public int getZjc() {
        byte[] idbyte=new byte[4];
        for (int i=0;i<4;i++){
            idbyte[i]=bytes[i+6];
        }
        return bytesToTen(idbyte);
    }

    public int getJm() {
        byte[] idbyte=new byte[2];
        for (int i=0;i<2;i++){
            idbyte[i]=bytes[i+10];
        }
        return bytesToTen(idbyte);
    }

    public int getWm() {
        byte[] idbyte=new byte[2];
        for (int i=0;i<2;i++){
            idbyte[i]=bytes[i+12];
        }
        return bytesToTen(idbyte);
    }

    public int getZs() {
        byte[] idbyte=new byte[2];
        for (int i=0;i<2;i++){
            idbyte[i]=bytes[i+14];
        }
        return bytesToTen(idbyte);
    }

    public String getProductNo() {
        byte[] namebyte=new byte[24];
        for (int i=0;i<24;i++){
            namebyte[i]=bytes[i+16];
        }
        return bytesToWords(namebyte);
    }

    public String getQQ() {
        byte[] namebyte=new byte[6];
        for (int i=0;i<6;i++){
            namebyte[i]=bytes[i+40];
        }
        return bytesToWords(namebyte);
    }

    public String getJzbh() {
        byte[] namebyte=new byte[16];
        for (int i=0;i<16;i++){
            namebyte[i]=bytes[i+48];
        }
        return bytesToWords(namebyte);
    }

    public int getBatch() {
        byte[] idbyte=new byte[2];
        for (int i=0;i<2;i++){
            idbyte[i]=bytes[i+64];
        }
        return bytesToTen(idbyte);
    }

    public String getInfo() {
        byte[] namebyte=new byte[30];
        for (int i=0;i<30;i++){
            namebyte[i]=bytes[i+66];
        }
        return bytesToWords(namebyte);
    }
}
