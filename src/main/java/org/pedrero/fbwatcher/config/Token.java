package org.pedrero.fbwatcher.config;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.pedrero.fbwatcher.utils.LocalDateTimeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Token implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6896083302298328376L;

	@XmlAttribute
	private String token;

	@XmlElement
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime expiration;

	public Token() {
		super();
	}

	public Token(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpiration() {
		return expiration;
	}

	public void setExpiracy(LocalDateTime expiration) {
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
