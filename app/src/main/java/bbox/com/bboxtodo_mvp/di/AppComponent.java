package bbox.com.bboxtodo_mvp.di;

import android.app.Application;

import javax.inject.Singleton;

import bbox.com.bboxtodo_mvp.BaseApplication;
import bbox.com.bboxtodo_mvp.mvp.MainModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityBuildersModule.class,
              //  MainModule.class
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder{

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}







