package bbox.com.bboxtodo_mvp.mvp;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.List;

import bbox.com.bboxtodo_mvp.models.Todo;
import dagger.Provides;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;

public interface ToDoMvp {

    interface View {

        void updateData(List<Todo> todos);

        void showSnackbar(String s);

        void refreshAdapter();

        void notifyDelete(int deletedPosition);

        void getFilterDialog(AlertDialog filterDialog);

        boolean isSwipeDeletable();

        void attachSwipeToRecyclerVw(ItemTouchHelper itemTouchHelper);

    }

    interface Presenter {

        void loadData(String filterDate, String filterStatus, String filterName);

        void rxUnsubscribe();

        void setView(ToDoMvp.View view);

        void addNewTodo();

        void mainAdapterMenuClick(String optionChoosen, Todo todo , String strIntentMode, boolean editModeEnabled , boolean menuEnabled);

        void deleteTodo(int id, final int deletedPosition);

        void updateTodo(int id, Todo todo);


       void setUpFilterDialogPresenter();


    }

    interface Model {

      Observable<List<Todo>> result(String filterDate, String filterStatus, String filterName);

      Observable<Todo> addTodoResult(Todo todo);

      Observable<Todo> updateTodoResult(int id, Todo todo);

      Call<Void> deleteTodoResult(int id);

    }
}

