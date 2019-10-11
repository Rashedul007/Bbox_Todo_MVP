package bbox.com.bboxtodo_mvp.mvp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.R;
import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import bbox.com.bboxtodo_mvp.util.VerticalSpacingItemDecorator;
import dagger.android.support.DaggerAppCompatActivity;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends DaggerAppCompatActivity implements ToDoMvp.View {

    @Inject
    ToDoMvp.Presenter presenter;

    ViewGroup rootView;
    RecyclerView mRecycleVw;
    public MainRecycleAdapter mAdapter;

    private boolean isSwipeDeleteable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize()
    {
        rootView = (ViewGroup)findViewById(R.id.main_content);

        mRecycleVw = (RecyclerView) findViewById(R.id.rclrVw);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecycleVw.setLayoutManager(gridLayoutManager);
        mRecycleVw.addItemDecoration(new VerticalSpacingItemDecorator(10));

        mAdapter = new MainRecycleAdapter(this);
        mRecycleVw.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MainRecycleAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, Todo mBeanObj) {

                switch(view.getId())
                {
                    case(R.id.btn_Edit):

                       presenter.mainAdapterMenuClick("edit" , mBeanObj, DetailActivity.IntentMode.TOVIEW.name(), true, false );

                        break;

                    case(R.id.card_view):
                        Log.d("TEstIDLog", "act sw:: "+ R.id.btn_Edit);
                        presenter.mainAdapterMenuClick("view" , mBeanObj, DetailActivity.IntentMode.TOVIEW.name(), false, true );
                        break;

                    case(R.id.chkBox_todoComplete):
                      if( ((CheckBox)view).isChecked())
                          mBeanObj.setStatus("Completed");
                      else
                          mBeanObj.setStatus("todo");

                      presenter.updateTodo(Integer.valueOf(mBeanObj.getId()), mBeanObj);

                        break;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.setView(this);
        presenter. loadData("","","");
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.rxUnsubscribe();
    }


    @Override
    public void updateData(List<Todo> todos) {
       mAdapter.setTodosInAdapter(todos);
    }

    @Override
    public void showSnackbar(String msg) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void refreshAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    //fab click
    public void addNewTodo(View view) {
        presenter.addNewTodo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                isSwipeDeleteable = false;
                presenter.setUpFilterDialogPresenter();

                return true;

            case R.id.menu_refresh:
                isSwipeDeleteable = true;
                presenter.loadData("", "", "");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public void getFilterDialog(AlertDialog filterDialog)
    {
        filterDialog.show();
    }


    @Override
    public void attachSwipeToRecyclerVw(ItemTouchHelper itemTouchHelper)
    {
        itemTouchHelper.attachToRecyclerView(mRecycleVw);
    }


    @Override
   public void notifyDelete(int deletedPosition)
    {
        mAdapter.notifyDeleted(deletedPosition);

    }

    @Override
    public boolean isSwipeDeletable()
    {
        return isSwipeDeleteable;
    }


}
