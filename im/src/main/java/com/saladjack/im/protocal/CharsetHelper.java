package com.saladjack.im.protocal;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 数据交互的编解码实现类。
 *
 * Created by saladjack on 17/2/16.
 */
public class CharsetHelper {
	public static final CharsetDecoder decoder = (Charset.forName(CharsetHelper.DECODE_CHARSET)).newDecoder();

	public final static String ENCODE_CHARSET = "UTF-8";
	public final static String DECODE_CHARSET = "UTF-8";

	public static String getString(byte[] b, int len) {
		try {
			return new String(b, 0 , len, DECODE_CHARSET);
		}
		catch (UnsupportedEncodingException e) {
			return new String(b, 0 , len);
		}
	}

	public static String getString(byte[] b, int start,int len) {
		try {
			return new String(b, start , len, DECODE_CHARSET);
		}
		catch (UnsupportedEncodingException e) {
			return new String(b, start , len);
		}
	}


	public static byte[] getBytes(String str) {
		if(str != null) {
			try {
				return str.getBytes(ENCODE_CHARSET);
			}
			catch (UnsupportedEncodingException e) {
				return str.getBytes();
			}
		}
		else
			return new byte[0];
	}
}