package ru.geekbrains.lesson7.simplenotes;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Класс обработки множественного выбора элементов ListView

public class MultiChoiceImplementation implements AbsListView.MultiChoiceModeListener {

    private AbsListView listView;

    public MultiChoiceImplementation(ListView listView) {
        this.listView = listView;
    }

    @Override
    //Метод вызывается при любом изменения состояния выделения рядов
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        Log.d(MainActivity.TAG, "onItemCheckedStateChanged");
        int selectedCount = listView.getCheckedItemCount();
        //Добавим количество выделенных рядов в Context Action Bar
        setSubtitle(actionMode, selectedCount);
    }

    @Override
    //Здесь надуваем наше меню из xml
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        Log.d(MainActivity.TAG, "onCreateActionMode");
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        Log.d(MainActivity.TAG, "onPrepareActionMode");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        Toast.makeText(listView.getContext(), "Action - " + menuItem.getTitle() + " ; Selected items: ", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        Log.d(MainActivity.TAG, "onDestroyActionMode");
    }

    // Установка количества выбранных пунктов в Title
    private void setSubtitle(ActionMode mode, int selectedCount) {
        switch (selectedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            default:
                mode.setTitle(String.valueOf(selectedCount));
                break;
        }
    }

}
