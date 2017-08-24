package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Message
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.presenter.DescriptionActivityPresenter
import com.ly.eserver.presenter.impl.DescriptionActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.yuyh.library.imgsel.ImageLoader
import com.yuyh.library.imgsel.ImgSelActivity
import com.yuyh.library.imgsel.ImgSelConfig
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.*


/**
 * 说明页面
 * Created by Max on 2017/8/11.
 */
class DescriptionActivity (override val layoutId: Int = R.layout.activity_description) :
        BaseActivity<DescriptionActivityPresenterImpl>(), DescriptionActivityPresenter.View{

    private var imageSelectConfig: ImgSelConfig? = null
    val REQUEST_CODE : Int = 200
    var pathList : List<String> = ArrayList<String>()
    override fun refreshView(mData: Any?) {
    }

    override fun onHandlerReceive(msg: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        initPictureSelector()
        mPresenter = DescriptionActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        tv_titlebar_title.text = "提交报告"
        ll_titlebar_back.visibility = LinearLayout.GONE
        iv_description_picture1.visibility = LinearLayout.GONE
        iv_description_picture2.visibility = LinearLayout.GONE
    }

    override fun initView() {

        iv_description_addpicture.setOnClickListener {
            ImgSelActivity.startActivity(this, imageSelectConfig, REQUEST_CODE);
        }

        bt_description_commit.setOnClickListener {
            if (et_description_description.text.toString() == ""){
                ToastUtils.showShort("说明不可为空")
            }else if (pathList.isEmpty()){
                ToastUtils.showShort("请选择图片")
            }else{
                info("pathList-------------" + pathList.get(0))
            }
        }

        ll_titlebar_close.setOnClickListener {
            finish()
        }
        iv_description_picture1.setOnClickListener {
//            if (!pathList.isEmpty())
//                startActivityForResult<BigPictureActivity>(requestCode = 1,"pathPicture" to pathList.get(0))
        }
        iv_description_picture2.setOnClickListener {
            if (pathList.size == 2){
//                startActivityForResult<BigPictureActivity>(requestCode = 1,"pathPicture" to pathList.get(1))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT)
            if (pathList.size == 1){
                iv_description_picture1.visibility = LinearLayout.VISIBLE
                iv_description_picture2.visibility = LinearLayout.GONE
                Glide.with(this).load("file://"+pathList.get(0)).into(iv_description_picture1)
            }else if (pathList.size == 2){
                iv_description_picture1.visibility = LinearLayout.VISIBLE
                iv_description_picture2.visibility = LinearLayout.VISIBLE
                Glide.with(this).load("file://"+pathList.get(0)).into(iv_description_picture1)
                Glide.with(this).load("file://"+pathList.get(1)).into(iv_description_picture2)
            }else {
                iv_description_picture1.visibility = LinearLayout.GONE
                iv_description_picture2.visibility = LinearLayout.GONE
            }
        }
    }

    /**
     * 初始化图片选择器
     */
    private fun initPictureSelector() {//配置图片加载器
        imageSelectConfig = ImgSelConfig.Builder(this, loader)
                .btnTextColor(Color.parseColor("#FFFFFF"))
                .statusBarColor(Color.parseColor("#58b1e3"))
                .backResId(R.drawable.ic_back)
                .title("选择图片").titleColor(Color.parseColor("#FFFFFF"))
                .titleBgColor(Color.parseColor("#58b1e3"))
                .cropSize(1, 1, 1600, 800)
                .needCrop(true).needCamera(true).maxNum(2)
                .build()
    }

    // 自定义图片加载器
    private val loader = ImageLoader { context, path, imageView ->
        // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
        Glide.with(context).load(path).into(imageView)
    }


}