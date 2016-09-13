package gj.udacity.capstone.hisab.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import gj.udacity.capstone.hisab.R;

import static gj.udacity.capstone.hisab.database.TransactionContract.BASE_URI;
import static gj.udacity.capstone.hisab.database.TransactionContract.Transaction;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class AddSliderFragment extends BottomSheetDialogFragment {

    private static final int CONTACT_INTENT_CODE = 300;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
    private EditText nameEditText,numberEditText;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.add_transaction, null);
        dialog.setContentView(contentView);

        nameEditText = (EditText) contentView.findViewById(R.id.add_name);
        numberEditText = (EditText) contentView.findViewById(R.id.add_number);
        final EditText reasonEditText = (EditText) contentView.findViewById(R.id.add_reason);
        final EditText amountEditText = (EditText) contentView.findViewById(R.id.add_amount);
        final Spinner categorySpinner = (Spinner) contentView.findViewById(R.id.add_category);
        final ImageView contactImageView = (ImageView) contentView.findViewById(R.id.add_contact);

        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, CONTACT_INTENT_CODE);
            }
        });

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.category_list));
        categorySpinner.setAdapter(categoryAdapter);

        Button saveButton = (Button) contentView.findViewById(R.id.add_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                //ID to be auto incremented
                //values.put(Transaction._ID, MovieID);
                values.put(Transaction.COLUMN_NAME, nameEditText.getText().toString());
                values.put(Transaction.COLUMN_REASON, reasonEditText.getText().toString());
                values.put(Transaction.COLUMN_AMOUNT, Integer.parseInt(amountEditText.getText().toString()));
                values.put(Transaction.COLUMN_SETTLED, 0);
                values.put(Transaction.COLUMN_CATEGORY, "Some");
                values.put(Transaction.COLUMN_DATE,"25-2-1996");

                getActivity().getContentResolver().insert(BASE_URI, values);
                AddSliderFragment.this.dismiss();
            }
        });

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK && requestCode == CONTACT_INTENT_CODE){
            Uri contactData = data.getData();

            Cursor cursor =  getActivity().getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();

            numberEditText.setText(cursor.
                    getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            nameEditText.setText(cursor.
                    getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        }
    }
}
