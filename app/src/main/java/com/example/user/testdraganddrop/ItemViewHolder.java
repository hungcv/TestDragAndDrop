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
    public View frame;
    public TextView textView;
    public int position;

    public ItemViewHolder(View itemView, int cellWidth, int cellHeight, @NonNull final OnDragItem onDragItem) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.img);
        frame = itemView.findViewById(R.id.frame_view);
        textView = (TextView) itemView.findViewById(R.id.tvTitle);
        frame.getLayoutParams().width = AppConstant.CELL_SIZE;
        frame.getLayoutParams().height = AppConstant.CELL_SIZE;
        image.getLayoutParams().width = AppConstant.CELL_SIZE;
        image.getLayoutParams().height = AppConstant.CELL_SIZE;
        image.invalidate();
        itemView.getLayoutParams().width = cellWidth;
        itemView.getLayoutParams().height = cellHeight;
        itemView.invalidate();
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
        frame = itemView.findViewById(R.id.frame_view);
        textView = (TextView) itemView.findViewById(R.id.tvTitle);
        frame.getLayoutParams().width = AppConstant.CELL_SIZE;
        frame.getLayoutParams().height = AppConstant.CELL_SIZE;
        image.getLayoutParams().width = AppConstant.CELL_SIZE;
        image.getLayoutParams().height = AppConstant.CELL_SIZE;
        image.invalidate();
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
        itemView.clearAnimation();
        showFrame(false);
        if (itemGrid.id == 0) {
            image.setImageDrawable(null);
        } else {
            if (textView!=null){
                textView.setText(itemGrid.text);
            }
            image.setImageResource(itemGrid.icon);
        }
    }

    public void showFrame(boolean showFrame) {
        if (showFrame) {
            frame.setVisibility(View.VISIBLE);
            image.setVisibility(View.INVISIBLE);
            if (textView != null) {
                textView.setVisibility(View.INVISIBLE);
            }
        } else {
            frame.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
            if (textView != null) {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}