package org.pedrero.fbwatcher.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Profile {
	private String id;
	private final List<String> mailAddresses = new ArrayList<>();
	private final List<String> freeRestAddresses = new ArrayList<>();
	private Token token;

	@XmlID
	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public List<String> getMailAddresses() {
		return mailAddresses;
	}

	@XmlElement
	public List<String> getFreeRestAddresses() {
		return freeRestAddresses;
	}

	@XmlElement
	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

}
