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
	public static final int ACTIVITY = 0;
	public static final int BASEADAPTER = 1;

	public void build(File file_project, File file_java) throws FileNotFoundException, IOException, IllegalArgumentException, ParserConfigurationException, SAXException
	{
		ArrayList<String> arr_ids = new ArrayList<>();
		String java = readFile(file_java);
		findXmL(file_project, file_java, java);
	}
	private String findXmL(File file_project, File file_java, String java) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException
	{
		String regex_setConentView = "(setContentView\\((\\w+\\.)*R\\.layout\\.(\\w+)\\);(\r?\n)*)";
		String regex_inflate = "[^\\w](\\w+\\.inflate\\s*\\((\\w+,)*R\\.layout\\.(\\w+),\\s*\\w+\\s*\\)\\s*;(\r?\n)*)";
		String regex_getView = "public\\s*View\\s*getView\\(int\\s*position,\\s*View\\s*convertView,\\s*ViewGroup\\s*parent\\)";
		String regex_onCreateView = "public\\s*View\\s*onCreateView";
		Matcher m_setConetView = getMatcher(java, regex_setConentView);
		Matcher m_inflate = getMatcher(java, regex_inflate);
		Matcher m_getView = getMatcher(java, regex_getView);
		Matcher m_onCreateView = getMatcher(java, regex_onCreateView);
		String xml_name = null;
		boolean isActivity = m_setConetView.find();
		boolean isAdapter = m_getView.find();
		boolean isFragment = m_onCreateView.find();
		if (isActivity)
		{
			xml_name = m_setConetView.group(3) + ".xml";
			buildActivity(regex_setConentView, xml_name, file_project, file_java, java);
		}
		if (isAdapter)
		{
			if (m_inflate.find())
			{
				xml_name = m_inflate.group(3) + ".xml";
				buildBaseAdapter(regex_inflate, xml_name, file_project, file_java, java);
			} else
			{
				throw new IllegalArgumentException("木有找到inflate...");
			}
		}
		if (isFragment)
		{
			if (m_inflate.find())
			{
				xml_name = m_inflate.group(3) + ".xml";
				buildFragment(regex_inflate, xml_name, file_project, file_java, java);
			} else
			{
				throw new IllegalArgumentException("木有找到inflate...");
			}
		}
		if (xml_name == null)
		{
			throw new IllegalArgumentException("木有找到setContentView|getView...");
		}
		return xml_name;
	}
	private Matcher getMatcher(String matchStr, String regex)
	{
		Pattern p_setContentView = Pattern.compile(regex);
		Matcher m_setConetView = p_setContentView.matcher(matchStr);
		return m_setConetView;
	}
	private void buildFragment(String regex_inflate, String xml_name, File file_project, File file_java, String java) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException
	{
		ArrayList<View> views = getViewByFileName(file_project, xml_name);
		String str_declarings = "";
		String str_finds = "";
		for (int i = 0; i < views.size(); i++)
		{
			String type = views.get(i).getName();
			String id = views.get(i).getId();
			String declaring = "\t" + type + " " + id + " = null;\n";
			if (!java.contains(declaring))
			{
				str_declarings += declaring;
				str_finds += "\t\t" + id + " = (" + type + ")v.findViewById(R.id." + id + ");\n";
			}
		}
		String replaceStr = java.replaceFirst("(\\{(\r?\n)*)", "$1" + str_declarings).replaceFirst(".*" + regex_inflate, "\t\tView v = $1" + str_finds);
		OutputStream os = new FileOutputStream(file_java);
		os.write(replaceStr.getBytes());
		os.close();
	}
	private void buildBaseAdapter(String regex_inflate, String xml_name, File file_project, File file_java, String java) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException
	{
		ArrayList<View> views = getViewByFileName(file_project, xml_name);
		String ifcontentview = "\t\tif (convertView == null) \n\t\t{\n\t\t\tconvertView = ";
		String str_finds = "";
		String viewholder = "\tpublic final class ViewHolder {\n";
		for (int i = 0; i < views.size(); i++)
		{
			String type = views.get(i).getName();
			String id = views.get(i).getId();
			str_finds += "\t\t\t" + id + " = (" + type + ")findViewById(R.id." + id + ");\n";
			viewholder += "\t\tpublic " + type + " " + id + "\n";
		}
		viewholder += "\t}\n}\n";
		Matcher m = getMatcher(java, "if\\s*\\(convertView\\s*==\\s*null\\s*\\)");
		if (m.find())// 已经生成过了
		{
			return;
		}
		String replaceStr = java.replaceAll(".*" + regex_inflate, ifcontentview + "$1" + str_finds + "\t\t\tconvertView.setTag(holder);\n\t\t}\n\t\telse\n\t\t{\n\t\t\tholder = (ViewHolder) convertView.getTag();\n\t\t}\n").replaceAll("\\}$", viewholder);
		OutputStream os = new FileOutputStream(file_java);
		os.write(replaceStr.getBytes());
		os.close();
	}
	private void buildActivity(String regex_setConentView, String xml_name, File file_project, File file_java, String java) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException
	{
		ArrayList<View> views = getViewByFileName(file_project, xml_name);
		String str_declarings = "";
		String str_finds = "";
		for (int i = 0; i < views.size(); i++)
		{
			String type = views.get(i).getName();
			String id = views.get(i).getId();
			String declaring = "\t" + type + " " + id + " = null;\n";
			if (!java.contains(declaring))
			{
				str_declarings += declaring;
				str_finds += "\t\t" + id + " = (" + type + ")findViewById(R.id." + id + ");\n";
			}
		}
		String replaceStr = java.replaceFirst("(\\{(\r?\n)*)", "$1" + str_declarings).replaceFirst(regex_setConentView, "$1" + str_finds);
		OutputStream os = new FileOutputStream(file_java);
		os.write(replaceStr.getBytes());
		os.close();
	}
	private ArrayList<View> getViewByFileName(File file_project, String xml_name) throws ParserConfigurationException, SAXException, IOException
	{
		File file_xml = new File(file_project, "res/layout/" + xml_name);
		ArrayList<View> views = getViewByContent(file_xml);
		return views;
	}
	private ArrayList<View> getViewByContent(File file_xml) throws ParserConfigurationException, SAXException, IOException
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
