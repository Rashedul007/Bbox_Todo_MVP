package bbox.com.bboxtodo_mvp.repository;

import java.util.List;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.models.Todo;
import dagger.Provides;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;


public interface Repository {

    Observable<List<Todo>> fetchToDosFromServer(String filterDate, String filterStatus,  String filterName);

    Observable<Todo> postTodoToServer(Todo todo);

    Observable<Todo> updateTodoInServer(int id, Todo todo);

    Call<Void> deleteTodoInServer(int id);


}
