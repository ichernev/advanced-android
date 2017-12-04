package com.exercise.permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;

public class TelephonyUtils {

    Context mContext;

    public TelephonyUtils(Context context) {
        mContext = context;
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public String getInfo() {

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "No Telephony";
        }

        StringBuilder builder = new StringBuilder();

        // Returns the unique device ID, for example, the IMEI for GSM and the MEID or ESN for CDMA
        // phones. Return null if device ID is not available. Requires Permission: READ_PHONE_STATE
        //builder.append("DEVICE_ID: ").append(tm.getDeviceId()).append("\n");


        // Returns the phone number string for line 1, for example, the MSISDN for a GSM phone.
        // Return null if it is unavailable. Requires Permission: READ_PHONE_STATE
        builder.append("LINE1_NUMBER: ").append(tm.getLine1Number()).append("\n");

        // Returns the ISO country code equivalent of the current registered operator's MCC (Mobile
        // Country Code).
        builder.append("NETWORK_COUNTRY_ISO: ").append(tm.getNetworkCountryIso()).append("\n");

        // Returns the numeric name (MCC+MNC) of current registered operator.
        builder.append("NETWORK_OPERATOR: ").append(tm.getNetworkOperator()).append("\n");

        // Returns the alphabetic name of current registered operator.
        builder.append("NETWORK_OPERATOR_NAME: ").append(tm.getNetworkOperatorName()).append("\n");

        return builder.toString();
    }

}
