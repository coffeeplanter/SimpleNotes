package ru.solovyov.ilya.simplenotes;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

// Класс обработки множественного выбора элементов ListView

class MultiChoiceImplementation implements AbsListView.MultiChoiceModeListener {

    private static final String TAG = "MultiChoiceModeListener";
    private AbsListView listView;
    private MainActivity parentActivity;

    MultiChoiceImplementation(Context context, ListView listView) {
        this.listView = listView;
        this.parentActivity = (MainActivity) context;
    }

    @Override
    // Метод вызывается при любом изменения состояния выделения рядов
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        Log.d(TAG, "onItemCheckedStateChanged");
        int selectedCount = listView.getCheckedItemCount();
        // Добавим количество выделенных элементов в Context Action Bar
        setSubtitle(actionMode, selectedCount);
    }

    @Override
    //Здесь надуваем наше меню из xml
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        Log.d(TAG, "onCreateActionMode");
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        Log.d(TAG, "onPrepareActionMode");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_all_action:
                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                // Перебираем с конца, чтобы не нарушать порядок нумерации в списке
                for (int i = (sparseBooleanArray.size() - 1); i >= 0; i--) {
                    if (sparseBooleanArray.valueAt(i)) {
                        parentActivity.notes.remove(sparseBooleanArray.keyAt(i));
                    }
                }
                actionMode.finish();
                parentActivity.adapter.notifyDataSetChanged();
                Toast.makeText(parentActivity, R.string.deleted_successfully_toast, Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        Log.d(TAG, "onDestroyActionMode");
    }

    // Установка количества выбранных пунктов в Title
    private void setSubtitle(ActionMode mode, int selectedCount) {
        switch (selectedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            default:
                mode.setTitle(SimpleNotesApplication.getResourceStringNotesSelectedNumber() + String.valueOf(selectedCount));
                break;
        }
    }

}
