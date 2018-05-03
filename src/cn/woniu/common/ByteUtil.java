package cn.woniu.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtil {
	boolean is_little_endian = true;
	
	public ByteUtil(boolean is_little_endian) {
		this.is_little_endian = is_little_endian;
	}


	ByteBuffer wrap(byte[] data, int offset, int size) {
		if(data == null)
		{
			throw new NullPointerException();
		}
		if(offset < 0 || size < 0)
		{
			throw new IllegalArgumentException();
		}
		if(data.length < offset + size)
		{
			throw new IndexOutOfBoundsException();
		}
		ByteBuffer buffer = ByteBuffer.wrap(data, offset, size);
		if(is_little_endian)
		{
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		else
		{
			buffer.order(ByteOrder.BIG_ENDIAN);
		}
		return buffer;
	}

	public int convert_to_int(byte[] data, int offset, int size) {
		if(size == 1)
		{
			return data[0];
		}
		ByteBuffer buffer = wrap(data, offset, size);
		if(size < 4)
		{
			return buffer.getShort();
		}
		return buffer.getInt();
	}


	public int convert_to_int(byte[] data, int offset) {
		return convert_to_int(data, offset, data.length - offset);
	}


	/**
	 * 
	 * @Title: findFirst  
	 * @Description: 查找源byte[]中的第一个key(byte[]), 并返回其index; 没有则返回-1  
	 *
	 * @param data
	 * @param key
	 * @return
	 */
	public static int findFirst(byte[] data, byte[] key) {
		if(data == null || data.length == 0)
		{
			return -1;
		}
		if(key == null || key.length == 0)
		{
			return -1;
		}
		for(int i = 0; i < data.length - 1; ++i)
		{
			boolean found = true;
			for(int j = 0; j < key.length; ++j)
			{
				if(data[i + j] != key[j])
				{
					found = false;
					break;
				}
			}
			if(found)
			{
				return i;
			}
		}
		return -1;
	}
}
