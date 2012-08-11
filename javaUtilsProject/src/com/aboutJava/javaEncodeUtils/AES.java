package com.aboutJava.javaEncodeUtils;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密/解密工具类。
 * 
 * @author zhangFeng
 * @since 0.1_2012-6-14
 */
public class AES {
	/** 默认密码 */
	private static final String DEFAULT_KEY = "0123456789ABCDEF";

	/** 加密分组长度 */
	private static final int AES_KEY_BIT_NUM = 128;

	/** AES加密相关信息配置文件名称 */
	private static final String CONFIG_FILE = "/aesconfig.properties";

	/** AES加密相关信息 */
	private static Properties properties = new Properties();

	/** AES加密/解密工具类的singleton实例 */
	private static AES instance = null;

	/** 编码器 */
	private Cipher encryptCipher = null;

	/** 解码器 */
	private Cipher decryptCipher = null;

	/**
	 * 创建一个新的SecurityUtil实例 。
	 * 
	 * @throws Exception
	 *             创建加密/解密工具失败。
	 * @since 0.1_2012-6-14
	 */
	private AES() throws Exception {
		this(DEFAULT_KEY);
	}

	/**
	 * 创建一个新的SecurityUtil实例 。
	 * 
	 * @param key
	 *            密钥字符串
	 * @throws Exception
	 *             创建加密/解密工具失败。
	 * @since 0.1_2012-6-14
	 */
	private AES(String key) throws Exception {
		// 生成密钥
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		// 防止linux下 随机生成key
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(key.getBytes());
		keyGenerator.init(AES_KEY_BIT_NUM, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] encodeFormat = secretKey.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(encodeFormat, "AES");

		// 生成编码器
		encryptCipher = Cipher.getInstance("AES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

		// 生成解码器
		decryptCipher = Cipher.getInstance("AES");
		decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	}

	/**
	 * 获取SecurityUtil的singleton实例。
	 * 
	 * @return SecurityUtil的singleton实例。
	 * @throws Exception
	 *             创建加密/解密工具失败。
	 * @since 0.1_2012-6-14
	 */
	public static AES getInstance() throws Exception {
		if (instance == null) {
			InputStream in = null;
			try {
				in = AES.class.getResourceAsStream(CONFIG_FILE);
				properties.load(in);
			} catch (Exception e) {
			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			// properties.list(System.out);
			instance = new AES(properties.getProperty("key", DEFAULT_KEY));
		}

		return instance;
	}

	/**
	 * 加密一个字节数组。
	 * 
	 * @param input
	 *            待加密的字节数组
	 * @return 加密后的字节数组
	 * @throws Exception
	 *             加密失败
	 * @since 0.1_2012-6-14
	 */
	public byte[] encript(byte[] input) throws Exception {
		return encryptCipher.doFinal(input);
	}

	/**
	 * 加密一个字符串。
	 * 
	 * @param input
	 *            待加密的字符串
	 * @return 加密后的字符串
	 * @throws Exception
	 *             加密失败
	 * @since 0.1_2012-6-14
	 */
	public String encript(String input) throws Exception {
		byte[] inputBytes = input.getBytes();
		return Base64.encode(encryptCipher.doFinal(inputBytes));
	}

	/**
	 * 解密一个字节数组。
	 * 
	 * @param input
	 *            待解密的字节数组
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             解密失败
	 * @since 0.1_2012-6-14
	 */
	public byte[] decript(byte[] input) throws Exception {
		return decryptCipher.doFinal(input);
	}

	/**
	 * 解密一个字符串。
	 * 
	 * @param input
	 *            待解密的字符串
	 * @return 解密后的字符串
	 * @throws Exception
	 *             解密失败
	 * @since 0.1_2012-6-14
	 */
	public String decript(String input) throws Exception {
		byte[] outBytes = decryptCipher.doFinal(Base64.decode(input));
		return new String(outBytes);
	}
}
