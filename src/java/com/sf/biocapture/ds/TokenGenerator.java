/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ds;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.entity.MsisdnDetail;
import com.sf.biocapture.entity.OTPStatusRecord;
import com.sf.biocapture.entity.enums.OtpStatusRecordTypeEnum;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import nw.orm.core.query.QueryModifier;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Marcel
 * @since 11-Oct-2016, 08:05:39
 */
public abstract class TokenGenerator extends DataService {

    @Inject
    protected BioCache cache;
    private final char ALPHABETS[] = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    private final char DIGITS[] = "123456789".toCharArray();
    private final char ALPHANUMERIC[] = "ABCDEFGHJKLMNPQRSTUVWXYZ123456789".toCharArray();

    public enum OTPStatus {

        EXPIRED, VALID, INVALID, ERROR
    }
    
    protected String generateToken(int width) {
        return generate(ALPHANUMERIC, width);
    }

    protected String generateAlphabets(int width) {
        return generate(ALPHABETS, width);
    }

    protected String generateDigits(int width) {
        return generate(DIGITS, width);
    }

    protected String generate(char[] CHARACTERS, int width) {
        int lb = 0;
        int hb = CHARACTERS.length - 1;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < width; i++) {
            int index = lb + (int) (Math.random() * (hb - lb + 1));
            buf.append(CHARACTERS[index]);
        }
        return buf.toString();
    }

    public boolean phoneNumberExist(String msisdn) {
        boolean exist = false;
        QueryModifier modifier = new QueryModifier(MsisdnDetail.class);
        modifier.addProjection(Projections.property("id"));
        modifier.setPaginated(0, 1);
        Long detail = getDbService().getByCriteria(Long.class, modifier, Restrictions.eq("msisdn", msisdn));
        if (detail != null) {
            exist = true;
        }
        return exist;
    }
    
    public List<OTPStatusRecord> getOtpRecords(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String otp, String msisdn){
    	QueryModifier qm = new QueryModifier(OTPStatusRecord.class);
        qm.addOrderBy(Order.desc("timeGenerated"));
        qm.setPaginated(0, 1);

        Junction j = Restrictions.conjunction();
        if(!otpStatusRecordTypeEnum.equals(OtpStatusRecordTypeEnum.VTU_AGENT_VALIDATION)){
        	j.add(Restrictions.eq("otp", otp));
        }
        j.add(Restrictions.eq("msisdn", msisdn));
        j.add(Restrictions.eq("otpUsed", Boolean.FALSE)).add(Restrictions.eq("otpStatusRecordTypeEnum", otpStatusRecordTypeEnum));
        //retrieve the kit status from here
        return getDbService().getListByCriteria(OTPStatusRecord.class, qm, j);

    }

    public OTPStatus verifyOTP(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String otp, String msisdn) {
        if ((otp == null) || otp.isEmpty() || (msisdn == null) || msisdn.isEmpty()) {
            return OTPStatus.INVALID;
        }

        List<OTPStatusRecord> otps = getOtpRecords(otpStatusRecordTypeEnum, otp, msisdn);
        
        if ((otps == null) || otps.isEmpty()) {
            return OTPStatus.ERROR;
        }

        OTPStatusRecord o = otps.get(0);

        OTPStatus otpStatus = OTPStatus.INVALID;
        Timestamp currentTime = new Timestamp(new Date().getTime());
        if (currentTime.getTime() > o.getExpirationTime().getTime()) {
            // otp expired.
            otpStatus = OTPStatus.EXPIRED;
        } else {
            otpStatus = OTPStatus.VALID;
        }

        o.setOtpUsed(true);
        o.setTimeUsed(new Timestamp(new Date().getTime()));
        getDbService().update(o);
        return otpStatus;
    }

    public int verifyOtpStatus(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String otp, String msisdn) {
        OTPStatus otpStatus = verifyOTP(otpStatusRecordTypeEnum, otp, msisdn);
        return getOTPCode(otpStatus);
    }
    
    public int getOTPCode(OTPStatus otpStatus){
    	switch (otpStatus) {
    	case ERROR:
    		return 3;
    	case EXPIRED:
    		return 2;
    	case INVALID:
    		return 4;
    	case VALID:
    		return 1;
    	}
    	return 3;
    }

    public abstract String generateOTP(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String msisdn);
}
