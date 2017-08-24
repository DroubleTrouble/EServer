package com.ly.eserver.ui.widgets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ly.eserver.R;


/**
 * 执行状态对话框
 *
 * @author Xuqn
 */
public class ProgressDialog extends Dialog {

    public static final String TAG = "ProgressDialog";

    private TextView dialogTitle;
    private TextView dialogMsg;
    private ProgressBar dialogProgress;

    @SuppressLint("StaticFieldLeak")
    private static ProgressDialog mDialog = null;

    private String titleValue;
    private String msgValue;

    public ProgressDialog(Context context) {
        this(context, null, null);
    }

    public ProgressDialog(Context context, String title, String msg) {
        super(context, R.style.ProgeressDialog);
        titleValue = title;
        msgValue = msg;
        mDialog = this;
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 获取上次实例化实例
     * @return
     */
    public static ProgressDialog getInstance() {
        if (mDialog != null) {
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
        }
        return mDialog;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);

        getWindow().setWindowAnimations(R.style.DialogAnimStyle);

        dialogTitle = (TextView) findViewById(R.id.tv_progressDialog_title);
        dialogMsg = (TextView) findViewById(R.id.tv_progressDialog_msg);
        dialogProgress = (ProgressBar) findViewById(R.id.pb_progressDialog_progressbar);

        //初始化
        setTitle(titleValue);
        setMsg(msgValue);

    }

    /**
     * 设置标题栏
     *
     * @param title
     */
    public void setTitle(String title) {
        titleValue = title;
        dialogTitle.setText(titleValue == null ? "请稍后" : titleValue);
    }

    /**
     * 设置消息
     * @param msg
     */
    public void setMsg(String msg) {
        msgValue = msg;
        dialogMsg.setText(msgValue == null ? "处理中..." : msgValue);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setDialogProgress(int progress) {
        dialogProgress.setProgress(progress);
    }

    /**
     * 设置进度条的显示/隐藏
     *
     * @param show
     */
    public void toggleProgress(boolean show) {
        dialogProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}

