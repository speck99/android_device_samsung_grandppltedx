/*
 * Copyright (c) 2014-2016, The CyanogenMod Project. All rights reserved.
 * Copyright (c) 2017, The LineageOS Project. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.telephony;

import static com.android.internal.telephony.RILConstants.*;

import android.content.Context;
import android.telephony.Rlog;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.telephony.PhoneNumberUtils;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccRefreshResponse;
import com.android.internal.telephony.uicc.IccUtils;
import java.util.ArrayList;
import java.util.Collections;

/**
 * RIL customization for Galaxy J2 Prime/Grand Prime Plus (GSM)
 *
 * {@hide}
 */

public class grandpplteRIL extends RIL implements CommandsInterface {

    /**********************************************************
     * SAMSUNG REQUESTS
     **********************************************************/
    static final boolean RILJ_LOGD = true;
    static final boolean RILJ_LOGV = false;
    
    /*request*/
    private static final int RIL_REQUEST_DIAL_EMERGENCY_CALL = 10001;

    /*response*/
    
    private static final int UNSOL_RESPONSE_NEW_CB_MSG = 11000; // alz
    
    private static final int RIL_UNSOL_STK_SEND_SMS_RESULT = 11002;
    private static final int RIL_UNSOL_STK_CALL_CONTROL_RESULT = 11003;

    private static final int RIL_UNSOL_DEVICE_READY_NOTI = 11008;
    private static final int RIL_UNSOL_AM = 11010;
    private static final int RIL_UNSOL_SIM_PB_READY = 11021;
	
    public grandpplteRIL(Context context, int preferredNetworkType, int cdmaSubscription) {
        this(context, preferredNetworkType, cdmaSubscription, null);
    }

    public grandpplteRIL(Context context, int preferredNetworkType, int cdmaSubscription, Integer instanceId) {
        super(context, preferredNetworkType, cdmaSubscription, instanceId);
    }

    private int translateStatus(int status) {
        switch (status & 7) {
            case 3:
                return 0;
            case 5:
                return 3;
            case 7:
                return 2;
            default:
                return 1;
        }
    }

    @Override
    public void writeSmsToSim(int status, String smsc, String pdu, Message response) {
        int status2 = translateStatus(status);
        RILRequest rr = RILRequest.obtain(RIL_REQUEST_WRITE_SMS_TO_SIM, response);
        rr.mParcel.writeInt(status2);
        rr.mParcel.writeString(pdu);
        rr.mParcel.writeString(smsc);
        send(rr);
    }

    @Override
    public void dial(String address, int clirMode, Message result) {
        dial(address, clirMode, null, result);
    }
    
    @Override
    public void dial(String address, int clirMode, UUSInfo uusInfo, Message result) {
	
	// inherit from SlteRIL
        if (PhoneNumberUtils.isEmergencyNumber(address)) { // -
            dialEmergencyCall(address, clirMode, result);
            return;
        } // -

        RILRequest rr = RILRequest.obtain(RIL_REQUEST_DIAL, result);
        rr.mParcel.writeString(address);
        rr.mParcel.writeInt(clirMode);
        rr.mParcel.writeInt(0);     // CallDetails.call_type
        rr.mParcel.writeInt(1);     // CallDetails.call_domain
        rr.mParcel.writeString(""); // CallDetails.getCsvFromExtras

        if (uusInfo == null) {
            rr.mParcel.writeInt(0);
        } else {
            rr.mParcel.writeInt(1);
            rr.mParcel.writeInt(uusInfo.getType());
            rr.mParcel.writeInt(uusInfo.getDcs());
            rr.mParcel.writeByteArray(uusInfo.getUserData());
        }
        riljLog(rr.serialString() + "> " + requestToString(rr.mRequest) );
        send(rr);
    }

    public void dialEmergencyCall(String address, int clirMode, Message result) {
        RILRequest rr = RILRequest.obtain(RIL_REQUEST_DIAL_EMERGENCY_CALL, result);
        rr.mParcel.writeString(address);
        rr.mParcel.writeInt(clirMode);
        
        rr.mParcel.writeInt(0);
        rr.mParcel.writeInt(3);
        rr.mParcel.writeString("");
       
        rr.mParcel.writeInt(0);
        send(rr);
    }

    @Override
    protected Object responseIccCardStatus(Parcel p) {
	/* TODO \ Selfnote : opensource RIL class is somewhat different than stock one. */
	
        IccCardStatus cardStatus = new IccCardStatus();
        cardStatus.setCardState(p.readInt());
        cardStatus.setUniversalPinState(p.readInt());
        cardStatus.mGsmUmtsSubscriptionAppIndex = p.readInt();
        cardStatus.mCdmaSubscriptionAppIndex = p.readInt();
        cardStatus.mImsSubscriptionAppIndex = p.readInt();
        int numApplications = p.readInt();
        
        if (numApplications > 8) {
            numApplications = 8;
        }
        
        cardStatus.mApplications = new IccCardApplicationStatus[numApplications];

        for (int i = 0; i < numApplications; i++) {
            IccCardApplicationStatus appStatus = new IccCardApplicationStatus();
            appStatus.app_type = appStatus.AppTypeFromRILInt(p.readInt());
            appStatus.app_state = appStatus.AppStateFromRILInt(p.readInt());
            appStatus.perso_substate = appStatus.PersoSubstateFromRILInt(p.readInt());
            appStatus.aid = p.readString();
            appStatus.app_label = p.readString();
            appStatus.pin1_replaced = p.readInt();
            appStatus.pin1 = appStatus.PinStateFromRILInt(p.readInt());
            appStatus.pin2 = appStatus.PinStateFromRILInt(p.readInt());

            /* compilation error */
            p.readInt(); // pin1_num_retries
            p.readInt(); // puk1_num_retries
            p.readInt(); // pin2_num_retries
            p.readInt(); // puk2_num_retries
            p.readInt(); // perso_unblock_retries
            cardStatus.mApplications[i] = appStatus;
        }
        return cardStatus;
    }
    
    @Override
    public Object responseCallList(Parcel p) {
        boolean z;
        int num = p.readInt();
        ArrayList<DriverCall> response = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            DriverCall dc = new DriverCall();
            dc.state = DriverCall.stateFromCLCC(p.readInt());
            dc.index = p.readInt();
            // almond: line is only for logging + compilation error // dc.id = (dc.index >> 8) & 255;
            dc.index &= 255;
            dc.TOA = p.readInt();
            dc.isMpty = p.readInt() != 0;
            dc.isMT = p.readInt() != 0;
            dc.als = p.readInt();
            if (p.readInt() == 0) {
                z = false;
            } else {
                z = true;
            }
            dc.isVoice = z;
            int type = p.readInt();
            int domain = p.readInt();
            String extras = p.readString();
           /* almond: remove all references of call details 
            *
            *dc.callDetails = new CallDetails(type, domain, null);
            *dc.callDetails.setExtrasFromCsv(extras);
            *Rlog.d(RILJ_LOG_TAG, "dc.index " + dc.index + " dc.id " + dc.id + " dc.callDetails " + dc.callDetails);
            */ 
            dc.isVoicePrivacy = p.readInt() != 0;
            dc.number = p.readString();
            dc.numberPresentation = DriverCall.presentationFromCLIP(p.readInt());
            dc.name = p.readString();
            Rlog.d(RILJ_LOG_TAG, "responseCallList dc.name" + dc.name);
            dc.namePresentation = DriverCall.presentationFromCLIP(p.readInt());
            if (p.readInt() == 1) {
                dc.uusInfo = new UUSInfo();
                dc.uusInfo.setType(p.readInt());
                dc.uusInfo.setDcs(p.readInt());
                dc.uusInfo.setUserData(p.createByteArray());
                
                riljLogv(String.format("Incoming UUS : type=%d, dcs=%d, length=%d", new Object[]{Integer.valueOf(dc.uusInfo.getType()), Integer.valueOf(dc.uusInfo.getDcs()), Integer.valueOf(dc.uusInfo.getUserData().length)}));
                riljLogv("Incoming UUS : data (string)=" + new String(dc.uusInfo.getUserData()));
                riljLogv("Incoming UUS : data (hex): " + IccUtils.bytesToHexString(dc.uusInfo.getUserData()));
            } else {
                riljLogv("Incoming UUS : NOT present!");
            }
            dc.number = PhoneNumberUtils.stringFromStringAndTOA(dc.number, dc.TOA);
            response.add(dc);
            if (dc.isVoicePrivacy) {
                this.mVoicePrivacyOnRegistrants.notifyRegistrants();
                riljLog("InCall VoicePrivacy is enabled");
            } else {
                this.mVoicePrivacyOffRegistrants.notifyRegistrants();
                riljLog("InCall VoicePrivacy is disabled");
            }
        }
        Collections.sort(response);
        if (num == 0 && this.mTestingEmergencyCall.getAndSet(false) && this.mEmergencyCallbackModeRegistrant != null) {
            riljLog("responseCallList: call ended, testing emergency call, notify ECM Registrants");
            this.mEmergencyCallbackModeRegistrant.notifyRegistrant();
        }
        return response;
    }
/*
    // again, SlteRIL
    @Override
    protected Object responseCallList(Parcel p) {
        int num;
        int voiceSettings;
        ArrayList<DriverCall> response;
        DriverCall dc;

        num = p.readInt();
        response = new ArrayList<DriverCall>(num);

        if (RILJ_LOGV) {
            riljLog("responseCallList: num=" + num +
                    " mEmergencyCallbackModeRegistrant=" + mEmergencyCallbackModeRegistrant +
                    " mTestingEmergencyCall=" + mTestingEmergencyCall.get());
        }
        for (int i = 0 ; i < num ; i++) {
            dc = new DriverCall();

            dc.state = DriverCall.stateFromCLCC(p.readInt());
            dc.index = p.readInt() & 0xff;
            dc.TOA = p.readInt();
            dc.isMpty = (0 != p.readInt());
            dc.isMT = (0 != p.readInt());
            dc.als = p.readInt();
            voiceSettings = p.readInt();
            dc.isVoice = (0 == voiceSettings) ? false : true;

            int call_type = p.readInt();            // Samsung CallDetails
            int call_domain = p.readInt();          // Samsung CallDetails
            String csv = p.readString();            // Samsung CallDetails
            if (RILJ_LOGV) {
                riljLog(String.format("Samsung call details: type=%d, domain=%d, csv=%s",
                               call_type, call_domain, csv));
            }

            dc.isVoicePrivacy = (0 != p.readInt());
            dc.number = p.readString();
            if (RILJ_LOGV) {
                riljLog("responseCallList dc.number=" + dc.number);
            }
            dc.numberPresentation = DriverCall.presentationFromCLIP(p.readInt());
            dc.name = p.readString();
            if (RILJ_LOGV) {
                riljLog("responseCallList dc.name=" + dc.name);
            }
            // according to ril.h, namePresentation should be handled as numberPresentation;
            dc.namePresentation = DriverCall.presentationFromCLIP(p.readInt());

            int uusInfoPresent = p.readInt();
            if (uusInfoPresent == 1) {
                dc.uusInfo = new UUSInfo();
                dc.uusInfo.setType(p.readInt());
                dc.uusInfo.setDcs(p.readInt());
                byte[] userData = p.createByteArray();
                dc.uusInfo.setUserData(userData);
                riljLogv(String.format("Incoming UUS : type=%d, dcs=%d, length=%d",
                                dc.uusInfo.getType(), dc.uusInfo.getDcs(),
                                dc.uusInfo.getUserData().length));
                riljLogv("Incoming UUS : data (string)="
                        + new String(dc.uusInfo.getUserData()));
                riljLogv("Incoming UUS : data (hex): "
                        + IccUtils.bytesToHexString(dc.uusInfo.getUserData()));

                // Make sure there's a leading + on addresses with a TOA of 145
                dc.number = PhoneNumberUtils.stringFromStringAndTOA(dc.number, dc.TOA);
            } else {
                riljLogv("Incoming UUS : NOT present!");
            }

            response.add(dc);

            if (dc.isVoicePrivacy) {
                mVoicePrivacyOnRegistrants.notifyRegistrants();
                riljLog("InCall VoicePrivacy is enabled");
            } else {
                mVoicePrivacyOffRegistrants.notifyRegistrants();
                riljLog("InCall VoicePrivacy is disabled");
            }
        }

        Collections.sort(response);

        if ((num == 0) && mTestingEmergencyCall.getAndSet(false)
                        && mEmergencyCallbackModeRegistrant != null) {
            if (mEmergencyCallbackModeRegistrant != null) {
                riljLog("responseCallList: call ended, testing emergency call," +
                            " notify ECM Registrants");
                mEmergencyCallbackModeRegistrant.notifyRegistrant();
            }
        }

        return response;
    }
*/

    /* this phone is GSM only */
    private void constructGsmSendSmsRilRequest(RILRequest rr, String smscPDU, String pdu) {
        rr.mParcel.writeInt(2);
        rr.mParcel.writeString(smscPDU);
        rr.mParcel.writeString(pdu);
    }


    @Override
    public void sendSMSExpectMore(String smscPDU, String pdu, Message result) {
        RILRequest rr = RILRequest.obtain(RIL_REQUEST_SEND_SMS, result);
        constructGsmSendSmsRilRequest(rr, smscPDU, pdu);
        riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));
        send(rr);
    }

    // according to SlteRIL
    /**
     * The RIL can't handle the RIL_REQUEST_SEND_SMS_EXPECT_MORE
     * request properly, so we use RIL_REQUEST_SEND_SMS instead.
     */
/*    @Override
    public void sendSMSExpectMore(String smscPDU, String pdu, Message result) {

	// idk how to search for device modem lol
        Rlog.v(RILJ_LOG_TAG, "grandpplte-ril-modem: sendSMSExpectMore");

        RILRequest rr = RILRequest.obtain(RIL_REQUEST_SEND_SMS, result);
        constructGsmSendSmsRilRequest(rr, smscPDU, pdu);

        if (RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }
*/
    
    // TODO: strip down certain network stuffs~
    @Override
    protected Object responseOperatorInfos(Parcel p) {
        ArrayList<OperatorInfo> ret;
        String[] strings = (String[]) responseStrings(p);
        if (strings.length % 6 != 0) {
            throw new RuntimeException("RIL_REQUEST_QUERY_AVAILABLE_NETWORKS: invalid response. Got " + strings.length + " strings, expected multible of 6");
        }
	
	/* might not be necessary        
	String isRoaming = TelephonyManager.getTelephonyProperty(this.mInstanceId.intValue(), "gsm.operator.isroaming", "");
        */
	/* removed some kind of CSC things~

	// use SlteRIL coding, same work but much more elegant
	// mQANElements in grandpplte is 6
	*/        
	ret = new ArrayList<OperatorInfo>(strings.length / 6);
        for (int i = 0 ; i < strings.length ; i += 6) {
            String strOperatorLong = strings[i+0];
            String strOperatorNumeric = strings[i+2];
            String strState = strings[i+3].toLowerCase();

            Rlog.v(RILJ_LOG_TAG,
                   "grandpplte-ril-modem: Add OperatorInfo: " + strOperatorLong +
                   ", " + strOperatorLong +
                   ", " + strOperatorNumeric +
                   ", " + strState);

            ret.add(new OperatorInfo(strOperatorLong, // operatorAlphaLong
                                     strOperatorLong, // operatorAlphaShort
                                     strOperatorNumeric,    // operatorNumeric
                                     strState));  // stateString
        }
        
        return ret;
    }

    // uses SlteRIL because stock one is waaayyy too lengthy and unusable
    // tons of bugs incoming (.......)
    @Override
    protected void processUnsolicited(Parcel p, int type) {
        Object ret;

        int dataPosition = p.dataPosition();
        int origResponse = p.readInt();
        int newResponse = origResponse;

        // Remap incorrect respones or ignore them
        switch (origResponse) {
            case RIL_UNSOL_STK_CALL_CONTROL_RESULT:
            case RIL_UNSOL_DEVICE_READY_NOTI: // Registrant notification
            case RIL_UNSOL_SIM_PB_READY: // Registrant notification 
        Rlog.v(RILJ_LOG_TAG,
                       "grandpplte-ril-modem: ignoring unsolicited response " +
                       origResponse);
                return;
        }

        if (newResponse != origResponse) {
            riljLog("grandpplteRIL: remap unsolicited response from " +
                    origResponse + " to " + newResponse);
            p.setDataPosition(dataPosition);
            p.writeInt(newResponse);
        }

        switch (newResponse) {
            case RIL_UNSOL_AM:
                ret = responseString(p);
                break;
            case RIL_UNSOL_STK_SEND_SMS_RESULT:
                ret = responseInts(p);
                break;
            default:
                // Rewind the Parcel
                p.setDataPosition(dataPosition);

                // Forward responses that we are not overriding to the super class
                super.processUnsolicited(p, type);
                return;
        }

        switch (newResponse) {
            case RIL_UNSOL_AM:
                String strAm = (String)ret;
                // Add debug to check if this wants to execute any useful am command
                Rlog.v(RILJ_LOG_TAG, "grandpplte-ril-modem: am=" + strAm);
                break;
        }
    }
}

