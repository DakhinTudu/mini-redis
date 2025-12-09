package com.miniredis.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serializer implementation using default Java Serialization.
 * It requires the object to implement Serializable.
 */

public class JavaSerializer implements Serializer{

	
	 /** Serializes an object into a byte array */
	@Override
	public byte[] serialize(Object obj) {
		try( ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bos);
				
				) {
			out.writeObject(obj); // convert object to byes
			return bos.toByteArray();
			
		}catch (Exception e){
			throw new RuntimeException("Serialization failed",e);
		}
	}

	
	/** Deserializes byte[] into an object of given type */
	@Override
	public <T> T deserialize(byte[] data, Class<T> type) {
		
	try(ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))){
		
		return type.cast(in.readObject()); // convert bytes to object
	}catch (Exception e) {
		throw new RuntimeException("Deserialization failed");
		
	}
	}

}
