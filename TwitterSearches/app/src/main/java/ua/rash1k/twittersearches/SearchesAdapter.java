package ua.rash1k.twittersearches;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.SearchesViewHolder> {

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private Set<String> tagList;

    public SearchesAdapter(Set<String> tagsList, View.OnClickListener onClickListener,
                           View.OnLongClickListener onLongClickListener) {
        this.tagList = tagsList;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public SearchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new SearchesViewHolder(view, mOnClickListener, mOnLongClickListener);
    }

    @Override
    public void onBindViewHolder(SearchesViewHolder holder, int position) {

        holder.mTextView.setText((String) tagList.toArray()[position]);
    }

    @Override
    public int getItemCount() {
        return tagList != null ? tagList.size() : 0;
    }

    public static class SearchesViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public SearchesViewHolder(View itemView,
                                  View.OnClickListener onClickListener,
                                  View.OnLongClickListener onLongClickListener) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);

        }
    }

}
