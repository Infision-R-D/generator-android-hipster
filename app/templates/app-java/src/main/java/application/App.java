package <%= appPackage %>.application;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;

<% if (jodatime == true) { %>import net.danlew.android.joda.JodaTimeAndroid; <% } %>

import <%= appPackage %>.BuildConfig;
import <%= appPackage %>.R;
import <%= appPackage %>.di.components.ApplicationComponent;
import <%= appPackage %>.di.ForApplication;
import <%= appPackage %>.di.components.DaggerApplicationComponent;
import <%= appPackage %>.di.modules.AndroidModule;
import <%= appPackage %>.di.modules.ApplicationModule;
import <%= appPackage %>.environment.EnvironmentModule;
import <%= appPackage %>.util.gson.GsonModule;
import <%= appPackage %>.util.logging.CrashReportingTree;

import javax.inject.Inject;

<% if (calligraphy == true) { %>import uk.co.chrisjenx.calligraphy.CalligraphyConfig; <% } %>


public class App extends Application {

public static ApplicationComponent graph;

    @ForApplication
    @Inject
    Context context;

    @Inject
    EnvironmentConfiguration environmentConfiguration;

    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        LeakCanary.install(this);

        <% if (jodatime == true) { %>JodaTimeAndroid.init(this); <% } %>
        <% if (calligraphy == true) { %>CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Regular.ttf").setFontAttrId(R.attr.fontPath).build()); <% } %>

        graph = DaggerApplicationComponent.builder()
                        .androidModule(new AndroidModule())
                        .gsonModule(new GsonModule())
                        .applicationModule(new ApplicationModule(this))
                        .environmentModule(new EnvironmentModule(this))
                        .build();
        graph.inject(this);

        environmentConfiguration.configure();

    }

}
