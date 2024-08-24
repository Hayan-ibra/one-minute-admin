package com.example.omadmin.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.example.omadmin.models.Orders;

import java.util.ArrayList;

public class MyDiffCallback extends DiffUtil.Callback {
        private final ArrayList<Orders> oldList;
        private final ArrayList<Orders> newList;

    public MyDiffCallback(ArrayList<Orders> oldList, ArrayList<Orders> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getTimeForDelete() == newList.get(newItemPosition).getTimeForDelete();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }


