package me.calebjones.spacelaunchnow.starship.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.main.starship.Notice;
import me.calebjones.spacelaunchnow.data.models.main.starship.RoadClosure;
import me.spacelaunchnow.starship.R;

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder> {

    private List<Notice> noticeList;
    private Context context;

    public NoticesAdapter(Context context) {
        noticeList = new ArrayList<>();
        this.context = context;
    }

    public void addItems(List<Notice> events) {
        this.noticeList = events;
        this.notifyDataSetChanged();
    }

    public void clear() {
        noticeList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Notice notice = noticeList.get(position);

        String date = DateFormat.getDateInstance(DateFormat.LONG).format(notice.getDate());
        holder.noticeTitle.setText(notice.getType().getName());
        holder.noticeDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView noticeTitle;
        private TextView noticeDate;
        private View noticeLink;


        public ViewHolder(View view) {
            super(view);

            noticeTitle = view.findViewById(R.id.title);
            noticeDate = view.findViewById(R.id.date);
            noticeLink = view.findViewById(R.id.open);

            noticeLink.setOnClickListener(v -> {
                Notice notice = noticeList.get(getAdapterPosition());
                if (notice.getUrl() != null) {
                    Utils.openCustomTab(context, notice.getUrl());
                }
            });

        }
    }
}
