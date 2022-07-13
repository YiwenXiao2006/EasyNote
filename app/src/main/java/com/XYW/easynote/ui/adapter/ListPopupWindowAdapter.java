package com.XYW.easynote.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.XYW.easynote.R;

import java.util.List;

public class ListPopupWindowAdapter extends BaseAdapter {

    private final List<ListPopupItem> items;

    public ListPopupWindowAdapter(List<ListPopupItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListPopupItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popup_menu_item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.TextView_menu_title.setText(getItem(position).getTitle());
        holder.ImageView_menu_Icon.setImageResource(getItem(position).getImageRes());
        return convertView;
    }

    static class ViewHolder {
        TextView TextView_menu_title;
        ImageView ImageView_menu_Icon;

        ViewHolder(View view) {
            TextView_menu_title = view.findViewById(R.id.TextView_menu_title);
            ImageView_menu_Icon = view.findViewById(R.id.ImageView_menu_Icon);
        }
    }
}
