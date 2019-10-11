package bbox.com.bboxtodo_mvp.mvp;

import java.text.SimpleDateFormat;

import javax.inject.Singleton;

import bbox.com.bboxtodo_mvp.mvp.model.MainModel;
import bbox.com.bboxtodo_mvp.mvp.presenter.MainPresenter;
import bbox.com.bboxtodo_mvp.network.MainApi;
import bbox.com.bboxtodo_mvp.repository.MainRepository;
import bbox.com.bboxtodo_mvp.repository.Repository;
import dagger.Module;
import dagger.Provides;

//@Module
public class MainModule {
/*    @Provides
    public ToDoMvp.Presenter provideMainPresenter(ToDoMvp.Model mainModel) {
        return new MainPresenter(mainModel);
    }

    @Provides
    public ToDoMvp.Model provideMainModel(Repository repository) {
        return new MainModel(repository);
    }

    @Singleton
    @Provides
    public MainRepository provideRepo(MainApi mainApi) {
        return new MainRepository(mainApi, new SimpleDateFormat("yyyy-MM-dd"));
    }*/

}
