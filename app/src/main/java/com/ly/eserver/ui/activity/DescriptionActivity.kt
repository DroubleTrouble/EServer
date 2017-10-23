package com.ly.eserver.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Message
import android.widget.LinearLayout
import com.amap.api.location.AMapLocation
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.ly.eserver.R
import com.ly.eserver.app.Constants
import com.ly.eserver.app.KotlinApplication
import com.ly.eserver.bean.DescriptionBean
import com.ly.eserver.presenter.DescriptionActivityPresenter
import com.ly.eserver.presenter.impl.DescriptionActivityPresenterImpl
import com.ly.eserver.ui.activity.base.BaseActivity
import com.qiniu.android.common.FixedZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import com.yuyh.library.imgsel.ImageLoader
import com.yuyh.library.imgsel.ImgSelActivity
import com.yuyh.library.imgsel.ImgSelConfig
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.android.synthetic.main.item_titlebar.*
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivityForResult
import java.util.*

/**
 * 说明页面
 * Created by Max on 2017/8/11.
 */
class DescriptionActivity (override val layoutId: Int = R.layout.activity_description) :
        BaseActivity<DescriptionActivityPresenterImpl>(), DescriptionActivityPresenter.View{

    private var imageSelectConfig: ImgSelConfig? = null
    val REQUEST_CODE : Int = 200
    val REQUEST_PIC1 : Int = 1
    val REQUEST_PIC2 : Int = 2
    var pathList : ArrayList<String> = ArrayList<String>()
    lateinit var QiniuToken : String
    lateinit var key : String
    lateinit var uploadManager : UploadManager
    var description : DescriptionBean = DescriptionBean()
    var amapLocation : AMapLocation? = null

    override fun refreshView(mData: Any?) {
        ToastUtils.showShort("发送成功")
    }

    override fun onHandlerReceive(msg: Message) {
    }

    override fun onBroadcastReceive(context: Context, intent: Intent) {
    }

    override fun initData() {
        initPictureSelector()
        mPresenter = DescriptionActivityPresenterImpl()
    }

    override fun loadData() {
        mLoadingPage!!.state = Constants.Companion.STATE_SUCCESS
        mLoadingPage!!.showPage()
        QiniuToken = intent.extras.getString("Qiniu")
        if (intent.extras.get("location").toString() != "") {
            amapLocation = intent.extras.get("location") as AMapLocation
        }
        tv_titlebar_title.text = "提交报告"
        ll_titlebar_back.visibility = LinearLayout.GONE
        iv_description_picture1.visibility = LinearLayout.GONE
        iv_description_picture2.visibility = LinearLayout.GONE
    }

    override fun initView() {

        //配置七牛云
        configQiniu()

        iv_description_addpicture.setOnClickListener {
            ImgSelActivity.startActivity(this, imageSelectConfig, REQUEST_CODE)
        }

        bt_description_commit.setOnClickListener {
            if (et_description_description.text.toString() == ""){
                ToastUtils.showShort("说明不可为空")
            }else if (pathList.isEmpty()){
                ToastUtils.showShort("请选择图片")
            }else{
                //两张图片还有问题
                val keylist = pathList
               //上传图片到七牛云
                for (i in pathList.indices){
                    key = KotlinApplication.useridApp.toString()+ "_" + pathList[i]
                    uploadManager.put(pathList[i], key, QiniuToken, { key, info, res ->
                                //res包含hash、key等信息，具体字段取决于上传策略的设置
                                if (info.isOK) {
                                    info( "Upload Success")
                                    keylist[i] = key
                                } else {
                                    info("Upload Fail")
                                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                }

                            }, null)
                }
                if (keylist.size != 0) {
                    description.picture1 = Constants.QINIU_API + keylist[0]
                    if (keylist.size == 2) {
                        description.picture2 = Constants.QINIU_API +keylist[1]
                    }
                }
                if (amapLocation != null)
                    description.location = amapLocation!!.latitude.toString() + "/" +amapLocation!!.longitude.toString()
                description.userid = KotlinApplication.useridApp
                description.projectid = KotlinApplication.projectidApp
                description.description = et_description_description.text.toString()
                description.time = Date(System.currentTimeMillis())//获取当前时间
                mPresenter.insertDescription(description)
            }
        }

        ll_titlebar_close.setOnClickListener {
            finish()
        }
        iv_description_picture1.setOnClickListener {
            if (!pathList.isEmpty()) {
                info("iv_description_picture1" + pathList.get(0) )
                startActivityForResult<BigPictureActivity>(REQUEST_PIC1, "pathPicture1" to pathList.get(0))

            }
        }
        iv_description_picture2.setOnClickListener {
            if (pathList.size == 2){
                info("iv_description_picture2" + pathList.get(1) )
                startActivityForResult<BigPictureActivity>(REQUEST_PIC2,"pathPicture2" to pathList.get(1))
            }
        }
    }

    private fun configQiniu() {
        val config = Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .zone(FixedZone.zone1)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build()

        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = UploadManager(config)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 图片选择结果回调
        when(requestCode){
            REQUEST_CODE ->{
                if (data != null){
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
            REQUEST_PIC1 ->{
                iv_description_picture1.visibility = LinearLayout.GONE
                pathList.removeAt(0)
            }
            REQUEST_PIC2 ->{
                iv_description_picture2.visibility = LinearLayout.GONE
                pathList.removeAt(1)
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