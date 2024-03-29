package cn.hhh.server.redis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description SerializableUtil
 * @Author HHH
 * @Date 2023/9/9 2:44
 */
public class SerializableUtil {

    private static byte[] EMPTY_BYPE = new byte[0];

    /**
     * java对象序列化成字节数组
     *
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) {
        if (object == null) {
            return EMPTY_BYPE;
        }
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(SerializableUtil.class.getSimpleName() + " requires a Serializable payload " +
                    "but received an object of type [" + object.getClass().getName() + "]");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 字节数组反序列化成java对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return object;
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 字节数组集合转对象集合
     * @param bytes
     * @return
     */
    public static List toObjectList(Collection<byte[]> bytes) {
        if (bytes == null || bytes.size() == 0) {
            return Collections.emptyList();
        }
        List<Object> objects = new ArrayList<>();
        for (byte[] aByte : bytes) {
            objects.add(SerializableUtil.toObject(aByte));
        }
        return objects;
    }

}

