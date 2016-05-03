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


public class SubStringByIndex extends Directive{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "subStringByIndex";
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
        SimpleNode sn_beginIndex = (SimpleNode) node.jjtGetChild(2);
		int beginIndex=(Integer)sn_beginIndex.value(context); 
        SimpleNode sn_endIndex = (SimpleNode) node.jjtGetChild(3);
		int endIndex=(Integer) sn_endIndex.value(context);
		
		if ('\''==str.charAt(0)&&'\''==str.charAt(str.length()-1)) {
			str=str.substring(1,str.length()-1);
		}
		
		if(0>beginIndex){
			beginIndex=0;
		}
		if(endIndex>str.length()){
			endIndex=str.length();
		}
		context.put(variable, "'"+str.substring(beginIndex, endIndex)+"'");
		return true;
	}

}
