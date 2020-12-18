package top.candysky.rabbit.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import top.candysky.rabbit.common.serializer.Serializer;

public class GenericMessageConverter implements MessageConverter {

    Serializer serializer;

    public GenericMessageConverter(Serializer serializer) {
        Preconditions.checkNotNull(serializer);
        this.serializer = serializer;
    }


    //public Object fromMessage(Message<?> message, Class<?> aClass) {
        /*
         message.getPayload())获取到实体，将实体反序列化成我们自己的
         但是继承org.springframework.messaging是不行的，我们的方法里面是数组或者String
         这个是不支持的
         */
    //    return this.serializer.deserialize(message.getPayload());
    //}

    /*
    序列化操作
    将我们自己封装的对象和springFramework的message相互转化
     */
    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(this.serializer.serializeRaw(o), messageProperties);
    }

    /*
    把springframework或者amqp的message转成我们自己识别的JAVA的message
    也就是top.candysky.rabbit.api.message
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return this.serializer.deserialize(message.getBody());
    }
}
