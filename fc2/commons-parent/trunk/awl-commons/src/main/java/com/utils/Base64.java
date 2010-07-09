package com.utils;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64 {
	public static String encode(byte [] data){
		BASE64Encoder tmp = new BASE64Encoder();
		return tmp.encode(data);
	}
	
	public static byte [] decode(String data){
		BASE64Decoder tmp = new BASE64Decoder();
		try {
			return tmp.decodeBuffer(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
