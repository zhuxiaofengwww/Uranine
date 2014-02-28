package com.version1.uranine.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.version1.uranine.domain.UpdateInfo;

public class UpdateInfoParser {

	/**
	 * parse output value from the server
	 * 
	 * @param is
	 * @return update info
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) {
		XmlPullParser parser = Xml.newPullParser();
		UpdateInfo info = new UpdateInfo();
		try {
			parser.setInput(is, "utf-8");

			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {

				switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());
					} else if ("description".equals(parser.getName())) {
						info.setDescription(parser.nextText());
					} else if ("path".equals(parser.getName())) {
						info.setPath(parser.nextText());
					}
					break;
				}
				type = parser.next();
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
