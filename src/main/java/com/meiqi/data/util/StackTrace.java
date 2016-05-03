package com.meiqi.data.util;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-25
 * Time: 下午5:55
 * To change this template use File | Settings | File Templates.
 */
public class StackTrace {
    public static void printStackTrace()
    {
        StackTraceElement[] elements = getCurrentThreadStackInfo();

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<elements.length; i++)
        {
            sb.append("className:"+elements[i].getClassName()+";fileName:"+elements[i].getFileName()
            +"methodName:"+elements[i].getMethodName()+"lineNumber:"+elements[i].getLineNumber());
            sb.append("\r\n");

        }
        // LogUtil.info("stackTrace:"+sb.toString());
    }
    /**
     * 如下方法中：我们可以有两种方式得到当前线程中栈的信息，
     * 当我们通过getStackTrace()得到线程栈的信息时，此时线程栈的栈顶存储的信息就是调用了getStackTrace()方法的信息，
     * 也就是getCurrentThreadStackInfo()的信息。
     * @return
     */
    public static StackTraceElement[] getCurrentThreadStackInfo()
    {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        if(elements == null)
        {
            elements = Thread.currentThread().getStackTrace();
        }
        return elements;
    }
}
