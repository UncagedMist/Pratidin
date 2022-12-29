package com.bihar.pratidin.Common;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.text.Html;
import android.text.format.DateUtils;

import com.bihar.pratidin.Model.Featured;
import com.bihar.pratidin.Model.Headline;
import com.bihar.pratidin.Model.Recent;
import com.bihar.pratidin.Model.Related;
import com.bihar.pratidin.Remote.IMyAPI;
import com.bihar.pratidin.Remote.RetrofitClient;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Common {
    public static final String BASE_URL = "https://biharpratidin.com/AndroidApp/";
    public static final String IMAGE_URL = "https://biharpratidin.com/";

    public static Headline CURRENT_HEADLINE;
    public static Featured CURRENT_FEAT;
    public static Recent CURRENT_RECENT;
    public static Related CURRENT_RELATED;


    public static final String TERMS_URL = IMAGE_URL+"terms-conditions";
    public static final String CONTACT_URL = IMAGE_URL+"contact-us";
    public static final String PRIVACY_URL = IMAGE_URL+"privacy-policy";

    public static IMyAPI getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IMyAPI.class);
    }

    public static void checkAppUpdate(Context context) {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))    {

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,AppUpdateType.IMMEDIATE,
                                (Activity) context,
                                12
                        );
                    }
                    catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static String calculateTimeAgo(String givenTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        try {
            long time = sdf.parse(givenTime).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

            return ago+"";
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
