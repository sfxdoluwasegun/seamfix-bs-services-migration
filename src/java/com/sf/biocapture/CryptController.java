package com.sf.biocapture;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;

import com.sf.biocapture.app.BsClazz;
import java.util.logging.Level;
import java.util.logging.Logger;

import sfx.crypto.CryptoReader;

@Singleton
public class CryptController extends BsClazz{

	private Crypter crypter;

	@PostConstruct
	public void init(){
		CryptoReader cr = new CryptoReader("map");
		try {
			crypter = new Crypter(cr);
		} catch (KeyczarException e) {
			logger.error("Exception ", e);
		}
	}

	public Crypter getCrypter() {
		return crypter;
	}

        
        public static void main(String[] args) {
            try {
                CryptController cc = new CryptController();
                cc.init();
                System.out.println(cc.crypter.decrypt("AHVZ0xhsL2YdwjgNWakIqzhp_lOngt7hkkhOAU9utLHCKcBVfFG-8FfmJyD65sDJwqk_UN7DciMTfMpeqrMwEH48LCDO_X-_GDxu7Cp7zuILEOUejPRzl0Y"));
            } catch (KeyczarException ex) {
                Logger.getLogger(CryptController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
