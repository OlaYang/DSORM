package com.meiqi.liduoo.fastweixin.api.response;

/**
 * @author peiyu
 */
public class ExecuteSqlResponse extends BaseResponse {

	private int updateCount;

	/**
	 * @return the updateCount
	 */
	public int getUpdateCount() {
		return updateCount;
	}

	/**
	 * @param updateCount
	 *            the updateCount to set
	 */
	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	/**
	 * @return the generatedKey
	 */
	public long getGeneratedKey() {
		return generatedKey;
	}

	/**
	 * @param generatedKey
	 *            the generatedKey to set
	 */
	public void setGeneratedKey(long generatedKey) {
		this.generatedKey = generatedKey;
	}

	private long generatedKey;
}
