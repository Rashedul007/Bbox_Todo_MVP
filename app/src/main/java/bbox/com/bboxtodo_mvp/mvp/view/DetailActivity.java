package bbox.com.bboxtodo_mvp.mvp.view;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import bbox.com.bboxtodo_mvp.R;
import bbox.com.bboxtodo_mvp.models.Todo;
import bbox.com.bboxtodo_mvp.mvp.DetailMvp;
import bbox.com.bboxtodo_mvp.mvp.ToDoMvp;
import dagger.android.support.DaggerAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends DaggerAppCompatActivity implements DetailMvp.View ,View.OnClickListener {
    @Inject
    DetailMvp.Presenter presenter;

    private final String LOG_TAG = "TodoDetailAct";

//region.. declare variables
    ViewGroup rootView;
    EditText edTxtName, edTxtDesc;
    TextView txtExpireDate, txtVwId;
    RadioGroup rdGroupStatus;
    RadioButton rdBtnTodo, rdBtnCompleted;
    Button btnSave;

    boolean isEditModeOn = false;
    boolean isMenuEnabled = false;
    String strIntentMode="";

   public enum IntentMode
    {
        TOADD, TOVIEW;
    }


    private int mYear, mMonth, mDay;

//endregion

    @Override
    public void updateData(Todo todo) {
        if (todo != null) {
            edTxtName.setText(todo.getName());
            txtExpireDate.setText(todo.getExpiry_date());

            if(todo.getStatus().toLowerCase().contains("completed"))
                rdBtnCompleted.setChecked(true);
            else
                rdBtnTodo.setChecked(true);


            edTxtDesc.setText("" + todo.getDescription());
            txtVwId.setText("" + todo.getId());

            disbleEditing();
        }
    }


    @Override
    public void showSnackbar(String msg) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setViewMode(String str) {
       strIntentMode = str;
    }

    @Override
    public void setSaveButtonVisibility(int viewMode) {
        btnSave.setVisibility(viewMode);
    }

    @Override
    public void setMenuEnabled(boolean boln) {
        isMenuEnabled = boln;
        invalidateOptionsMenu();
    }

    @Override
    public void navigateToMain() {
        NavUtils.navigateUpFromSameTask(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
        getIntentFromMain();

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.setView(this);
    }

    private void initialize() {

        rootView = (ViewGroup)findViewById(R.id.detailRootView);
        edTxtName = (EditText) findViewById(R.id.edTxtVw_Detail_Name);
        txtExpireDate = (TextView) findViewById(R.id.txtVw_Detail_ExpDate);

         rdGroupStatus = (RadioGroup) findViewById(R.id.radioGpStatus);
         rdBtnTodo = (RadioButton)findViewById(R.id.radBtnTodo) ;
         rdBtnCompleted = (RadioButton)findViewById(R.id.radBtnCompleted);

        edTxtDesc = (EditText) findViewById(R.id.edTxtVw_Detail_Desc);

        txtVwId = (TextView) findViewById(R.id.txtVw_Desc_Id);

        btnSave = (Button) findViewById(R.id.btn_Detail_Save);

        txtExpireDate.setOnClickListener(this);

        disbleEditing();


    }

    private void getIntentFromMain()
    {
        if(getIntent().hasExtra("intent_Main_mode"))
        {
            strIntentMode = (getIntent().getStringExtra("intent_Main_mode"));

            isEditModeOn = getIntent().getBooleanExtra("intent_Main_setEditModeEnabled", false);
            isMenuEnabled = getIntent().getBooleanExtra("intent_Main_setMenuEnabled", false);

            if(strIntentMode.equals(IntentMode.TOVIEW.name()))
            {   Todo intentTodo = (Todo) getIntent().getParcelableExtra("intent_Main_obj");

                edTxtName.setText(intentTodo.getName());
                txtExpireDate.setText(intentTodo.getExpiry_date());
                edTxtDesc.setText(intentTodo.getDescription());
                txtVwId.setText(String.valueOf(intentTodo.getId()));

                if(intentTodo.getStatus().toLowerCase().contains("completed"))
                    rdBtnCompleted.setChecked(true);
                else
                    rdBtnTodo.setChecked(true);
            }

            if(isEditModeOn) {
                enableEditing();
                btnSave.setVisibility(View.VISIBLE);            }
            else
                btnSave.setVisibility(View.GONE);


        }
    }


    public void clkSave(View view) {

            int radioButtonId_date= rdGroupStatus.getCheckedRadioButtonId();
            RadioButton mRdBtn_Sts = (RadioButton)findViewById(radioButtonId_date);

            Todo objToDo = new Todo(0, edTxtName.getText().toString(), mRdBtn_Sts.getText().toString(), edTxtDesc.getText().toString(), txtExpireDate.getText().toString());

            presenter.saveTodo(Integer.valueOf(txtVwId.getText().toString()) ,objToDo ,strIntentMode);
    }


//------ for calender
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtVw_Detail_ExpDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txtExpireDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }


    private void disbleEditing() {
        disableEditText(edTxtName);
        disableEditText(edTxtDesc);
        rdBtnCompleted.setEnabled(false);
        rdBtnTodo.setEnabled(false);
        txtExpireDate.setEnabled(false);
        txtExpireDate.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditing() {
        isEditModeOn = true;
        enableEditText(edTxtName);
        enableEditText(edTxtDesc);
        rdBtnCompleted.setEnabled(true);
        rdBtnTodo.setEnabled(true);

        edTxtName.requestFocus();
        txtExpireDate.setEnabled(true);
        txtExpireDate.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem));
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        if(isMenuEnabled == false){
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }else{
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                    enableEditing();
                    btnSave.setVisibility(View.VISIBLE);
                return true;

            case R.id.menu_delete:
                 presenter.deleteTodo(Integer.valueOf(txtVwId.getText().toString()));
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("blnIsEditModeOn", isEditModeOn);
        savedInstanceState.putBoolean("blnIsMenuEnabled", isMenuEnabled);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        isEditModeOn = savedInstanceState.getBoolean("blnIsEditModeOn");
        isMenuEnabled = savedInstanceState.getBoolean("blnIsMenuEnabled");

    }


    @Override
    protected void onStop() {
        super.onStop();
        presenter.rxUnsubscribe();


    }


}
