package com.gennlife.packagingservice.arithmetic.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Chenjinfeng on 2017/2/10.
 */
public class DataUtils {
    public static void saveFile(String filename, Collection<String> collection) throws IOException {
        FileWriter fw = new FileWriter(filename);
        for (String value : collection) {
            fw.write(value + "\n");
        }
        fw.close();
    }

    public static void saveFile(String filename, String data) throws IOException {
        FileWriter fw = new FileWriter(filename);
        fw.write(data);
        fw.close();
    }

    public static void readLine(String filename, Collection<String> save) throws IOException {
        File file = new File(filename);
        BufferedReader reader = null;
        System.out.println("以行为单位读取文件内容，一次读一行");

        reader = new BufferedReader(new FileReader(file));

        String tempString = null;


        while ((tempString = reader.readLine()) != null) {
            String data=tempString.replace("\n","").replace("\r","");
            if(!StringUtil.isEmptyStr(data))save.add(data);
        }

        reader.close();


    }

    public static String getNowDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        return formatter.format(new Date());
    }
}

