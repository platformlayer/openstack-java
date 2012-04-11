package org.openstack.client.internals;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.openstack.client.internals.SimpleClassInfo.FieldInfo;

import com.google.common.collect.Lists;

/**
 * A JSON deserializer that can deserialize a 'naked' array e.g. [ { }, { } ]
 */
public class ArrayDeserializer<T> extends CustomDeserializer<T> {
    final Class<T> clazz;
    final SimpleClassInfo classInfo;
    final FieldInfo arrayField;

    public ArrayDeserializer(Class<T> clazz, String arrayField) {
        super(clazz);
        this.clazz = clazz;
        this.classInfo = SimpleClassInfo.get(clazz);

        this.arrayField = classInfo.getField(arrayField);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        T t = newInstance(clazz);

        JsonToken token = jp.getCurrentToken();
        if (token != JsonToken.START_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "Unexpected token");
        }

        List list = Lists.newArrayList();

        Class elementClass = arrayField.getCollectionItemType();

        while ((token = jp.nextToken()) != JsonToken.END_ARRAY) {
            switch (token) {
            case START_OBJECT:

                Object o = jp.readValueAs(elementClass);
                list.add(o);
                break;

            default:
                throw ctxt.wrongTokenException(jp, JsonToken.START_OBJECT, "Unexpected token");
            }
        }

        arrayField.setValue(t, list);

        return t;
    }

}
