package com.sf.biocapture.ws.vtu;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;

import com.sf.biocapture.CryptController;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.vtu.enums.TransactionTypeEnum;
import com.sf.vas.airtimevend.mtn.dto.MtnVtuInitParams;
import com.sf.vas.airtimevend.mtn.dto.VendDto;
import com.sf.vas.airtimevend.mtn.dto.VendResponseDto;
import com.sf.vas.airtimevend.mtn.service.VtuMtnService;

/**
 * 
 * @author Nnanna
 * @since 12 Nov 2017, 22:57:21
 */
@Stateless
public class MTNVTUWrapperService extends BsClazz {
	@Inject
	AccessDS accessDS;
	
	@Inject
	private CryptController cryptControl;
	
	private VtuMtnService vtuService;
	
	@PostConstruct
	private void init(){
		String encryptedUsername = accessDS.getSettingValue(SettingsEnum.VTU_VEND_USERNAME);
		String encryptedPassword = accessDS.getSettingValue(SettingsEnum.VTU_VEND_PASSWORD);
		
		Crypter crypter = cryptControl.getCrypter();
		String username = null;
		String password = null;
		if(StringUtils.isNotBlank(encryptedUsername) && StringUtils.isNotBlank(encryptedPassword)){
			try {
				username = crypter.decrypt(encryptedUsername);
				password = crypter.decrypt(encryptedPassword);
			} catch (KeyczarException e) {
				logger.error("Error decrypting VTU user credentials ", e);
			}
		}
		
		String serviceUrl = accessDS.getSettingValue(SettingsEnum.VTU_SERVICE_URL);
		
		logger.debug("VTU Service URL: {}", serviceUrl);
		
		MtnVtuInitParams params = new MtnVtuInitParams();
		params.setServiceUrl(serviceUrl);
		params.setVendUsername(username);
		params.setVendPassword(password);
		
		vtuService = new VtuMtnService(params);
	}
	
	public VendResponseDto sendVendRequest(VendDto vendDto){
		return vtuService.sendVendRequest(vendDto);
	}
	
	public String getTariffTypeId(TransactionTypeEnum txnType){
		if(txnType.equals(TransactionTypeEnum.AIRTIME)){
			return accessDS.getSettingValue(SettingsEnum.MTN_AIRTIME_TARIFF_TYPE_ID);
		}else{
			return accessDS.getSettingValue(SettingsEnum.MTN_DATA_TARIFF_TYPE_ID);
		}
	}
}
