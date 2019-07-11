package com.lovin.ads;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.playmil.blockfit3d.R;
import com.tapque.ads.AdsManager;

public class AdsTestActivity extends Activity implements View.OnClickListener, AdsManager.AdsStateChangeListenner {

    String TAG="Ads";
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_main);
        initView();
        AdsManager.instance().init(this,true,this);
    }
    TextView logTextView;
    Button showBannerBtn;
    Button hideBannerBtn;
    Button showInterstitialBtn;
    Button requestOtherInterstitialBtn;
    Button showOtherInterstitialBtn;
    Button enableAutoPlayBtn;
    Button showRewardVideoBtn;
    void  initView(){
        showBannerBtn=findViewById(R.id.showBannerBtn);
        showBannerBtn.setOnClickListener(this);

        hideBannerBtn=findViewById(R.id.hideBannerBtn);
        hideBannerBtn.setOnClickListener(this);

        showInterstitialBtn=findViewById(R.id.showInterstitialBtn);
        showInterstitialBtn.setOnClickListener(this);

        requestOtherInterstitialBtn=findViewById(R.id.requestOtherInterstitialBtn);
        requestOtherInterstitialBtn.setOnClickListener(this);


        showOtherInterstitialBtn=findViewById(R.id.showOtherInterstitialBtn);
        showOtherInterstitialBtn.setOnClickListener(this);

        enableAutoPlayBtn=findViewById(R.id.enable_auto_play);
        enableAutoPlayBtn.setOnClickListener(this);

        showRewardVideoBtn=findViewById(R.id.showRewardVideoBtn);
        showRewardVideoBtn.setOnClickListener(this);
        logTextView=findViewById(R.id.logText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.showBannerBtn:
                AdsManager.instance().requestBanner(R.id.adview);
                break;
            case  R.id.hideBannerBtn:
                AdsManager.instance().destoryBanner();
                break;
            case  R.id.showInterstitialBtn:
                if(AdsManager.instance().hasIntertitial(0)){
                    AdsManager.instance().showInterstitialInAndroid(0);
                }
                break;
            case   R.id.requestOtherInterstitialBtn:
                AdsManager.instance().requestInterstitial(1);
                break;
            case  R.id.showOtherInterstitialBtn:
                break;
            case  R.id.showRewardVideoBtn:
                if(AdsManager.instance().hasReawrdVideo())
                AdsManager.instance().showRewardVideos();
                break;
        }
    }

    @Override
    public void onAdsStateChange(String adsState) {
        switch (adsState)
        {
            case "INIT_SUCCEED":
                AdsManager.instance().requestInterstitial(0);
                AdsManager.instance().requestRewardVideo();
                break;
        }
    }
}
