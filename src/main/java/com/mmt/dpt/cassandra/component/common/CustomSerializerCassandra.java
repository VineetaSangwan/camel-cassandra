package com.mmt.dpt.cassandra.component.common;

import com.netflix.astyanax.annotations.Component;
import com.netflix.astyanax.serializers.AbstractSerializer;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.CompositeSerializer;
import com.netflix.astyanax.serializers.ObjectSerializer;
import com.netflix.astyanax.serializers.StringSerializer;

public class CustomSerializerCassandra {

	public static AbstractSerializer getSerializer(String serializerString){
		switch (serializerString) {
		case "custom_tsbIDSerializer":
			return new AnnotatedCompositeSerializer(TimeStampBookingIDColumnKey.class);
		case "CompositeSerializer":
			return CompositeSerializer.get();
		case "StringSerializer":
			return StringSerializer.get();
		case "CustomCompositeStringCol":
			return new AnnotatedCompositeSerializer(CompositeStringCol.class);
		default:
			return ObjectSerializer.get();
		}
	}
	
}
