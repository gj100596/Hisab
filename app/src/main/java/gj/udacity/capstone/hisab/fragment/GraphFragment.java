package gj.udacity.capstone.hisab.fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.database.TransactionContract;

public class GraphFragment extends Fragment {

    EditText startDate,endDate;
    PieChart expensePie;


    public GraphFragment() {
    }

    public static GraphFragment newInstance() {
        return new GraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        startDate = (EditText) view.findViewById(R.id.startDate);
        endDate = (EditText) view.findViewById(R.id.endDate);
        expensePie = (PieChart) view.findViewById(R.id.expensePie);


        ImageView startCalendar = (ImageView) view.findViewById(R.id.startCalendar);
        final ImageView endCalendar = (ImageView) view.findViewById(R.id.endCalendar);
        Button go = (Button) view.findViewById(R.id.dateQuery);

        Calendar instance = Calendar.getInstance();
        final int month = instance.get(Calendar.MONTH);
        final int day = instance.get(Calendar.DAY_OF_MONTH);
        final int year = instance.get(Calendar.YEAR);

        startCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = monthOfYear<10?("0"+monthOfYear):(""+monthOfYear);
                        String day = dayOfMonth<10?("0"+dayOfMonth):(""+dayOfMonth);
                        startDate.setText(year+"-"+month+"-"+day);
                    }
                },year,month,day).show();
            }
        });
        endCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = monthOfYear<10?("0"+monthOfYear):(""+monthOfYear);
                        String day = dayOfMonth<10?("0"+dayOfMonth):(""+dayOfMonth);
                        endDate.setText(year+"-"+month+"-"+day);
                    }
                },year,month,day).show();
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = monthOfYear<10?("0"+monthOfYear):(""+monthOfYear);
                        String day = dayOfMonth<10?("0"+dayOfMonth):(""+dayOfMonth);
                        startDate.setText(year+"-"+month+"-"+day);
                    }
                },year,month,day).show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = monthOfYear<10?("0"+monthOfYear):(""+monthOfYear);
                        String day = dayOfMonth<10?("0"+dayOfMonth):(""+dayOfMonth);
                        endDate.setText(year+"-"+month+"-"+day);
                    }
                },year,month,day).show();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startD = startDate.getText().toString(),endD = endDate.getText().toString();
                Cursor cursor = getActivity().getContentResolver()
                        .query(TransactionContract.Transaction.getDatedChartUri(startD+"_"+endD),
                                null,null,null,null,null);
                setupPie(cursor);
            }
        });

        Cursor cursor = getActivity().getContentResolver()
                .query(TransactionContract.Transaction.getChartUri(),
                        null,null,null,null,null);
        setupPie(cursor);
        return view;
    }

    private void setupPie(Cursor cursor) {

        ArrayList<String> labels = new ArrayList<>();
        for(int i=0;i<cursor.getCount();i++) {
            cursor.moveToPosition(i);
            labels.add(cursor.getString(0));
        }

        cursor.moveToFirst();
        ArrayList<Entry> voteEntries = new ArrayList<>();
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
            voteEntries.add(new Entry((float) cursor.getInt(1)/cursor.getCount(),i));
        }

        PieDataSet dataset = new PieDataSet(voteEntries,"Category");
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);

        //PieData data
        PieData resultForPie = new PieData(labels, dataset);
        expensePie.setData(resultForPie);
        expensePie.setDescription("");
        expensePie.animateY(1000);
    }
}
