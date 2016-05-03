package com.meiqi.liduoo.fastweixin.api.enums;

/**
 * 二维码接口状态枚举
 *
 * @author peiyu
 * @since 1.2
 */
public enum QrcodeType {

	/**
	 * 临时二维码
	 */
	QR_SCENE,

	/**
	 * 永久二维码
	 */
	QR_LIMIT_SCENE,
	/**
	 * 永久字符串二维码（Frank Added）
	 */
	QR_LIMIT_STR_SCENE, 
	QR_CARD, 
	QR_MULTIPLE_CARD
}
