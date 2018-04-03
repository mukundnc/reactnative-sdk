package com.benowp;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.mf.mpos.pub.CommEnum;
import com.mf.mpos.pub.Controler;
import com.mf.mpos.pub.param.ReadCardParam;
import com.mf.mpos.pub.result.ReadCardResult;

import java.util.concurrent.CountDownLatch;

/**
 * Created by mukc on 01-04-2018.
 */

public class Button extends ReactContextBaseJavaModule {

    @Override
    public String getName() {
        return "ButtonT";
    }

    public Button(ReactApplicationContext reactContext) {
        super(reactContext);
        Controler.Init(reactContext, CommEnum.CONNECTMODE.BLUETOOTH ,0);
    }

    @ReactMethod
    public void getTestName(final Promise callback){
        callback.resolve("testValue");
    }

    @ReactMethod
    public void readCard(final Promise callback) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] retMsg = {"message empty"};
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    Controler.connectPos("04:23:03:00:77:47");
                    try {
                        if (Controler.posConnected()) {
                            ReadCardParam param = new ReadCardParam();
                            param.setAmount(1);
                            param.setTransType(CommEnum.TRANSTYPE.FUNC_SALE);
                            ReadCardResult ret = Controler.ReadCard(param);
                            StringBuilder sb = new StringBuilder();
                            sb.append("results:" + ret.commResult.toDisplayName() + "\n");
                            sb.append("card type:" + ret.getCartTypeName() + "\n");
                            // sb.append( "FallBack:" + ret.fallback + "\n" );
                            sb.append("master account:" + ret.pan + "\n");
                            sb.append("Card Expiration:" + ret.expData + "\n");
                            sb.append("service code:" + ret.serviceCode + "\n");
                            sb.append("track2 Length:" + ret.track2Len + "\n");
                            sb.append("track2 Data:" + ret.track2 + "\n");
                            sb.append("track3 Length:" + ret.track3Len + "\n");
                            sb.append("track3 Data:" + ret.track3 + "\n");
                            sb.append("data random number:" + ret.randomdata + "\n");
                            sb.append("card sequence number:" + ret.pansn + "\n");
                            sb.append("IC Data:" + ret.icData + "\n");
                            sb.append("PIN Length:" + ret.pinLen + "\n");
                            sb.append("PIN ciphertext:" + ret.pinblock + "\n");
                            retMsg[0] = sb.toString();
                        }
                    } catch (Exception ex) {
                        retMsg[0] = ex.getMessage();
                    }
                    Controler.disconnectPos();
                    latch.countDown();
                }
            }).start();
        latch.await();
        callback.resolve(retMsg[0]);
    }

    @ReactMethod
    public void setUserIdentifier(final String userId){
        com.usebutton.sdk.Button.getButton(getReactApplicationContext()).setUserIdentifier(userId);
    }

    @ReactMethod
    public void logout(){
        com.usebutton.sdk.Button.getButton(getReactApplicationContext()).logout();
    }

    @ReactMethod
    public void getButtonRef(final Promise callback){
        try{
            String ref = com.usebutton.sdk.Button.getButton(getReactApplicationContext()).getReferrerToken();

            if(ref != null && !ref.isEmpty()){
                callback.resolve(ref);
            }else{
                callback.reject("getButtonRefError", "button ref is null or empty");
            }

        }catch (Exception e){
            callback.reject("getButtonRefError", e.getMessage());
        }
    }

}

