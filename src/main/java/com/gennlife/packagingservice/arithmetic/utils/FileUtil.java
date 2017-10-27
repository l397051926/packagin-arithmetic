package com.gennlife.packagingservice.arithmetic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by chen-song on 16/6/25.
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static String readFile(String fileName){
        InputStream inputStream =  null;
        String content = null;
        try {
            inputStream = FileUtil.class.getResourceAsStream("/"+fileName);
            if(inputStream == null){
                logger.error("inputStream=null");
                logger.error(fileName);
                throw new IOException();
            }
            content = readString(new InputStreamReader(inputStream, "utf-8"));
        } catch (IOException e) {
            logger.error("读取文件出错",e);
            return null;
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("", e);
                return null;
            }
        }
        return content;
    }

    public static final String readString(Reader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
        try {
            char[] buf = new char[1024];
            for (int i = 0; (i = reader.read(buf)) != -1;)
                sb.append(buf, 0, i);
        } finally {
            try {
                reader.close();
            } catch (Throwable e) {
                logger.warn("资源关闭时出错:"+reader.getClass().getName(),e);
            }
        }
        return sb.toString();
    }

    public static String readFileRemoveNote(String fileName)  {
        StringBuffer stringBuffer=new StringBuffer();
        try {

            InputStream inputStream = FileUtil.class.getResourceAsStream("/" + fileName);
            BufferedReader reader = null;
            if(inputStream==null)inputStream=new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));

            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                String data = tempString.trim();
                if(data.startsWith("//"))continue;
                if (!StringUtil.isEmptyStr(data)) stringBuffer.append(data);
            }
            reader.close();
        }
        catch (Exception e)
        {
            logger.error("readFileRemoveNote ",e);
        }
        return stringBuffer.toString();
    }

    public static void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                logger.info("目录: " + path + " 创建成功");
            }
        }
    }
}
