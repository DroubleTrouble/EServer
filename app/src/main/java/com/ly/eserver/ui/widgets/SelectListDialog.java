package com.ly.eserver.ui.widgets;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.eserver.R;

import java.util.List;


/**
 * 列表选择对话框
 * 重写试试
 * @author Xuqn
 */
public class SelectListDialog extends Dialog implements OnClickListener, DialogInterface.OnCancelListener {


    public static final int MODE_NOT_CONFIRM = 0;
    public static final int MODE_CONFIRM = 1;

    private Context mContext;

    private ListView selectList;

    private SelectAdapter mAdapter;
    private OnSelectedListener onSelectedListener = null;
    private OnCancelListener onCancelListener = null;

    private String titleValue;
    private String[] selectListData;


    private SelectListDialog(Context context) {
        super(context, R.style.ListDialog);
        mContext = context;
    }

    public SelectListDialog(Context context, String title, String[] data,  String selectItem) {
        this(context, title, data, selectItem, false);
    }

    public SelectListDialog(Context context, String title, String[] data, String selectItem, OnSelectedListener listener) {
        this(context, title, data, selectItem, false);
        onSelectedListener = listener;
    }

    /**
     * @param context
     * @param title
     * @param data
     * @param selectItem
     * @param cancelable 可取消选择
     */
    public SelectListDialog(Context context, String title, String[] data, String selectItem, boolean cancelable) {
        this(context);
        if (title == null) {
            titleValue = "请选择";
        } else {
            titleValue = title;
        }
        refreshSelectList(data);

        mAdapter = new SelectAdapter(mContext, selectListData, selectItem, cancelable);
    }

    public SelectListDialog(Context context, String title, String[] data, int selectIndex) {
        this(context, title, data, selectIndex, false);
    }

    public SelectListDialog(Context context, String title, String[] data, int selectIndex, boolean cancelable) {
        this(context);
        if (title == null) {
            titleValue = "请选择";
        } else {
            titleValue = title;
        }
        refreshSelectList(data);
        mAdapter = new SelectAdapter(mContext, selectListData, selectIndex, cancelable);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_list);

        getWindow().setWindowAnimations(R.style.DialogAnimStyle);

        selectList = (ListView) findViewById(R.id.select_list);

        selectList.setAdapter(mAdapter);

//        if (selectListData.length > 10) {
//            selectList.getLayoutParams().height = 400;
//        }

        setOnCancelListener(this);
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void refreshSelectList(String[] data) {
        if (data == null) {
            selectListData = new String[0];
        } else {
            selectListData = data;
        }
        if (mAdapter != null) {
            mAdapter.refreshSelectList(selectListData);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取选择数据列表
     *
     * @return
     */
    public String[] getData() {
        return selectListData;
    }

    /**
     * 设置选择监听
     *
     * @param listener
     */
    public void setOnSelectedListener(OnSelectedListener listener) {
        onSelectedListener = listener;
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(onCancelListener);
        if (listener != this) onCancelListener = listener;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }

    /**
     * 选择监听
     *
     * @author Xuqn
     */
    public interface OnSelectedListener {

        void onItemSelected(View v, int index);

        void onNothingSelected();
    }
    public class SelectAdapter extends BaseAdapter implements View.OnClickListener {

        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private String[] mData;
        private SparseBooleanArray checkStatus;

        private String selectData;
        private int selectIndex;

        private boolean cancelAble = false;//可反选

        public SelectAdapter(Context context, String[] data, String selectdata) {
            this(context, data, selectdata,  false);
        }

        public SelectAdapter(Context context, String[] data, String selectdata, boolean cancelable) {
            selectIndex = -1;
            selectData = selectdata;
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            refreshSelectList(data);
            cancelAble = cancelable;
        }

        public SelectAdapter(Context context, String[] data, int selectIndex ) {
            this(context, data, selectIndex, false);
        }

        public SelectAdapter(Context context, String[] data, int selectIndex, boolean cancelable) {
            if (selectIndex >= 0) {
                this.selectIndex = selectIndex;
                if (data != null && data.length >= selectIndex + 1) {
                    selectData = data[selectIndex];
                } else {
                    selectData = null;
                }
            } else {
                selectData = null;
            }
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            refreshSelectList(data);
            cancelAble = cancelable;
        }

        /**
         * 刷新选择列表
         *
         * @param data
         */
         void refreshSelectList(String[] data) {
            if (data == null) {
                mData = new String[0];
            } else {
                mData = data;
            }
            if (checkStatus != null) checkStatus.clear();
            checkStatus = new SparseBooleanArray(mData.length);
            if (selectData == null) return;
            for (int i = 0, size = mData.length; i < size; i++) {
                if (mData[i].equals(selectData)) {
                    selectIndex = i;
                    checkStatus.put(i, true);
                    break;
                }
            }
        }


        public int getSelectIndex() {
            return selectIndex;
        }

        private class ViewHolder {
            TextView selectItem;
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View v;

            if (convertView == null) {
                v = mLayoutInflater.inflate(R.layout.dialog_select_list_item, null);
                holder = new ViewHolder();
                holder.selectItem = (TextView) v.findViewById(R.id.select_item);
                holder.selectItem.setOnClickListener(this);
                v.setTag(holder);
            } else {
                v = convertView;
                holder = (ViewHolder) v.getTag();
            }

            if (holder != null) {
                holder.selectItem.setText(mData[position]);
                holder.selectItem.setTag(position);
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            TextView item = (TextView) v;
            int index = (Integer) item.getTag();

                if (!checkStatus.get(index, false)) {
                    selectIndex = index;
                    checkStatus.clear();
                    checkStatus.put(index, true);
                } else if (cancelAble) {
                    selectIndex = -1;
                    checkStatus.clear();
                }
                notifyDataSetChanged();
//				else{
//					item.setChecked(false);
//					checkStatus.put(index, false);
//				}
                if (onSelectedListener != null)
                    onSelectedListener.onItemSelected(v, index);

        }
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (onCancelListener != null) onCancelListener.onCancel(dialog);
        if (onSelectedListener != null) onSelectedListener.onNothingSelected();
    }

}
