package org.hyperledger.fabric.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
/**
 * generate Enrollment from certFile and keyFile
 * @author PC
 *
 */
public class EnrollmentFromFS implements Enrollment, Serializable {
	
	private static final long serialVersionUID = -8749778961006556028L;
	
	private PrivateKey privateKey;
    private final String cert;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    EnrollmentFromFS(PrivateKey privateKey, String cert) {
    	this.privateKey = privateKey;
    	this.cert = cert;
    }
    
    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws Exception {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        try (final PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo)pemParser.readObject();
        }

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }
    
	@Override
	public PrivateKey getKey() {
		return privateKey;
	}

	@Override
	public String getCert() {
		return cert;
	}
    
    public static EnrollmentFromFS getEnrollmentFromFS(File keyFile, File certFile) throws Exception {
    	//privateKey 
		PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(keyFile)));
		//System.out.println("-----------------------");
		//System.out.println("privatekey:" + privateKey);
		//System.out.println("-----------------------");

		//Certificate
		String cert = new String(IOUtils.toByteArray(new FileInputStream(certFile)), "UTF-8");
		//System.out.println("***********************");
		//System.out.println("cert:" + cert);
		//System.out.println("***********************");
		
		return new EnrollmentFromFS(privateKey, cert);
    }
    
    
}





