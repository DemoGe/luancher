package com.netxeon.beeui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netxeon.beeui.R;
import com.netxeon.beeui.activity.AppActicity;
import com.netxeon.beeui.activity.CleanActivity;
import com.netxeon.beeui.activity.EditorActivity;
import com.netxeon.beeui.activity.MainActivity;
import com.netxeon.beeui.adapter.ShortcutsAdapter;
import com.netxeon.beeui.bean.Shortcut;
import com.netxeon.beeui.utils.AnimUtil;
import com.netxeon.beeui.utils.DBHelper;
import com.netxeon.beeui.utils.Data;
import com.netxeon.beeui.utils.DateUtil;
import com.netxeon.beeui.utils.GridViewTV;
import com.netxeon.beeui.utils.Util;
import com.netxeon.beeui.weather.WeatherUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;

import static android.security.KeyStore.getApplicationContext;

/**
 *
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    private ShortcutsAdapter mAdapter;
    private GridViewTV gridView;
    private PackageManager pm;
    private List<Shortcut> mShortcut;
    private String mCurrentCategory = Data.HOME;
    private final String ADDITIONAL = "additional";
    private RelativeLayout music, online, local, image, browser, apps, datetime, weather, storage, setting;
    //shortcut列数限制
    public static final int columns = 11;
    //shortcut行数限制
    private int rows = 1;
    //放大倍数
    private float scale = 1.1f;
    public static boolean setLastRNull = false;
    public RelativeLayout lastR = null;
    private boolean isPaused = false;

    private TextView dateText;
    private TextView timeText;
    private TextView weekText;
    private TextView apmText;
    private TextView weather_city1;
    private TextView weathrer_temperature1;
   private TextView weather_info1;
    private ImageView weather_image1;
    private TextView  storage_display;
    private  static int mWeatherCode =3200;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo netInfo;
    private static boolean GET_WEATHER_OK = false;
    private WeatherReceiver mWeatherReceiver;
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
//            gridView.setFocusable(false);
        } else {
            //getData();
//            gridView.setFocusable(true);
            if (lastR != null) {
                lastR.requestFocus();
            }

        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShortcut = new ArrayList<>();
        aaa = Util.getString(getActivity(), WeatherUtils.WEATHER_CITY);
    }
   private  static  String aaa;
    public void initWeather() {
        ConnectivityManager connManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager.getActiveNetworkInfo() != null){
            if(!connManager.getActiveNetworkInfo().isConnected()) return;
            Timer mTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    WeatherUtils.updateWeather(getActivity(), mUpdateWeatherHandler, aaa);
                }
            };
            try {
                mTimer.schedule(task, 2000, 1000 * 60 * 60 * 3);//do it after 10 seconds , per 3 hours do it again
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
        }
    }
    private void getWeatherAtonResume(){
        if (!GET_WEATHER_OK) {
            ConnectivityManager connManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connManager.getActiveNetworkInfo() != null){
                if(!connManager.getActiveNetworkInfo().isConnected()) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        WeatherUtils.updateWeather(getActivity(), mUpdateWeatherHandler,aaa);
                    }
                }).start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_beelink_ui, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewInit(view);
        setListener();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityInit();
        registerBroadcast();
    }

    private void registerBroadcast() {
          mWeatherReceiver = new WeatherReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mWeatherReceiver, intentFilter);
    }

    private void activityInit() {
        pm = getActivity().getPackageManager();
        mAdapter = new ShortcutsAdapter(getActivity(), mShortcut, pm, false);
        gridView.setAdapter(mAdapter);
        gridView.setNumColumns(columns);
    }

    private void viewInit(View view) {
        music = (RelativeLayout) view.findViewById(R.id.music_icon);
        online= (RelativeLayout) view.findViewById(R.id.online_video_icon);
        local= (RelativeLayout) view.findViewById(R.id.local_video_icon);
        image= (RelativeLayout) view.findViewById(R.id.images_icon);
        browser= (RelativeLayout) view.findViewById(R.id.browser_icon);
        apps= (RelativeLayout) view.findViewById(R.id.apps_icon);


        gridView = (GridViewTV) view.findViewById(R.id.fragment_home_gridview);
        datetime =(RelativeLayout)view.findViewById(R.id.date_time_icon);
        weather=(RelativeLayout)view.findViewById(R.id.weather_icon);
        storage=(RelativeLayout)view.findViewById(R.id.storage_cleanup_icon);
        storage_display=(TextView)view.findViewById(R.id.storage_left);
        setting=(RelativeLayout)view.findViewById(R.id.settings_icon);
        dateText=(TextView)view.findViewById(R.id.system_date_value);
        weekText=(TextView)view.findViewById(R.id.system_time_weekday);
        apmText=(TextView)view.findViewById(R.id.system_time_apm);
        timeText=(TextView)view.findViewById(R.id.system_time_value) ;
        weather_city1=(TextView) view.findViewById(R.id.title_weather_city);
        weather_image1=(ImageView) view.findViewById(R.id.title_weather_image);
        weather_info1=(TextView) view.findViewById(R.id.title_weather_info);
        weathrer_temperature1=(TextView) view.findViewById(R.id.title_weather_temperature);
    }

    private void setListener() {
        music.setOnClickListener(this);
        music.setOnFocusChangeListener(this);
        local.setOnClickListener(this);
        local.setOnFocusChangeListener(this);
        online.setOnClickListener(this);
        online.setOnFocusChangeListener(this);
        image.setOnClickListener(this);
        image.setOnFocusChangeListener(this);
        browser.setOnClickListener(this);
        browser.setOnFocusChangeListener(this);
        apps.setOnClickListener(this);
        apps.setOnFocusChangeListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemSelectedListener(this);
        gridView.setOnFocusChangeListener(this);
        datetime.setOnClickListener(this);
        datetime.setOnFocusChangeListener(this);
        weather.setOnClickListener(this);
        weather.setOnFocusChangeListener(this);
        storage.setOnClickListener(this);
        storage.setOnFocusChangeListener(this);
        setting.setOnFocusChangeListener(this);
        setting.setOnFocusChangeListener(this);
        datetime.setOnFocusChangeListener(this);
        setting.setOnClickListener(this);
        setting.setOnFocusChangeListener(this);
        timeHandle.post(timeRun);

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        if (isPaused) {
            mAdapter.refreshApps();
            isPaused = false;
        }
        if (setLastRNull) {
            lastR = null;
            setLastRNull = false;
        }
        if (lastR != null) {
            switch (lastR.getId()) {
                case R.id.date_time_icon:
                    datetime.requestFocus();
                    break;
                case R.id.weather_icon:
                    weather.requestFocus();
                    break;
                case R.id.storage_cleanup_icon:
                    storage.requestFocus();
                    break;
                case R.id.settings_icon:
                    setting.requestFocus();
                    break;
                case R.id.apps_icon:
                    apps.requestFocus();
                    break;
                case R.id.browser_icon:
                    browser.requestFocus();
                    break;
            }
        }


        getData();
        super.onResume();
        getWeatherAtonResume();
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        if (mWeatherReceiver!=null){
        getActivity().unregisterReceiver(mWeatherReceiver);}
        super.onDestroyView();

    }

    private void getData() {
        mShortcut.clear();
        mShortcut.addAll(DBHelper.getInstance(getActivity()).queryByCategory(mCurrentCategory));
        Shortcut forAddItem = new Shortcut();
        forAddItem.setComponentName(ADDITIONAL);
        mShortcut.add(forAddItem);
        //L.d("zzy", "notifyDataSetChanged");
        mAdapter.notifyDataSetChanged(mShortcut);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ShortcutsAdapter.ShortcutHolder holder = (ShortcutsAdapter.ShortcutHolder) view.getTag();
        ComponentName componentName = holder.componentName;
        if (ADDITIONAL.equals(componentName.getPackageName())) {//点击了加号
            Intent editIntent = new Intent(getActivity(), EditorActivity.class);
            editIntent.putExtra(Util.CATEGORY, mCurrentCategory)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(editIntent);
            lastR = null;
        } else {//否则点击跳转
            try {
                Intent mainintent = new Intent(Intent.ACTION_MAIN, null);
                mainintent.addCategory(Intent.CATEGORY_LAUNCHER);
                mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                mainintent.setComponent(componentName);
                startActivity(mainintent);
            } catch (Exception e) {
//                Log.e("error", "FolderActivity.ItemClickListener.onItemClick() startActivity failed: " + componentName);
            }
        }
    }

    /**
     * 通过包名，类名跳转
     *
     * @param pkg
     * @param cls
     */
    private void startThirdApp(String pkg, String cls) {
        Intent startintent = new Intent();
        //"com.android.tv.settings", "com.android.tv.settings.MainSettings"
        ComponentName mComp = new ComponentName(pkg, cls);
        startintent.setComponent(mComp);
        startActivity(startintent);
    }

    /**
     * 通过包名跳转
     *
     * @param pkg
     */
    private void startThirdAppforPM(String pkg) {
        try {
            startActivity(pm.getLaunchIntentForPackage(pkg));
        } catch (Exception e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.noapp), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.online_video_icon:
                startCateFragment(Data.STREAM);
                lastR = (RelativeLayout) v;

                break;
            case R.id.local_video_icon:
                startCateFragment(Data.MOVIE);
                lastR = (RelativeLayout) v;

                break;
            case R.id.images_icon:
                startCateFragment(Data.PHOTO);
                lastR = (RelativeLayout) v;

                break;
            case R.id.music_icon:
                startCateFragment(Data.MUSIC);
                lastR = (RelativeLayout) v;

//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.movie.name());
//                startActivity(intent);
                break;
            case R.id.browser_icon:
                lastR = (RelativeLayout) v;
                startThirdAppforPM(getResources().getString(R.string.browser_internet));

//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.favourites.name());
//                startActivity(intent);
                break;
            case R.id.apps_icon:
                lastR = (RelativeLayout) v;
                Intent appactivity = new Intent(getActivity(), AppActicity.class);
                startActivity(appactivity);

//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.music.name());
//                startActivity(intent);
                break;
            case R.id.date_time_icon:
                lastR = (RelativeLayout) v;
                //内部存储设置
                //Intent dateIntent = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
                Intent dateIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(dateIntent);

                break;
            case R.id.weather_icon:
                lastR = (RelativeLayout) v;
                showEditCityForWeatherDialog();

                break;
            case R.id.storage_cleanup_icon:
                lastR = (RelativeLayout) v;
                Intent cleanapp = new Intent(getActivity(), CleanActivity.class);
                startActivityForResult(cleanapp, 1);

                break;
            case R.id.settings_icon:

                lastR = (RelativeLayout) v;
                Intent settingsIntent = new Intent();
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName mComp = new ComponentName(getString(R.string.setting_pkg), getString(R.string.setting_main_activity));
                settingsIntent.setComponent(mComp);
                try {
                    startActivity(settingsIntent);
                } catch (Exception e) {
                    //              L.i("netexon setting not found");
                    settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(settingsIntent);
                }
                break;
     /*       case R.id.home_icon_stream:
                startCateFragment(Data.STREAM);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.stream.name());
//                startActivity(intent);
                break;
            case R.id.home_icon_internet:
                startCateFragment(Data.INTERNET);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.internet.name());
//                startActivity(intent);
                break;
            case R.id.home_icon_game:
                startCateFragment(Data.GAME);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.game.name());
//                startActivity(intent);
                break;
            case R.id.home_icon_photo:
                startCateFragment(Data.PHOTO);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.photo.name());
//                startActivity(intent);
                break;
            case R.id.home_icon_social:
                startCateFragment(Data.SOCIAL);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.social.name());
//                startActivity(intent);
                break;
            case R.id.home_icon_files:
                startThirdAppforPM(getResources().getString(R.string.file_brower));
                break;*/
            default:
                lastR = null;
                break;
        }


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.online_video_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.local_video_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.images_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.music_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.movie.name());
//                startActivity(intent);
                    break;
                case R.id.browser_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.favourites.name());
//                startActivity(intent);
                    break;
                case R.id.apps_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
//                intent = new Intent(getActivity(), CategoryActivity.class);
//                intent.putExtra(Util.CATEGORY, Data.Cate.music.name());
//                startActivity(intent);
                    break;
                case R.id.date_time_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.weather_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.storage_cleanup_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.settings_icon:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;

             /*   case R.id.home_icon_left_row1:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_left_row2:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_left_row3:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_movie:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_favourites:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_music:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_stream:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_internet:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_game:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_photo:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_social:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;
                case R.id.home_icon_files:
                    v.bringToFront();
                    AnimUtil.setViewScale(v, scale);
                    break;*/
                case R.id.fragment_home_gridview:
//                    Log.i("bo", "gridview has focus");
                    new Thread(run).start();
                    break;

            }
        } else {
            switch (v.getId()) {
                case R.id.online_video_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.local_video_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.images_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.music_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.browser_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.apps_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.date_time_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.weather_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.storage_cleanup_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.settings_icon:
                    AnimUtil.setViewScaleDefault(v);
                    break;
                case R.id.fragment_home_gridview:
                    if (mOldView != null) {
                        AnimUtil.setViewScaleDefault(mOldView);
                    }
                    isSelect = false;
                    break;
            }
        }
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {
//            try {
//                Thread.sleep(50);
            handler.sendEmptyMessage(0);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (isSelect) {
                        isSelect = false;
                    } else {
                        // 如果是第一次进入该gridView，则进入第一个item，如果不是第一次进去，则选择上次出来的item
                        if (mOldView == null) {
                            mOldView = gridView.getChildAt(0);
                            if (mOldView != null) {
                                AnimUtil.setViewScale(mOldView, scale);
                            }
                        } else {
                            AnimUtil.setViewScale(mOldView, scale);
                        }

//                Log.i("bo", "heyheyhey");
                    }
                    break;
            }
        }
//            Log.i("bo", "handle get msg");

        ;
    };


    private View mOldView;
    private boolean isSelect = false;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null && gridView.hasFocus()) {
            //  Log.d("bo", view.toString() + " bringToFront");

            isSelect = true;
            AnimUtil.setViewScale(view, scale);
            if (mOldView != null)
                AnimUtil.setViewScaleDefault(mOldView);
        }
        mOldView = view;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void startCateFragment(String cate) {
        CategoryFragment categoryFragment = ((MainActivity) getActivity()).getCategoryFragment();
        categoryFragment.setCurrentCategory(cate);
        ((MainActivity) getActivity()).changeFragment(categoryFragment);
    }

    private Handler timeHandle = new Handler();
    private Runnable timeRun = new Runnable() {
        public void run() {
            dateText.setText(DateUtil.getDate());
            timeText.setText(DateUtil.getTime());
            weekText.setText(DateUtil.getWeek());
            apmText.setText(DateUtil.getAmOrPm());
            timeHandle.postDelayed(this, 5000);
        }

    };
    //wifi,weather
    private Handler mUpdateWeatherHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

//            L.i("MainActivity.UpdateWeatherHandler msg.what : " + msg.what);
            switch (msg.what) {
                case WeatherUtils.MSG_WEATHER_NO_CITY: {
                    if (weather_city1 != null) weather_city1.setText(R.string.weather_no_city);
                     Util.setString(getActivity(), WeatherUtils.WEATHER_CITY, "empty");
                    break;
                }
                case WeatherUtils.MSG_WEATHER_OK: {
                    WeatherInfo weatherInfo = (WeatherInfo) msg.obj;
                    if (weather_city1 != null && weather_image1 != null && weatherInfo != null) {
                        mWeatherCode = weatherInfo.getCurrentCode();
                        int temp = (int) ((weatherInfo.getCurrentTemp() - 32) / 1.8);
                        weathrer_temperature1.setText(temp + "ºC");
                        weather_city1.setText(weatherInfo.getLocationCity());
                        weather_info1.setText(weatherInfo.getCurrentText());
                        weathrer_temperature1.setVisibility(View.VISIBLE);
                        if (mWeatherCode >= 0 && mWeatherCode <= 47) {
                            weather_image1.setImageResource(Data.getWeatherIcon(mWeatherCode));//设置通过weathercode设置已经在本地的天气图片
                        } else {
                            weather_image1.setImageResource(R.mipmap.weather3200);
                        }
                         Util.setString(getActivity(), WeatherUtils.WEATHER_CITY, weatherInfo.getLocationCity());
//                        L.i("MainActivity.UpdateWeatherHandler display weather info !");
                        GET_WEATHER_OK = true;

                    } else {
                        if (weather_city1 != null && weather_image1 != null) {
                            // weather_city1.setText("Sunny to cloudy");
                            //   weathrer_temperature1.setText("28");
                            //  weather_info1.setText("走到这里");
                            initWeather();

                        }
                        //    Toast.makeText(getActivity(), R.string.weather_edit_city_error, Toast.LENGTH_LONG).show();
//                        L.i("MainActivity.UpdateWeatherHandler weather views are null !");

                    }
                    break;
                }

            }
        }
    };

    public void showEditCityForWeatherDialog() {
        final EditText editor = new EditText(getActivity());
        editor.setSingleLine();
        new AlertDialog.Builder(getActivity()).setTitle(R.string.weather_edit_city_dialog_tips).setIcon(android.R.drawable.ic_dialog_info).setView(editor).setPositiveButton(R.string.weather_edit_city_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             String  _location = editor.getText().toString();
                if (!TextUtils.isEmpty(_location)) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editor.getWindowToken(), 0);
                    WeatherUtils.updateWeather(getActivity(), mUpdateWeatherHandler, _location);
                    Util.setString(getActivity(), WeatherUtils.WEATHER_CITY, _location);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.weather_edit_city_dialog_not_empty, Toast.LENGTH_LONG).show();
                }
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            String d = data.getStringExtra("data");
            int result = Integer.valueOf(d);
            storage_display.setText(d + "%");

        }
    }
    private class WeatherReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                GET_WEATHER_OK = false;
                initWeather();
            }
        }}

}
