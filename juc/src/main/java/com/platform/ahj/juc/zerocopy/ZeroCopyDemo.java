package com.platform.ahj.juc.zerocopy;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Description: 零拷贝示例
 * @Author: ziyu
 * @Created: 2025/2/12-17:03
 * @Since:
 */
public class ZeroCopyDemo {
    private final static String CONTENT = "Zero copy implemented by MappedByteBuffer";

    private final static String FILE_NAME = "juc/mmap.txt";

    private final static String CHARSET = "UTF-8";

    public static void main(String[] args) {

        new ZeroCopyDemo().writeToFileByMappedByteBuffer();
        new ZeroCopyDemo().readFromFileByMappedByteBuffer();
    }

    /**
     * 方法writeToFileByMappedByteBuffer作用为：
     * 通过MappedByteBuffer写入文件
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public void writeToFileByMappedByteBuffer() {
        Path path = Paths.get(FILE_NAME);
        byte[] bytes = CONTENT.getBytes(Charset.forName(CHARSET));
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ,
                                                        StandardOpenOption.WRITE,
                                                        StandardOpenOption.TRUNCATE_EXISTING)) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(bytes);
                mappedByteBuffer.force();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法readFromFileByMappedByteBuffer作用为：
     * 通过MappedByteBuffer读取文件
     *
     * @param
     * @return void
     * @throws
     * @author ziyu
     */
    public void readFromFileByMappedByteBuffer() {
        Path path = Paths.get(FILE_NAME);
        int length = CONTENT.getBytes(Charset.forName(CHARSET)).length;
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, length);
            if (mappedByteBuffer != null) {
                byte[] bytes = new byte[length];
                mappedByteBuffer.get(bytes);
                String content = new String(bytes, StandardCharsets.UTF_8);
                assertEquals(content, "Zero copy implemented by MappedByteBuffer");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assertEquals(String content, String zeroCopyImplementedByMappedByteBuffer) {
        if (!content.equals(zeroCopyImplementedByMappedByteBuffer)) {
            throw new RuntimeException("content is not equal");
        }
    }

}
