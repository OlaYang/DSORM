package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.EnDecryptUtil;
import com.meiqi.data.util.LogUtil;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-11-26
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 * 加密解密函数
 */

public class ENDECRYPT extends Function {
    static final String NAME = ENDECRYPT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }
        final String source = DataUtil.getStringValue(args[0]);
        final String type = DataUtil.getStringValue(args[1]); // en为加密, de为解密
        if (type.equalsIgnoreCase("en")) {
            String privateKey = "";
            int versionID = 0;
            _CIPHER cipher = new _CIPHER(privateKey, source);
            try {
                String encodeStr = EnDecryptUtil.encode(cipher);
                return versionID + "," + encodeStr;
            } catch (IOException e) {
                return StringPool.EMPTY;
            }
        } else if (type.equalsIgnoreCase("de")) {
            if (args.length != 3) {
                throw new RengineException(calInfo.getServiceName(), NAME + "参数个数不匹配,找不到解密版本号");
            }
            int versionID;
            try {
                versionID = DataUtil.getNumberValue(args[2]).intValue();
            } catch (Exception e) {
                throw new RengineException(calInfo.getServiceName(), NAME + "无法识别的版本号类型");
            }

            // 根据版本号及有效状态获取私钥
            // 判断是否存在的版本号
            String privateKeyInDB = "";

            //解密
            try {
                _CIPHER cipher = (_CIPHER) EnDecryptUtil.decode(source);
                if (cipher != null && privateKeyInDB.equals(cipher.privateKey)) {
                    return cipher.source;
                } else {
                    LogUtil.info("私钥匹配失败");
                    return StringPool.EMPTY;
                }
            } catch (Exception e) {
                LogUtil.info("解密失败");
                return StringPool.EMPTY;
            }

        } else {
            throw new RengineException(calInfo.getServiceName(), NAME + "不支持的操作方式,仅支持加密en和解密de");
        }


    }

    class _CIPHER implements Serializable {
        private String privateKey;
        private String source;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _CIPHER)) return false;

            _CIPHER cipher = (_CIPHER) o;

            if (privateKey != null ? !privateKey.equals(cipher.privateKey) : cipher.privateKey != null)
                return false;
            if (source != null ? !source.equals(cipher.source) : cipher.source != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = privateKey != null ? privateKey.hashCode() : 0;
            result = 31 * result + (source != null ? source.hashCode() : 0);
            return result;
        }

        _CIPHER(String privateKey, String source) {
            this.privateKey = privateKey;
            this.source = source;
        }
    }
}
