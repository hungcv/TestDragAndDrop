package com.example.user.testdraganddrop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 7/5/2015.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public TextView textView;
    public int position;

    public ItemViewHolder(View itemView, int cellWidth, int cellHeight, @NonNull final OnDragItem onDragItem) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.img);
        textView = (TextView) itemView.findViewById(R.id.tvTitle);
        image.getLayoutParams().width = AppConstant.CELL_SIZE;
        image.getLayoutParams().height = AppConstant.CELL_SIZE;
        itemView.getLayoutParams().width = cellWidth;
        itemView.getLayoutParams().height = cellHeight;
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (image.getVisibility() == View.VISIBLE) {
                    onDragItem.onStartDrag(ItemViewHolder.this, position);
                }
                return false;
            }
        });
    }

    public ItemViewHolder(View itemView, @NonNull final OnDragItem onDragItem) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.img);
        textView = (TextView) itemView.findViewById(R.id.tvTitle);
        image.getLayoutParams().width = AppConstant.CELL_SIZE;
        image.getLayoutParams().height = AppConstant.CELL_SIZE;
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (image.getVisibility() == View.VISIBLE) {
                    onDragItem.onStartDrag(ItemViewHolder.this, position);
                }
                return false;
            }
        });
    }

    public void build(int position, ItemGrid itemGrid) {
        this.position = position;
        itemView.setVisibility(View.VISIBLE);
        itemView.clearAnimation();
        if (itemGrid.id == 0) {
            image.setImageDrawable(null);
            if (textView != null) {
                textView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (textView != null) {
                textView.setText(itemGrid.text);
                textView.setVisibility(View.VISIBLE);
            }
            image.setImageResource(itemGrid.icon);
        }
    }
}