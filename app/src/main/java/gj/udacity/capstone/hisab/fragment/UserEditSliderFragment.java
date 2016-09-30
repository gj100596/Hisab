package gj.udacity.capstone.hisab.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.gcm.RegistrationIntentService;

/**
 * Created by Gaurav on 11-09-2016.
 */

public class UserEditSliderFragment extends BottomSheetDialogFragment {

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

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.user_detail_dialog, null);
        dialog.setContentView(contentView);

        final EditText name = (EditText) contentView.findViewById(R.id.user_name);
        final EditText number = (EditText) contentView.findViewById(R.id.user_number);
        Button save = (Button) contentView.findViewById(R.id.user_save);

        final SharedPreferences userDetail = getActivity().
                getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);

        name.setText(userDetail.getString(getString(R.string.shared_pref_name),""));
        number.setText(userDetail.getString(getString(R.string.shared_pref_number),""));


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty()
                        && !number.getText().toString().isEmpty()
                        && number.getText().toString().length() == 10
                        ) {
                    SharedPreferences.Editor editor = userDetail.edit();
                    editor.putString(getString(R.string.shared_pref_name), name.getText().toString());
                    editor.putString(getString(R.string.shared_pref_number), number.getText().toString());
                    editor.apply();

                    Intent gcmToken = new Intent(MainActivity.thisAct, RegistrationIntentService.class);
                    getActivity().startService(gcmToken);
                }

                UserEditSliderFragment.this.dismiss();
            }
        });
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
}
