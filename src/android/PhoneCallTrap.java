package io.tcg.phonecalltrap;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.content.BroadcastReceiver;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;


public class PhoneCallTrap extends CordovaPlugin {

    CallStateListener listener;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        prepareListener();

        listener.setCallbackContext(callbackContext);

        return true;
    }

    private void prepareListener() {
        if (listener == null) {
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}

class CallStateListener extends PhoneStateListener {
public class PhoneCallReceiver extends BroadcastReceiver {
 Context context = null;
 private static final String TAG = "Phone call";
 private ITelephony telephonyService;

@Override
 public void onReceive(Context context, Intent intent) {
  if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) 
    return;

//  Log.v(TAG, "Receving....");
  TelephonyManager telephony = (TelephonyManager) 
  context.getSystemService(Context.TELEPHONY_SERVICE);  
  try {
      Log.v(TAG, "Get getTeleService...");
      Class c = Class.forName(telephony.getClass().getName());
      Method m = c.getDeclaredMethod("getITelephony");
      m.setAccessible(true);
      telephonyService = (ITelephony) m.invoke(telephony);
      telephonyService.silenceRinger();
     // Log.v(TAG, "Answering Call now...");
      telephonyService.answerRingingCall();
     // Log.v(TAG, "Call answered...");
      //telephonyService.endCall();
  } catch (Exception e) {
   e.printStackTrace();
 //  Log.e(TAG,
  //         "FATAL ERROR: could not connect to telephony subsystem");
  // Log.e(TAG, "Exception object: " + e);
  }
 }
}
    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
       super.onCallStateChanged(state, incomingNumber);

        if (callbackContext == null) return;

        String msg = "";
String number= " ";
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            msg = "IDLE";
            break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
            msg = "OFFHOOK";
            break;

            case TelephonyManager.CALL_STATE_RINGING:
                number = incomingNumber;
            msg = "RINGING";

                break;
        }

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
}
