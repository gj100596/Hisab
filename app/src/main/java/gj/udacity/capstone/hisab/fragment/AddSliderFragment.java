package gj.udacity.capstone.hisab.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import gj.udacity.capstone.hisab.R;

import static gj.udacity.capstone.hisab.database.TransactionContract.BASE_URI;
import static gj.udacity.capstone.hisab.database.TransactionContract.Transaction;

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

    private AutoCompleteTextView nameEditText;
    private EditText numberEditText,reasonEditText,amountEditText;
    private String[] categoryArray;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.add_transaction, null);
        dialog.setContentView(contentView);

        nameEditText = (AutoCompleteTextView) contentView.findViewById(R.id.add_name);
        numberEditText = (EditText) contentView.findViewById(R.id.add_number);
        reasonEditText = (EditText) contentView.findViewById(R.id.add_reason);
        amountEditText = (EditText) contentView.findViewById(R.id.add_amount);
        final Spinner categorySpinner = (Spinner) contentView.findViewById(R.id.add_category);
        final ImageView contactImageView = (ImageView) contentView.findViewById(R.id.add_contact);
        final RadioGroup transactionType = (RadioGroup) contentView.findViewById(R.id.radioGroup);

        final ArrayList<String> phone = new ArrayList<String>();

        ContentResolver cr = getActivity().getContentResolver();
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);
        }else{
            // Get The Contact List for suggestions
            Cursor contactCursor = cr
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (contactCursor.moveToNext()) {
                String contactName = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone.add(
                        contactName.split(" ")[0] + " " + contactNumber
                );
            }
            contactCursor.close();
        }
        // Get Name and Number of People added in our app but not in contact
        Cursor dbContact = cr
                .query(Transaction.getNameNoUri(), null, null, null, null);

        while (dbContact.moveToNext())
        {
            String contactName = dbContact.getString(0);
            String contactNumber = dbContact.getString(1);
            phone.add(
                    contactName.split(" ")[0]+" "+contactNumber
            );
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, phone);

        nameEditText.setAdapter(adapter);
        nameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String part[] = ((TextView)view).getText().toString().split(" ",2);

                nameEditText.setText(part[0]);
                numberEditText.setText((part[1]).replace(" ",""));
            }
        });

        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, CONTACT_INTENT_CODE);
            }
        });

        // Category List
        categoryArray = getResources().getStringArray(R.array.category_list);
        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categoryArray);
        categorySpinner.setAdapter(categoryAdapter);

        // Transaction Type
        transactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(amountEditText.getText().toString().isEmpty()){
                    amountEditText.setTextColor(getResources().getColor(R.color.sliderOptionBG));
                    amountEditText.setText("-");
                }
                else {
                    int amountVal = 0;
                    try {
                        amountVal = Integer.parseInt(amountEditText.getText().toString());
                    }catch(Exception e){
                        Toast.makeText(getActivity(),"Make Sure Amount Value is Integer",Toast.LENGTH_SHORT).show();
                    }
                    if (checkedId == R.id.takenRadio) {
                        if (amountVal > 0)
                            amountVal *= -1;
                        amountEditText.setTextColor(getResources().getColor(R.color.sliderOptionBG));
                    } else {
                        if (amountVal < 0)
                            amountVal *= -1;
                        amountEditText.setTextColor(getResources().getColor(R.color.materialGreen));
                    }
                    amountEditText.setText("" + amountVal);
                }
            }
        });


        //Save Transaction
        Button saveButton = (Button) contentView.findViewById(R.id.add_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkForEmpty()) {

                    ContentValues values = new ContentValues();
                    values.put(Transaction.COLUMN_NAME, nameEditText.getText().toString());
                    values.put(Transaction.COLUMN_NUMBER, numberEditText.getText().toString());
                    values.put(Transaction.COLUMN_REASON, reasonEditText.getText().toString());
                    values.put(Transaction.COLUMN_AMOUNT, Integer.parseInt(amountEditText.getText().toString()));
                    values.put(Transaction.COLUMN_SETTLED, 0);
                    values.put(Transaction.COLUMN_CATEGORY, categoryArray[categorySpinner.getSelectedItemPosition()]);
                    Calendar instance = Calendar.getInstance();
                    int month = instance.get(Calendar.MONTH);
                    String monthString = month < 10 ? ("0" + month) : (month + "");
                    String date = instance.get(Calendar.YEAR) + "-"
                            + monthString + "-"
                            + instance.get(Calendar.DAY_OF_MONTH);
                    values.put(Transaction.COLUMN_DATE, date);

                    getActivity().getContentResolver().insert(BASE_URI, values);
                    AddSliderFragment.this.dismiss();
                }
            }
        });

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private boolean checkForEmpty() {
        if(nameEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Please Enter Name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(numberEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Please Enter Mobile Number of Person",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(reasonEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Please Enter Reason",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(amountEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Please Enter Amount",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK){ // && requestCode == CONTACT_INTENT_CODE){
            Uri contactData = data.getData();

            Cursor cursor =  getActivity().getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();

            numberEditText.setText(
                    cursor
                        .getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .split(" ")[0]);
            nameEditText.setText(cursor.
                    getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        }
    }
}
