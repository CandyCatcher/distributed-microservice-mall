package top.candysky.rabbit.common.serializer;

/**
 * 序列号和反序列化的接口
 */
public interface Serializer {

    /*
    序列化成字节数组
     */
    byte[] serializeRaw(Object data);

    /*
    序列化成字符
     */
    String serialize(Object data);

    <T> T deserialize(String content);

    <T> T deserialize(Object content);
}
