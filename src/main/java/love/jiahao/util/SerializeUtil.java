package love.jiahao.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * <big>封装后的序列化工具类</big>
 * <p></p>
 *
 * @author 13684
 * @data 2024/7/9 上午9:23
 */
public class SerializeUtil {
    /**
     * 对给定的对象进行序列化。
     * 使用Kryo库将对象转换为字节数组的字符串表示，以便于存储或传输。
     *
     * @param object 需要被序列化的对象。
     * @return 序列化后对象的字节数组的字符串表示。
     * @throws RuntimeException 如果序列化过程中发生异常。
     */
    public static String serialize(Object object) {
        // 创建Kryo实例用于序列化
        Kryo kryo = new Kryo();
        // 注册当前对象的类，以确保Kryo能够序列化它
        kryo.register(object.getClass());

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             // 使用ByteArrayOutputStream和Output来准备序列化过程
        ) {
            Output output = new Output(bos);
            // 使用Kryo将对象写入输出流
            kryo.writeObject(output, object);
            // 关闭输出流
            output.close();
            // 将序列化后的字节数组转换为字符串形式返回
            return Arrays.toString(bos.toByteArray());
        } catch (Exception e) {
            // 如果序列化过程中发生异常，抛出运行时异常
            throw new RuntimeException(e);
        }
    }


    /**
     * 反序列化方法，用于将序列化后的字符串恢复为原始对象。
     *
     * @param str 序列化后的字符串。
     * @param clazz 原始对象的类类型，用于指导反序列化过程。
     * @return 反序列化后的对象。
     * @throws RuntimeException 如果反序列化过程中发生IO异常，则抛出运行时异常。
     */
    public static <T> T deserialize(String str, Class<T> clazz) {
        // 打印序列化字符串，用于调试
        System.out.println(str);

        // 将字符串转换为字节数组，为Kryo反序列化做准备
        byte[] bytes = parseStringToByteArray(str);

        // 创建Kryo实例，用于执行反序列化
        Kryo kryo = new Kryo();

        // 注册待反序列化的类，以确保Kryo能够正确反序列化该类的对象
        kryo.register(clazz);

        try (
                // 创建字节输入流，用于Kryo读取序列化数据
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ) {
            // 创建Kryo输入对象，绑定到字节输入流
            Input input = new Input(bis);

            // 使用Kryo从输入流中读取并反序列化对象
            T t = kryo.readObject(input, clazz);

            // 关闭Kryo输入对象，释放资源
            input.close();

            // 返回反序列化后的对象
            return t;
        } catch (IOException e) {
            // 如果发生IO异常，抛出运行时异常，将异常信息封装在RuntimeException中
            throw new RuntimeException(e);
        }
    }


    /**
     * 将字符串表示的字节序列转换为字节数组。
     * 字符串应该是一个由逗号分隔的字节值列表，并且最外层被方括号包围。
     * 例如，"[1, 2, 3]" 应该被转换为字节数组 {1, 2, 3}。
     *
     * @param input 待转换的字符串，格式为 "[1, 2, 3]"。
     * @return 字符串表示的字节序列转换后的字节数组。
     */
    public static byte[] parseStringToByteArray(String input) {
        // 移除最外层的方括号
        // 去除方括号
        input = input.substring(1, input.length() - 1);

        // 使用逗号分割字符串，得到字节值的字符串数组
        String[] split = input.split(",");

        // 初始化字节数组，长度与字符串数组相同
        byte[] bytes = new byte[split.length];

        // 遍历字符串数组，将每个字符串转换为字节，并存储到字节数组中
        for (int i = 0; i < split.length; i++) {
            // 去除字符串两端的空格，然后转换为字节
            bytes[i] = Byte.parseByte(split[i].trim());
        }

        // 返回转换后的字节数组
        return bytes;
    }
}

