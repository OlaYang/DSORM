package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions._O._O_CONCAT;
import com.meiqi.data.engine.functions._O._O_CONCATBYGROUP;
import com.meiqi.data.engine.functions._O._O_CONCATBYPARA;
import com.meiqi.data.engine.functions._O._O_COUNT;
import com.meiqi.data.engine.functions._O._O_COUNTBYGROUP;
import com.meiqi.data.engine.functions._O._O_COUNTBYPARA;
import com.meiqi.data.engine.functions._O._O_COUNTDIS;
import com.meiqi.data.engine.functions._O._O_COUNTDISBYPARA;
import com.meiqi.data.engine.functions._O._O_COUNTIF;
import com.meiqi.data.engine.functions._O._O_COUNTIFBYGROUP;
import com.meiqi.data.engine.functions._O._O_COUNTIFBYPARA;
import com.meiqi.data.engine.functions._O._O_COUNTIFS;
import com.meiqi.data.engine.functions._O._O_COUNTIFSBYPARA;
import com.meiqi.data.engine.functions._O._O_FREQUENT;
import com.meiqi.data.engine.functions._O._O_GET;
import com.meiqi.data.engine.functions._O._O_GETBYPARA;
import com.meiqi.data.engine.functions._O._O_GETBYPARA2;
import com.meiqi.data.engine.functions._O._O_GETROW;
import com.meiqi.data.engine.functions._O._O_JSON;
import com.meiqi.data.engine.functions._O._O_JSONBYPARA;
import com.meiqi.data.engine.functions._O._O_JSONBYPARA2;
import com.meiqi.data.engine.functions._O._O_JSONTABLEBYPARA;
import com.meiqi.data.engine.functions._O._O_MAX;
import com.meiqi.data.engine.functions._O._O_MAXBYGROUP;
import com.meiqi.data.engine.functions._O._O_MAXBYPARA;
import com.meiqi.data.engine.functions._O._O_MIN;
import com.meiqi.data.engine.functions._O._O_MINBYGROUP;
import com.meiqi.data.engine.functions._O._O_MINBYPARA;
import com.meiqi.data.engine.functions._O._O_MODE;
import com.meiqi.data.engine.functions._O._O_MODEBYPARA;
import com.meiqi.data.engine.functions._O._O_PERCENTINMAX;
import com.meiqi.data.engine.functions._O._O_Product;
import com.meiqi.data.engine.functions._O._O_RANK;
import com.meiqi.data.engine.functions._O._O_RANKBYPARA;
import com.meiqi.data.engine.functions._O._O_RANKSTR;
import com.meiqi.data.engine.functions._O._O_SUM;
import com.meiqi.data.engine.functions._O._O_SUMBYGROUP;
import com.meiqi.data.engine.functions._O._O_SUMBYPARA;
import com.meiqi.data.engine.functions._O._O_SUMIF;
import com.meiqi.data.engine.functions._O._O_SUMIFBYGROUP;
import com.meiqi.data.engine.functions._O._O_SUMIFBYPARA;
import com.meiqi.data.engine.functions._O._O_SUMIFS;
import com.meiqi.data.engine.functions._O._O_SUMIFSBYPARA;
import com.meiqi.data.engine.functions._O._O_VLOOKUP;
import com.meiqi.data.engine.functions._O._O_VLOOKUPBYPARA;
import com.meiqi.data.engine.functions.matlab.hist.HISTPLOT;
import com.meiqi.data.engine.functions.matlab.hist._O_HISTBYPARAPLOT;
import com.meiqi.data.engine.functions.matlab.hist._O_HISTPLOT;
import com.meiqi.data.engine.functions.matlab.kmeans.K_MEANSPLOT;
import com.meiqi.data.engine.functions.matlab.kmeans._O_K_MEANSPLOT;
import com.meiqi.data.engine.functions.matlab.scatte.SCATTEPLOT;
import com.meiqi.data.engine.functions.matlab.scatte._O_SCATTEPLOT;
import com.meiqi.data.engine.functions.matlab.scatte3d.SCATTE3DPLOT;
import com.meiqi.data.engine.functions.matlab.scatte3d._O_SCATTE3DPLOT;
import com.meiqi.data.engine.functions.service.TWO_SUB_NUM;
import com.meiqi.data.engine.functions.third._T_SOLR;
import com.meiqi.data.engine.functions.weka._WEKA_APRIORI;
import com.meiqi.data.engine.functions.weka._WEKA_KMEANS;
import com.meiqi.data.util.Type;
import com.meiqi.liduoo.data.engine.functions._W_.WX_WEB_API;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_ACCOUNTLIST;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_ADDACCOUNT;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_DELETEACCOUNT;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_ONLINELIST;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_SENDMESSAGE;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_UPDATE_ACCOUNT;
import com.meiqi.liduoo.data.engine.functions._W_._W_CUSTOM_UPLOAD_HEADIMG;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_CREATE;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_DELETE;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_GETBYOPENID;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_LIST;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_MOVEUSER;
import com.meiqi.liduoo.data.engine.functions._W_._W_GROUP_UPDATE;
import com.meiqi.liduoo.data.engine.functions._W_._W_JSAPI_SIGNATURE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_ADDFILE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_ADDNEWS;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_BATCHGET;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_COUNT;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_DELETE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MATERIAL_DOWNLOAD;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_DOWNLOAD;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_UPLOADIMAGE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_UPLOADMEDIA;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_UPLOADNEWS;
import com.meiqi.liduoo.data.engine.functions._W_._W_MENU_CREATE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MENU_DELETE;
import com.meiqi.liduoo.data.engine.functions._W_._W_MENU_GET;
import com.meiqi.liduoo.data.engine.functions._W_._W_MESSAGE_FORMAT;
import com.meiqi.liduoo.data.engine.functions._W_._W_MESSAGE_PREVIEW;
import com.meiqi.liduoo.data.engine.functions._W_._W_MESSAGE_SENDTOALL;
import com.meiqi.liduoo.data.engine.functions._W_._W_MESSAGE_SENDTOGROUP;
import com.meiqi.liduoo.data.engine.functions._W_._W_MESSAGE_SENDTOUSER;
import com.meiqi.liduoo.data.engine.functions._W_._W_OAUTH_GETPAGEURL;
import com.meiqi.liduoo.data.engine.functions._W_._W_OAUTH_USER;
import com.meiqi.liduoo.data.engine.functions._W_._W_QRCODE_CREATE;
import com.meiqi.liduoo.data.engine.functions._W_._W_RMI_CALL;
import com.meiqi.liduoo.data.engine.functions._W_._W_RMI_GETDATA;
import com.meiqi.liduoo.data.engine.functions._W_._W_SMS_SEND;
import com.meiqi.liduoo.data.engine.functions._W_._W_SQL_EXECUTE;
import com.meiqi.liduoo.data.engine.functions._W_._W_SQL_TRANSBEGIN;
import com.meiqi.liduoo.data.engine.functions._W_._W_SQL_TRANSCOMMIT;
import com.meiqi.liduoo.data.engine.functions._W_._W_SQL_TRANSROLLBACK;
import com.meiqi.liduoo.data.engine.functions._W_._W_SYS_CALLBACKIP;
import com.meiqi.liduoo.data.engine.functions._W_._W_SYS_SHORTURL;
import com.meiqi.liduoo.data.engine.functions._W_._W_TEMPLATE_ADD;
import com.meiqi.liduoo.data.engine.functions._W_._W_TEMPLATE_SEND;
import com.meiqi.liduoo.data.engine.functions._W_._W_TEMPLATE_SETINDUSTRY;
import com.meiqi.liduoo.data.engine.functions._W_._W_USER_INFO;
import com.meiqi.liduoo.data.engine.functions._W_._W_USER_INFOLIST;
import com.meiqi.liduoo.data.engine.functions._W_._W_USER_LIST;
import com.meiqi.liduoo.data.engine.functions._W_._W_USER_SETREMARK;
import com.meiqi.liduoo.data.engine.functions._W_._W_USER_TOTAL;
import com.meiqi.liduoo.data.engine.functions._W_._W_UTIL_TOMAP;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_BATCHGET;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_CHANGECODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_CHECKCODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_CONSUMECODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_CREATE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_DECRYPTCODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_DELETE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETCOINSINFO;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETDETAIL;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETORDER;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETORDERLIST;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETPAYPRICE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_GETUSERCARDS;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_IMPORTCODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_INVALID;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_MODIFYSTOCK;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_MPNEWSCONTENT;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_PAYACTIVATE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_PAYCONFIRM;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_QRCODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_QUERYCODE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_RECHARGE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_SIGNATURE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_CARD_UPDATE;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_DC_CARD_BIZUININFO;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_DC_CARD_CARDINFO;
import com.meiqi.liduoo.data.engine.functions._W_CARD_._W_DC_MEMBER_CARDINFO;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_ARTICLE_SUMMARY;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_ARTICLE_TOTAL;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_INTERFACE_SUMMARY;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_INTERFACE_SUMMARY_HOUR;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSGDIST;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSGDIST_MONTH;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSGDIST_WEEK;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSG_HOUR;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSG_MONTH;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSG_SUMMARY;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_MSG_WEEK;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_CUMULATE;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_READ;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_READ_HOUR;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_SHARE;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_SHARE_HOUR;
import com.meiqi.liduoo.data.engine.functions._W_DC_._W_DC_USER_SUMMARY;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_DELETEPOI;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_GETPOI;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_GPSTOBAIDU;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_SEARCHNEARBY;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_STATICIMAGEURL;
import com.meiqi.liduoo.data.engine.functions._W_LBS_._W_LBS_UPDATEPOI;
import com.meiqi.liduoo.data.engine.functions._W_PAY_._W_PAY_PUSHORDER;
import com.meiqi.liduoo.data.engine.functions._W_PAY_._W_PAY_REDPACK_QUERY;
import com.meiqi.liduoo.data.engine.functions._W_PAY_._W_PAY_REDPACK_SEND;
import com.meiqi.liduoo.data.engine.functions._W_PAY_._W_PAY_TOFANS_QUERY;
import com.meiqi.liduoo.data.engine.functions._W_PAY_._W_PAY_TOFANS_SEND;

/**
 * User: Date: 13-11-4 Time: 下午12:35
 */
public abstract class Function {
	/**
	 * 函数实现信息
	 */
	private static final HashMap<String, Function> FUNCTIONS = new HashMap<String, Function>();

	static {

		FUNCTIONS.put(ARITH.NAME, new ARITH());
		FUNCTIONS.put(AND.NAME, new AND());
		FUNCTIONS.put(NOT.NAME, new NOT());
		FUNCTIONS.put(OR.NAME, new OR());
		FUNCTIONS.put(DATE.NAME, new DATE());
		FUNCTIONS.put(ABS.NAME, new ABS());
		FUNCTIONS.put(ROUND.NAME, new ROUND());
		FUNCTIONS.put(T.NAME, new T());
		FUNCTIONS.put(TRIM.NAME, new TRIM());
		FUNCTIONS.put(FLOOR.NAME, new FLOOR());
		FUNCTIONS.put(NOW.NAME, new NOW());
		FUNCTIONS.put(ROUNDDOWN.NAME, new ROUNDDOWN());
		FUNCTIONS.put(ROUNDUP.NAME, new ROUNDUP());
		FUNCTIONS.put(CONCATENATE.NAME, new CONCATENATE());
		FUNCTIONS.put(FIND.NAME, new FIND());
		FUNCTIONS.put(FIND2.NAME, new FIND2());
		FUNCTIONS.put(EXACT.NAME, new EXACT());
		FUNCTIONS.put(CEILING.NAME, new CEILING());
		FUNCTIONS.put(COUNTA.NAME, new COUNTA());
		FUNCTIONS.put(COUNTBLANK.NAME, new COUNTBLANK());
		FUNCTIONS.put(FROM_UNIXTIME.NAME, new FROM_UNIXTIME());
		FUNCTIONS.put(DAY.NAME, new DAY());
		FUNCTIONS.put(DAYS360.NAME, new DAYS360());
		FUNCTIONS.put(INT.NAME, new INT());
		FUNCTIONS.put(HOUR.NAME, new HOUR());
		FUNCTIONS.put(MINUTE.NAME, new MINUTE());
		FUNCTIONS.put(MONTH.NAME, new MONTH());
		FUNCTIONS.put(SECOND.NAME, new SECOND());
		FUNCTIONS.put(YEAR.NAME, new YEAR());
		FUNCTIONS.put(LEN.NAME, new LEN());
		FUNCTIONS.put(MID.NAME, new MID());
		FUNCTIONS.put(ROW.NAME, new ROW());
		FUNCTIONS.put(SUBSTITUTE.NAME, new SUBSTITUTE());
		FUNCTIONS.put(MOD.NAME, new MOD());
		FUNCTIONS.put(UPPER.NAME, new UPPER());
		FUNCTIONS.put(TIME.NAME, new TIME());
		FUNCTIONS.put(DATETIME.NAME, new DATETIME());
		FUNCTIONS.put(VLOOKUP.NAME, new VLOOKUP());
		FUNCTIONS.put(RAND.NAME, new RAND());
		FUNCTIONS.put(LEFT.NAME, new LEFT());
		FUNCTIONS.put(RIGHT.NAME, new RIGHT());
		FUNCTIONS.put(TEXT.NAME, new TEXT());
		FUNCTIONS.put(SIGN.NAME, new SIGN());
		FUNCTIONS.put(LN.NAME, new LN());
		FUNCTIONS.put(ISEVEN.NAME, new ISEVEN());
		FUNCTIONS.put(EVEN.NAME, new EVEN());
		FUNCTIONS.put(ISNUMBER.NAME, new ISNUMBER());
		FUNCTIONS.put(ISTEXT.NAME, new ISTEXT());
		FUNCTIONS.put(PI.NAME, new PI());
		FUNCTIONS.put(COS.NAME, new COS());
		FUNCTIONS.put(SIN.NAME, new SIN());
		FUNCTIONS.put(SQRT.NAME, new SQRT());
		FUNCTIONS.put(TAN.NAME, new TAN());
		FUNCTIONS.put(REPLACE.NAME, new REPLACE());
		FUNCTIONS.put(TODAY.NAME, new TODAY());
		FUNCTIONS.put(SEARCH.NAME, new SEARCH());
		FUNCTIONS.put(DATEDIF.NAME, new DATEDIF());
		FUNCTIONS.put(EVAL.NAME, new EVAL());
		FUNCTIONS.put(ISEMPTY.NAME, new ISEMPTY());
		FUNCTIONS.put(TWO_D_SUB.NAME, new TWO_D_SUB());
		FUNCTIONS.put(RANKSTR.NAME, new RANKSTR());
		FUNCTIONS.put(SUMIFSBYGROUP.NAME, new SUMIFSBYGROUP());
		FUNCTIONS.put(COUNTIFSBYGROUP.NAME, new COUNTIFSBYGROUP());
		FUNCTIONS.put(STRAMOUNT.NAME, new STRAMOUNT());
		FUNCTIONS.put(WEEKDAY.NAME, new WEEKDAY());
		FUNCTIONS.put(CONCATBYGROUP.NAME, new CONCATBYGROUP());
		FUNCTIONS.put(COUNTBYGROUP.NAME, new COUNTBYGROUP());
		FUNCTIONS.put(SUMBYGROUP.NAME, new SUMBYGROUP());
		FUNCTIONS.put(JSONFORMAT.NAME, new JSONFORMAT());
		FUNCTIONS.put(RANKBYGROUP.NAME, new RANKBYGROUP());
		FUNCTIONS.put(JSONOBJ.NAME, new JSONOBJ());
		FUNCTIONS.put(SMALL.NAME, new SMALL());
		FUNCTIONS.put(LARGE.NAME, new LARGE());
		FUNCTIONS.put(WEEKNUM.NAME, new WEEKNUM());
		FUNCTIONS.put(_WEKA_KMEANS.NAME, new _WEKA_KMEANS());
		FUNCTIONS.put(_WEKA_APRIORI.NAME, new _WEKA_APRIORI());
		FUNCTIONS.put(DISIFS.NAME, new DISIFS());
		FUNCTIONS.put(MODE.NAME, new MODE());
		FUNCTIONS.put(HISTPLOT.NAME, new HISTPLOT());
		FUNCTIONS.put(SCATTEPLOT.NAME, new SCATTEPLOT());
		FUNCTIONS.put(SCATTE3DPLOT.NAME, new SCATTE3DPLOT());
		FUNCTIONS.put(K_MEANSPLOT.NAME, new K_MEANSPLOT());
		FUNCTIONS.put(SUBSTITUTEMORE.NAME, new SUBSTITUTEMORE());
		FUNCTIONS.put(DISTINCT.NAME, new DISTINCT());
		FUNCTIONS.put(EDATE.NAME, new EDATE());
		FUNCTIONS.put(DELIVERY.NAME, new DELIVERY());
		FUNCTIONS.put(DELIVERY2.NAME, new DELIVERY2());
		FUNCTIONS.put(PHPUNSERIALIZE.NAME, new PHPUNSERIALIZE());
		FUNCTIONS.put(SPLIT_STR_JSON.NAME, new SPLIT_STR_JSON());
		FUNCTIONS.put(NUMBERFORMAT.NAME, new NUMBERFORMAT());
		FUNCTIONS.put(FREQUENT.NAME, new FREQUENT());
		FUNCTIONS.put(GETBYJSON.NAME, new GETBYJSON());
		FUNCTIONS.put(TWO_SUB_NUM.NAME, new TWO_SUB_NUM());
		FUNCTIONS.put(PINYIN.NAME, new PINYIN());

		FUNCTIONS.put(_O_CONCAT.NAME, new _O_CONCAT());
		FUNCTIONS.put(_O_CONCATBYPARA.NAME, new _O_CONCATBYPARA());
		FUNCTIONS.put(_O_COUNT.NAME, new _O_COUNT());
		FUNCTIONS.put(_O_COUNTBYGROUP.NAME, new _O_COUNTBYGROUP());
		FUNCTIONS.put(_O_COUNTBYPARA.NAME, new _O_COUNTBYPARA());
		FUNCTIONS.put(_O_COUNTIF.NAME, new _O_COUNTIF());
		FUNCTIONS.put(_O_COUNTIFBYGROUP.NAME, new _O_COUNTIFBYGROUP());
		FUNCTIONS.put(_O_COUNTIFBYPARA.NAME, new _O_COUNTIFBYPARA());
		FUNCTIONS.put(_O_COUNTIFS.NAME, new _O_COUNTIFS());
		FUNCTIONS.put(_O_COUNTIFSBYPARA.NAME, new _O_COUNTIFSBYPARA());
		FUNCTIONS.put(_O_GET.NAME, new _O_GET());
		FUNCTIONS.put(_O_GETBYPARA.NAME, new _O_GETBYPARA());
		FUNCTIONS.put(_O_GETBYPARA2.NAME, new _O_GETBYPARA2());
		FUNCTIONS.put(_O_MAX.NAME, new _O_MAX());
		FUNCTIONS.put(_O_MAXBYGROUP.NAME, new _O_MAXBYGROUP());
		FUNCTIONS.put(_O_MAXBYPARA.NAME, new _O_MAXBYPARA());
		FUNCTIONS.put(_O_MIN.NAME, new _O_MIN());
		FUNCTIONS.put(_O_MINBYGROUP.NAME, new _O_MINBYGROUP());
		FUNCTIONS.put(_O_MINBYPARA.NAME, new _O_MINBYPARA());
		FUNCTIONS.put(_O_PERCENTINMAX.NAME, new _O_PERCENTINMAX());
		FUNCTIONS.put(_O_RANK.NAME, new _O_RANK());
		FUNCTIONS.put(_O_SUM.NAME, new _O_SUM());
		FUNCTIONS.put(_O_SUMBYGROUP.NAME, new _O_SUMBYGROUP());
		FUNCTIONS.put(_O_SUMBYPARA.NAME, new _O_SUMBYPARA());
		FUNCTIONS.put(_O_SUMIF.NAME, new _O_SUMIF());
		FUNCTIONS.put(_O_SUMIFBYGROUP.NAME, new _O_SUMIFBYGROUP());
		FUNCTIONS.put(_O_SUMIFBYPARA.NAME, new _O_SUMIFBYPARA());
		FUNCTIONS.put(_O_SUMIFS.NAME, new _O_SUMIFS());
		FUNCTIONS.put(_O_SUMIFSBYPARA.NAME, new _O_SUMIFSBYPARA());
		FUNCTIONS.put(_O_VLOOKUP.NAME, new _O_VLOOKUP());
		FUNCTIONS.put(_O_VLOOKUPBYPARA.NAME, new _O_VLOOKUPBYPARA());
		FUNCTIONS.put(_O_RANKBYPARA.NAME, new _O_RANKBYPARA());
		FUNCTIONS.put(_O_RANKSTR.NAME, new _O_RANKSTR());
		FUNCTIONS.put(_O_CONCATBYGROUP.NAME, new _O_CONCATBYGROUP());
		FUNCTIONS.put(_O_Product.NAME, new _O_Product());
		FUNCTIONS.put(_O_JSON.NAME, new _O_JSON());
		FUNCTIONS.put(_O_JSONBYPARA.NAME, new _O_JSONBYPARA());
		FUNCTIONS.put(_O_JSONBYPARA2.NAME, new _O_JSONBYPARA2());
		FUNCTIONS.put(_O_MODE.NAME, new _O_MODE());
		FUNCTIONS.put(_O_MODEBYPARA.NAME, new _O_MODEBYPARA());
		FUNCTIONS.put(_O_HISTPLOT.NAME, new _O_HISTPLOT());
		FUNCTIONS.put(_O_HISTBYPARAPLOT.NAME, new _O_HISTBYPARAPLOT());
		FUNCTIONS.put(_O_SCATTEPLOT.NAME, new _O_SCATTEPLOT());
		FUNCTIONS.put(_O_SCATTE3DPLOT.NAME, new _O_SCATTE3DPLOT());
		FUNCTIONS.put(_O_K_MEANSPLOT.NAME, new _O_K_MEANSPLOT());
		FUNCTIONS.put(_O_JSONTABLEBYPARA.NAME, new _O_JSONTABLEBYPARA());
		FUNCTIONS.put(_O_COUNTDIS.NAME, new _O_COUNTDIS());
		FUNCTIONS.put(_O_COUNTDISBYPARA.NAME, new _O_COUNTDISBYPARA());
		FUNCTIONS.put(_O_FREQUENT.NAME, new _O_FREQUENT());

		FUNCTIONS.put(_T_SOLR.NAME, new _T_SOLR());

		REG_MATCH reg_match = new REG_MATCH();

		FUNCTIONS.put(REG_MATCH.NAME, reg_match);
		FUNCTIONS.put("_S_" + REG_MATCH.NAME, reg_match);

		MD5 md5 = new MD5();
		FUNCTIONS.put(MD5.NAME, md5);
		FUNCTIONS.put("_S_" + MD5.NAME, md5);

		GET get = new GET();
		FUNCTIONS.put(GET.NAME, get);
		FUNCTIONS.put("_S_" + GET.NAME, get);

		IN in = new IN();
		FUNCTIONS.put(IN.NAME, in);
		FUNCTIONS.put("_S_" + IN.NAME, in);

		AVERAGEBYGROUP averagebygroup = new AVERAGEBYGROUP();
		FUNCTIONS.put(AVERAGEBYGROUP.NAME, averagebygroup);
		FUNCTIONS.put("_S_" + AVERAGEBYGROUP.NAME, averagebygroup);

		CONCAT concat = new CONCAT();
		FUNCTIONS.put(CONCAT.NAME, concat);
		FUNCTIONS.put("_S_" + CONCAT.NAME, concat);

		COUNT count = new COUNT();
		FUNCTIONS.put(COUNT.NAME, count);
		FUNCTIONS.put("_S_" + COUNT.NAME, count);

		COUNTIF countif = new COUNTIF();
		FUNCTIONS.put(COUNTIF.NAME, countif);
		FUNCTIONS.put("_S_" + COUNTIF.NAME, countif);

		COUNTIFS countifs = new COUNTIFS();
		FUNCTIONS.put(COUNTIFS.NAME, countifs);
		FUNCTIONS.put("_S_" + COUNTIFS.NAME, countifs);

		DIS dis = new DIS();
		FUNCTIONS.put(DIS.NAME, dis);
		FUNCTIONS.put("_S_" + DIS.NAME, dis);

		LOCAT locat = new LOCAT();
		FUNCTIONS.put(LOCAT.NAME, locat);
		FUNCTIONS.put("_S_" + LOCAT.NAME, locat);

		MAX max = new MAX();
		FUNCTIONS.put(MAX.NAME, max);
		FUNCTIONS.put("_S_" + MAX.NAME, max);

		MAXBYGROUP maxbygroup = new MAXBYGROUP();
		FUNCTIONS.put(MAXBYGROUP.NAME, maxbygroup);
		FUNCTIONS.put("_S_" + MAXBYGROUP.NAME, maxbygroup);

		MIN min = new MIN();
		FUNCTIONS.put(MIN.NAME, min);
		FUNCTIONS.put("_S_" + MIN.NAME, min);

		MINBYGROUP minbygroup = new MINBYGROUP();
		FUNCTIONS.put(MINBYGROUP.NAME, minbygroup);
		FUNCTIONS.put("_S_" + MINBYGROUP.NAME, minbygroup);

		PERCENTINMAX percentinmax = new PERCENTINMAX();
		FUNCTIONS.put(PERCENTINMAX.NAME, percentinmax);
		FUNCTIONS.put("_S_" + PERCENTINMAX.NAME, percentinmax);

		RANK rank = new RANK();
		FUNCTIONS.put(RANK.NAME, rank);
		FUNCTIONS.put("_S_" + RANK.NAME, rank);

		SUM sum = new SUM();
		FUNCTIONS.put(SUM.NAME, sum);
		FUNCTIONS.put("_S_" + SUM.NAME, sum);

		SUMIF sumif = new SUMIF();
		FUNCTIONS.put(SUMIF.NAME, sumif);
		FUNCTIONS.put("_S_" + SUMIF.NAME, sumif);

		SUMIFS sumifs = new SUMIFS();
		FUNCTIONS.put(SUMIFS.NAME, sumifs);
		FUNCTIONS.put("_S_" + SUMIFS.NAME, sumifs);

		FUNCTIONS.put(AVERAGE.NAME, new AVERAGE());
		UNIXTIME unixtime = new UNIXTIME();
		FUNCTIONS.put(UNIXTIME.NAME, unixtime);
		FUNCTIONS.put("_S_" + UNIXTIME.NAME, unixtime);

		FUNCTIONS.put(CASE.NAME, new CASE());
		FUNCTIONS.put(CATEGORIESREPLACE.NAME, new CATEGORIESREPLACE());
		FUNCTIONS.put(GETIMGURL.NAME, new GETIMGURL());
		FUNCTIONS.put(GETIMGCOUNT.NAME, new GETIMGCOUNT());
		FUNCTIONS.put(FOLDERCOUNT.NAME, new FOLDERCOUNT());
		FUNCTIONS.put(GETDATABYSOLR.NAME, new GETDATABYSOLR());
		FUNCTIONS.put(FORMATDATA.NAME, new FORMATDATA());
		FUNCTIONS.put(GET_SAME_CHARS.NAME, new GET_SAME_CHARS());
		FUNCTIONS.put(REMOVE_SAME_CHARS.NAME, new REMOVE_SAME_CHARS());
		FUNCTIONS.put(SEND_EMAIL.NAME, new SEND_EMAIL());
		FUNCTIONS.put(HTMLTABLE.NAME, new HTMLTABLE());
		FUNCTIONS.put(FINDPHONEAREA.NAME, new FINDPHONEAREA());
		FUNCTIONS.put("CHARSMARRY", new CHARSMARRY());
		FUNCTIONS.put(GETMEMCACHEDATA.NAME, new GETMEMCACHEDATA());
		FUNCTIONS.put(GETPOLYGONAREA.NAME, new GETPOLYGONAREA());
		FUNCTIONS.put(SUMWEIGHT.NAME, new SUMWEIGHT());
		FUNCTIONS.put(GETPRODUCTDIVISOR.NAME, new GETPRODUCTDIVISOR());
		FUNCTIONS.put(LOADSERVICE.NAME, new LOADSERVICE());
		FUNCTIONS.put(CLEARCACHE.NAME, new CLEARCACHE());
		FUNCTIONS.put(GETXMLVALUE.NAME, new GETXMLVALUE());
		FUNCTIONS.put(COORDINATECALCULATE.NAME, new COORDINATECALCULATE());

        FUNCTIONS.put(SIMPLEBETCHMUSHROOM.NAME, new SIMPLEBETCHMUSHROOM());
        FUNCTIONS.put(MUSHROOM.NAME, new MUSHROOM());
        FUNCTIONS.put(CODE.NAME, new CODE());
        FUNCTIONS.put(UTF8.NAME, new UTF8());
        FUNCTIONS.put(REFRESHSOLR.NAME, new REFRESHSOLR());
        
		FUNCTIONS.put(_W_DC_ARTICLE_SUMMARY.NAME, new _W_DC_ARTICLE_SUMMARY());
		FUNCTIONS.put(_W_DC_ARTICLE_TOTAL.NAME, new _W_DC_ARTICLE_TOTAL());
		FUNCTIONS.put(_W_DC_INTERFACE_SUMMARY_HOUR.NAME, new _W_DC_INTERFACE_SUMMARY_HOUR());
		FUNCTIONS.put(_W_DC_INTERFACE_SUMMARY.NAME, new _W_DC_INTERFACE_SUMMARY());
		FUNCTIONS.put(_W_DC_MSG_HOUR.NAME, new _W_DC_MSG_HOUR());
		FUNCTIONS.put(_W_DC_MSG_MONTH.NAME, new _W_DC_MSG_MONTH());
		FUNCTIONS.put(_W_DC_MSG_SUMMARY.NAME, new _W_DC_MSG_SUMMARY());
		FUNCTIONS.put(_W_DC_MSG_WEEK.NAME, new _W_DC_MSG_WEEK());
		FUNCTIONS.put(_W_DC_MSGDIST_MONTH.NAME, new _W_DC_MSGDIST_MONTH());
		FUNCTIONS.put(_W_DC_MSGDIST_WEEK.NAME, new _W_DC_MSGDIST_WEEK());
		FUNCTIONS.put(_W_DC_MSGDIST.NAME, new _W_DC_MSGDIST());
		FUNCTIONS.put(_W_DC_USER_CUMULATE.NAME, new _W_DC_USER_CUMULATE());
		FUNCTIONS.put(_W_DC_USER_READ_HOUR.NAME, new _W_DC_USER_READ_HOUR());
		FUNCTIONS.put(_W_DC_USER_READ.NAME, new _W_DC_USER_READ());
		FUNCTIONS.put(_W_DC_USER_SHARE_HOUR.NAME, new _W_DC_USER_SHARE_HOUR());
		FUNCTIONS.put(_W_DC_USER_SHARE.NAME, new _W_DC_USER_SHARE());
		FUNCTIONS.put(_W_DC_USER_SUMMARY.NAME, new _W_DC_USER_SUMMARY());
		FUNCTIONS.put(_W_CUSTOM_ACCOUNTLIST.NAME, new _W_CUSTOM_ACCOUNTLIST());
		FUNCTIONS.put(_W_CUSTOM_ONLINELIST.NAME, new _W_CUSTOM_ONLINELIST());
		FUNCTIONS.put(_W_CUSTOM_ADDACCOUNT.NAME, new _W_CUSTOM_ADDACCOUNT());
		FUNCTIONS.put(_W_CUSTOM_DELETEACCOUNT.NAME, new _W_CUSTOM_DELETEACCOUNT());
		FUNCTIONS.put(_W_CUSTOM_SENDMESSAGE.NAME, new _W_CUSTOM_SENDMESSAGE());
		FUNCTIONS.put(_W_CUSTOM_UPDATE_ACCOUNT.NAME, new _W_CUSTOM_UPDATE_ACCOUNT());
		FUNCTIONS.put(_W_CUSTOM_UPLOAD_HEADIMG.NAME, new _W_CUSTOM_UPLOAD_HEADIMG());
		FUNCTIONS.put(_W_GROUP_CREATE.NAME, new _W_GROUP_CREATE());
		FUNCTIONS.put(_W_GROUP_DELETE.NAME, new _W_GROUP_DELETE());
		FUNCTIONS.put(_W_GROUP_GETBYOPENID.NAME, new _W_GROUP_GETBYOPENID());
		FUNCTIONS.put(_W_GROUP_LIST.NAME, new _W_GROUP_LIST());
		FUNCTIONS.put(_W_GROUP_MOVEUSER.NAME, new _W_GROUP_MOVEUSER());
		FUNCTIONS.put(_W_GROUP_UPDATE.NAME, new _W_GROUP_UPDATE());
		FUNCTIONS.put(_W_JSAPI_SIGNATURE.NAME, new _W_JSAPI_SIGNATURE());
		FUNCTIONS.put(_W_MATERIAL_ADDFILE.NAME, new _W_MATERIAL_ADDFILE());
		FUNCTIONS.put(_W_MATERIAL_ADDNEWS.NAME, new _W_MATERIAL_ADDNEWS());
		FUNCTIONS.put(_W_MATERIAL_BATCHGET.NAME, new _W_MATERIAL_BATCHGET());
		FUNCTIONS.put(_W_MATERIAL_COUNT.NAME, new _W_MATERIAL_COUNT());
		FUNCTIONS.put(_W_MATERIAL_DELETE.NAME, new _W_MATERIAL_DELETE());
		FUNCTIONS.put(_W_MATERIAL_DOWNLOAD.NAME, new _W_MATERIAL_DOWNLOAD());
		FUNCTIONS.put(_W_MEDIA_DOWNLOAD.NAME, new _W_MEDIA_DOWNLOAD());
		FUNCTIONS.put(_W_MEDIA_UPLOADIMAGE.NAME, new _W_MEDIA_UPLOADIMAGE());
		FUNCTIONS.put(_W_MEDIA_UPLOADMEDIA.NAME, new _W_MEDIA_UPLOADMEDIA());
		FUNCTIONS.put(_W_MEDIA_UPLOADNEWS.NAME, new _W_MEDIA_UPLOADNEWS());
		FUNCTIONS.put(_W_MENU_CREATE.NAME, new _W_MENU_CREATE());
		FUNCTIONS.put(_W_MENU_DELETE.NAME, new _W_MENU_DELETE());
		FUNCTIONS.put(_W_MENU_GET.NAME, new _W_MENU_GET());
		FUNCTIONS.put(_W_MESSAGE_SENDTOALL.NAME, new _W_MESSAGE_SENDTOALL());
		FUNCTIONS.put(_W_MESSAGE_SENDTOGROUP.NAME, new _W_MESSAGE_SENDTOGROUP());
		FUNCTIONS.put(_W_MESSAGE_PREVIEW.NAME, new _W_MESSAGE_PREVIEW());
		FUNCTIONS.put(_W_MESSAGE_SENDTOUSER.NAME, new _W_MESSAGE_SENDTOUSER());
		FUNCTIONS.put(_W_OAUTH_GETPAGEURL.NAME, new _W_OAUTH_GETPAGEURL());
		FUNCTIONS.put(_W_QRCODE_CREATE.NAME, new _W_QRCODE_CREATE());
		FUNCTIONS.put(_W_SYS_CALLBACKIP.NAME, new _W_SYS_CALLBACKIP());
		FUNCTIONS.put(_W_SYS_SHORTURL.NAME, new _W_SYS_SHORTURL());
		FUNCTIONS.put(_W_TEMPLATE_ADD.NAME, new _W_TEMPLATE_ADD());
		FUNCTIONS.put(_W_TEMPLATE_SEND.NAME, new _W_TEMPLATE_SEND());
		FUNCTIONS.put(_W_TEMPLATE_SETINDUSTRY.NAME, new _W_TEMPLATE_SETINDUSTRY());
		FUNCTIONS.put(_W_USER_INFO.NAME, new _W_USER_INFO());
		FUNCTIONS.put(_W_USER_INFOLIST.NAME, new _W_USER_INFOLIST());
		FUNCTIONS.put(_W_USER_LIST.NAME, new _W_USER_LIST());
		FUNCTIONS.put(_W_USER_SETREMARK.NAME, new _W_USER_SETREMARK());
		FUNCTIONS.put(_W_USER_TOTAL.NAME, new _W_USER_TOTAL());
		FUNCTIONS.put(_W_OAUTH_USER.NAME, new _W_OAUTH_USER());
		
		FUNCTIONS.put(_W_SQL_TRANSBEGIN.NAME, new _W_SQL_TRANSBEGIN());
		FUNCTIONS.put(_W_SQL_TRANSCOMMIT.NAME, new _W_SQL_TRANSCOMMIT());
		FUNCTIONS.put(_W_SQL_TRANSROLLBACK.NAME, new _W_SQL_TRANSROLLBACK());
		FUNCTIONS.put(_W_SQL_EXECUTE.NAME, new _W_SQL_EXECUTE());
		FUNCTIONS.put(_O_GETROW.NAME, new _O_GETROW());
		FUNCTIONS.put(_W_PAY_REDPACK_SEND.NAME, new _W_PAY_REDPACK_SEND());
		FUNCTIONS.put(_W_PAY_REDPACK_QUERY.NAME, new _W_PAY_REDPACK_QUERY());
		FUNCTIONS.put(_W_PAY_TOFANS_SEND.NAME, new _W_PAY_TOFANS_SEND());
		FUNCTIONS.put(_W_PAY_TOFANS_QUERY.NAME, new _W_PAY_TOFANS_QUERY());
		FUNCTIONS.put(_W_PAY_PUSHORDER.NAME, new _W_PAY_PUSHORDER());
		
		FUNCTIONS.put(_W_SMS_SEND.NAME, new _W_SMS_SEND());
		FUNCTIONS.put(_W_RMI_GETDATA.NAME, new _W_RMI_GETDATA());
		
		FUNCTIONS.put(_W_MESSAGE_FORMAT.NAME, new _W_MESSAGE_FORMAT());
		FUNCTIONS.put(_W_UTIL_TOMAP.NAME, new _W_UTIL_TOMAP());
		//-Added by guianzhou,百度地图功能函数，2016-1-15
		FUNCTIONS.put(_W_LBS_DELETEPOI.NAME, new _W_LBS_DELETEPOI());
		FUNCTIONS.put(_W_LBS_GETPOI.NAME, new _W_LBS_GETPOI());
		FUNCTIONS.put(_W_LBS_GPSTOBAIDU.NAME, new _W_LBS_GPSTOBAIDU());
		FUNCTIONS.put(_W_LBS_SEARCHNEARBY.NAME, new _W_LBS_SEARCHNEARBY());
		FUNCTIONS.put(_W_LBS_STATICIMAGEURL.NAME, new _W_LBS_STATICIMAGEURL());
		FUNCTIONS.put(_W_LBS_UPDATEPOI.NAME, new _W_LBS_UPDATEPOI());
		
		//-Added by guianzhou,微信卡券功能函数，2016-01-26
		FUNCTIONS.put(_W_CARD_CHANGECODE.NAME, new  _W_CARD_CHANGECODE());
		FUNCTIONS.put(_W_CARD_CONSUMECODE.NAME, new  _W_CARD_CONSUMECODE());
		FUNCTIONS.put(_W_CARD_CREATE.NAME, new  _W_CARD_CREATE());
		FUNCTIONS.put(_W_CARD_DECRYPTCODE.NAME, new  _W_CARD_DECRYPTCODE());
		FUNCTIONS.put(_W_CARD_DELETE.NAME, new  _W_CARD_DELETE());
		FUNCTIONS.put(_W_CARD_GETDETAIL.NAME, new _W_CARD_GETDETAIL ());
		FUNCTIONS.put(_W_CARD_GETUSERCARDS.NAME, new  _W_CARD_GETUSERCARDS());
		FUNCTIONS.put(_W_CARD_INVALID.NAME, new _W_CARD_INVALID ());
		FUNCTIONS.put(_W_CARD_MODIFYSTOCK.NAME, new _W_CARD_MODIFYSTOCK ());
		FUNCTIONS.put(_W_CARD_QUERYCODE.NAME, new  _W_CARD_QUERYCODE());
		FUNCTIONS.put(_W_CARD_SIGNATURE.NAME, new _W_CARD_SIGNATURE ());
		FUNCTIONS.put(_W_CARD_UPDATE.NAME, new _W_CARD_UPDATE ());
		FUNCTIONS.put(_W_DC_CARD_BIZUININFO.NAME, new _W_DC_CARD_BIZUININFO ());
		FUNCTIONS.put(_W_CARD_CHECKCODE.NAME, new _W_CARD_CHECKCODE ());
		FUNCTIONS.put(_W_CARD_IMPORTCODE.NAME, new _W_CARD_IMPORTCODE ());
		FUNCTIONS.put(_W_CARD_MPNEWSCONTENT.NAME, new _W_CARD_MPNEWSCONTENT ());
		FUNCTIONS.put(_W_CARD_BATCHGET.NAME, new _W_CARD_BATCHGET ());
		FUNCTIONS.put(_W_CARD_QRCODE.NAME, new _W_CARD_QRCODE ());
		
		FUNCTIONS.put(_W_CARD_GETCOINSINFO.NAME, new _W_CARD_GETCOINSINFO ());
		FUNCTIONS.put(_W_CARD_GETORDER.NAME, new _W_CARD_GETORDER ());
		FUNCTIONS.put(_W_CARD_GETORDERLIST.NAME, new _W_CARD_GETORDERLIST ());
		FUNCTIONS.put(_W_CARD_GETPAYPRICE.NAME, new _W_CARD_GETPAYPRICE ());
		FUNCTIONS.put(_W_CARD_PAYACTIVATE.NAME, new _W_CARD_PAYACTIVATE ());
		FUNCTIONS.put(_W_CARD_PAYCONFIRM.NAME, new _W_CARD_PAYCONFIRM ());
		FUNCTIONS.put(_W_CARD_RECHARGE.NAME, new _W_CARD_RECHARGE ());

		FUNCTIONS.put(_W_DC_CARD_CARDINFO.NAME, new  _W_DC_CARD_CARDINFO());
		FUNCTIONS.put(_W_DC_MEMBER_CARDINFO.NAME, new _W_DC_MEMBER_CARDINFO ());
		

		FUNCTIONS.put(WX_WEB_API.NAME, new WX_WEB_API ());

		FUNCTIONS.put(BASE64_ENCODE.NAME, new BASE64_ENCODE ());
		FUNCTIONS.put(BASE64_DECODE.NAME, new BASE64_DECODE ());
		FUNCTIONS.put(_W_RMI_CALL.NAME, new _W_RMI_CALL ());
		
	}

	/**
	 * calInfo里有当前数据源的名称, 参数, 缓存等 args是多个参数
	 *
	 * @param calInfo
	 * @param args
	 * @return
	 * @throws RengineException
	 * @throws CalculateError
	 */
	public abstract Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError;

	/**
	 * 获取对应函数的实现
	 *
	 * @param name
	 * @return
	 */
	public static Function getFunction(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		return FUNCTIONS.get(name);
	}

	/**
	 * 是否可以进行数值操作
	 *
	 * @param input
	 * @return
	 * @throws RengineException
	 */
	protected static boolean canNumberOP(Object input) throws RengineException {
		final Type type = DataUtil.getType(input);

		return type == Type.DOUBLE || type == Type.LONG || type == Type.DATE
				|| (type == Type.STRING && canParseDouble((String) input));
	}

	private static boolean canParseDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取对应的参数
	 *
	 * @param args
	 *            入参, 以.json结尾的会进行JSON解析
	 * @param start
	 *            从哪个位置开始取参数
	 * @param param
	 *            原始父级的参数
	 * @param isBYPARA
	 *            是否默认不继承参数, 如_O_GETBYPARA等函数默认不继承, 但是_O_SUM默认继承
	 * @return
	 * @throws RengineException
	 */
	protected static Map<String, Object> getParam(Object[] args, int start, Map<String, Object> param, boolean isBYPARA)
			throws RengineException {
		if (args.length > start) {
			List<Object> params = new ArrayList<Object>();
			Map<String, Object> currentParam = new HashMap<String, Object>();
			boolean isInherit = false;

			for (int i = start; i < args.length; i += 2) {
				if (i + 1 >= args.length) {
					final Object tmp = args[i];
					if (tmp instanceof Boolean) {
						isInherit = (Boolean) tmp;
					}
					break;
				}

				final String value = DataUtil.getStringValue(args[i]);
				String key = DataUtil.getStringValue(args[i + 1]);

				if (key.length() == 0 || value.length() == 0) {
					continue;
				}

				Object trueValue = value;

				if (key.endsWith(".json")) {
					if (key.length() == 5) {
						DataUtil.inheritParam(currentParam, DataUtil.parse(value));
						continue;
					} else {
						key = key.substring(0, key.length() - 5);
						trueValue = DataUtil.parse(value);

					}
				}

				params.add(trueValue);
				params.add(key);
			}

			for (int i = 0; i < params.size(); i += 2) {
				currentParam.put((String) params.get(i + 1), params.get(i));
			}

			if (isInherit) {
				DataUtil.inheritParam(currentParam, param);
			}

			return currentParam;
		} else {
			if (isBYPARA) {
				return DataUtil.EMPTY;
			} else {
				return param;
			}
		}
	}

}
