package com.sevensoupcans.sfsoftware.util.resources.json;

public final class JsonField 
{

	private final Object value;
	private final JsonFieldType type;
	
	public JsonField(String value) 
	{
		this.value = value;
		this.type = JsonFieldType.STRING;
	}
	
	public JsonField(Integer value) 
	{
		this.value = value;
		this.type = JsonFieldType.INTEGER;
	}
	
	public String asString()
	{
		return (String) this.value;
	}
	
	public int asInteger()
	{
		return (Integer) this.value;
	}

}
