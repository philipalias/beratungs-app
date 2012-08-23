package com.example.beratungskonfigurator;

import java.util.HashMap;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.tabs.*;

/**
 * This demonstrates how you can implement switching between the tabs of a
 * TabHost through fragments.  It uses a trick (see the code below) to allow
 * the tabs to switch between fragments instead of simple views.
 */
public class TabActivity extends FragmentActivity {
    TabHost mTabHost;
    TabManager mTabManager;
    static int kundeId;
    
    private static final String KUNDE_TAB = "Kunde";
	private static final String WOHNUNG_TAB = "Wohnung";
	private static final String GESUNDHEIT_TAB = "Gesundheit";
	private static final String PROBLEM_TAB = "Problemstellung";
	private static final String ANWENDUNGSFALL_TAB = "Anwendungsfall";
	private static final String KONFIGURATION_TAB = "persönliche Konfiguration";
	private static final String UEBERSICHT_TAB = "Übersicht";
	private static final String HERSTELLER_TAB = "Herstellersuche";
	private static final String EXPORT_TAB = "Export";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        kundeId = getIntent().getExtras().getInt("kundeId");

        setContentView(R.layout.tabs_layout);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        
        mTabManager.addTab(mTabHost.newTabSpec(KUNDE_TAB).setIndicator(KUNDE_TAB),KundeActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(WOHNUNG_TAB).setIndicator(WOHNUNG_TAB),WohnungActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(GESUNDHEIT_TAB).setIndicator(GESUNDHEIT_TAB),GesundheitActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(PROBLEM_TAB).setIndicator(PROBLEM_TAB),ProblemActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(ANWENDUNGSFALL_TAB).setIndicator(ANWENDUNGSFALL_TAB),AnwendungsfallActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(KONFIGURATION_TAB).setIndicator(KONFIGURATION_TAB),KonfigurationActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(UEBERSICHT_TAB).setIndicator(UEBERSICHT_TAB),UebersichtActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(HERSTELLER_TAB).setIndicator(HERSTELLER_TAB),HerstellerActivity.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(EXPORT_TAB).setIndicator(EXPORT_TAB),ExportActivity.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
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
            //args.putString("message","JD Nachricht via Bundle");
            args.putInt("sendKundeId", kundeId);


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