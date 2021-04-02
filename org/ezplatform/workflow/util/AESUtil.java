package org.ezplatform.workflow.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {

	/**
	 * 加密
	 *
	 * @param seedKey 秘钥种子
	 * @param content 明文
	 * @return
	 */
	public static String encode(String seedKey, String content) {
		if (StringUtils.isBlank(seedKey)||StringUtils.isBlank(content)) return content;

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(seedKey.getBytes());
			keyGenerator.init(128, random);
			SecretKey originalKey = keyGenerator.generateKey();
			byte[] raw = originalKey.getEncoded();
			System.out.println("raw:" + Arrays.toString(raw));
			SecretKey key = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] byteEncode = content.getBytes("utf-8");
			byte[] byteAES = cipher.doFinal(byteEncode);
			String aesEncode = new String(new BASE64Encoder().encode(byteAES));
			return aesEncode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		//如果有错就返加nulll
		return null;
	}

	/**
	 * 解密
	 *
	 * @param seedKey 秘钥种子
	 * @param content 密文
	 * @return
	 */
	public static String decode(String seedKey, String content) {
		if (StringUtils.isBlank(seedKey)||StringUtils.isBlank(content)) return content;

		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(seedKey.getBytes());
			keygen.init(128, random);
			SecretKey originalKey = keygen.generateKey();
			byte[] raw = originalKey.getEncoded();
			SecretKey key = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] byteContent = new BASE64Decoder().decodeBuffer(content);
			byte[] byteDecode = cipher.doFinal(byteContent);
			String aesDecode = new String(byteDecode, "utf-8");
			return aesDecode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		//如果有错就返加nulll
		return null;
	}

	/**
	 * 图片解密
	 *
	 * @param base64Content 图片密文base64字符串
	 * @param slatKey       解密盐
	 * @return 图片明文base64字符串
	 * @throws Exception
	 */
	public static String decrypt(String base64Content, String slatKey) {
		if (StringUtils.isBlank(base64Content)||StringUtils.isBlank(slatKey))  return null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKey secretKey = new SecretKeySpec(slatKey.getBytes(), "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] content = Base64.decodeBase64(base64Content.getBytes());
			byte[] encrypted = cipher.doFinal(content);
			return new String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
