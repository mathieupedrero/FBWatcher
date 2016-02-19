package org.pedrero.fbwatcher.config;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Token {
	private String token;
	private Date expiration;

	@XmlAttribute
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@XmlAttribute
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiracy(Date expiration) {
		this.expiration = expiration;
	}
}
