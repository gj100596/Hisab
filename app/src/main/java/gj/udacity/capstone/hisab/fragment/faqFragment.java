package gj.udacity.capstone.hisab.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import gj.udacity.capstone.hisab.R;
import gj.udacity.capstone.hisab.adapter.faqAdapter;

public class faqFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.faq_layout, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listFAQ);
        String[] question = getResources().getStringArray(R.array.faq_question);
        String[] answer = getResources().getStringArray(R.array.faq_answer);
        listView.setAdapter(new faqAdapter(getActivity(),question,answer));
        return view;
    }
}
