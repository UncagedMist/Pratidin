package com.bihar.pratidin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bihar.pratidin.Adapter.CategoryAdapter;
import com.bihar.pratidin.Adapter.HeadlineAdapter;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Common.MyApplication;
import com.bihar.pratidin.Interface.CategoryClickListener;
import com.bihar.pratidin.Model.Category;
import com.bihar.pratidin.Model.Headline;
import com.bihar.pratidin.Remote.IMyAPI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.rvadapter.AdmobNativeAdAdapter;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements CategoryClickListener {

    RecyclerView recyclerCat, recyclerHead;

    IMyAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    NoInternetDialog noInternetDialog;

    List<Category> listCategories = new ArrayList<>();

    TextView txtCategory;

//    SwipeRefreshLayout refreshLayout;

    String catId = "NULL";

    ProgressDialog dialog,progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noInternetDialog = new NoInternetDialog.Builder(this).build();


        mService = Common.getAPI();

        dialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);

        txtCategory = findViewById(R.id.txtCategory);
        recyclerCat = findViewById(R.id.recyclerCategory);
        recyclerHead = findViewById(R.id.recyclerHeadline);
//        refreshLayout = findViewById(R.id.swipe);

        recyclerHead.setNestedScrollingEnabled(true);

        recyclerCat.setHasFixedSize(true);
        recyclerCat.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        recyclerHead.setHasFixedSize(true);
        recyclerHead.setLayoutManager(new LinearLayoutManager(this));

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (catId.equals("NULL"))  {
//                    loadHead("1");
//                }
//                else {
//                    loadHead(catId);
//                }
//
//                refreshLayout.setRefreshing(false);
//            }
//        });

        loadCat();

        loadAllNews();
    }

    private void loadCat() {
        compositeDisposable.add(
                mService.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Category>>() {
                                       @Override
                                       public void accept(List<Category> categoryList) throws Exception {

                                           listCategories.clear();

                                           listCategories = categoryList;

                                           CategoryAdapter adapter = new CategoryAdapter(
                                             MainActivity.this,
                                             categoryList,
                                                   MainActivity.this::onCategoryClick
                                           );

                                           recyclerCat.setAdapter(adapter);
                                       }
                                   }
                        )
        );
    }

    @Override
    public void onCategoryClick(int pos) {

        String id = String.valueOf(listCategories.get(pos).id);
        String name = listCategories.get(pos).name;

        if (name != null)   {
            txtCategory.setVisibility(View.VISIBLE);
            txtCategory.setText(name);
        }
        else {
            txtCategory.setVisibility(View.GONE);
        }

        dialog.setTitle(name + " के समाचार लोड हो रहे हैं| कृपया प्रतीक्षा करें....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        catId = id;

        loadHead(catId);
    }


    private void loadHead(String id) {
        compositeDisposable.add(
                mService.getHeadlines(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Headline>>() {
                                       @Override
                                       public void accept(List<Headline> headlines) throws Exception {

                                           displayHeadlines(headlines);

                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
    }

    private void displayHeadlines(List<Headline> headlines) {

        HeadlineAdapter adapter = new HeadlineAdapter(this,headlines);

        if (MyApplication.getInstance().isShowAds())    {
            AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                            getString(R.string.native_id),
                            adapter,
                            "small")
                    .adItemInterval(3)
                    .build();

            recyclerHead.setAdapter(admobNativeAdAdapter);
        }
        else {
            recyclerHead.setAdapter(adapter);
        }
    }

    private void loadAllNews() {
        dialog.setTitle("कृपया प्रतीक्षा करें...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        compositeDisposable.add(
                mService.getAllNews()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Headline>>() {
                                       @Override
                                       public void accept(List<Headline> headlines) throws Exception {
                                           displayHeadlines(headlines);
                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        compositeDisposable.clear();
        super.onDestroy();
        noInternetDialog.onDestroy();

    }
}