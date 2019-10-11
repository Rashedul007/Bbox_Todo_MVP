package bbox.com.bboxtodo_mvp.di;



import bbox.com.bboxtodo_mvp.mvp.view.DetailActivity;
import bbox.com.bboxtodo_mvp.mvp.view.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {


    @ContributesAndroidInjector(  /*modules = { MainViewModelModule.class }*/    )
     abstract MainActivity contributeMainActivity();


    @ContributesAndroidInjector(
            modules = { /*DetailViewModelModule.class */}
    )
    abstract DetailActivity contributeDetailActivity();
}
