package com.app.sriparas.config;

public class AppConfig {

    public static final String  CONSTANT_FASTTAG="https://www.sriparas.com/zambo/fastag/";
    public static final String  CONSTANT_RECHARGE="https://www.sriparas.com/zambo/recharge/";
    static final String CONSTANT="https://www.sriparas.com/mobileapi/";

    public static final String FASTTAG_USERINFO = CONSTANT_FASTTAG+"getSenderInfo";
    public static final String FASTTAG_OTP_KYC = CONSTANT_FASTTAG+"remOtp";
    public static final String FASTTAG_OTP_VERIFY = CONSTANT_FASTTAG+"verifyRemitter";
    public static final String GET_BENE_FASTAG = CONSTANT_FASTTAG+"getBeneficiary";
    public static final String KYC_FASTAG = CONSTANT_FASTTAG+"createSender";
    public static final String FASTAG_BANK = CONSTANT_FASTTAG+"getBanks";
    public static final String ADD_BENEFICIARY_FASTAG = CONSTANT_FASTTAG+"addBeneficiary";
    public static final String RECHARGE_FASTAG = CONSTANT_FASTTAG+"sendMoney";
    public static final String CHARGES_FASTAG = CONSTANT_FASTTAG+"sendMoneyCharge";
    public static final String FASTAG_PAYMENT_RESPOSNE = CONSTANT_FASTTAG+"razorpayPaymentTransaction";
    public static final String GET_BALANCE = CONSTANT+"getUserBalance";
    /**/
    public static final String RECHARGE_OPOERATOR_LIST =CONSTANT_RECHARGE+"getOperator" ;
    public static final String MOBILE_RECHARGE = CONSTANT_RECHARGE+"getRecharge";
}
