/**
 * @author quekunkun
 *
 */
package com.bus.chelaile.model;

public class OpenIdInfof {
	private String openid;
	private String lang;
	
	public OpenIdInfof() {
		super();
	}
	public OpenIdInfof(String openid, String lang) {
		super();
		this.openid = openid;
		this.lang = lang;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	
}