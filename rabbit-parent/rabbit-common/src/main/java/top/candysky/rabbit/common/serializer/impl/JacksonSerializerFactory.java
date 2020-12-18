package top.candysky.rabbit.common.serializer.impl;

import top.candysky.rabbit.api.Message;
import top.candysky.rabbit.common.serializer.Serializer;
import top.candysky.rabbit.common.serializer.SerializerFactory;

public class JacksonSerializerFactory implements SerializerFactory {

	public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();
	
	@Override
	public Serializer create() {
		return JacksonSerializer.createParametricType(Message.class);
	}

}
