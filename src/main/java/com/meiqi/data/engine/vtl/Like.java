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

public class Like extends Directive{

	@Override
	public String getName() {
		return "like";
	}

	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		// TODO Auto-generated method stub

		SimpleNode sn_variable = (SimpleNode) node.jjtGetChild(0); 
		String variable=(String)sn_variable.value(context);
		
		SimpleNode sn_str = (SimpleNode) node.jjtGetChild(1);     
        String str = (String)sn_str.value(context);     
        SimpleNode sn_match = (SimpleNode) node.jjtGetChild(2);     
        String match = (String)sn_match.value(context);  
        char matchSymbol='%';
		//0无匹配 1左匹配 2右匹配 3全匹配
		int matchType=0;
		if(matchSymbol == match.charAt(0)){
			matchType=1;
			match=match.substring(1);
		}
		int matchEndIndex=match.length()-1;
		if(matchSymbol == match.charAt(matchEndIndex)){
			if(1==matchType){
				matchType=3;
			}else{
				matchType=2;
			}
			match=match.substring(0, matchEndIndex);
		}
		boolean tag=false;
		if(3==matchType){
				tag=0<=str.indexOf(match);
		}else if(2==matchType){
				tag=0==str.indexOf(match);
		}else if(1==matchType){
				tag=(str.length()-match.length())==str.indexOf(match, str.length()-match.length());
		}else{
			tag=str.equals(match);
		}
		context.put(variable, tag);
//		writer.write(tag==true?"true":"false");
		return true;
	}

}
