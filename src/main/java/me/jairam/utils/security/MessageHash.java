package me.jairam.utils.security;

import java.security.*;

public class MessageHash {

	public static final String MD5 = "MD5";
	
	public static String getHash(String msg, String hashtype)
	{
		byte[] msgBytes = msg.getBytes();
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance(hashtype);
		}
		catch(NoSuchAlgorithmException nsae)
		{
			return null; 
		}
		return new String(md.digest(msgBytes));
	}
	
	
}
