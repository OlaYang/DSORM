package com.meiqi.openservice.bean.sort;

/**
 * 用户请求的url信息分解
 * 
 * @author Administrator
 *
 */
public class ReqUrlInfo {
	//连接头
	private String head = "/albums/";
	//只看案例
	private String seeCase = "";
	//颜色编号
	private String colorNum="";
	//风格编号
	private String styletagNum = "";
	//空间编号
	private String areatagNum = "";
	//局部编号
	private String detailtagNum = "";
	//户型编号
	private String squaretagNum = "";
	//当前编号4大类拼接数
	private Integer count = 0;
	//当前点击的4类之1，只有当count=1才使用
	private String ctype="";
	private String cnum="";
	//当前额外的参数 最新 最热 推荐 默认最热
	private String extra="";
	//无-的额外参数
	private String no_extra="";
	//当前的城市
	private String city="";
	
	//没有点击
	public String getUrlCount0(String tempType,String num){
		return head+seeCase+getColorNum()+"c-"+num+extra+city+"/";
	}
	
	
	//只有一个点击
	public String getUrlCount1(String tempType,String num){
			if(num.equals(cnum)){
				if("".equals(getColorNum())&&"".equals(getExtra())&&"".equals(getCity())){
					return head+seeCase;
				}else{
					return head+seeCase+getColorNum()+"c"+extra+city+"/";
				}
			}else{
				return head+seeCase+getColorNum()+"c-"+num+extra+city+"/";
			}
	}
	//清除0
	private String clear0(String num){
		if("-0".equals(num)){
			return "";
		}
		return num;
	}
	
	
	public String getHotUrl(String inputUrl){
		if(inputUrl.contains("-newest")){
			return inputUrl.replaceAll("-newest", "");
		}else if(inputUrl.contains("newest")){
			return inputUrl.replaceAll("newest/", "");
		}else{
			return inputUrl;
		}
		
	}
	
	public String getRecommendUrl(){
		StringBuilder sb=new StringBuilder(head);
		if(0==count){
			if("-recommend".equals(extra)){
				sb.append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append(city);
			}else{
				sb.append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append("recommend").append(city).append("/");
			}
		}else{
			if("-recommend".equals(extra)){
				sb.append("c").append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append(city).append("/");	
			}else{
				sb.append("c").append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append("-recommend").append(city).append("/");	
			}
		}
		return sb.toString();
	}
	
	public String getNewUrl(String inputUrl){
		String isSearch="";
		if(inputUrl.contains("search")){
			isSearch="search";
		}
		if("".equals(isSearch)){
			if(0==count){
				if(inputUrl.contains("-newest")){
					return inputUrl.replaceAll("newest", "");
				}else if(inputUrl.contains("newest")){
					return inputUrl.replaceAll("newest/", "");
				}else{
					inputUrl=inputUrl.substring(0, inputUrl.length()-1);
					return inputUrl+"/newest/";
				}
			}else{
				if(inputUrl.contains("-newest")){
					return inputUrl.replaceAll("-newest", "");
				}else{
					inputUrl=inputUrl.substring(0, inputUrl.length()-1);
					return inputUrl+"-newest/";
				}
			}
		}else{
			return inputUrl;
		}
		
		
		
//		StringBuilder sb=new StringBuilder(head);
//		if(0==count){
//			if("-newest".equals(extra)){
//				sb.append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append(city);
//			}else{
//				sb.append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append("newest").append(city).append("/");
//			}
//			
//		}else{
//			if("-newest".equals(extra)){
//				sb.append("c").append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append(city).append("/");	
//			}else{
//				sb.append("c").append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append("-newest").append(city).append("/");	
//			}
//			
//		}
//		return sb.toString();
	}
	
	
	public String getCityUrl(String city){
		StringBuilder sb=new StringBuilder(head);
		if(0<count){
			sb.append("c").append(getStyletagNum(1)).append(getAreatagNum(1)).append(getDetailtagNum(1)).append(getSquaretagNum(1)).append(extra);;
		}else{
			sb.append(no_extra);
		}
		
		if(""!=city&&!city.equals(this.city)){
			if(0<count||!"".equals(extra)){
				sb.append("-");
			}
			if("-city234".equals(city)){
				sb.append("city234");
			}else{
				sb.append("city272");
			}
		}
		sb.append("/");
		return sb.toString();
	}
	
	public String toString(){
		return head+seeCase+getColorNum()+"c"+styletagNum+areatagNum+detailtagNum+squaretagNum+extra+city+"/";
	}
	
	public String getCnum() {
		return cnum;
	}

	public void setCnum(String cnum) {
		this.cnum = cnum;
	}

	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getSeeCase() {
		return seeCase;
	}
	public void setSeeCase(String seeCase) {
		this.seeCase = seeCase;
	}
	public String getColorNum() {
		if("".equals(colorNum)||"0".equals(colorNum)){
			return "";
		}else{
			return colorNum+"-";
		}
	}
	public void setColorNum(String colorNum) {
		this.colorNum = colorNum;
	}
	public String getStyletagNum(int i) {
		if(2>count){
			if("".equals(styletagNum)||"-0".equals(styletagNum)){
				return "";
			}
		}else{
			if("".equals(styletagNum)){
				return "-0";
			}
		}
		return styletagNum;
	}
	public String getStyletagNum() {
		if("".equals(styletagNum)){
			return "-0";
		}
		return styletagNum;
	}
	public void setStyletagNum(String styletagNum) {
		this.styletagNum = styletagNum;
	}
	public String getAreatagNum(int i) {
		if(2>count){
			if("".equals(areatagNum)||"-0".equals(areatagNum)){
				return "";
			}
		}else{
			if("".equals(areatagNum)){
				return "-0";
			}
		}
		return areatagNum;
	}
	public String getAreatagNum() {
		if("".equals(areatagNum)){
			return "-0";
		}
		return areatagNum;
	}
	public void setAreatagNum(String areatagNum) {
		this.areatagNum = areatagNum;
	}
	public String getDetailtagNum(int i) {
		if(2>count){
			if("".equals(detailtagNum)||"-0".equals(detailtagNum)){
				return "";
			}
		}else{
			if("".equals(detailtagNum)){
				return "-0";
			}
		}
		return detailtagNum;
	}
	public String getDetailtagNum() {
		if("".equals(detailtagNum)){
				return "-0";
		}
		return detailtagNum;
	}
	public void setDetailtagNum(String detailtagNum) {
		this.detailtagNum = detailtagNum;
	}
	
	public String getSquaretagNum(int i) {
		if(2>count){
			if("".equals(squaretagNum)||"-0".equals(squaretagNum)){
				return "";
			}
		}else{
			if("".equals(squaretagNum)){
				return "-0";
			}
		}
		return squaretagNum;
	}
	
	public String getSquaretagNum() {
		if("".equals(squaretagNum)){
			return "-0";
		}
		return squaretagNum;
	}
	
	public void setSquaretagNum(String squaretagNum) {
		this.squaretagNum = squaretagNum;
	}
	public Integer getCount() {
		return count;
	}
	public void addCount(Integer ac){
		this.count+=ac;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}


	public String getNo_extra() {
		return no_extra;
	}


	public void setNo_extra(String no_extra) {
		this.no_extra = no_extra;
	}
	
	// private String
}
