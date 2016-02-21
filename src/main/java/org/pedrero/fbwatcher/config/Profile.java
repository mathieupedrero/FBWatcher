package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Profile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6923235266006904340L;
	private final String id;
	private final List<String> mailAddresses = new ArrayList<>();
	private final List<String> freeRestAddresses = new ArrayList<>();
	private Token token;

	public Profile(String id) {
		super();
		this.id = id;
	}

	@XmlID
	@XmlAttribute
	public String getId() {
		return id;
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

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return Objects.equals(id, ((Profile) obj).getId());
	}

}
