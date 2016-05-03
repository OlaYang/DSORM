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

/**
 * 向上取整
 * 例如 传入5 2 5对2余1 返回3
 * @author meiqidr
 *
 */
public class Ceil extends Directive{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ceil";
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node)
			throws IOException, ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		// TODO Auto-generated method stub
		SimpleNode sn_variable = (SimpleNode) node.jjtGetChild(0); 
		String variable=(String)sn_variable.value(context);
		
		SimpleNode sn_cs = (SimpleNode) node.jjtGetChild(1); 
		double cs= Double.parseDouble(sn_cs.value(context).toString());
		
		SimpleNode sn_bcs = (SimpleNode) node.jjtGetChild(2);    
		double bcs=Double.parseDouble(sn_bcs.value(context).toString());
		int q=(int) Math.ceil(cs/bcs);
		context.put(variable, q);
		return false;
	}

}
