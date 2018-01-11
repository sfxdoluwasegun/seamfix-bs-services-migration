package com.sf.biocapture.proxy;

/**
 *
 * @author @wizzclems
 */
public class ProxyTranslator {

    public BasicData toBasicData(com.sf.biocapture.entity.BasicData b) {
        BasicData bd = new BasicData();
        bd.setBiometricCaptureAgent(b.getBiometricCaptureAgent());
        bd.setBirthday(b.getBirthday());
        bd.setFirstname(b.getFirstname());
        bd.setGender(b.getGender());
        bd.setLastBasicDataEditAgent(b.getLastBasicDataEditAgent());
        bd.setMatchFound(b.isMatchFound());
        bd.setOthername(b.getOthername());
        bd.setSurname(b.getSurname());
        bd.setIsProcessed(b.isIsProcessed());
        bd.setSmsStatus(b.getSmsStatus());
        bd.setSyncStatus(b.getSyncStatus());
        bd.setLastBasicDataEditLoginId(b.getLastBasicDataEditLoginId());
        bd.setStateOfRegistration(b.getState().getName());
        return bd;
    }

    public WsqImage toWsqImage(com.sf.biocapture.entity.WsqImage w) {
        WsqImage wi = new WsqImage();
        wi.setCompressionRatio(w.getCompressionRatio());
        wi.setFinger(w.getFinger());
        wi.setNfiq(w.getNfiq());
        wi.setReasonCode(w.getReasonCode().getCode());
        wi.setWsqData(w.getWsqData());

        return wi;
    }

    public PassportDetail toPassportDetail(PassportDetail proxyPassportDetail, com.sf.biocapture.entity.PassportDetail pd) {
        PassportDetail p = null;

        if (proxyPassportDetail == null) {
            p = new PassportDetail();
        }

        if ((p != null) && (p.getSignature() != null)) {
            p.getSignature().setSignatureData(pd.getSignature().getSignatureData());
        }

        return p;
    }

    public PassportDetail toPassportDetail(com.sf.biocapture.entity.PassportDetail pd) {
        PassportDetail p = new PassportDetail();
        p.getSignature().setSignatureData(pd.getSignature().getSignatureData());
        return p;
    }

    public SignatureData toSignatureData(com.sf.biocapture.entity.SignatureData s) {
        SignatureData p = new SignatureData();
        p.setSignatureData(s.getSignatureData());
        return p;
    }

    public SpecialData toSpecialData(com.sf.biocapture.entity.SpecialData s) {
        SpecialData sd = new SpecialData();
        sd.setBiometricData(s.getBiometricData());
        sd.setBiometricDataType(s.getBiometricDataType());
        sd.setReason(s.getReason());

        return sd;
    }

    public PassportData toPassport(com.sf.biocapture.entity.PassportData p) {
        PassportData pd = new PassportData();
        pd.setFaceCount(p.getFaceCount());
        pd.setPassportData(p.getPassportData());

        return pd;
    }

    public DynamicData toProxyDynamicData(com.sf.biocapture.entity.DynamicData d) {
        DynamicData dd = new DynamicData();

        dd.setDa1(d.getDa1() != null ? d.getDa1().trim() : d.getDa1());
        dd.setDa10(d.getDa10() != null ? d.getDa10().trim() : d.getDa10());
        dd.setDa11(d.getDa11() != null ? d.getDa11().trim() : d.getDa11());
        dd.setDa12(d.getDa12() != null ? d.getDa12().trim() : d.getDa12());
        dd.setDa13(d.getDa13() != null ? d.getDa13().trim() : d.getDa13());
        dd.setDa14(d.getDa14() != null ? d.getDa14().trim() : d.getDa14());
        dd.setDa15(d.getDa15() != null ? d.getDa15().trim() : d.getDa15());
        dd.setDa16(d.getDa16() != null ? d.getDa16().trim() : d.getDa16());
        dd.setDa17(d.getDa17() != null ? d.getDa17().trim() : d.getDa17());
        dd.setDa18(d.getDa18() != null ? d.getDa18().trim() : d.getDa18());
        dd.setDa19(d.getDa19() != null ? d.getDa19().trim() : d.getDa19());
        dd.setDa2(d.getDa2() != null ? d.getDa2().trim() : d.getDa2());
        dd.setDa20(d.getDa20() != null ? d.getDa20().trim() : d.getDa20());
        dd.setDa3(d.getDa3() != null ? d.getDa3().trim() : d.getDa3());
        dd.setDa4(d.getDa4() != null ? d.getDa4().trim() : d.getDa4());
        dd.setDa5(d.getDa5() != null ? d.getDa5().trim() : d.getDa5());
        dd.setDa6(d.getDa6() != null ? d.getDa6().trim() : d.getDa6());
        dd.setDa7(d.getDa7() != null ? d.getDa7().trim() : d.getDa7());
        dd.setDa8(d.getDa8() != null ? d.getDa8().trim() : d.getDa8());
        dd.setDa9(d.getDa9() != null ? d.getDa9().trim() : d.getDa9());
        dd.setDda1(d.getDda1());
        dd.setDda10(d.getDda10());
        dd.setDda11(d.getDda11());
        dd.setDda12(d.getDda12());
        dd.setDda13(d.getDda13());
        dd.setDda14(d.getDda14());
        dd.setDda15(d.getDda15());
        dd.setDda16(d.getDda16());
        dd.setDda17(d.getDda17());
        dd.setDda18(d.getDda18());
        dd.setDda19(d.getDda19());
        dd.setDda2(d.getDda2());
        dd.setDda20(d.getDda20());
        dd.setDda2(d.getDda2());
        dd.setDda3(d.getDda3());
        dd.setDda4(d.getDda4());
        dd.setDda5(d.getDda5());
        dd.setDda6(d.getDda6());
        dd.setDda7(d.getDda7());
        dd.setDda8(d.getDda8());
        dd.setDda9(d.getDda9());

        return dd;

    }

}
