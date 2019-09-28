package io.tcg.phonecalltrap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
public class PhoneCallTrap extends CordovaPlugin {

public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = "";
        String number= " ";

        ITelephony telephonyService;
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
         number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");

                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);

                    if ((number != null)) {
                        telephonyService.endCall();
                        //Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                msg = "RINGING";
                // Toast.makeText(context, "Ring " + number, Toast.LENGTH_SHORT).show();

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                msg = "OFFHOOK";
                //   Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                msg = "IDLE";
                //  Toast.makeText(context, "Idle "+ number, Toast.LENGTH_SHORT).show();
                JSONObject jso = new JSONObject();
                String outpool = "";
                //JSONArray msgJSON = new JSONArray();
              //  String number="muky";
          //   String number=intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                try{
                    jso.put("state", msg);
                    jso.put("incomingNumber", number);
                    outpool = jso.toString();
                }catch(JSONException e){
                    outpool = "{state: '" + msg + "', incomingNumber: '" + number + "'}";
                }
        
                PluginResult result = new PluginResult(PluginResult.Status.OK, outpool);
                result.setKeepCallback(true);
        
                callbackContext.sendPluginResult(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
}