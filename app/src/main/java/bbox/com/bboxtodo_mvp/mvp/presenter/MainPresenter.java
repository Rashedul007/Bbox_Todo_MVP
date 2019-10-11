package bbox.com.bboxtodo_mvp.mvp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.R;
import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.mvp.view.DetailActivity;
import bbox.com.bboxtodo_mvp.mvp.view.MainActivity;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.startActivity;

public class MainPresenter implements ToDoMvp.Presenter {

    private ToDoMvp.View view;
    private ToDoMvp.Model model;
    private Disposable subscription = null;

    List<Todo>  listTodo= new ArrayList<Todo>();


    @Inject
    public MainPresenter(ToDoMvp.Model model) {
        this.model = model;
    }

    @Override
    public void loadData(String filterDate, String filterStatus, String filterName) {
        listTodo.clear();
        subscription =    model.result(  filterDate,  filterStatus,  filterName)
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<List<Todo>, ObservableSource<Todo>>() {
                    @Override
                    public ObservableSource<Todo> apply(List<Todo> todos) throws Exception {
                          return Observable.fromIterable(todos).subscribeOn(Schedulers.io());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Todo>() {
                    @Override
                    public void onComplete() {
                        if (view != null)
                               view.updateData(listTodo);

                        swipeRecyclerDelete(listTodo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view != null)
                            view.showSnackbar("Error getting Todos");
                    }

                    @Override
                    public void onNext(Todo todo) {
                        listTodo.add(todo);
                    }
                });


    }

    @Override
    public void rxUnsubscribe() {
        if (subscription != null) {
            if (!subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    @Override
    public void setView(ToDoMvp.View view) {
        this.view = view;
    }

    @Override
    public void addNewTodo() {
        Intent intentMain = new Intent((Context) view, DetailActivity.class);

        intentMain.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOADD.name());
        intentMain.putExtra("intent_Main_setEditModeEnabled", true);
        intentMain.putExtra("intent_Main_setMenuEnabled", false);
        ((Context) view).startActivity(intentMain);
    }



    @Override
    public void mainAdapterMenuClick(String optionChoosen, Todo todo , String strIntentMode, boolean editModeEnabled , boolean menuEnabled) {
        Intent intentMain = new Intent((Context) view, DetailActivity.class);

        intentMain.putExtra("intent_Main_obj", todo);
        intentMain.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOVIEW.name());
        intentMain.putExtra("intent_Main_setEditModeEnabled", editModeEnabled);
        intentMain.putExtra("intent_Main_setMenuEnabled", menuEnabled);

        ((Context) view).startActivity(intentMain);
    }

    @Override
   public void updateTodo(int id, Todo todo)
    {
        model.updateTodoResult(id, todo)
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
                        view.showSnackbar("Updated successfully");
                        view.refreshAdapter();
                    }
                });
    }


    @Override
    public void deleteTodo(int id, final int deletedPosition) {
        Call<Void> call = model.deleteTodoResult(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                {
                    view.showSnackbar("Successfully deleted");
                  view.notifyDelete(deletedPosition); }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    public void  setUpFilterDialogPresenter()
    {

        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder((Context)view);

        LayoutInflater li = LayoutInflater.from((Context)view);
        final View promptsView = li.inflate(R.layout.dialog_filter, null);

        mAlertBuilder.setPositiveButton("ok", null);
        mAlertBuilder.setNegativeButton("cancel", null);
        mAlertBuilder.setView(promptsView);

        final RadioGroup mRadioGroupDate = (RadioGroup) promptsView.findViewById(R.id.radioGroupDate);
        final RadioGroup mRadioGroupStatus = (RadioGroup) promptsView.findViewById(R.id.radioGroupStatus);
        final EditText mEdTxtVwName = (EditText) promptsView.findViewById(R.id.edDlName);

        final AlertDialog mAlertDialog = mAlertBuilder.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btnDialog_positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnDialog_positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int radioButtonId_date= mRadioGroupDate.getCheckedRadioButtonId();
                        RadioButton mRdBtn_Date = (RadioButton)promptsView.findViewById(radioButtonId_date);

                        int radioButtonId_status = mRadioGroupStatus.getCheckedRadioButtonId();
                        RadioButton mRdBtn_status = (RadioButton)promptsView.findViewById(radioButtonId_status);

                        String strdt = "";

                        if(radioButtonId_date == R.id.rdBtnWeek)
                            strdt = getCalculatedDate(-7);
                        else if(radioButtonId_date == R.id.rdBtnMonth)
                            strdt = getCalculatedDate(-30);
                        else if(radioButtonId_date == R.id.rdBtn6Month)
                            strdt = getCalculatedDate(-180);


                        String strStatus="";
                        if(mRdBtn_status != null)
                            strStatus = mRdBtn_status.getText().toString();

                       loadData(strdt, strStatus, mEdTxtVwName.getText().toString());

                        // mainViewModel.getToDosFromServer(strdt, strRd, mEdTxtVwName.getText().toString());

                        mAlertDialog.dismiss();

                    }
                });
            }
        });
      //  mAlertDialog.show();

        view.getFilterDialog(mAlertDialog);


    }


    public static String getCalculatedDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, days);
        Date newDate = calendar.getTime();


        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        return s.format(newDate);
    }



    public void swipeRecyclerDelete(final List<Todo> todos)
    {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT )
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                if(!view.isSwipeDeletable())
                    return false;

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    final int position = viewHolder.getAdapterPosition();
                    deleteTodo(todos.get(position).getId() , position);

                } catch(Exception e) {
                    Log.e("MainActivity", e.getMessage());
                }
            }



            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor((Context)view, R.color.recycler_view_item_swipe_right_background))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .addSwipeRightLabel(((Context)view).getString(R.string.action_delete))
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        view.attachSwipeToRecyclerVw(itemTouchHelper);
        //itemTouchHelper.attachToRecyclerView(mRecycleVw);
    }


}
