package com.bihar.pratidin.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bihar.pratidin.Adapter.AuthorAdapter;
import com.bihar.pratidin.Adapter.RelatedAdapter;
import com.bihar.pratidin.AdsManager.GoogleAds;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Common.MyApplication;
import com.bihar.pratidin.MainActivity;
import com.bihar.pratidin.Model.Author;
import com.bihar.pratidin.Model.Related;
import com.bihar.pratidin.R;
import com.bihar.pratidin.Remote.IMyAPI;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.florent37.diagonallayout.DiagonalLayout;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecentActivity extends AppCompatActivity {

    ImageView imageView;

    TextView txtTitle, txtTime,txtSummary, txtContent;

    NoInternetDialog noInternetDialog;
    SpinKitView spinKitView;

    TextToSpeech textToSpeech;

    RecyclerView recyclerRelated, recyclerAd, recyclerAuthor;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI mService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyApplication.getInstance().isShowAds())   {
            GoogleAds.loadGoogleFullscreen(this);
        }

        setContentView(R.layout.activity_recent);

        mService = Common.getAPI();

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        imageView = findViewById(R.id.layoutImage);
        txtTitle = findViewById(R.id.txtTitle);
        txtTime = findViewById(R.id.txtTime);
        txtSummary = findViewById(R.id.txtSummary);
        txtContent = findViewById(R.id.txtContent);
        spinKitView = findViewById(R.id.spin_kit);

        recyclerRelated = findViewById(R.id.recyclerRelated);
        recyclerAd = findViewById(R.id.recyclerAd);
        recyclerAuthor = findViewById(R.id.recyclerAuthor);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);

        recyclerAuthor.setHasFixedSize(true);
        recyclerAuthor.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDetails();
                loadRelated();
                loadAd();

                refreshLayout.setRefreshing(false);
            }
        });

        recyclerRelated.setHasFixedSize(true);
        recyclerRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        recyclerAd.setHasFixedSize(true);
        recyclerAd.setLayoutManager(new LinearLayoutManager(this));

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED)  {
                        Log.e("TTS", "Language not supported..." );
                    }
                }
                else {
                    Log.e("TTS", "Init Failed...");
                }
            }
        });

        if (MyApplication.getInstance().isShowAds()) {
            GoogleAds.loadNativeAds(this, (View) null, findViewById(R.id.admob_native_container), (NativeAdView) findViewById(R.id.native_ad_view));
        }
        else {
            findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }

        findViewById(R.id.imgAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+918800454970";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        findViewById(R.id.txtBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoogleAds.mInterstitialAd != null)  {
                    GoogleAds.mInterstitialAd.show(RecentActivity.this);
                }
                else {
                    finish();
                }
            }
        });

        findViewById(R.id.txtMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecentActivity.this, MainActivity.class));
                finish();
            }
        });

        findViewById(R.id.txtListen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "";
                String summary = "";
                String content = "";

                if (Common.CURRENT_RECENT.title != null)    {
                    title = Common.stripHtml(Common.CURRENT_RECENT.title);
                }

                if (Common.CURRENT_RECENT.summary != null)    {
                    summary = Common.stripHtml(Common.CURRENT_RECENT.summary);
                }


                if (Common.CURRENT_RECENT.content != null)    {
                    content = Common.stripHtml(Common.CURRENT_RECENT.content);
                }

                String readNews = title + " " + summary + " "+content;

                textToSpeech.setPitch(1.0F);
                textToSpeech.setSpeechRate(0.65F);

                textToSpeech.speak(readNews,TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        findViewById(R.id.txtShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.shareApp(RecentActivity.this,Common.CURRENT_RECENT.title,Common.CURRENT_RECENT.title_slug);
            }
        });

        loadDetails();
        loadRelated();
        loadAd();

        loadAuthor();
    }

    private void loadAuthor() {
        compositeDisposable.add(
                mService.getAuthor(Common.CURRENT_RECENT.user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Author>>() {
                                       @Override
                                       public void accept(List<Author> authors) throws Exception {
                                           recyclerAuthor.setAdapter(new AuthorAdapter(RecentActivity.this, authors));
                                       }
                                   }
                        )
        );
    }

    private void loadDetails() {
        Picasso.get()
                .load(new StringBuilder(Common.IMAGE_URL)
                        .append(Common.CURRENT_RECENT.image_big).toString())
                .error(R.drawable.logo)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        spinKitView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        spinKitView.setVisibility(View.GONE);
                    }
                });

        if (Common.CURRENT_RECENT.title != null)    {
            String title = Common.stripHtml(Common.CURRENT_RECENT.title);
            txtTitle.setText(title);
        }
        else {
            txtTitle.setVisibility(View.GONE);
        }

        if (Common.CURRENT_RECENT.summary != null)    {
            String summary = Common.stripHtml(Common.CURRENT_RECENT.summary);
            txtSummary.setText(summary);
        }
        else {
            txtSummary.setVisibility(View.GONE);
        }


        if (Common.CURRENT_RECENT.content != null)    {
            String content = Common.stripHtml(Common.CURRENT_RECENT.content);
            txtContent.setText(content);
        }
        else {
            txtContent.setVisibility(View.GONE);
        }

        String timeAgo = Common.calculateTimeAgo(Common.CURRENT_RECENT.created_at);
        txtTime.setText(timeAgo);

    }


    private void loadRelated() {
        compositeDisposable.add(
                mService.getRelated(Common.CURRENT_RECENT.category_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Related>>() {
                                       @Override
                                       public void accept(List<Related> relatedList) throws Exception {
                                           recyclerRelated.setAdapter(new RelatedAdapter(
                                                   RecentActivity.this,
                                                   relatedList)
                                           );
                                       }
                                   }
                        )
        );
    }

    private void loadAd() {

    }

    @Override
    public void onBackPressed() {
        if (GoogleAds.mInterstitialAd != null)  {
            GoogleAds.mInterstitialAd.show(this);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null)   {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        compositeDisposable.clear();
        compositeDisposable.dispose();

        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}