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
			b.build(new File("f:/AndroidEclipse/workspace/DoToPlay/"),new File("f:/AndroidEclipse/workspace/DoToPlay/src/com/example/dotoplay/MyAdapter.java"));
		} catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
