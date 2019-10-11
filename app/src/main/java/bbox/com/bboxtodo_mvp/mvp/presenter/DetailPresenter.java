package bbox.com.bboxtodo_mvp.mvp.presenter;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.DetailMvp;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.mvp.view.DetailActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPresenter implements DetailMvp.Presenter {

    private DetailMvp.View view;
    private ToDoMvp.Model model;

    private Disposable subscription = null;

    @Inject
    public DetailPresenter(ToDoMvp.Model model) {
        this.model = model;
    }

    @Override
    public void saveTodo(int id, Todo todo, String strIntentMode) {

        if( isStringEmpty(todo.getName()) || isStringEmpty(todo.getStatus())  || isStringEmpty(todo.getDescription()) || isStringEmpty(todo.getExpiry_date()))
            view.showSnackbar("Please enter all values!");
        else{


            if (strIntentMode.equals(DetailActivity.IntentMode.TOADD.name())) //for adding
            {
                subscription =  model.addTodoResult(todo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Todo>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (view != null)
                                    view.showSnackbar("Error adding Todos");
                            }

                            @Override
                            public void onNext(Todo todo) {
                                view.updateData(todo);
                                view.showSnackbar("Added successfully");
                            }
                        });


                view.setViewMode(DetailActivity.IntentMode.TOVIEW.name());
                view.setSaveButtonVisibility(View.GONE);
                 }

            else if (strIntentMode.equals(DetailActivity.IntentMode.TOVIEW.name())) {   //for updating
                subscription =  model.updateTodoResult(id, todo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Todo>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (view != null)
                                    view.showSnackbar("Error updating Todos");
                            }

                            @Override
                            public void onNext(Todo todo) {
                                view.updateData(todo);
                                view.showSnackbar("Updated successfully");
                            }
                        });

                view.setSaveButtonVisibility(View.GONE);
            }

            view.setMenuEnabled(true);
        }
            
    }

    @Override
    public void deleteTodo(int id) {
        Call<Void> call = model.deleteTodoResult(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                    Toast.makeText(((Context) view), "Delete Success", Toast.LENGTH_SHORT).show();
                    view.navigateToMain();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
     public void setView(DetailMvp.View view) {
        this.view = view;
    }
    private boolean isStringEmpty(String str) {
        return str.trim().length() == 0;
    }



    @Override
    public void rxUnsubscribe() {
        if (subscription != null) {
            if (!subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

}
