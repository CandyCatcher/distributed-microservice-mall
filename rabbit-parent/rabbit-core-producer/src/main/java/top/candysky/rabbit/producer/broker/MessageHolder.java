package top.candysky.rabbit.producer.broker;

import com.google.common.collect.Lists;
import top.candysky.rabbit.api.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageHolder {

    private List<Message> messages = new ArrayList<>();

    /*
    使用一个threadLocal存储当前线程的变量
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ThreadLocal<MessageHolder> holder = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new MessageHolder();
        }
    };

    public static void add(Message message) {
        holder.get().messages.add(message);
    }

    public static List<Message> clear() {
        ArrayList<Message> messages = Lists.newArrayList(holder.get().messages);
        holder.remove();
        return messages;
    }
}
