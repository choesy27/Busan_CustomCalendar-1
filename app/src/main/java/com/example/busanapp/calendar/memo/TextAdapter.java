package com.example.busanapp.calendar.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busanapp.R;

import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {
    private ArrayList<String> mData = null;
//  private Context mContext;
//  private Cursor mCursor;
//  private Typeface face;

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int pos);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongListener = listener;
    }

    //
    public TextAdapter(ArrayList<String> list) {
        mData = list;
    }

    // ViewHolder (화면에 표시될 아이템뷰 저장)
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(v, pos);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    mLongListener.onItemLongClick(v, pos);
                }
                return true;
            });
            item = itemView.findViewById(R.id.memoItem);
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하고 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_list, parent, false);
        TextAdapter.ViewHolder vh = new TextAdapter.ViewHolder(view);

        return vh;
    }

    // position에 해당되는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = mData.get(position);

        holder.item.setText(text);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}