package com.meiqi.data.engine.vtl;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class SubStringIndex extends Directive{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "subStringIndex";
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		// TODO Auto-generated method stub
		SimpleNode sn_variable = (SimpleNode) node.jjtGetChild(0); 
		String variable=(String)sn_variable.value(context);
		
		SimpleNode sn_str = (SimpleNode) node.jjtGetChild(1);     
        String str = (String)sn_str.value(context);     
        SimpleNode sn_delim= (SimpleNode) node.jjtGetChild(2);
		String delim=(String)sn_delim.value(context); 
        SimpleNode sn_count = (SimpleNode) node.jjtGetChild(3);
        int count=(Integer) sn_count.value(context);
        
        if ('\''==str.charAt(0)&&'\''==str.charAt(str.length()-1)) {
			str=str.substring(1,str.length()-1);
		}
        
      //判断正负，调用指定方法处理
		if(0<count){//正处理
			String[] strSplit=str.split(delim);
			if(count>strSplit.length){
				count=strSplit.length;
			}
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<count;i++){
				if(count == (i+1)){
					sb.append(strSplit[i]);
				}else{
					sb.append(strSplit[i]+delim);
				}
			}
			context.put(variable, "'"+sb.toString()+"'");
		}else{//负处理
			str=new StringBuilder(str).reverse().toString();
			count=Math.abs(count);
			String[] strSplit=str.split(delim);
			if(count>strSplit.length){
				count=strSplit.length;
			}
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<count;i++){
				if(count == (i+1)){
					sb.append(strSplit[i]);
				}else{
					sb.append(strSplit[i]+delim);
				}
			}
			context.put(variable, "'"+(new StringBuilder(sb).reverse().toString())+"'");
		}
		return true;
	}

}
