package com.zyl.finder.test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.zyl.finder.findviewbyid.Builder;

public class BuilderTest
{
	public BuilderTest()
	{
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args)
	{
		try
		{
			Builder b = new Builder();
//			String strProject = "F:/AndroidEclipse/workspace/Weixin";
//			String strJava = "F:/AndroidEclipse/workspace/Weixin/src/com/example/weixin/fragment/WeiXinFragment.java";
			String strProject = "F:/AndroidEclipse/workspace/Test";
			String strJava = "F:/AndroidEclipse/workspace/Test/src/com/example/test/MyAdapter.java";
			b.build(new File(strProject), new File(strJava));
		} catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
