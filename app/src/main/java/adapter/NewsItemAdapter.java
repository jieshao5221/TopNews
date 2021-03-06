package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiao.rlj.myapplication.R;
import com.qiao.rlj.myapplication.WebActivity;

import java.util.ArrayList;
import java.util.List;

import bean.NewsItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import greendao.gen.DBManger;
import view.PopupList;

/**
 * Created by renlijie on 16/12/28.
 */

public class NewsItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<NewsItem> itemList = new ArrayList<>();
    private Context context;
    private int ViewType = 0; //0. 大图 1.小图左 2.小图右 3.loadMore

    public NewsItemAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (ViewType){
            case 0:
                return new NewsItemViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.news_item03,parent,false));
            case 1:
                return new NewsItemViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.news_item01,parent,false));
            case 2:
                return new NewsItemViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.news_item02,null));
            case 3:
                return new LoadMoreViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.load_more,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof NewsItemViewHolder){
            NewsItemViewHolder((NewsItemViewHolder) holder,position);
        }
    }



    public void NewsItemViewHolder(NewsItemViewHolder holder, int position) {

        final NewsItem item = itemList.get(position);

        Uri uri = Uri.parse(item.thumbnailPics03);
        holder.sdv.setImageURI(uri);

        holder.date.setText(item.date);
        holder.author.setText(item.authorName);
        holder.title.setText(item.title);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url",item.url);
                intent.putExtra("author",item.authorName);
                context.startActivity(intent);
            }
        });
        bindPopMenu(holder.ll,item);


    }

    @Override
    public int getItemCount() {

        return itemList.size() !=0 ? itemList.size()+1 :itemList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(getItemCount() == position + 1){
            ViewType = 3;
        }else {
            ViewType = itemList.get(position).showType;
        }

        return ViewType;
    }

    /**
     * 添加数据集
     * @param arrayList
     */
    public void addItems(ArrayList<NewsItem> arrayList){
        itemList.addAll(arrayList);
        notifyDataSetChanged();

    }

    /**
     * 删除指定数据
     * @param item
     */
    public void deleteItem(NewsItem item){
        itemList.remove(item);
        notifyDataSetChanged();
    }

    /**
     * 收藏指定新闻
     * @param newsItem
     */
    public void collectNewsItem(NewsItem newsItem){
        DBManger.getDbManger(context).insertNewsItem(newsItem);
    }

    /**
     * 弹出吐司
     * @param message
     */
    public void doToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * 绑定气泡菜单
     * @param ll
     * @param item
     */
    public void bindPopMenu(LinearLayout ll, final NewsItem item){
        List<String> popupListMenu = new ArrayList<>();
        popupListMenu.add(context.getResources().getString(R.string.menu_collection));
        popupListMenu.add(context.getResources().getString(R.string.menu_delete));
        PopupList popupList = new PopupList();
        popupList.init(context,ll, popupListMenu, new PopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                switch (position){
                    case 0:
                        collectNewsItem(item);
                        doToast("收藏成功");
                        break;
                    case 1:
                        deleteItem(item);
                        doToast("删除成功");
                        break;
                }
            }

        });
        popupList.setTextSize(popupList.sp2px(15));
        popupList.setTextPadding(popupList.dp2px(10), popupList.dp2px(5), popupList.dp2px(10), popupList.dp2px(5));
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));

    }


    class LoadMoreViewHolder extends RecyclerView.ViewHolder{

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    class NewsItemViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.sdv)
        SimpleDraweeView sdv;
        @BindView(R.id.news_data)
        TextView date;
        @BindView(R.id.news_author)
        TextView author;
        @BindView(R.id.news_title)
        TextView title;
        @BindView(R.id.news_ll)
        LinearLayout ll;

        public NewsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}
