package com.example.omadmin.shops.bottomsheet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.omadmin.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UpdateItemBottomSheet extends BottomSheetDialogFragment {

    private MyCallback callback;

    public UpdateItemBottomSheet(MyCallback callback) {

        this.callback=callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_update_item, container, false);

        EditText edt_price=view.findViewById(R.id.bottom_sheet_update_item_price);
        EditText edit_quantity=view.findViewById(R.id.bottom_sheet_update_item_quantty);
        Button btn_update=view.findViewById(R.id.bottom_sheet_update_item_button_update);



       btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edit_quantity.getText().toString()) || !TextUtils.isEmpty(edt_price.getText().toString()) ){

                    if (!TextUtils.isEmpty(edit_quantity.getText().toString()) && TextUtils.isEmpty(edt_price.getText().toString())){
                        Long quantity =Long.valueOf(edit_quantity.getText().toString().trim());
                        callback.onSuccess(null,quantity);
                    } else if (!TextUtils.isEmpty(edt_price.getText().toString()) && TextUtils.isEmpty(edit_quantity.getText().toString())) {
                        Double price=Double.valueOf(edt_price.getText().toString().trim());
                        callback.onSuccess(price,null);

                    }else {
                        callback.onSuccess(Double.valueOf(edt_price.getText().toString().trim()),Long.valueOf(edit_quantity.getText().toString().trim()));
                    }


                    dismiss();


                }else {
                    Toast.makeText(getActivity(), "Please fill at least one field ", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }


    public interface MyCallback {
        void onSuccess(Double pricee,Long quantity);

    }


}
