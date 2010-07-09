package com.utils;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

public class ZIP {
	static public byte [] compress(byte [] data,int lvl){
		/*
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Deflater deflater = new Deflater(0, true);
			DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream, deflater);
			deflaterOutputStream.write(data, 0, data.length);
			deflaterOutputStream.close();
			
			return outputStream.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		*/
		return data;
		
	}
	static public byte [] decompress(byte[] data){
		/*
		try {
			ByteArrayInputStream inputStream = new ByteInputStream(data,0,data.length);
			 
			 Inflater inflater = new Inflater(true);
			 InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream,inflater);			 
			 inflaterInputStream.close();
			 
			 return data;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	*/
		
		 
		return data;

	}
	
	public static void main(String arg[]){
		String tmp="coucoucoucoucoucoucoucoucoucoucoucoucoucoucoucou";
		byte [] compress = ZIP.compress(tmp.getBytes(), 5);
		System.out.println(new String(compress));
		String uncompress= new String(ZIP.decompress(compress));
		System.out.println(uncompress);
		
		
	}
}
