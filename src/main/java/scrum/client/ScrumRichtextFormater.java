package scrum.client;

import ilarkesto.gwt.client.RichtextFormater;

public class ScrumRichtextFormater implements RichtextFormater {

	public String toHtml(String s) {
		return ScrumGwtApplication.get().richtextToHtml(s);
	}

}