package com.zyl.finder.findviewbyid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Builder
{
	public String build(File file_project, File file_java) throws FileNotFoundException, IOException, IllegalArgumentException, ParserConfigurationException, SAXException
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<String> arr_ids = new ArrayList<>();
		String java = readFile(file_java);
		String regex_setConentView = "(setContentView\\((\\w+\\.)*R\\.layout\\.(\\w+)\\);(\r?\n)*)";
		Pattern p = Pattern.compile(regex_setConentView);
		Matcher m = p.matcher(java);
		String xml_name = null;
		if (m.find())
		{
			xml_name = m.group(3) + ".xml";
		}
		if (xml_name == null)
		{
			throw new IllegalArgumentException("Ä¾ÓÐÕÒµ½setContentView...");
		}
		File file_xml = new File(file_project, "res/layout/" + xml_name);
		ArrayList<View> views = getView(file_xml);
		String str_declarings = "";
		String str_finds = "";
		for (int i = 0; i < views.size(); i++)
		{
			String type = views.get(i).getName();
//			String variable = type.substring(0, 1).toLowerCase() + type.substring(1, type.length());
			String id = views.get(i).getId();
			String declaring = "\t" + type + " " + id + " = null;\n";
			if(!java.contains(declaring))
			{
				str_declarings += declaring;
				str_finds += "\t\t" + id +" = (" +type+")findViewById(R.id."+id+");\n";
			}
		}
		String replaceStr = java.replaceFirst("(\\{(\r?\n)*)", "$1" + str_declarings).replaceFirst(regex_setConentView, "$1"+str_finds);
		OutputStream os = new FileOutputStream(file_java);
		os.write(replaceStr.getBytes());
		os.close();
		return sb.toString();
	}
	private ArrayList<View> getView(File file_xml) throws ParserConfigurationException, SAXException, IOException
	{
		ArrayList<View> v = new ArrayList<View>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		Document doc = db.parse(file_xml);
		Element elmt = doc.getDocumentElement();
		handleNode(elmt, v);
		parseXML(v, elmt);
		return v;
	}
	private void parseXML(ArrayList<View> v, Node elmt)
	{
		NodeList nodes = elmt.getChildNodes();
		int m = 1;
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			switch (node.getNodeType())
			{
			case Node.ELEMENT_NODE:
				parseXML(v, node);
				handleNode(node, v);
				break;
			default:
				break;
			}
		}
	}
	private void handleNode(Node node, ArrayList<View> v)
	{
		String nodeName = node.getNodeName();
		Node attribute = node.getAttributes().getNamedItem("android:id");
		if (attribute != null)
		{
			String id = attribute.getNodeValue();
			v.add(new View(nodeName, id.split("/")[1]));
		}
	}
	private String readFile(File f) throws FileNotFoundException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = new FileInputStream(f);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1)
		{
			baos.write(buffer, 0, len);
		}
		is.close();
		baos.close();
		return baos.toString();
	}
}
