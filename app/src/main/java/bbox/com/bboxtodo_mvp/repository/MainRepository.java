package bbox.com.bboxtodo_mvp.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.network.MainApi;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;


public class MainRepository implements Repository{
    private static final String LOG_TAG = "shaon_mvvm";

    private MainApi mainApi;

    SimpleDateFormat dtFormatter;

   @Inject
    public MainRepository(MainApi mainApi, SimpleDateFormat dtFormatter) {
        this.mainApi = mainApi;
        this.dtFormatter = dtFormatter;
    }


    public Observable<List<Todo>> fetchToDosFromServer(String filterDate, String filterStatus,  String filterName)
    {
        Observable<List<Todo>> returnedData =     mainApi.getTodos().subscribeOn(Schedulers.io());


        if(!isStringNull(filterDate) || !isStringNull(filterStatus) || !isStringNull(filterName)) {

            if(isStringNull(filterDate))
                filterDate = "1800-01-01";

            return filterToDos(filterDate, filterStatus, filterName, returnedData);
        }

        return returnedData;

    }



    public Observable<Todo> postTodoToServer(Todo todo)
    {
        Observable<Todo> returnedData =    mainApi.createTodos(todo).subscribeOn(Schedulers.io());

        return returnedData;
    }


    public Observable<Todo> updateTodoInServer(int id, Todo todo)
    {
        Observable<Todo> returnedData =    mainApi.updateTodos(id, todo).subscribeOn(Schedulers.io());

        return returnedData;
    }

    public Call<Void> deleteTodoInServer(int id)
    {
        Call<Void> returnedData =    mainApi.deleteTodo(id);

        return returnedData;
    }



  public Observable<List<Todo>> filterToDos(final String filterDate, final String filterStatus, final String filterName, Observable<List<Todo>> returnedDatafromAPi)
       {
           Observable<List<Todo>> resultFilteredObservable =
                                   returnedDatafromAPi
                                           .subscribeOn(Schedulers.io())
                                           .flatMap(new Function<List<Todo>, ObservableSource<Todo>>() {
                                               @Override
                                               public ObservableSource<Todo> apply(List<Todo> todos) throws Exception {
                                                   return Observable.fromIterable(todos).subscribeOn(Schedulers.io());
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   Date datenow = dtFormatter.parse(filterDate);
                                                   Date dateTodo= dtFormatter.parse(todo.getExpiry_date());

                                                   return dateTodo.after(datenow);
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   if(filterStatus==null || filterStatus.equals(""))
                                                       return true;

                                                   return todo.getStatus().toLowerCase().equals(filterStatus.toLowerCase());
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   return todo.getName().toLowerCase().contains(filterName.toLowerCase());
                                               }
                                           })

                                           .toList()
                                           .toObservable()   ;

           return resultFilteredObservable;
    }



    public boolean isStringNull(String myString)
    {
        return myString.trim().length() == 0;

    }





}























