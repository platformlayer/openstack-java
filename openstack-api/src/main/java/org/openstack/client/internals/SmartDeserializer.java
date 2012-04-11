package org.openstack.client.internals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.openstack.client.internals.SimpleClassInfo.FieldInfo;

/**
 * A JSON deserializer that can cope if an object is wrapped or not-wrapped
 * 
 * i.e.. { server: { } } or just { }
 */
public class SmartDeserializer<T> extends CustomDeserializer<T> {

	public SmartDeserializer(Class<T> clazz) {
		super(clazz);
	}

	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		T t = newInstance(clazz);

		JsonToken token = jp.getCurrentToken();
		if (token != JsonToken.START_OBJECT) {
			throw ctxt.wrongTokenException(jp, JsonToken.START_OBJECT, "Unexpected token");
		}

		int keyCount = 0;

		while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
			switch (token) {
			case FIELD_NAME:
				String key = jp.getText();

				FieldInfo fieldInfo = classInfo.getField(key);
				if (fieldInfo == null) {
					if (keyCount == 0) {
						if (key.equals(classInfo.getJsonName())) {
							// It looks like a wrapper field
							jp.nextToken();
							t = deserialize(jp, ctxt);
							if (JsonToken.END_OBJECT != jp.nextToken()) {
								throw ctxt.wrongTokenException(jp, JsonToken.END_OBJECT, "Expected end of object");
							}
							return t;
						}
					}

					if (classInfo.hasAnyAttribute()) {
						Map<QName, Object> anyAttribute = classInfo.getAnyAttribute(t);
						QName qname = new QName(key);
						switch (jp.nextToken()) {
						case VALUE_STRING:
							anyAttribute.put(qname, jp.getText());
							break;

						default:
							throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "Unexpected token");
						}
					} else {
						reportUnknownProperty(ctxt, clazz, key);
						jp.skipChildren();
					}
					continue;
				}

				if (fieldInfo.getCollectionItemType() != null) {
					jp.nextToken();
					Class elementClass = fieldInfo.getCollectionItemType();
					List list = readArray(elementClass, jp, ctxt);
					fieldInfo.setValue(t, list);
				} else {
					jp.nextToken();
					Class<?> valueType = fieldInfo.getValueType();
					Object o = jp.readValueAs(valueType);
					fieldInfo.setValue(t, o);
				}
				break;

			default:
				throw ctxt.wrongTokenException(jp, JsonToken.FIELD_NAME, "Unexpected token");
			}

			keyCount++;
		}

		return t;
	}

}
