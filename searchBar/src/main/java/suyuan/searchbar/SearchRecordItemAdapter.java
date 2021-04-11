package suyuan.searchbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import suyuan.searchbar.databinding.ItemSearchRecordBinding;


/**
 * @author suyaun
 */
public class SearchRecordItemAdapter extends RecyclerView.Adapter<SearchRecordItemAdapter.MyViewHolder> {
    private SearchBar.SearchRecordContent searchRecordContent;

    public void setSearchRecordContent(SearchBar.SearchRecordContent searchRecordContent) {
        this.searchRecordContent = searchRecordContent;
    }

    public SearchRecordItemAdapter(SearchBar.SearchRecordContent searchRecordContent) {
        this.searchRecordContent = searchRecordContent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemSearchRecordBinding searchRecordBinding = ItemSearchRecordBinding.bind(holder.itemView);
        if (searchRecordContent != null) {
            searchRecordContent.setContent(searchRecordBinding.itemSearchRecordContent, position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchRecordContent.onItemClick(holder.itemView, position);
                }
            });
            searchRecordBinding.itemSearchRecordDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //放删除数据的东西
                    searchRecordContent.onDelete((Button) v, position);

                }
            });
        }

        //释放资源
        searchRecordBinding = null;
    }

    @Override
    public int getItemCount() {
        return searchRecordContent.getDataSize();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
