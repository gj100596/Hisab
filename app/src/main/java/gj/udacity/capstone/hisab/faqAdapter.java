package gj.udacity.capstone.hisab;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class faqAdapter extends ArrayAdapter<Integer> {
    String[] question, answer;
    Activity context;

    public faqAdapter(Activity context, String question[], String answer[]) {
        super(context, question.length);
        this.context = context;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public int getCount() {
        return question.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.faq_listview_element, null, true);
        TextView que = (TextView) view.findViewById(R.id.question);
        TextView ans = (TextView) view.findViewById(R.id.answer);
        que.setText(question[position]);
        ans.setText(answer[position]);
        return view;
    }
}
