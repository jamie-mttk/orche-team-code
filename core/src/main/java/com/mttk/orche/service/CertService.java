package com.mttk.orche.service;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.bson.Document;

import com.mttk.orche.addon.AdapterConfig;
import com.mttk.orche.core.PersistService;

/**
 * 管理证书相关的服务 注意KeyStore存储密码和每个PrivateKey存储密码可以不同 可以配合util.cert下的类使用
 */
public interface CertService extends PersistService {
	// key store的目录
	// CA - Certificate authority,认证机构
	// PENDING - Pending for check,过期的或无法确认的
	// PARTNER - Partner certificate,合作伙伴的
	// PWN - 自己的
	public enum KEY_STORE_FOLDER {
		CA("Certificate authority"), PENDING("Pending for check"), PARTNER("Partner certificate"),
		OWN("Own certificate");

		private final String label;

		private KEY_STORE_FOLDER(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	// 别名类型，证书或私钥(含证书)
	public enum ALIAS_TYPE {
		CERT, KEY
	};

	// 针对KeyStore的列表、删除和修改直接针对document操作
	/**
	 * 创建一个KeyStore并返回id
	 * 
	 * @param type        pkcs12/jks,null代表jks
	 * @param name
	 * @param description
	 * @return 主键编号
	 * @throws Exception
	 */
	String createKeyStore(KEY_STORE_FOLDER folder, String type, String name, String description, String configFolder)
			throws Exception;

	/**
	 * 把给定keyStoreImport的所有别名导入到keyStore(对应Document的_id)里
	 * 
	 * @param keyStore
	 * @param keyStoreImport
	 * @param passwordImport 用于读取keyStoreImport私钥的密码
	 * @throws Exception
	 */
	void importKeyStore(String keyStore, KeyStore keyStoreImport, String passwordImport) throws Exception;

	/**
	 * 把所有keyStore里的别名都导出到keyStoreOutputt
	 * 
	 * @param keyStore
	 * @param aliasList      别名列表,可以为空
	 * @param keyStoreOutput
	 * @Param password keyStoreOutput用来加密私钥的密码
	 * @throws Exception
	 */
	void exportKeyStore(String keyStore, List<String> aliasList, KeyStore keyStoreOutput, String password)
			throws Exception;

	/**
	 * 列出指定keyStore的所有别名
	 * 
	 * @param keyStore
	 * @return
	 * @throws Exception
	 */
	List<AdapterConfig> alias(String keyStore) throws Exception;

	/**
	 * 删除指定别名
	 * 
	 * @param keyStore
	 * @param alias
	 * @throws Exception
	 */
	void removeAlias(String keyStore, String alias) throws Exception;

	/**
	 * 增加certificate到keyStore
	 * 
	 * @param keyStore
	 * @param alias
	 * @param description
	 * @param type
	 * @param certificate
	 * @throws Exception
	 */
	void addCertificate(String keyStore, String alias, String description, X509Certificate certificate)
			throws Exception;

	/**
	 * 增加PrivateKey到KeyStore 为了简化起见,使用KeyStore存储相同的密码
	 * 
	 * @param keyStore
	 * @param alias
	 * @param description
	 * @param certificate
	 * @param privateKey
	 * @throws Exception
	 */
	void addPrivateKey(String keyStore, String alias, String description, X509Certificate certificate,
			PrivateKey privateKey) throws Exception;

	/**
	 * 从keyStore里读取指定别名的证书
	 * 
	 * @param keyStore
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	X509Certificate loadCertificate(String keyStore, String alias) throws Exception;

	/**
	 * 从keyStore里读取指定别名的私钥
	 * 
	 * @param keyStore
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	PrivateKey loadPrivateKey(String keyStore, String alias) throws Exception;
}
