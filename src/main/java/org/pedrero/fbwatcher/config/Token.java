package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6896083302298328376L;
	private final String token;
	private Date expiration;

	public Token(String token) {
		super();
		this.token = token;
	}

	@XmlAttribute
	public String getToken() {
		return token;
	}

	@XmlAttribute
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiracy(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return Objects.equals(token, ((Token) obj).getToken());
	}
}
