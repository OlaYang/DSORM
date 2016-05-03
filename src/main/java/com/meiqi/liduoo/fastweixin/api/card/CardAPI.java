package com.meiqi.liduoo.fastweixin.api.card;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meiqi.liduoo.fastweixin.api.BaseAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.QrcodeType;
import com.meiqi.liduoo.fastweixin.api.enums.ResultType;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardBatchGetResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardCheckCodeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardConsumeCodeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardCreateResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardDecryptCodeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardDepositCodeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardGetCoinsInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardGetMpnewsContentResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardGetPayPriceResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardGetUserCardsResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardPayActivateResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardQueryCodeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardRechargeResponse;
import com.meiqi.liduoo.fastweixin.api.response.CardUpdateResponse;
import com.meiqi.liduoo.fastweixin.api.response.GetCardBizuinInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.GetCardCardInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.GetCardMemberInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.QrcodeResponse;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 菜单相关API 1.3.7支持个性化菜单
 *
 * @author peiyu
 * @since 1.2
 */
public class CardAPI extends BaseAPI {

	private static final Logger LOG = LoggerFactory.getLogger(CardAPI.class);

	public CardAPI(ApiConfig config) {
		super(config);
	}

	/**
	 * 创建卡券
	 * 微信API文档：http://mp.weixin.qq.com/wiki/15/e33671f4ef511b77755142b37502928f.
	 * html#.E4.BC.98.E6.83.A0.E5.88.B8
	 * 
	 * @param card
	 *            卡券对象
	 * @return 调用结果
	 */
	public CardCreateResponse createCard(String jsonCard) {
		BeanUtil.requireNonNull(jsonCard, "card is null");
		String url = BASE_API_URL + "card/create?access_token=#";
		// https://api.weixin.qq.com/card/create?access_token=ACCESS_TOKEN
		BaseResponse r = executePost(url, jsonCard);// card.toJsonString());
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardCreateResponse response = JSONUtil.toBean(resultJson, CardCreateResponse.class);
		return response;
	}

	/**
	 * 修改卡券信息接口
	 * 
	 * @param jsonCard
	 *            卡券信息JSON格式字符串
	 * @return CardUpdateResponse 修改结果信息
	 */
	public CardUpdateResponse updateCard(String jsonCard) {
		BeanUtil.requireNonNull(jsonCard, "card is null");
		String url = BASE_API_URL + "card/update?access_token=#";
		BaseResponse r = executePost(url, jsonCard);// card.toJsonString());
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardUpdateResponse response = JSONUtil.toBean(resultJson, CardUpdateResponse.class);
		return response;
	}

	/**
	 * 删除卡券
	 * 
	 * @param cardId
	 *            卡券ID
	 * @return
	 */
	public BaseResponse deleteCard(String cardId) {
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/delete?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		return r;
	}

	/**
	 * 获取js-sdk所需的签名，简化逻辑 不太在意随机数和时间戳的场景，使用更加方便
	 * 
	 * @param url
	 *            当前网页的URL，不包含#及其后面部分
	 * @return 签名以及相关参数
	 */
	public Map<String, Object> getSignature(Map<String, Object> params) {
		Map<String, Object> response = new HashMap<String, Object>();
		String cardApiTicket = this.config.getCardApiTicket();
		return getSignature(params, cardApiTicket);
	}

	public Map<String, Object> getSignature(Map<String, Object> params, String cardApiTicket) {
		Map<String, Object> response = new HashMap<String, Object>();
		if (!params.containsKey("nonce_str")) {
			params.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", ""));
		}
		if (!params.containsKey("timestamp")) {
			params.put("timestamp", System.currentTimeMillis() / 1000 + "");
		}
		// params.put("signType", "SHA1");
		try {
			WxCardSign signer = new WxCardSign();
			signer.AddData(cardApiTicket);
			for (String key : params.keySet()) {
				if (params.get(key) != null) {
					signer.AddData(params.get(key).toString());
				}
			}
			String sign = signer.GetSignature();

			response.put("cardSign", sign);
			response.putAll(params);

		} catch (Exception e) {
			LOG.error("获取签名异常:", e);
			response.put("errcode", ResultType.OTHER_ERROR.getCode().toString());
			response.put("errmsg", "获取签名异常");
			return response;
		}
		response.put("errcode", ResultType.SUCCESS.getCode().toString());

		return response;
	}

	/**
	 * 卡券线下核销-查询Code接口
	 * 
	 * @param code
	 *            卡券Code，单张卡券的唯一标准。
	 * @param cardId
	 *            卡券ID代表一类卡券。自定义code卡券必填。
	 * @param check_consume
	 *            是否校验code核销状态，填入true和false时的code异常状态返回数据不同。
	 * @return
	 */
	public CardQueryCodeResponse queryCode(String code, String cardId, boolean check_consume) {
		BeanUtil.requireNonNull(code, "code is null");
		String url = BASE_API_URL + "card/code/get?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("check_consume", check_consume);
		if (StrUtil.isNotBlank(cardId)) {
			params.put("card_id", cardId);
		}

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardQueryCodeResponse response = JSONUtil.toBean(resultJson, CardQueryCodeResponse.class);
		return response;
	}

	/**
	 * 核销卡券
	 * 
	 * @param code
	 *            需核销的Code码。
	 * @param cardId
	 *            卡券ID。创建卡券时use_custom_code填写true时必填。非自定义Code不必填写。
	 * @return
	 */
	public CardConsumeCodeResponse consumeCode(String code, String cardId) {
		BeanUtil.requireNonNull(code, "code is null");
		String url = BASE_API_URL + "card/code/consume?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		if (StrUtil.isNotBlank(cardId)) {
			params.put("card_id", cardId);
		}

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardConsumeCodeResponse response = JSONUtil.toBean(resultJson, CardConsumeCodeResponse.class);
		return response;
	}

	/**
	 * Code解码接口
	 * 
	 * code解码接口支持两种场景：
	 * 1.商家获取choos_card_info后，将card_id和encrypt_code字段通过解码接口，获取真实code。
	 * 2.卡券内跳转外链的签名中会对code进行加密处理，通过调用解码接口获取真实code。
	 * 
	 * @param encryptCode
	 *            加密的Code码
	 * @param cardId
	 *            卡券ID。创建卡券时use_custom_code填写true时必填。非自定义Code不必填写。
	 * @return
	 */
	public CardDecryptCodeResponse decryptCode(String encryptCode) {
		BeanUtil.requireNonNull(encryptCode, "encryptCode is null");
		String url = BASE_API_URL + "card/code/decrypt?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("encrypt_code", encryptCode);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardDecryptCodeResponse response = JSONUtil.toBean(resultJson, CardDecryptCodeResponse.class);
		return response;
	}

	/**
	 * 用于获取用户卡包里的，属于该appid下的卡券。
	 * 
	 * @param openid
	 *            需要查询的用户openid
	 * @param cardId
	 *            卡券ID。不填写时默认查询当前appid下的卡券。
	 * @return
	 */
	public CardGetUserCardsResponse getUserCards(String openid, String cardId) {
		BeanUtil.requireNonNull(openid, "openid is null");
		String url = BASE_API_URL + "card/user/getcardlist?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("openid", openid);
		if (StrUtil.isNotBlank(cardId)) {
			params.put("card_id", cardId);
		}

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		CardGetUserCardsResponse response = JSONUtil.toBean(resultJson, CardGetUserCardsResponse.class);
		return response;
	}

	/**
	 * 查询卡券字段详情及卡券所处状态。建议开发者调用卡券更新信息接口后调用该接口验证是否更新成功。
	 * 
	 * @param cardId
	 * @return String,由于微信返回数据比较复杂，先不做JSON解析
	 */
	public String getCardDetail(String cardId) {
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/get?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		// CardGetUserCardsponse response = JSONUtil.toBean(resultJson,
		// CardGetUserCardsponse.class);
		return resultJson;
	}

	/**
	 * 调用修改库存接口增减某张卡券的库存。
	 * 
	 * @param cardId
	 *            卡券ID
	 * @param changeValue
	 *            正数表示增加库存，负数表示减少库存
	 * @return BaseResponse
	 */
	public BaseResponse modifyStock(String cardId, int changeValue) {
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/modifystock?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("card_id", cardId);
		if (changeValue > 0) {
			params.put("increase_stock_value", changeValue);
		} else {
			params.put("reduce_stock_value", Math.abs(changeValue));
		}

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		return r;
	}

	/**
	 * 更改Code接口
	 * 
	 * @param code
	 *            需变更的Code码。
	 * @param newCode
	 *            变更后的有效Code码。
	 * @param cardId
	 *            卡券ID。自定义Code码卡券为必填。
	 * @return
	 */
	public BaseResponse changeCode(String code, String newCode, String cardId) {
		BeanUtil.requireNonNull(code, "code is null");
		BeanUtil.requireNonNull(newCode, "newCode is null");
		String url = BASE_API_URL + "card/code/update?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("new_code", newCode);
		if (StrUtil.isNotBlank(cardId)) {
			params.put("card_id", cardId);
		}

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		return r;
	}

	/**
	 * 设置卡券失效接口
	 * 
	 * @param code
	 *            设置失效的Code码。
	 * @param cardId
	 *            卡券ID。
	 * @return
	 */
	public BaseResponse invalid(String code, String cardId) {
		BeanUtil.requireNonNull(code, "code is null");
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/code/unavailable?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		return r;
	}

	/**
	 * 导入code接口
	 * 
	 * @param cardId
	 * @param codes
	 * @return
	 */
	public CardDepositCodeResponse depositCode(String cardId, String[] codes) {
		BeanUtil.requireNonNull(codes, "codes is null");
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/code/deposit?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", codes);
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardDepositCodeResponse response = JSONUtil.toBean(resultJson, CardDepositCodeResponse.class);

		return response;
	}

	/**
	 * 核查Code
	 * 
	 * @param cardId
	 * @param codes
	 * @return { "errcode":0, "errmsg":"ok"
	 *         "exist_code":["11111","22222","33333"],
	 *         "not_exist_code":["44444","55555"] }
	 */
	public CardCheckCodeResponse checkCode(String cardId, String[] codes) {
		BeanUtil.requireNonNull(codes, "codes is null");
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/code/checkcode?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", codes);
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardCheckCodeResponse response = JSONUtil.toBean(resultJson, CardCheckCodeResponse.class);

		return response;
	}

	/**
	 * 获取卡券嵌入图文消息的标准格式代码，将返回代码填入上传图文素材接口中content字段，即可获取嵌入卡券的图文消息素材。
	 * <p>
	 * Note:目前该接口仅支持填入非自定义code的卡券,自定义code的卡券需先进行code导入后调用。
	 * 
	 * @param cardId
	 * @return
	 */
	public CardGetMpnewsContentResponse getMpNewsContent(String cardId) {
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "card/mpnews/gethtml?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardGetMpnewsContentResponse response = JSONUtil.toBean(resultJson, CardGetMpnewsContentResponse.class);

		return response;
	}

	/**
	 * 批量查询卡券列表
	 * 
	 * @param offset
	 *            查询卡列表的起始偏移量，从0开始，即offset: 5是指从从列表里的第六个开始读取。
	 * @param count
	 *            需要查询的卡片的数量（数量最大50）。
	 * @param status_list
	 *            支持开发者拉出指定状态的卡券列表，例：仅拉出通过审核的卡券。
	 * @return
	 */
	public CardBatchGetResponse batchGet(int offset, int count, String[] status_list) {
		String url = BASE_API_URL + "card/batchget?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("count", count);
		params.put("status_list", status_list);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardBatchGetResponse response = JSONUtil.toBean(resultJson, CardBatchGetResponse.class);

		return response;
	}

	/**
	 * 本接口用于提前查询本次新增库存需要多少券点
	 * 
	 * @param cardId
	 *            需要来配置库存的card_id
	 * @param quantity
	 *            本次需要兑换的库存数目
	 * @return
	 */
	public CardGetPayPriceResponse getPayPrice(String cardId, int quantity) {
		String url = BASE_API_URL + "card/pay/getpayprice?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("quantity", quantity);
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardGetPayPriceResponse response = JSONUtil.toBean(resultJson, CardGetPayPriceResponse.class);

		return response;
	}

	/**
	 * 确认兑换库存接口:本接口用于确认兑换库存，确认后券点兑换为库存，过程不可逆。
	 * 
	 * @param cardId
	 *            需要来兑换库存的card_id
	 * @param quantity
	 *            本次需要兑换的库存数目
	 * @param orderId
	 *            仅可以使用上面得到的订单号，保证批价有效性
	 * @return
	 */
	public BaseResponse payConfirm(String cardId, int quantity, String orderId) {
		String url = BASE_API_URL + "card/pay/confirm?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("quantity", quantity);
		params.put("card_id", cardId);
		params.put("order_id", orderId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));

		return r;
	}

	/**
	 * 查询券点余额接口
	 * 
	 * @return { "errcode": 0, "errmsg": "ok", "free_coin": 200 "pay_coin": 1
	 *         "total_coin": 201 }
	 */
	public CardGetCoinsInfoResponse getCoinsInfo() {
		String url = BASE_API_URL + "card/pay/getcoinsinfo?access_token=#";
		BaseResponse r = executePost(url, null);
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardGetCoinsInfoResponse response = JSONUtil.toBean(resultJson, CardGetCoinsInfoResponse.class);

		return response;
	}

	/**
	 * 开通券点账户
	 * 
	 * @return { "errcode": 0, "errmsg": "ok", "reward": 200 }
	 */
	public CardPayActivateResponse payActivate() {
		String url = BASE_API_URL + "card/pay/activate?access_token=#";

		BaseResponse r = executePost(url, null);
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardPayActivateResponse response = JSONUtil.toBean(resultJson, CardPayActivateResponse.class);

		return response;
	}

	/**
	 * 充值券点接口
	 * 
	 * 开发者可以通过此接口为券点账户充值券点，1元等于1点。开发者调用接口后可以获得一个微信支付的支付二维码链接，
	 * 开发者可以将链接转化为二维码扫码支付。
	 * 
	 * @param coin_count
	 *            需要充值的券点数目，1点=1元
	 * @return { "errcode": 0, "errmsg": "ok", "order_id":
	 *         "100005790120***221401000171", "qrcode_url":
	 *         "weixin://wxpay/bizpayurl?pr=xxxxxxxxx", "qrcode_buffer":
	 *         "pwxs*************xxxxxxxxxx", }
	 */
	public CardRechargeResponse recharge(int coin_count) {
		String url = BASE_API_URL + "card/pay/recharge?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("coin_count", coin_count);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		CardRechargeResponse response = JSONUtil.toBean(resultJson, CardRechargeResponse.class);

		return response;
	}

	public QrcodeResponse createQrcode(QrcodeType actionName, String cardInfo, Integer expireSeconds) {
		BeanUtil.requireNonNull(actionName, "actionName is null");
		BeanUtil.requireNonNull(cardInfo, "cardInfo is null");

		LOG.debug("创建二维码信息.....");

		QrcodeResponse response = null;
		String url = BASE_API_URL + "card/qrcode/create?access_token=#";

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("action_name", actionName);
		Map<String, Object> actionInfo = new HashMap<String, Object>();
		if (actionName == QrcodeType.QR_MULTIPLE_CARD) {
			actionInfo.put("multiple_card", cardInfo);
		} else {
			actionInfo.put("card", cardInfo);
		}
		param.put("action_info", actionInfo);
		if (BeanUtil.nonNull(expireSeconds) && 0 != expireSeconds) {
			param.put("expire_seconds", expireSeconds);
		}
		BaseResponse r = executePost(url, JSONUtil.toJson(param));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
		response = JSONUtil.toBean(resultJson, QrcodeResponse.class);
		return response;
	}

	/**
	 * 查询订单详情接口
	 * 
	 * 本接口用于查询充值订单的状态
	 * 
	 * @param orderId
	 *            上一步中获得的订单号，作为一次交易的唯一凭证
	 * @return
	 */
	public String getOrder(String orderId) {
		String url = BASE_API_URL + "card/pay/getorder?access_token=#";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("order_id", orderId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		return resultJson;
	}

	/**
	 * 查询流水详情接口
	 * 
	 * 本接口用于查询券点的流水详情。
	 * 
	 * @param filterJson
	 *            JSON格式查询条件（如何优化，方便前台使用？） { "offset": 0, "count": 5,
	 *            "order_type": "ORDER_TYPE_WXPAY", "nor_filter": { "status":
	 *            "ORDER_STATUS_SUCC" }, "sort_info": { "sort_key":
	 *            "SORT_BY_TIME", "sort_type": "SORT_DESC" }, "begin_time":
	 *            "1440420538", "end_time": "1450713203" }
	 * @return
	 */
	public String getOrderList(String filterJson) {
		String url = BASE_API_URL + "card/pay/getorderlist?access_token=#";

		BaseResponse r = executePost(url, filterJson);
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		return resultJson;
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 支持调用该接口拉取本商户的总体数据情况，包括时间区间内的各指标总量。
	 * 
	 * <p>
	 * 特别注意： 1. 查询时间区间需<=62天，否则报错{errcode: 61501，errmsg: "date range error"}； 2.
	 * 传入时间格式需严格参照示例填写”2015-06-15”，否则报错{errcode":61500,"errmsg":"date format
	 * error"}
	 * 
	 * @param begin_date
	 *            查询数据的起始时间。
	 * @param end_date
	 *            查询数据的截至时间。
	 * @param cond_source
	 *            卡券来源，0为公众平台创建的卡券数据、1是API创建的卡券数据s
	 * @return
	 */
	public GetCardBizuinInfoResponse getCardBizuinInfo(Date beginDate, Date endDate, int cond_source) {
		BeanUtil.requireNonNull(beginDate, "beginDate is null");
		BeanUtil.requireNonNull(endDate, "endDate is null");
		String url = BASE_API_URL + "datacube/getcardbizuininfo?access_token=#";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("begin_date", DATE_FORMAT.format(beginDate));
		params.put("end_date", DATE_FORMAT.format(endDate));
		params.put("cond_source", cond_source);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		GetCardBizuinInfoResponse response = JSONUtil.toBean(resultJson, GetCardBizuinInfoResponse.class);

		return response;
	}

	/**
	 * 获取免费券数据接口
	 * 
	 * <p>
	 * 支持开发者调用该接口拉取免费券（优惠券、团购券、折扣券、礼品券）在固定时间区间内的相关数据。
	 * <p>
	 * 特别注意：
	 * <p>
	 * 1.该接口目前仅支持拉取免费券（优惠券、团购券、折扣券、礼品券）的卡券相关数据，暂不支持特殊票券（电影票、会议门票、景区门票、飞机票）数据。
	 * <p>
	 * 2.查询时间区间需<=62天，否则报错{"errcode:" 61501，errmsg: "date range error"}；
	 * <p>
	 * 3.传入时间格式需严格参照示例填写如”2015-06-15”，否则报错｛"errcode":"date format error"｝
	 * 
	 * @param begin_date
	 *            查询数据的起始时间。
	 * @param end_date
	 *            查询数据的截至时间。
	 * @param cardId
	 *            卡券ID
	 * @param cond_source
	 *            卡券来源，0为公众平台创建的卡券数据、1是API创建的卡券数据s
	 * @return
	 */
	public GetCardCardInfoResponse getCardCardInfo(Date beginDate, Date endDate, String cardId, int cond_source) {
		BeanUtil.requireNonNull(beginDate, "beginDate is null");
		BeanUtil.requireNonNull(endDate, "endDate is null");
		BeanUtil.requireNonNull(cardId, "cardId is null");
		String url = BASE_API_URL + "datacube/getcardcardinfo?access_token=#";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("begin_date", DATE_FORMAT.format(beginDate));
		params.put("end_date", DATE_FORMAT.format(endDate));
		params.put("cond_source", cond_source);
		params.put("card_id", cardId);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		GetCardCardInfoResponse response = JSONUtil.toBean(resultJson, GetCardCardInfoResponse.class);

		return response;
	}

	/**
	 * 拉取会员卡数据接口
	 * 
	 * <p>
	 * 为支持开发者调用API查看卡券相关数据，微信卡券团队封装数据接口并面向具备卡券功能权限的开发者开放使用。
	 * 开发者调用该接口可获取本商户下的所有卡券相关的总数据以及指定卡券的相关数据。
	 * <p>
	 * 特别注意：
	 * <p>
	 * 1.需在获取卡券相关数据前区分卡券创建渠道：公众平台创建、调用卡券接口创建。
	 * <p>
	 * 2.查询时间区间需<=62天，否则报错{"errcode:" 61501，errmsg: "date range error"}；
	 * <p>
	 * 3.传入时间格式需严格参照示例填写如”2015-06-15”，否则报错｛"errcode":"date format error"｝
	 * 
	 * @param begin_date
	 *            查询数据的起始时间。
	 * @param end_date
	 *            查询数据的截至时间。
	 * @param cond_source
	 *            卡券来源，0为公众平台创建的卡券数据、1是API创建的卡券数据s
	 * @return
	 */
	public GetCardMemberInfoResponse getCardMemberCardInfo(Date beginDate, Date endDate, int cond_source) {
		BeanUtil.requireNonNull(beginDate, "beginDate is null");
		BeanUtil.requireNonNull(endDate, "endDate is null");
		String url = BASE_API_URL + "datacube/getcardmembercardinfo?access_token=#";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("begin_date", DATE_FORMAT.format(beginDate));
		params.put("end_date", DATE_FORMAT.format(endDate));
		params.put("cond_source", cond_source);

		BaseResponse r = executePost(url, JSONUtil.toJson(params));
		String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();

		GetCardMemberInfoResponse response = JSONUtil.toBean(resultJson, GetCardMemberInfoResponse.class);

		return response;
	}
}
