package com.bihar.pratidin.Remote;

import com.bihar.pratidin.Model.Author;
import com.bihar.pratidin.Model.Category;
import com.bihar.pratidin.Model.Featured;
import com.bihar.pratidin.Model.Headline;
import com.bihar.pratidin.Model.Recent;
import com.bihar.pratidin.Model.Related;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IMyAPI {

    @GET("getCategories.php")
    Observable<List<Category>> getCategories();

    @FormUrlEncoded
    @POST("getPosts.php")
    Observable<List<Headline>> getHeadlines(
            @Field("category_id") String category_id
    );

    @FormUrlEncoded
    @POST("getPosts.php")
    Observable<List<Related>> getRelated(
            @Field("category_id") String category_id
    );

    @GET("getFeatured.php")
    Observable<List<Featured>> getFeatured();

    @GET("getRecent.php")
    Observable<List<Recent>> getRecent();

    @GET("getAllNews.php")
    Observable<List<Headline>> getAllNews();

    @FormUrlEncoded
    @POST("updateViewCount.php")
    Call<String> updateViewCount(
            @Field("pageviews") String pageViews,
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("getAuthor.php")
    Observable<List<Author>> getAuthor(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("insertToken.php")
    Call<String> insertToken(
            @Field("deviceId")String deviceId,
            @Field("token")String token
    );
}
