/**
 * Deferrable.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

/**
 * interface for deferrable actions
 * 
 * @author immo
 * 
 */

public interface Deferrable {

	/**
	 * @return true, if the action has been deferred
	 */
	boolean isDeferred();

	/**
	 * ask for the modifier action to be stalled, e.g. to stop changing the
	 * title of the document when inside an &lt;efmlquote>-tag
	 */
	void RequestDeferring();

}
