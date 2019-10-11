package bbox.com.bboxtodo_mvp.mvp;

import java.util.List;

import bbox.com.bboxtodo_mvp.models.Todo;
import io.reactivex.Observable;

public interface DetailMvp {

    interface View {

        void updateData(Todo todos);

        void showSnackbar(String s);

        void setViewMode(String s);
        void setSaveButtonVisibility(int viewMode);
        void setMenuEnabled(boolean boln);

        void navigateToMain();

    }

    interface Presenter {
        void setView(DetailMvp.View view);


        void saveTodo(int id, Todo todo , String strIntentMode);

        void deleteTodo(int id);

        void rxUnsubscribe();

    }


}

