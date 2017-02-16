package com.saladjack.im.protocal;

import com.google.gson.Gson;

import java.util.UUID;

/**
 * 协议报文对象.
 *
 * Created by saladjack on 17/2/16.
 */
public class Protocal {
	private int type = 0;
	private String dataContent = null;
	private int from = -1;
	private int to = -1;
	private String fp = null;
	private boolean QoS = false;
	private transient int retryCount = 0;


	public Protocal(int type, String dataContent, int from, int to) {
		this(type, dataContent, from, to, false, null);
	}
	public Protocal(int type, String dataContent, int from, int to, boolean QoS, String fingerPrint)
	{
		this.type = type;
		this.dataContent = dataContent;
		this.from = from;
		this.to = to;
		this.QoS = QoS;
		if(QoS && fingerPrint == null)
			fp = Protocal.genFingerPrint();
		else
			fp = fingerPrint;
	}


	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getDataContent()
	{
		return dataContent;
	}

	public void setDataContent(String dataContent)
	{
		this.dataContent = dataContent;
	}

	public int getFrom()
	{
		return from;
	}

	public void setFrom(int from)
	{
		this.from = from;
	}

	public int getTo()
	{
		return to;
	}

	public void setTo(int to)
	{
		this.to = to;
	}

	public String getFp()
	{
		return fp;
	}

	public int getRetryCount()
	{
		return retryCount;
	}

	public void increaseRetryCount()
	{
		retryCount += 1;
	}

	public boolean isQoS() {
		return QoS;
	}

	public String toGsonString()
	{
		return new Gson().toJson(this);
	}

	public byte[] toBytes()
	{
		return CharsetHelper.getBytes(toGsonString());
	}

	@Override
	public Object clone() {
		Protocal cloneP = new Protocal(this.getType(), this.getDataContent(), this.getFrom(), this.getTo(), this.isQoS(), this.getFp());
		return cloneP;
	}

	public static String genFingerPrint()
	{
		return UUID.randomUUID().toString();
	}
}