package com.bjw.ComAssistant;

/*************************************************
 *@date：2017/10/31
 *@author：  zxj
 *@description： 数据转换工具
*************************************************/
public class MyFunc {
	//-------------------------------------------------------
	// 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    static public int isOdd(int num)
	{
		return num & 0x1;
	}
    //-------------------------------------------------------
    static public int HexToInt(String inHex)//Hex字符串转int
    {
    	return Integer.parseInt(inHex, 16);
    }
    //-------------------------------------------------------
    static public byte HexToByte(String inHex)//Hex字符串转byte
    {
    	return (byte)Integer.parseInt(inHex,16);
    }
    //-------------------------------------------------------
    static public String Byte2Hex(Byte inByte)//1字节转2个Hex字符
    {
    	return String.format("%02x", inByte).toUpperCase();
    }
    //-------------------------------------------------------
	static public String ByteArrToHex(byte[] inBytArr)//字节数组转转hex字符串
	{
		StringBuilder strBuilder=new StringBuilder();
		int j=inBytArr.length;
		for (int i = 0; i < j; i++)
		{
			strBuilder.append(Byte2Hex(inBytArr[i]));
			strBuilder.append("");
		}
		return strBuilder.toString(); 
	}
  //-------------------------------------------------------
    static public String ByteArrToHex(byte[] inBytArr,int offset,int byteCount)//字节数组转转hex字符串，可选长度
	{
    	StringBuilder strBuilder=new StringBuilder();
		int j=byteCount;
		for (int i = offset; i < j; i++)
		{
			strBuilder.append(Byte2Hex(inBytArr[i]));
		}
		return strBuilder.toString();
	}
	//-------------------------------------------------------
	//转hex字符串转字节数组
    static public byte[] HexToByteArr(String inHex)//hex字符串转字节数组
	{
		int hexlen = inHex.length();
		byte[] result;
		if (isOdd(hexlen)==1)
		{//奇数
			hexlen++;
			result = new byte[(hexlen/2)];
			inHex="0"+inHex;
		}else {//偶数
			result = new byte[(hexlen/2)];
		}
	    int j=0;
		for (int i = 0; i < hexlen; i+=2)
		{
			result[j]=HexToByte(inHex.substring(i,i+2));
			j++;
		}
	    return result; 
	}


	//16进制转10进制
	public static int HexToOXInt(String strHex){
		int nResult = 0;
		if ( !IsHex(strHex) )
			return nResult;
		String str = strHex.toUpperCase();
		if ( str.length() > 2 ){
			if ( str.charAt(0) == '0' && str.charAt(1) == 'X' ){
				str = str.substring(2);
			}
		}
		int nLen = str.length();
		for ( int i=0; i<nLen; ++i ){
			char ch = str.charAt(nLen-i-1);
			try {
				nResult += (GetHex(ch)*GetPower(16, i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nResult;
	}

	//计算16进制对应的数值
	public static int GetHex(char ch) throws Exception{
		if ( ch>='0' && ch<='9' )
			return (int)(ch-'0');
		if ( ch>='a' && ch<='f' )
			return (int)(ch-'a'+10);
		if ( ch>='A' && ch<='F' )
			return (int)(ch-'A'+10);
		throw new Exception("error param");
	}

	//计算幂
	public static int GetPower(int nValue, int nCount) throws Exception{
		if ( nCount <0 )
			throw new Exception("nCount can't small than 1!");
		if ( nCount == 0 )
			return 1;
		int nSum = 1;
		for ( int i=0; i<nCount; ++i ){
			nSum = nSum*nValue;
		}
		return nSum;
	}
	//判断是否是16进制数
	public static boolean IsHex(String strHex){
		int i = 0;
		if ( strHex.length() > 2 ){
			if ( strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x') ){
				i = 2;
			}
		}
		for ( ; i<strHex.length(); ++i ){
			char ch = strHex.charAt(i);
			if ( (ch>='0' && ch<='9') || (ch>='A' && ch<='F') || (ch>='a' && ch<='f') )
				continue;
			return false;
		}
		return true;
	}
}