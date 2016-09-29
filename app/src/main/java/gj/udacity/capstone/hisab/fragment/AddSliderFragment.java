package gj.udacity.capstone.hisab.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.util.FlowLayout;

import static android.view.View.inflate;
import static gj.udacity.capstone.hisab.database.TransactionContract.BASE_URI;
import static gj.udacity.capstone.hisab.database.TransactionContract.Transaction;
import static java.lang.Integer.parseInt;

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

    private MultiAutoCompleteTextView nameEditText;
    private EditText numberEditText, reasonEditText, amountEditText;
    private String[] categoryArray;
    private FlowLayout selectedContact;
    private ArrayAdapter<String> adapter;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = inflate(getContext(), R.layout.add_transaction, null);
        dialog.setContentView(contentView);

        nameEditText = (MultiAutoCompleteTextView) contentView.findViewById(R.id.add_name);
        numberEditText = (EditText) contentView.findViewById(R.id.add_number);
        reasonEditText = (EditText) contentView.findViewById(R.id.add_reason);
        amountEditText = (EditText) contentView.findViewById(R.id.add_amount);
        final Switch meInGroupSwitch = (Switch) contentView.findViewById(R.id.meInGroup);
        final Spinner categorySpinner = (Spinner) contentView.findViewById(R.id.add_category);
        final RadioGroup transactionType = (RadioGroup) contentView.findViewById(R.id.radioGroup);

        final ArrayList<String> phone = new ArrayList<String>();

        ContentResolver cr = getActivity().getContentResolver();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);
        } else {
            // Get The Contact List for suggestions
            Cursor contactCursor = cr
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (contactCursor.moveToNext()) {
                String contactName = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                //Remove any non numeric character like - or ( or )
                contactNumber.replaceAll("[^\\d.]", "");

                String newNo = contactNumber.replace(" ", "");
                if (newNo.length() > 9) {
                    newNo = newNo.substring(newNo.length() - 10);

                    String concat = contactName.split(" ")[0] + " " + newNo;
                    if (!phone.contains(concat))
                        phone.add(concat);
                }
                else{
                    Log.e("HLOG","Number less than 10 digit");
                }
            }
            contactCursor.close();
        }
        // Get Name and Number of People added in our app but not in contact
        Cursor dbContact = cr
                .query(Transaction.getNameNoUri(), null, null, null, null);

        while (dbContact.moveToNext()) {
            String contactName = dbContact.getString(0);
            String contactNumber = dbContact.getString(1);
            String concat = contactName.split(" ")[0] + " " + contactNumber;
            if (!phone.contains(concat))
                phone.add(concat);
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, phone);

        selectedContact = (FlowLayout) contentView.findViewById(R.id.selectedContact);

        nameEditText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        nameEditText.setAdapter(adapter);
        nameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String text = ((TextView) view).getText().toString();
                String part[] = text.split(" ", 2);

                View tag = View.inflate(getContext(), R.layout.view_sample_tag, null);

                ViewGroup.MarginLayoutParams m = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                m.setMargins(5, 5, 5, 5);

                tag.setLayoutParams(m);
                TextView name = (TextView) tag.findViewById(R.id.name);
                TextView no = (TextView) tag.findViewById(R.id.no);
                ImageView close = (ImageView) tag.findViewById(R.id.close);
                close.setTag(text);

                name.setText(part[0]);
                no.setText(part[1]);
                selectedContact.addView(tag);
                if(selectedContact.getChildCount()>1){
                    meInGroupSwitch.setVisibility(View.VISIBLE);
                }
                else{
                    meInGroupSwitch.setVisibility(View.GONE);
                }

                int index = phone.indexOf(text);
                if(index!=-1)
                    phone.remove(index);
                adapter = null;
                adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, phone);
                nameEditText.setAdapter(adapter);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedContact.removeView((View) v.getParent());
                        if(selectedContact.getChildCount()>1){
                            meInGroupSwitch.setVisibility(View.VISIBLE);
                        }
                        else{
                            meInGroupSwitch.setVisibility(View.GONE);
                        }
                        phone.add(v.getTag().toString());
                        adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line, phone);
                        adapter.notifyDataSetChanged();
                        nameEditText.setAdapter(adapter);
                    }
                });
                nameEditText.setText("");

            }
        });

        // Add New person's detail
        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inp = numberEditText.getText().toString();
                if(inp.length() == 10){
                    View tag = View.inflate(getContext(), R.layout.view_sample_tag, null);
                    String nameString = nameEditText.getText().toString();
                    String noString = numberEditText.getText().toString();

                    ViewGroup.MarginLayoutParams m = new ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    m.setMargins(5, 5, 5, 5);

                    tag.setLayoutParams(m);
                    TextView name = (TextView) tag.findViewById(R.id.name);
                    TextView no = (TextView) tag.findViewById(R.id.no);
                    ImageView close = (ImageView) tag.findViewById(R.id.close);

                    name.setText(nameString);
                    no.setText(noString);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedContact.removeView((View) v.getParent());
                            if(selectedContact.getChildCount()>1){
                                meInGroupSwitch.setVisibility(View.VISIBLE);
                            }
                            else{
                                meInGroupSwitch.setVisibility(View.GONE);
                            }
                        }
                    });

                    selectedContact.addView(tag);
                    if(selectedContact.getChildCount()>1){
                        meInGroupSwitch.setVisibility(View.VISIBLE);
                    }
                    else{
                        meInGroupSwitch.setVisibility(View.GONE);
                    }
                    nameEditText.setText("");
                    numberEditText.setText("");
                }
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
                if (amountEditText.getText().toString().isEmpty()) {
                    amountEditText.setTextColor(getResources().getColor(R.color.sliderOptionBG));
                    amountEditText.setText("-");
                } else {
                    int amountVal = 0;
                    try {
                        amountVal = parseInt(amountEditText.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.warning, Toast.LENGTH_SHORT).show();
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

                if (checkForEmpty()) {
                    String reason = reasonEditText.getText().toString();
                    int amount = Integer.parseInt(amountEditText.getText().toString());
                    String category = categoryArray[categorySpinner.getSelectedItemPosition()];
                    Calendar instance = Calendar.getInstance();
                    int month = instance.get(Calendar.MONTH);
                    String monthString = month < 10 ? ("0" + month) : (month + "");
                    int dayOfMonth = instance.get(Calendar.DAY_OF_MONTH);
                    String dayString = dayOfMonth<10?("0"+dayOfMonth):(""+dayOfMonth);
                    String date = instance.get(Calendar.YEAR) + "-"
                            + monthString + "-"
                            + dayOfMonth;
                    int totalChild = selectedContact.getChildCount();
                    if(totalChild>1 && meInGroupSwitch.isChecked())
                        totalChild++;
                    amount/=totalChild;

                    for(int i=0;i<selectedContact.getChildCount();i++) {
                        View view = selectedContact.getChildAt(i);
                        TextView name = (TextView) view.findViewById(R.id.name);
                        TextView no = (TextView) view.findViewById(R.id.no);

                        ContentValues values = new ContentValues();
                        values.put(Transaction.COLUMN_NAME, name.getText().toString());
                        values.put(Transaction.COLUMN_NUMBER, no.getText().toString());
                        values.put(Transaction.COLUMN_REASON, reason);
                        values.put(Transaction.COLUMN_AMOUNT, amount);
                        values.put(Transaction.COLUMN_SETTLED, 0);
                        values.put(Transaction.COLUMN_CATEGORY, category);

                        values.put(Transaction.COLUMN_DATE, date);

                        getActivity().getContentResolver().insert(BASE_URI, values);
                    }

                    AddSliderFragment.this.dismiss();
                }
            }
        });

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    private boolean checkForEmpty() {

        if (selectedContact.getChildCount()== 0) {
            Toast.makeText(getActivity(), R.string.name_no_warning, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reasonEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), R.string.reason_warning, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amountEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), R.string.amount_warning, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
