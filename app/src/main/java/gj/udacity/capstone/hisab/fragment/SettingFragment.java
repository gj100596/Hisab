package gj.udacity.capstone.hisab.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.activity.MainActivity;
import gj.udacity.capstone.hisab.util.Constant;
import gj.udacity.capstone.hisab.util.ServerRequest;

public class SettingFragment extends Fragment {
    TextView faq, feedback, TandC,backup,restore;
    Button delete;
    Switch pendingRem, notSound, notVibrate;
    public SharedPreferences optionPref;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        faq = (TextView) view.findViewById(R.id.faq);
        feedback = (TextView) view.findViewById(R.id.feedback);
        TandC = (TextView) view.findViewById(R.id.tandc);
        delete = (Button) view.findViewById(R.id.deleteAC);
        pendingRem = (Switch) view.findViewById(R.id.pendingRem);
        notSound = (Switch) view.findViewById(R.id.notSound);
        notVibrate = (Switch) view.findViewById(R.id.notVibrate);
        backup = (TextView) view.findViewById(R.id.backup);
        restore = (TextView) view.findViewById(R.id.restore);

      optionPref = getActivity()
                .getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);

        if (!optionPref.getBoolean(getString(R.string.pending_rem), true))
            pendingRem.setChecked(false);
        if (!optionPref.getBoolean(getString(R.string.not_sound), true))
            notSound.setChecked(false);
        if (!optionPref.getBoolean(getString(R.string.not_vibrate), true))
            notVibrate.setChecked(false);

        pendingRem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.stop_reminder_title));
                    builder.setMessage(getString(R.string.stop_reminder_msg));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = optionPref.edit();
                            editor.putBoolean(getString(R.string.pending_rem), false);
                            editor.apply();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pendingRem.setChecked(true);
                            SharedPreferences.Editor editor = optionPref.edit();
                            editor.putBoolean(getString(R.string.pending_rem), true);
                            editor.apply();
                        }
                    });
                    builder.show();
                }
                else{
                    SharedPreferences.Editor editor = optionPref.edit();
                    editor.putBoolean(getString(R.string.pending_rem), true);
                    editor.apply();
                }
            }
        });

        notSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = optionPref.edit();
                if (!isChecked)
                    editor.putBoolean(getString(R.string.not_sound), false);
                else
                    editor.putBoolean(getString(R.string.not_sound), true);

                editor.apply();
            }
        });

        notVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = optionPref.edit();
                if (!isChecked)
                    editor.putBoolean(getString(R.string.not_vibrate), false);
                else
                    editor.putBoolean(getString(R.string.not_vibrate), true);
                editor.apply();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "fortentia.askit.in@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.feed, new faqFragment())
                        .addToBackStack("FAQ")
                        .commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.delete_title));
                builder.setMessage(getString(R.string.delete_msg));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletAC();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), R.string.nice_choice, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
        });

        TandC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/site/fortentiahisab/"));
                startActivity(i);
            }
        });
        
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String inputPath = "/data/data/gj.udacity.capstone.hisab/databases/hisab.db";
                File folder = new File("/sdcard/Hisab");
                if(!folder.isDirectory()){
                    folder.mkdir();
                }
                final String outputPath = "/sdcard/Hisab/backup.db";
                File db = new File(outputPath);
                if(db.exists()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Backup");
                    builder.setMessage("Old Backup already exist. Do you want to overwrite it?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copyFile(inputPath, outputPath, "Backup");
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
                else {
                    copyFile(inputPath, outputPath, "Backup");
                }
            }
        });
        
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File folder = new File("/sdcard/Hisab");
                if(!folder.isDirectory()){
                    Toast.makeText(getActivity(),"No Backup Found",Toast.LENGTH_SHORT).show();
                }
                else {
                    String inputPath = "/sdcard/Hisab/backup.db";
                    String outputPath = "/data/data/gj.udacity.capstone.hisab/databases/hisab.db";
                    copyFile(inputPath, outputPath, "Restore");
                }
            }
        });
        
        return view;
    }

    private void copyFile(String inputFilePath, String outputFilePath,String type){

        try {

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(type);
            progressDialog.show();

            FileInputStream inputFile = new FileInputStream(inputFilePath);
            FileOutputStream outputFile = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputFile.read(buffer)) != -1) {
                outputFile.write(buffer, 0, bytesRead);
            }

            outputFile.flush();
            outputFile.close();
            inputFile.close();

            progressDialog.dismiss();
            Toast.makeText(getActivity(),type + " complete.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deletAC() {
        String url = Constant.url + "/hisab/removeid";
        StringRequest tokenRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = optionPref.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                SharedPreferences userDetail = MainActivity.thisAct.
                        getSharedPreferences(getString(R.string.user_shared_preef), Context.MODE_PRIVATE);
                param.put("UserID",optionPref
                        .getString(getString(R.string.shared_pref_number),getString(R.string.default_usernumber)));
                return param;
            }
        };
        ServerRequest.getInstance(getActivity()).getRequestQueue().add(tokenRequest);
    }

}
