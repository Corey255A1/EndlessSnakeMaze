package com.endlessmaze.game;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.Callbacks.ICallback;
import com.endlessmaze.game.Controls.MenuPage;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.endlessmaze.game.EndlessMaze;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class AndroidLauncher extends AndroidApplication implements RewardedVideoAdListener {
	private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mBombAd;
    private boolean bRewardLoaded = false;
    private boolean bGameHasStarted = false;
    private EndlessMaze theMaze;
    private int levelSelectCount = 1;
    private int resetCount = 2;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        levelSelectCount = 0;
		//Add the LibGDX Layout to the screen
		RelativeLayout layout = new RelativeLayout(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        theMaze = new EndlessMaze();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gView = initializeForView(theMaze, config);

		layout.addView(gView);

        //Add the Ad to the layout
        mAdView = new AdView(this);
        mAdView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if(bGameHasStarted && !theMaze.InMenu()) {
                    mAdView.setVisibility(View.VISIBLE);
                    mAdView.bringToFront();
                    mAdView.requestLayout();
                    mAdView.invalidate();
                }
            }
        });
        mAdView.loadAd(adRequest);

		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
				adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		layout.addView(mAdView, adParams);

        //Set our content to the layout.
		setContentView(layout);
        mBombAd = MobileAds.getRewardedVideoAdInstance(this);
        mBombAd.setRewardedVideoAdListener(this);
        mBombAd.loadAd(getResources().getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                theMaze.OnReturnFromInterstitial();
                levelSelectCount=2;
                resetCount = 3;
            }
        });
        theMaze.AddLevelSelectCallback(new ICallback< MenuPage.LevelSelect>(){
            @Override
            public void Callback(CallbackPayload<MenuPage.LevelSelect> val) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    if(--levelSelectCount<=0) {
                        if (mInterstitialAd.isLoaded()) {
                            theMaze.SetInAd();
                            theMaze.PauseTimer();
                            mInterstitialAd.show();
                        }
                        else {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                    }
                    mAdView.loadAd(new AdRequest.Builder().build());
                    mAdView.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
        theMaze.AddResetCallback(new ICallback<Integer>(){
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                if(--resetCount<=0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd.isLoaded()) {
                                theMaze.SetInAd();
                                theMaze.PauseTimer();
                                mInterstitialAd.show();
                            }
                            else {
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        }
                    });
                }
            }
        });
        theMaze.AddMenuCallback(new ICallback<Integer>(){
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // mAdView.destroy();
                        mAdView.setVisibility(View.GONE);
                    }
                });
            }
        });
        theMaze.AddGameStartedCallback(new ICallback<Integer>(){
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bGameHasStarted = true;
                        mAdView.setVisibility(View.VISIBLE);
                        mAdView.bringToFront();
                        mAdView.requestLayout();
                        mAdView.invalidate();
                    }
                });
            }
        });
        theMaze.AddMenuClosed(new ICallback<Integer>(){
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // mAdView.loadAd(new AdRequest.Builder().build());
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        theMaze.AddActionItemCB(new ICallback<Integer>(){
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bRewardLoaded && mBombAd.isLoaded())
                        {
                            theMaze.SetInAd();
                            theMaze.PauseTimer();
                            mBombAd.show();
                        }
                        else
                        {
                            theMaze.OnReturnFromRewardAd(2);
                        }

                    }
                });
            }
        });

	}
    @Override
    public void onResume() {
        mBombAd.resume(this);
        theMaze.ResumeTimer();
        super.onResume();
    }

    @Override
    public void onPause() {
        mBombAd.pause(this);
        theMaze.PauseTimer();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mBombAd.destroy(this);
        super.onDestroy();
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        bRewardLoaded = true;
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        mBombAd.loadAd(getResources().getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());
        theMaze.OnReturnFromRewardAd(0);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        theMaze.OnReturnFromRewardAd(1);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        theMaze.OnReturnFromRewardAd(2);
    }
}
