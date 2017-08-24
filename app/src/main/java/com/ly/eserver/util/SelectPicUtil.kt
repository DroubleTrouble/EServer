package com.ly.eserver.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.ly.eserver.app.KotlinApplication
import org.jetbrains.anko.*
import java.io.File
import java.io.FileNotFoundException

/**
 * 选择本地图片工具类
 * <br></br>
 * 因为直接获取图片容易崩溃，所以直接存入SD卡，再获取
 * <br></br>
 * 又因为写法不正确容易导致部分机型无法使用，所以封装起来复用
 * <br></br>
 * 使用方法：
 * <br></br>
 * 1、调用getByAlbum、getByCamera去获取图片
 * <br></br>
 * 2、在onActivityResult中调用本工具类的onActivityResult方法进行处理
 * <br></br>
 * 3、onActivityResult返回的Bitmap记得空指针判断
 *
 *
 * <br></br><br></br>
 * PS：本工具类只能处理裁剪图片，如果不想裁剪，不使用本工具类的onActivityResult，自己做处理即可

 * @author linin630
 */
class SelectPicUtil : AnkoLogger {
    companion object {

        /**
         * 临时存放图片的地址，如需修改，请记得创建该路径下的文件夹
         */
        private val lsimg = KotlinApplication.instance().filesDir.absolutePath

        val GET_BY_ALBUM = 801//如果有冲突，记得修改
        val GET_BY_CAMERA = 802//如果有冲突，记得修改
        val CROP = 803//如果有冲突，记得修改

        /**
         * 从相册获取图片
         */
        fun getByAlbum(act: Activity) {
            val getAlbum = Intent(Intent.ACTION_GET_CONTENT)
            getAlbum.type = "image/*"
            act.startActivityForResult(getAlbum, GET_BY_ALBUM)
        }

        /**
         * 通过拍照获取图片
         */
        fun getByCamera(act: Activity) {
            val state = Environment.getExternalStorageState()
            if (state == Environment.MEDIA_MOUNTED) {
                val getImageByCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(lsimg))
                getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
                act.startActivityForResult(getImageByCamera, GET_BY_CAMERA)
            } else {
                error("请确认已经插入SD卡")
            }
        }

        /**
         * 处理获取的图片，注意判断空指针
         */
        @JvmOverloads fun onActivityResult(act: Activity, requestCode: Int, resultCode: Int, data: Intent,
                                           w: Int = 0, h: Int = 0, aspectX: Int = 0, aspectY: Int = 0): Bitmap {
            var bm: Bitmap? = null
            if (resultCode == Activity.RESULT_OK) {
                var uri: Uri? = null
                when (requestCode) {
                    GET_BY_ALBUM -> {
                        uri = data.data
                        uri = dealUri(act, uri)//适配4.4系统
                        act.startActivityForResult(crop(uri, w, h, aspectX, aspectY), CROP)
                    }
                    GET_BY_CAMERA -> {
                        uri = Uri.parse(lsimg)
                        act.startActivityForResult(crop(uri, w, h, aspectX, aspectY), CROP)
                    }
                    CROP -> bm = dealCrop(act)
                }
            }
            return bm!!
        }

        /**
         * 裁剪，例如：输出100*100大小的图片，宽高比例是1:1
         * @param w       输出宽
         * @param h       输出高
         * @param aspectX 宽比例
         * @param aspectY 高比例
         */
        @JvmOverloads fun crop(uri: Uri, w: Int = 480, h: Int = 480, aspectX: Int = 1, aspectY: Int = 1): Intent {
            var w = w
            var h = h
            var aspectX = aspectX
            var aspectY = aspectY
            if (w == 0 && h == 0) {
                h = 480
                w = h
            }
            if (aspectX == 0 && aspectY == 0) {
                aspectY = 1
                aspectX = aspectY
            }
            val intent = Intent("com.android.camera.action.CROP")
            // 照片URL地址
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", aspectX)
            intent.putExtra("aspectY", aspectY)
            intent.putExtra("outputX", w)
            intent.putExtra("outputY", h)
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(lsimg))
            // 输出格式
            intent.putExtra("outputFormat", "JPEG")
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", true)
            intent.putExtra("return-data", false)
            return intent
        }

        /**
         * 处理裁剪，获取裁剪后的图片
         */
        fun dealCrop(context: Context): Bitmap {
            // 裁剪返回
            val uri = Uri.parse(lsimg)
            var bitmap: Bitmap? = null
            try {
                bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return bitmap!!
        }

        /**
         * 适配4.4系统
         */
        fun dealUri(act: Activity, uri: Uri): Uri {
            var uri = uri
            var filePath: String? = null
            if (DocumentsContract.isDocumentUri(act, uri)) {
                val wholeID = DocumentsContract.getDocumentId(uri)
                val id = wholeID.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
                val column = arrayOf(MediaStore.Images.Media.DATA)
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor = act.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf<String>(id), null)
                val columnIndex = cursor!!.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
            } else {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = act.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                filePath = cursor.getString(column_index)
            }
            uri = Uri.fromFile(File(filePath))
            return uri
        }
    }

}
/**
 * 处理获取的图片，注意判断空指针，默认大小480*480，比例1:1
 */
/**
 * 默认裁剪输出480*480，比例1:1
 */
