package cn.woniu.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import cn.woniu.common.ByteUtil;

public class JpegUtil
{
	static final int HEADER_SIZE = 4096;
	
	public static ImageInfo getInfo(String input)
	{
		File file = new File(input);
		if(!file.exists())
		{
			return null;
		}
		ImageInfo info = new ImageInfo();
		RandomAccessFile stream = null;
		try
		{
			stream = new RandomAccessFile(input, "r");
			byte[] data = new byte[Math.min(HEADER_SIZE, (int)stream.length())];
			stream.readFully(data);
			// app0
			if(!parseApp0(data, info))
			{
				return null;
			}
			// sof0
			if(!parseSof0(data, info))
			{
				return null;
			}
			info.color_depth = 24;	// jpeg is always 24-bit
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(stream != null)
			{
				try
				{
					stream.close();
				}
				catch(IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return info;
	}
	enum APP0_DEF
	{
		MARKER,
		LENGTH,
		ID,
		VERSION,
		UNIT,
		XDENSITY,
		YDENSITY,
		THUMBNAIL_WIDTH,
		THUMBNAIL_HEIGHT,
		THUMBNAIL_DATA,
	}
	static boolean parseApp0(byte[] data, ImageInfo info)
	{
		byte[][] app0 = {
				new byte[]{(byte)0xFF, (byte)0xE0},	// marker
				new byte[2],						// length
				new byte[5],						// JFIF\0
				new byte[2],						// version
				new byte[1],						// unit, 0,1,2
				new byte[2],						// x density
				new byte[2],						// y density
				new byte[1],						// thumbnail width
				new byte[1],						// thumbnail height
													// thumbnail data, not included here
			};
		// search for marker
		int offset = ByteUtil.findFirst(data, app0[APP0_DEF.MARKER.ordinal()]);
		if(offset < 0)
		{
			// not found the section
			return false;
		}

		// skip marker itself
		offset += app0[APP0_DEF.MARKER.ordinal()].length * (Byte.SIZE >> 3);
		
		// fill all other app0 section data
		for(int i = 1; i < app0.length; ++i)
		{
			int size = app0[i].length * (Byte.SIZE >> 3);
			app0[i] = Arrays.copyOfRange(data, offset, offset + size);
			offset += size;
		}
		
		// fill ImageInfo
		ByteUtil util = new ByteUtil(false);	// jpeg file is always big-endian
		int unit = util.convert_to_int(app0[APP0_DEF.UNIT.ordinal()], 0);
		int xdpi = util.convert_to_int(app0[APP0_DEF.XDENSITY.ordinal()], 0);
		int ydpi = util.convert_to_int(app0[APP0_DEF.YDENSITY.ordinal()], 0);
		switch(unit)
		{
		case 1:
			// pixel / inch
			info.xdpi = xdpi;
			info.ydpi = ydpi;
			break;
		case 2:
			// pixel / cm, convert to pixel / inch
			info.xdpi = xdpi * 2.54f;
			info.ydpi = ydpi * 2.54f;
			break;
		default:
			// 0, x density : y density, not support
			// other, invalid value
			return false;
		}
		return true;
	}
	enum SOF0_DEF
	{
		MARKER,
		LENGTH,
		SAMPLE_PRECISION,
		WIDTH,
		HEIGHT,
		COMPONENT_COUNT,
		COMPONENT_INFO,		// 1byte ID, 1byte H/V sample factor, 1byte Quantization Table selector
	}
	static boolean parseSof0(byte[] data, ImageInfo info)
	{
		byte[][] sof0 = {
			new byte[]{(byte)0xFF, (byte)0xC0},	// marker
			new byte[2],						// length
			new byte[1],						// sample precision, normally 8
			new byte[2],						// image width in pixel
			new byte[2],						// image height in pixel
			new byte[1],						// component info, 1,3,4, always 3 for JFIF
												// component info, not included here
		};
		
		int offset = ByteUtil.findFirst(data, sof0[SOF0_DEF.MARKER.ordinal()]);
		if(offset < 0)
		{
			// not found the section
			return false;
		}

		// skip the marker itself
		offset += sof0[SOF0_DEF.MARKER.ordinal()].length * (Byte.SIZE >> 3);
		
		// fill all other app0 section data
		for(int i = 1; i < sof0.length; ++i)
		{
			int size = sof0[i].length * (Byte.SIZE >> 3);
			sof0[i] = Arrays.copyOfRange(data, offset, offset + size);
			offset += size;
		}
		
		// fill ImageInfo
		ByteUtil util = new ByteUtil(false);	// jpeg file is always big-endian
		info.size.width = util.convert_to_int(sof0[SOF0_DEF.WIDTH.ordinal()], 0);
		info.size.height = util.convert_to_int(sof0[SOF0_DEF.HEIGHT.ordinal()], 0);
		
		return true;
	}
	public static void main(String[] args)
	{
		ImageInfo info = null;
		info = getInfo("D:\\Work\\Founder\\4503.jpeg");
		info = getInfo("D:\\Work\\Founder\\20151125112306705_0001.jpg");
	}
}
