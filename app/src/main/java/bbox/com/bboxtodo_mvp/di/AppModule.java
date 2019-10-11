package bbox.com.bboxtodo_mvp.di;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import bbox.com.bboxtodo_mvp.mvp.DetailMvp;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.mvp.model.MainModel;
import bbox.com.bboxtodo_mvp.mvp.presenter.DetailPresenter;
import bbox.com.bboxtodo_mvp.mvp.presenter.MainPresenter;
import bbox.com.bboxtodo_mvp.network.MainApi;
import bbox.com.bboxtodo_mvp.repository.MainRepository;
import bbox.com.bboxtodo_mvp.repository.Repository;
import bbox.com.bboxtodo_mvp.util.Constants;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance(){

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }




    @Singleton
    @Provides
    static MainApi provideMainApi(Retrofit retrofit){
        return retrofit.create(MainApi.class);
    }



 /*   @Singleton
    @Provides
    static MainRepository provideMainRepository(MainApi mainApi){
            return new MainRepository(mainApi, new SimpleDateFormat("yyyy-MM-dd"));

    }*/

    @Provides
    public Repository provideRepository(MainApi mainApi) {
        return new MainRepository(mainApi, new SimpleDateFormat("yyyy-MM-dd"));
    }


    @Provides
    public ToDoMvp.Presenter provideMainPresenter(ToDoMvp.Model mainModel) {
        return new MainPresenter(mainModel);
    }

    @Provides
    public ToDoMvp.Model provideMainModel(Repository repository) {
        return new MainModel(repository);
    }


//-------------- for Details MVP

    @Provides
    public DetailMvp.Presenter provideDetailPresenter(ToDoMvp.Model mainModel) {
        return new DetailPresenter(mainModel);
    }

   


}
