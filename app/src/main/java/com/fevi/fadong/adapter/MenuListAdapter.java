package com.fevi.fadong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fevi.fadong.R;
import com.fevi.fadong.adapter.dto.MenuList;

import java.util.List;

/**
 * Created by 1000742 on 15. 1. 2..
 */
public class MenuListAdapter extends ArrayAdapter<MenuList> {
    private Context context;
    private List<MenuList> menuLists;

    public MenuListAdapter(Context context, int resource, List<MenuList> menuLists) {
        super(context, resource, menuLists);
        this.context = context;
        this.menuLists = menuLists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        AdapterHolder adapterHolder = new AdapterHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.drawer_list_item, null);
        }

        MenuList menuList = menuLists.get(position);
        adapterHolder.imageView = (ImageView) v.findViewById(R.id.imageView);
        adapterHolder.textView = (TextView) v.findViewById(R.id.textView);

        adapterHolder.textView.setText(menuList.getMenuName());

        return v;
    }

    @Override
    public int getCount() {
        return menuLists.size();
    }

    @Override
    public MenuList getItem(int position) {
        return menuLists.get(position);
    }

    class AdapterHolder {
        ImageView imageView;
        TextView textView;
    }


}
