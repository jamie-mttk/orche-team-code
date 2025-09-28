package com.mttk.orche.util.security;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class KeyStoreUtil {
	public static KeyStore getKeyStore(InputStream is, String password) throws Exception{
		KeyStore ks=KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(is, password.toCharArray());
		return ks;
	}
	public static PrivateKey getPrivateKey(KeyStore ks,String alias,String password) throws Exception{
		return (PrivateKey)ks.getKey(alias, password.toCharArray());
	}
	public static PublicKey getPublicKey(KeyStore ks,String alias) throws Exception{
		return ks.getCertificate(alias).getPublicKey();
	}
	public static X509Certificate castCertificate(Certificate cert) throws GeneralSecurityException {
        if (cert == null) {
            throw new GeneralSecurityException("Certificate is null");
        }
        if (!(cert instanceof X509Certificate)) {
            throw new GeneralSecurityException("Certificate must be an instance of X509Certificate");
        }

        return (X509Certificate) cert;
    }

}
