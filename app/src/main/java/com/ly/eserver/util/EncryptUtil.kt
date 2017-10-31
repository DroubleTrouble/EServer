package com.ly.eserver.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import kotlin.experimental.and

/**
 * 加密密钥的工具类
 * @author LiuYanLu
 */
object EncryptUtil {
    /**
     * 系统生成令牌的过期时间
     */
    val TOKEN_EXPIRED_DATE = 3 // days
    /**
     * 令牌分隔符
     */
    val TOKEN_SEPARATOR = ","
    /**
     * 令牌前缀
     */
    val TOKEN_PREFIX = "#TOKEN"
    /**
     * 令牌的密钥
     */
    private val TOKEN_KEY = "solution.auto.imes.tokenKey"
    /**
     * 令牌中的日期表示形式
     */
    private val FORMAT_DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 将明文的密码以SHA-1算法加密

     * @param passwd 明文密码
     * *
     * @return String SHA-1算法加密后的密码签名
     */
    fun shaEncryptText(passwd: String): String? {
        var md: MessageDigest? = null
        val bt = passwd.toByteArray()
        var strDes: String? = null
        try {
            md = MessageDigest.getInstance("SHA-1")
            md!!.update(bt)
            strDes = bytes2Hex(md.digest()) // to HexString
        } catch (e: NoSuchAlgorithmException) {
            println("Invalid algorithm.")
            return null
        }

        return strDes
    }

    /**
     * 验证用户输入的密码是否与数据库中SHA-1算法加密后的密码相同

     * @param passwd      明文密码
     * *
     * @param sha1_passwd SHA-1加密算法后的密码签名
     * *
     * @return true 相同 false 不相同
     */
    fun validatePassword(passwd: String?, sha1_passwd: String?): Boolean {

        if (null == passwd || null == sha1_passwd)
            return false

        if (sha1_passwd == shaEncryptText(passwd)) {
            return true
        }

        return false
    }

    /**
     *  用户登录验证成功后，系统自动为用户生成TOKEN : 通过可逆加密算法，将username + login
     * timestamp转换为字符串，作为TOKEN返回给客户端。

     * @param username 用户登录名
     * *
     * @return 令牌
     */
    fun generateToken(username: String): String {
        val currentDate = getCurrentDateAsString(FORMAT_DATE_YYYY_MM_DD_HH_MM_SS)

        val before_token_str = currentDate + TOKEN_SEPARATOR + username

        return addTokenPrefix(desEncryptText(before_token_str))
    }

    /**
     *  验证用户的TOKEN是否正确 : 通过可逆加密算法，将Token拆分为username和login timestamp。
     * 判断拆分出来的username是否与输入的用户名相同，login时间是否没有超过指定的有效期

     * @param username 用户登录用ID
     * *
     * @param token    用户的Token
     * *
     * @return true 验证通过 false 验证失败
     */
    @Throws(Exception::class)
    fun validateToken(username: String, token: String): Array<String> {
        val err_str = "Invalid token!"
        val current_time = Calendar.getInstance()
        var encryptToken: String = removeTokenPrefix(token)
        // remove prefix
        encryptToken = desDecryptText(encryptToken)
        if (null == encryptToken || encryptToken == "")
            throw Exception(err_str)

        val sep_index = encryptToken.split(TOKEN_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (sep_index == null || sep_index.size <= 0)
            throw Exception(err_str)

        val strtoken_time = sep_index[0]
        val strtoken_username = sep_index[1]

        if (!username.equals(strtoken_username, ignoreCase = true))
            throw Exception(err_str)

        // Date token_date = parseDate(strtoken_time,
        // FORMAT_DATE_YYYY_MM_DD_HH_MM_SS);
        //
        // Calendar expiredDate = Calendar.getInstance();
        // expiredDate.setTime(token_date);
        // expiredDate.add(Calendar.DAY_OF_MONTH, TOKEN_EXPIRED_DATE);
        // if (expiredDate.before(current_time))
        // throw new Exception(err_str);
        return sep_index
    }

    /**
     * Byte数组到字符串转换

     * @param bts Byte数组
     * *
     * @return 转换后的字符串
     */
    private fun bytes2Hex(bts: ByteArray): String {
        var des = ""
        var tmp: String? = null
        for (i in bts.indices) {
            tmp = Integer.toHexString((bts[i] and 0xFF.toByte()).toInt())
            if (tmp!!.length == 1) {
                des += "0"
            }
            des += tmp
        }
        return des
    }

    /**
     * 字符串到Byte数组的转换

     * @param srcStr 字符串
     * *
     * @return Byte数组
     */
    private fun hex2Bytes(srcStr: String): ByteArray {
        val byte_len = srcStr.length / 2
        val result = ByteArray(byte_len)
        for (i in 0..byte_len - 1) {
            val bytestr = srcStr.substring(i * 2, i * 2 + 2)
            result[i] = Integer.valueOf(bytestr, 16)!!.toByte()
        }
        return result
    }

    /**
     * 加密生成令牌

     * @param inputstr 加密的信息
     * *
     * @return 生成的令牌信息
     */
    fun desEncryptText(inputstr: String): String {

        try {
            // DES算法要求有一个可信任的随机数源
            val sr = SecureRandom()

            // 从原始密匙数据创建DESKeySpec对象
            val dks = DESKeySpec(TOKEN_KEY.toByteArray())

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            val keyFactory = SecretKeyFactory.getInstance("DES")

            val securekey = keyFactory.generateSecret(dks)

            // Cipher对象实际完成加密操作
            val cipher = Cipher.getInstance("DES")

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr)

            // 现在，获取数据并加密
            // 正式执行加密操作
            return bytes2Hex(cipher.doFinal(inputstr.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 解密令牌信息

     * @param inputStr 令牌信息
     * *
     * @return 解密后的信息
     */
    fun desDecryptText(inputStr: String): String {
        try {
            // DES算法要求有一个可信任的随机数源
            val sr = SecureRandom()

            // 从原始密匙数据创建一个DESKeySpec对象
            val dks = DESKeySpec(TOKEN_KEY.toByteArray())

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val securekey = keyFactory.generateSecret(dks)

            // Cipher对象实际完成解密操作
            val cipher = Cipher.getInstance("DES")

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, sr)

            // 现在，获取数据并解密
            // 正式执行解密操作
            return String(cipher.doFinal(hex2Bytes(inputStr)))
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 以PBE算法生成加密令牌的信息

     * @param inputstr 输入的信息
     * *
     * @return 生成的令牌信息
     */
    private fun encodePBEToken(inputstr: String): String? {

        try {
            val pbeKeySpec = PBEKeySpec(TOKEN_KEY.toCharArray())
            val keyFac = SecretKeyFactory
                    .getInstance("PBEWithMD5AndDES")
            val pbeKey = keyFac.generateSecret(pbeKeySpec)

            val salt = byteArrayOf(0xc7.toByte(), 0x73.toByte(), 0x21.toByte(), 0x8c.toByte(), 0x7e.toByte(), 0xc8.toByte(), 0xee.toByte(), 0x99.toByte())

            val count = 2

            // 生成pbe算法所需的参数对象，两个参数详见 RSA的 PKCS #5 标准
            val pbeParamSpec = PBEParameterSpec(salt, count)

            // 生成一个加密器
            val pbeCipher = Cipher.getInstance("PBEWithMD5AndDES")

            // 初始化加密器
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec)

            // 明文
            val cleartext = inputstr.toByteArray()

            // 加密
            val ciphertext = pbeCipher.doFinal(cleartext)

            // 返回密文
            return bytes2Hex(ciphertext)
        } catch (e: Exception) {
            println(e)
            return null
        }

    }

    /**
     * 解密PBE令牌的信息

     * @param inputStr 加密过的令牌信息
     * *
     * @return 解密后的信息
     */
    private fun decodePBEToken(inputStr: String): String {
        try {
            val pbeKeySpec = PBEKeySpec(TOKEN_KEY.toCharArray())
            val keyFac = SecretKeyFactory
                    .getInstance("PBEWithMD5AndDES")
            val pbeKey = keyFac.generateSecret(pbeKeySpec)

            val salt = byteArrayOf(0xc7.toByte(), 0x73.toByte(), 0x21.toByte(), 0x8c.toByte(), 0x7e.toByte(), 0xc8.toByte(), 0xee.toByte(), 0x99.toByte())

            val count = 2

            // 生成pbe算法所需的参数对象，两个参数详见 RSA的 PKCS #5 标准
            val pbeParamSpec = PBEParameterSpec(salt, count)

            // 生成一个加密器
            val pbeCipher = Cipher.getInstance("PBEWithMD5AndDES")

            // 初始化加密器
            pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec)

            // 密文
            val ciphertext = hex2Bytes(inputStr)

            // 解密
            val cleartext = pbeCipher.doFinal(ciphertext)

            // 返回明文
            return String(cleartext)
        } catch (e: Exception) {
            println(e)
        }

        return ""
    }

    /**
     * Main运行测试函数

     * @param args
     */
    @JvmStatic fun main(args: Array<String>) {
        val strSrc = "Pass1234"
        println("Source String:" + strSrc)
        println("Encrypted String:")
        println("Use Def:" + EncryptUtil.shaEncryptText(strSrc)!!)

        val id = 1
        val username = "admin"
        println("Username String:" + username)
        println(DateUtil
                .getCurrentDateAsString(DateUtil.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS))
        val token = EncryptUtil.generateToken(username)
        println(DateUtil
                .getCurrentDateAsString(DateUtil.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS))
        println("Token String:" + token)

        try {
            println("Validate Token String:" + EncryptUtil.validateToken(username, token))
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        println(DateUtil
                .getCurrentDateAsString(DateUtil.FORMAT_DATE_YYYY_MM_DD_HH_MM_SS))

        try {
            println("Validate Token String:" + EncryptUtil.validateToken(username, token))
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    /**
     * 以指定格式过去当前日期信息

     * @param formatPattern 指定日期格式
     * *
     * @return 当前日期
     */
    private fun getCurrentDateAsString(formatPattern: String): String {
        val date = Date()
        return SimpleDateFormat(formatPattern).format(date)
    }

    /**
     * 按照指定格式解析日期信息字符串

     * @param stringValue   日期信息字符串
     * *
     * @param formatPattern 指定格式
     * *
     * @return 日期
     */
    private fun parseDate(stringValue: String, formatPattern: String): Date? {
        val format = SimpleDateFormat(formatPattern)
        try {
            return format.parse(stringValue)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 判断该信息是否令牌加密

     * @param str 信息
     * *
     * @return 是或否
     */
    fun isEncryptToken(str: String): Boolean {
        return str.startsWith(TOKEN_PREFIX)
    }

    /**
     * 去除加密令牌信息中的前缀

     * @param str 令牌信息
     * *
     * @return 去除前缀后的令牌信息
     */
    private fun removeTokenPrefix(str: String): String {
        var str = str
        if (str.startsWith(TOKEN_PREFIX)) {
            str = str.substring(TOKEN_PREFIX.length)
        }
        return str
    }

    /**
     * 加入令牌前缀

     * @param str 输入字符串
     * *
     * @return 追加令牌前缀后的字符串
     */
    private fun addTokenPrefix(str: String): String {
        return TOKEN_PREFIX + str
    }
}


