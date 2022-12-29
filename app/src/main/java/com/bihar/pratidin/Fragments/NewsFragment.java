package com.bihar.pratidin.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bihar.pratidin.Activity.FeatureDetActivity;
import com.bihar.pratidin.Activity.HomeActivity;
import com.bihar.pratidin.Activity.NotifyActivity;
import com.bihar.pratidin.Adapter.RecentAdapter;
import com.bihar.pratidin.AdsManager.GoogleAds;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Common.MyApplication;
import com.bihar.pratidin.MainActivity;
import com.bihar.pratidin.Model.Featured;
import com.bihar.pratidin.Model.Recent;
import com.bihar.pratidin.R;
import com.bihar.pratidin.Remote.IMyAPI;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    ImageSlider imageSlider;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI mService;

    RecyclerView recyclerView;

    NoInternetDialog noInternetDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    ProgressDialog dialog,progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        mService = Common.getAPI();

        dialog = new ProgressDialog(getContext());
        progressDialog = new ProgressDialog(getContext());

        swipeRefreshLayout = view.findViewById(R.id.swipe);

        imageSlider = view.findViewById(R.id.image_slider);
        recyclerView = view.findViewById(R.id.recyclerView);

        if (MyApplication.getInstance().isShowAds()) {
            GoogleAds.loadNativeAds((Activity) getContext(), (View) null, view.findViewById(R.id.admob_native_container), (NativeAdView) view.findViewById(R.id.native_ad_view));
        }
        else {
            view.findViewById(R.id.admob_native_container).setVisibility(View.GONE);
        }
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        view.findViewById(R.id.txtNotification).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), NotifyActivity.class));
//            }
//        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeatured();
                loadRecent();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadFeatured();

        loadRecent();

        view.findViewById(R.id.deltaRelative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        view.findViewById(R.id.parentRelative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        view.findViewById(R.id.txtMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });



        view.findViewById(R.id.imgAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+918800454970";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });


        return view;
    }


    private void loadFeatured() {
        dialog.setTitle("कृपया प्रतीक्षा करें...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        compositeDisposable.add(
                mService.getFeatured()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Featured>>() {
                                       @Override
                                       public void accept(List<Featured> featuredList) throws Exception {
                                           ArrayList<SlideModel> slideModels = new ArrayList<>();

                                           slideModels.clear();

                                           for (int i = 0; i < featuredList.size(); i++) {
                                               slideModels.add(new SlideModel(
                                                       new StringBuilder(Common.IMAGE_URL)
                                                               .append(featuredList.get(i).image_slider).toString(),
                                                       Common.stripHtml(featuredList.get(i).title)
                                                       ,null)
                                               );
                                           }
                                           imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                                           dialog.dismiss();

                                           imageSlider.setItemClickListener(new ItemClickListener() {
                                               @Override
                                               public void onItemSelected(int position) {
                                                   updateViewCount(featuredList.get(position));
                                                   Intent intent = new Intent(getContext(), FeatureDetActivity.class);
                                                   Common.CURRENT_FEAT = featuredList.get(position);
                                                   startActivity(intent);
                                               }
                                           });
                                       }
                                   }
                        )
        );
    }

    private void updateViewCount(Featured featured) {
        int prev = Integer.parseInt(featured.pageviews);
        int count = prev + 1;

        mService.updateViewCount(
                        String.valueOf(count),
                        featured.id)
                .enqueue(new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    private void loadRecent() {
        progressDialog.setTitle("कृपया प्रतीक्षा करें...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        compositeDisposable.add(
                mService.getRecent()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Recent>>() {
                                       @Override
                                       public void accept(List<Recent> recentList) throws Exception {
                                           recyclerView.setAdapter(new RecentAdapter(getContext(),recentList));
                                           progressDialog.dismiss();
                                       }
                                   }
                        )
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFeatured();
        loadRecent();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}