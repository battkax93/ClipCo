package com.sunny.putra.clipco.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.sunny.putra.clipco.Clip;
import com.sunny.putra.clipco.ListClipActivity;
import com.sunny.putra.clipco.R;
import com.sunny.putra.clipco.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListClipAdapter extends RecyclerView.Adapter<ListClipAdapter.ViewHolder> {

    private static OnItemClickListener sListener;
    private List<Clip> mList = new ArrayList<>();

    LayoutInflater mInflater;
    Context mContext;

    public ListClipAdapter(Context mContext, List<Clip> list) {
        this.mContext = mContext;
        this.mList = list;
        notifyDataSetChanged();

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.clip_adapter,
                        parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cv)
        CardView cv;
        @BindView(R.id.tvClip)
        TextView tvClip;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.btnTrash)
        ImageView btnDel;

        public ViewHolder(View v) {
            super(v);

            tvClip = v.findViewById(R.id.tvClip);
            tvDate = v.findViewById(R.id.tvDate);
            btnDel = v.findViewById(R.id.btnTrash);
            cv = v.findViewById(R.id.cv);

            ButterKnife.bind(this, v);
            /*cv.setClickable(true);
            cv.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnTrash:
                    if (sListener != null) sListener.onYesClick(view, getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Clip clip = mList.get(position);
        final ListClipActivity lc = new ListClipActivity();
        holder.tvClip.setText(clip.clip);
        holder.tvDate.setText(clip.timesamp);
        holder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogHelper.print_me("clipboard = " + clip.clip);
                lc.showDialog(mContext, clip.clip, clip.id);
                return false;
            }
        });
                /*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListClipActivity lc = (ListClipActivity) mContext;
                lc.deleteRow(String.valueOf(clip.id));
            }
        });*/
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListClipActivity lc = (ListClipActivity) mContext;
                lc.moveToMain(String.valueOf(clip.id),clip.clip);

            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        sListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Clip getItem(int position) {
        return mList.get(position);
    }

    public interface OnItemClickListener {
        void onYesClick(View view, int position);

        void onNoClick(View view, int position);
    }

}
