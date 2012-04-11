package org.openstack.client.internals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

import com.google.common.collect.Lists;

public abstract class CustomDeserializer<T> extends StdDeserializer<T> {
	protected final Class<T> clazz;
	protected final SimpleClassInfo classInfo;

	public CustomDeserializer(Class<T> clazz) {
		super(clazz);
		this.clazz = clazz;
	    this.classInfo = SimpleClassInfo.get(clazz);
	}


	protected <V> List<V> readArray(Class<V> elementClass, JsonParser jp, DeserializationContext ctxt)
			throws JsonProcessingException, IOException {
		List<V> list = Lists.newArrayList();

		JsonToken token = jp.getCurrentToken();
		if (token != JsonToken.START_ARRAY) {
			throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "Unexpected token");
		}

		while ((token = jp.nextToken()) != JsonToken.END_ARRAY) {
			switch (token) {
			case START_OBJECT:
				V o = jp.readValueAs(elementClass);
				list.add(o);
				break;

			default:
				throw ctxt.wrongTokenException(jp, JsonToken.START_OBJECT, "Unexpected token");
			}
		}

		return list;
	}

	protected static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Error building " + clazz, e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Error building " + clazz, e);
		}
	}

}
