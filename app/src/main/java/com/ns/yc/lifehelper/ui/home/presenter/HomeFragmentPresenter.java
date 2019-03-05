package com.ns.yc.lifehelper.ui.home.presenter;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

import com.blankj.utilcode.util.Utils;
import com.ns.yc.lifehelper.R;
import com.ns.yc.lifehelper.ui.home.contract.HomeFragmentContract;
import com.ns.yc.lifehelper.ui.main.view.MainActivity;
import com.ycbjie.library.base.config.AppConfig;
import com.ycbjie.library.db.cache.CacheHomeNews;
import com.ycbjie.library.model.HomeBlogEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.subscriptions.CompositeSubscription;


/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/3/21
 * 描    述：Home主页面
 * 修订历史：
 *
 *       v1.5 修改于11月3日，改写代码为MVP架构
 * ================================================
 */
public class HomeFragmentPresenter implements HomeFragmentContract.Presenter {

    private HomeFragmentContract.View mHomeView;
    @NonNull
    private CompositeSubscription mSubscriptions;
    private Realm realm;
    private MainActivity activity;

    public HomeFragmentPresenter(HomeFragmentContract.View homeView) {
        this.mHomeView = homeView;
        mSubscriptions = new CompositeSubscription();
    }


    @Override
    public void subscribe() {
        initRealm();
    }


    @Override
    public void unSubscribe() {
        if(mSubscriptions.isUnsubscribed()){
            mSubscriptions.unsubscribe();
        }
        if(activity!=null){
            activity = null;
        }
    }


    @Override
    public void bindActivity(MainActivity activity) {
        this.activity = activity;
    }


    private void initRealm() {
        if(realm ==null){
            realm = AppConfig.INSTANCE.getRealmHelper();
        }
    }


    @Override
    public void getHomeNewsData() {
        initRealm();
        RealmResults<CacheHomeNews> cacheHomeNewses;
        if(realm.where(CacheHomeNews.class).findAll()!=null){
            cacheHomeNewses = realm.where(CacheHomeNews.class).findAll();
        }else {
            return;
        }
        List<HomeBlogEntity> list = new ArrayList<>();
        for(int a=0 ; a<cacheHomeNewses.size() ; a++){
            HomeBlogEntity homeNews = new HomeBlogEntity();
            homeNews.setUrl(cacheHomeNewses.get(a).getUrl());
            homeNews.setAuthor(cacheHomeNewses.get(a).getAuthor());
            homeNews.setImageUrl(cacheHomeNewses.get(a).getImageUrl());
            homeNews.setLogo(cacheHomeNewses.get(a).getLogo());
            homeNews.setSummary(cacheHomeNewses.get(a).getSummary());
            homeNews.setTime(cacheHomeNewses.get(a).getTime());
            homeNews.setTitle(cacheHomeNewses.get(a).getTitle());
            list.add(homeNews);
        }
        mHomeView.setNewsData(list);
    }


    @Override
    public ArrayList<String> getMarqueeTitle() {
        ArrayList<String> list = new ArrayList<>();
        String[] title = activity.getResources().getStringArray(R.array.main_marquee_title);
        SpannableString ss1 = new SpannableString(title[0]);
        ss1.setSpan(new ForegroundColorSpan(Color.BLACK),  2, title[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        list.add(ss1.toString());
        SpannableString ss2 = new SpannableString(title[1]);
        ss2.setSpan(new ForegroundColorSpan(Color.BLACK),  2, title[1].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        list.add(ss2.toString());
        SpannableString ss3 = new SpannableString(title[2]);
        ss3.setSpan(new URLSpan("http://www.ximalaya.com/zhubo/71989305/"), 2, title[2].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        list.add(ss3.toString());
        return list;
    }


    @Override
    public List<Object> getBannerData() {
        List<Object> lists = new ArrayList<>();
        TypedArray bannerImage = activity.getResources().obtainTypedArray(R.array.banner_image);
        for (int i = 0; i < 8 ; i++) {
            int image = bannerImage.getResourceId(i, R.drawable.ic_investment);
            lists.add(image);
        }
        bannerImage.recycle();
        return lists;
    }


    @Override
    public void getGalleryData() {
        final TypedArray array = Utils.getApp().getResources().obtainTypedArray(R.array.image_girls);
        final ArrayList<Bitmap> images = new ArrayList<>();
        for(int a=0 ; a<array.length() ; a++){
            int resourceId = array.getResourceId(a, R.drawable.ic_data_picture);
            Bitmap bitmap = toBitmap(resourceId);
            images.add(bitmap);
        }
        array.recycle();
        mHomeView.downloadBitmapSuccess(images);
    }

    private Bitmap toBitmap(int image) {
        return ((BitmapDrawable) Utils.getApp().getResources().getDrawable(image)).getBitmap();
    }

}
