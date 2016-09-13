package gj.udacity.capstone.hisab.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gj.udacity.capstone.hisab.R;

public class DetailFragment extends Fragment {

    private static final String ARG = "item_id";

    private String userID;

    public DetailFragment() {
        // Required empty public constructor
    }


    public static DetailFragment newInstance(String id){
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        RecyclerView historyList = (RecyclerView) view.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //historyList.setAdapter(new FeedRecyclerViewAdapter(DummyContent.ITEMS,getActivity()));
        return view;
    }

}
