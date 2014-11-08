package com.zyl.finder.findviewbyid;

public class View
{
	private String name;
	private String id;

	public View(String name, String id)
	{
		this.name = name;
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	@Override
	public String toString()
	{
		return "View [name=" + name + ", id=" + id + "]";
	}
}