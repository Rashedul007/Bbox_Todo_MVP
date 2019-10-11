package bbox.com.bboxtodo_mvp.network;

import java.util.List;
import bbox.com.bboxtodo_mvp.models.Todo;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MainApi {

    // get Todos
     @GET("todos.json")
     Observable<List<Todo>> getTodos();


    // new Todos
    @POST("todos.json")
    Observable<Todo> createTodos(@Body Todo objTodo);

    // update Todos
    @PATCH("todos/{id}.json")
    Observable<Todo> updateTodos(@Path("id") int id, @Body Todo objTodo);

    // delete Todos
    @DELETE("todos/{id}.json")
    Call<Void> deleteTodo(@Path("id") int id);




}
