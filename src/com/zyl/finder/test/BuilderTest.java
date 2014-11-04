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
			b.build(new File("X:\\ECLIPSE+ADT+SDK[Android]\\workspace\\Test"),new File("x:\\ECLIPSE+ADT+SDK[Android]\\workspace\\Test\\src\\com\\example\\test\\MainActivity.java"));
		} catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
