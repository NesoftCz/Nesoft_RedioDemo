package com.neusoft.gemini.radio.adapter;

import java.util.List;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.neusoft.radio.R;
import com.neusoft.radio.RadioActivity;

/**
 * Class : PresentGridViewAdapter
 * 
 * @author : NEU_GEMINI
 *         
 */
public class PresetGridViewAdapter extends BaseAdapter {

    private final static String TAG = "PresetGridViewAdapter";
    private List<String> mData;
    private RadioActivity mContext;
    private boolean[] mCheck;
    private int mCurrentPos = -1;
    private boolean isValid = true;
    private Handler handler = new Handler();
    private boolean isLongClick = false;
    private LongPressedRunnable mLongPressed = new LongPressedRunnable();

    class LongPressedRunnable implements Runnable {
        int position;

        public void setPosition(int pos) {
            position = pos;
        }

        @Override
        public void run() {
            if(!mContext.isASelOn()){
                isLongClick = true;
                mContext.presetSave(position + 1);
                Log.d(TAG, "mContext.presetSave(position + 1)");
            }
           
        }
    }

    public PresetGridViewAdapter(RadioActivity context, List<String> data) {
        this.mContext = context;
        this.mData = data;
        if (mData != null && mData.size() > 0) {
            mCheck = new boolean[mData.size()];
        }
    }

    @Override
    public int getCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        if (mData != null && mData.size() > 0) {
            return mData.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.neu_gemini_present_item, null);
            holder = new ViewHolder();
            holder.mPresent = (Button) convertView
                    .findViewById(R.id.preset_freq);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mPresent.setText(mData.get(position));
        if (isValid) {
            holder.mPresent.setClickable(true);
            if (mCheck[position]) {
                holder.mPresent
                        .setBackgroundResource(R.drawable.radio_channel_btn_p);
                holder.mPresent.setTextColor(mContext.getResources().getColor(
                        R.color.preset_blue));
            } else {
                holder.mPresent.setTextColor(mContext.getResources().getColor(
                        R.color.preset_gray));
                holder.mPresent
                        .setBackgroundResource(R.drawable.radio_channel_btn_n);
            }
            final int pos = position;
            holder.mPresent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isLongClick && !mContext.isASelOn()) {
                        mContext.presetCall(pos + 1);
                        Log.d(TAG, "mContext.presetCall(pos + 1)");
                    }
                }
            });

            holder.mPresent.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "mContext.isASelOn() = " + mContext.isASelOn() );
                    if(!mContext.isASelOn()){
                        switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isLongClick = false;
                            handler.removeCallbacks(mLongPressed);
                            mLongPressed.setPosition(pos);
                            handler.postDelayed(mLongPressed, 2000);
                            Log.d(TAG, "MotionEvent.ACTION_DOWN ");
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            Log.d(TAG, "MotionEvent.ACTION_CANCEL");
                        case MotionEvent.ACTION_UP:
                            handler.removeCallbacks(mLongPressed);
                            Log.d(TAG, "MotionEvent.ACTION_UP");
                            break;
                        default:
                            break;
                        }
                    }
                    return false;
                }
            });
        } else {
            holder.mPresent.setTextColor(mContext.getResources().getColor(
                    R.color.preset_invalid));
            holder.mPresent
                    .setBackgroundResource(R.drawable.radio_channel_btn_n);
            holder.mPresent.setClickable(false);
            holder.mPresent.setOnClickListener(null);
            holder.mPresent.setOnLongClickListener(null);
        }
        return convertView;
    }
    public void removePresetRunable(){
        Log.d(TAG, "removePresetRunable() + 166");
        if (handler != null && mLongPressed != null) {
            Log.d(TAG, "removePresetRunable() + 168");
            handler.removeCallbacks(mLongPressed);
        }
    } 

    class ViewHolder {
        public Button mPresent;
    }

    public int getmCurrentPos() {
        return mCurrentPos;
    }

    public void setmCurrentPos(int mCurrentPos) {
        this.mCurrentPos = mCurrentPos;
    }

    public void setData(int num, String freq) {
        mData.set(num, freq);
        if (!mCheck[num]) {
            if (mCurrentPos != -1) {
                mCheck[mCurrentPos] = false;
            }
            mCurrentPos = num;
        }
        notifyDataSetChanged();
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
        notifyDataSetChanged();
    }

    public void changeItemBg(int num) {
        if (num == -1) {
            if (mCurrentPos != -1) {
                mCheck[mCurrentPos] = false;
            }
            mCurrentPos = -1;
        } else {
            if (!mCheck[num]) {
                if (mCurrentPos != -1) {
                    mCheck[mCurrentPos] = false;
                }
                mCheck[num] = true;
                mCurrentPos = num;
            }
        }
        notifyDataSetChanged();
    }

}
