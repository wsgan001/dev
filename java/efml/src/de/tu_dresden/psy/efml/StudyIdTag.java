/**
 * StudyIdTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

import javax.naming.OperationNotSupportedException;

/**
 * 
 * 
 * implements the &lt;studyid>...&lt;/studyid> tag
 * 
 * 
 * @author albrecht
 * 
 */

public class StudyIdTag extends StandardDeferrable implements AnyTag,
		GlobalModifier {

	/**
	 * store current token
	 */
	private String token;

	/**
	 * use body tag to set id variable
	 */
	private BodyTag body;

	public StudyIdTag(BodyTag body) {
		this.body = body;
		token = "";
	}

	@Override
	public void open(Writer writer) throws IOException {
	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getPlainContent();
		} else
			throw new OperationNotSupportedException(
					"<studyid> cannot enclose "
							+ innerTag.getClass().toString());

	}
	
	@Override
	public void DoAction() {
		body.setStudy(token);
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();
		
		representation.append("<studyid");
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.token));
		representation.append("</studyid>");
		
		return representation.toString();
	}

}
