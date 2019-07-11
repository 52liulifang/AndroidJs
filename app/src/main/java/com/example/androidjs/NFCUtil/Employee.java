package com.example.androidjs.NFCUtil;

import android.support.annotation.Nullable;
import static com.example.androidjs.NFCUtil.stringUtils.byteToTen;
import static com.example.androidjs.NFCUtil.stringUtils.bytesToTen;
import static com.example.androidjs.NFCUtil.stringUtils.bytesToWords;
import static com.example.androidjs.NFCUtil.stringUtils.wordsTobyte;
import static com.example.androidjs.NFCUtil.stringUtils.wtenToByte;
import static com.example.androidjs.NFCUtil.stringUtils.wtenToHexToByte;

public class Employee {
    public byte[] getBytes() {
        return bytes;
    }
    private static byte[] bytes = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private int type;
    private int number;
    private String name;
    public static Employee employee;
    private Employee(){}
    /**
     * @param type
     * @param id
     * @param name 最多6个汉字，否则错误
     */
    public Employee(int type,int id,String name){
        bytes[0]=wtenToByte(type);
        for (int i=0;i<2;i++){
            bytes[i+2]=wtenToHexToByte(id,2)[i];
        }
        byte[] namebyte=wordsTobyte(name,12);
        for (int i=0;i<12;i++){
            bytes[i+4]=namebyte[i];
        }
    }
    public static Employee getInstance(byte[] btes){
        bytes=btes;
        if (employee==null){
            employee=new Employee();
        }
        return employee;
    }
    public int getType() {
        type=byteToTen(bytes[0]);
        return type;
    }
    public int getNumber() {
        byte[] idbyte=new byte[2];
        for (int i=0;i<2;i++){
            idbyte[i]=bytes[i+2];
        }
        return bytesToTen(idbyte);
    }
    public String getName() {
        byte[] namebyte=new byte[12];
        for (int i=0;i<12;i++){
            namebyte[i]=bytes[i+4];
        }
        return bytesToWords(namebyte);
    }
    public void setType(byte type) {
        this.type = type;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setName(String name) {
        this.name = name;
    }



}
