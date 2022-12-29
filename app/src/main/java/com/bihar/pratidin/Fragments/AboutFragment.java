package com.bihar.pratidin.Fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bihar.pratidin.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {

    String version;

    AboutPage aboutPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            version = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Element versionElement = new Element();
        versionElement.setTitle("Version : "+version);

        aboutPage = new AboutPage(getContext(),R.style.Widget_App_AboutPage);
        aboutPage = new AboutPage(getContext(),false);


        return new AboutPage(getContext())
                .isRTL(false)
                .setDescription("biharpratidin.com is a news web portal owned and managed by iHunTech Pvt Ltd.")
                .setImage(R.mipmap.ic_launcher)
                .addItem(versionElement)
                .addGroup("Connect with us")
                .addEmail("info@biharpratidin.com")
                .addWebsite("https://biharpratidin.com/")
                .addFacebook("BiharPratidin")
                .addTwitter("BiharPratidin")
                .addYoutube("UCS9EhW2B58f6QEfjXvc_ulQ")
                .addPlayStore(getContext().getPackageName())
                .addInstagram("bihar_pratidin")
                .create();

    }
}