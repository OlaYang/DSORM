package com.meiqi.data.util;

import com.alibaba.fastjson.JSONWriter;
import com.meiqi.data.handler.BaseRespInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-9-26
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
public class JSONStreamUtil {
    public static String toJSONString(File file, List<Map<String, String>> rows) throws Exception {
        JSONWriter writer = new JSONWriter(new FileWriter(file));
        writer.startObject();
        writer.writeKey("code");
        writer.writeValue("0");
        writer.writeKey("description");
        writer.writeValue("success");
        writer.writeKey("rows");
        writer.writeValue(rows);
        writer.endObject();
        writer.close();
        InputStream in = new FileInputStream(file);
        String result = readFileByNio(in);
        file.delete();
        return result;
    }

    public static String readFileByNio(InputStream fis) throws Exception {
        StringBuffer sb = new StringBuffer();
        FileChannel channel = ((FileInputStream) fis).getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }

        return sb.toString();
    }
}
