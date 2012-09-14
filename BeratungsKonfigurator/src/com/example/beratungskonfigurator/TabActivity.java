package com.example.beratungskonfigurator;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.example.beratungskonfigurator.tabs.AnwendungsfallActivity;
import com.example.beratungskonfigurator.tabs.ExportActivity;
import com.example.beratungskonfigurator.tabs.GesundheitActivity;
import com.example.beratungskonfigurator.tabs.KonfigurationActivity;
import com.example.beratungskonfigurator.tabs.KundeActivity;
import com.example.beratungskonfigurator.tabs.WohnungActivity;


public class TabActivity extends FragmentActivity {
    TabHost mTabHost;
    TabManager mTabManager;
    static int kundeId;
    static int angehoerigerId;
    
    private static final String KUNDE_TAB = "Kundendaten";
	private static final String WOHNUNG_TAB = "Wohnungsdaten";
	private static final String GESUNDHEIT_TAB = "gesundheitliche Problemstellung";
	private static final String ANWENDUNGSFALL_TAB = "Anwendungsfall";
	private static final String KONFIGURATION_TAB = "persönliche Konfiguration";
	private static final String EXPORT_TAB = "Export";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        kundeId = getIntent().getExtras().getInt("kundeId");
        angehoerigerId = getIntent().getExtras().getInt("angehoerigerId");

        setContentView(R.layout.tabs_layout);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        
        mTabManager.addTab(mTabHost.newTabSpec(KUNDE_TAB).setIndicator(""),KundeActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(WOHNUNG_TAB).setIndicator(""),WohnungActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(GESUNDHEIT_TAB).setIndicator(""),GesundheitActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(ANWENDUNGSFALL_TAB).setIndicator(""),AnwendungsfallActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(KONFIGURATION_TAB).setIndicator(""),KonfigurationActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(EXPORT_TAB).setIndicator(""),ExportActivity.class, null);
        
        

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
        TabWidget tw = mTabHost.getTabWidget();

        tw.getChildAt(0).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_01)); 
        tw.getChildAt(1).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_02)); 
        tw.getChildAt(2).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_03)); 
        tw.getChildAt(3).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_04)); 
        tw.getChildAt(4).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_05)); 
        tw.getChildAt(5).setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_indicator_06)); 
    }

    
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            Bundle args = new Bundle();
            args.putInt("sendKundeId", kundeId);
            args.putInt("sendAngehoerigerId", angehoerigerId);


            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {                
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }
                mLastTab = newTab;
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
}