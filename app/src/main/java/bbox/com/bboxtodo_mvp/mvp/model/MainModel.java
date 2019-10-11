package bbox.com.bboxtodo_mvp.mvp.model;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.repository.MainRepository;
import bbox.com.bboxtodo_mvp.repository.Repository;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;

public class MainModel implements ToDoMvp.Model {

    private Repository repository;

    @Inject
    public MainModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<List<Todo>> result(String filterDate, String filterStatus, String filterName) {
       return repository.fetchToDosFromServer(filterDate,filterStatus, filterName);
    }


    @Override
    public Observable<Todo> addTodoResult(Todo todo) {
        return repository.postTodoToServer(todo);
    }


    @Override
    public Observable<Todo> updateTodoResult(int id, Todo todo) {
        return repository.updateTodoInServer(id, todo);
    }

    @Override
    public Call<Void> deleteTodoResult(int id) {
        return repository.deleteTodoInServer(id);
    }

}
