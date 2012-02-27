/**
 * EquivalentAssertions.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * implements an AssertionInterface that will take care of differently derived
 * but otherwise equivalent assertions
 * 
 * @author albrecht
 * 
 */

public class EquivalentAssertions implements AssertionInterface {

	private Object subject, predicate, object;
	
	private int hashSubject,hashPredicate,hashObject;

	private Set<AssertionInterface> assertions;
	
	private boolean old;
	
	@Override
	public boolean isOld() {
		return old;
	}
	
	@Override
	public void markAsOld() {
		old = true;
	}


	/**
	 * create a new equivalence class from a representing assertion only
	 * containing this single assertion
	 * 
	 * @param representant
	 */
	public EquivalentAssertions(AssertionInterface representant) {
		assertions = new HashSet<AssertionInterface>();
		assertions.add(representant);
		subject = representant.getSubject();
		object = representant.getObject();
		predicate = representant.getPredicate();
		old = false;
		hashSubject = subject.hashCode();
		hashPredicate = predicate.hashCode();
		hashObject = object.hashCode();
	}

	/**
	 * add another assertion to the equivalence class if possible
	 * 
	 * @param a
	 */
	public void add(AssertionInterface a) {
		if (isEqualTo(a) != true)
			return;
		if (a == this)
			return;
		
		if (a instanceof EquivalentAssertions) {
			EquivalentAssertions ea = (EquivalentAssertions) a;
			assertions.addAll(ea.assertions);
		} else {
			assertions.add(a);
		}
	}

	@Override
	public Object getSubject() {
		return subject;
	}

	@Override
	public Object getObject() {
		return object;
	}

	@Override
	public Object getPredicate() {
		return predicate;
	}

	@Override
	public boolean isEqualTo(AssertionInterface assertion) {
		if (hashPredicate != assertion.getPredicate().hashCode())
			return false;
		if (hashSubject != assertion.getSubject().hashCode())
			return false;
		if (hashObject != assertion.getObject().hashCode())
			return false;
		
		if (assertion.getPredicate().equals(predicate) == false) {
			return false;
		}

		if (assertion.getSubject().equals(subject) == false) {
			return false;
		}

		return assertion.getObject().equals(object);
	}

	@Override
	public boolean isPremise(AssertionInterface assertion) {
		for (AssertionInterface a : assertions) {
			if (a.isPremise(assertion))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hashObject;
		result = prime * result
				+ hashPredicate;
		result = prime * result + hashSubject;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
				
		if (getClass() != obj.getClass())
			return false;
		
		return isEqualTo((EquivalentAssertions) obj);
	}

	@Override
	public String toString() {

		String p = subject.toString() + "·" + predicate.toString() + "·"
				+ object.toString() + " [" + assertions.size() + "]";
		
		TreeSet<String> ordered = new TreeSet<String>();
		
		for (AssertionInterface assertion: assertions) {
			ordered.add(assertion.toString());
		}
		for (Iterator<String> it = ordered.iterator(); it.hasNext();) {
			p += "\n"+ it.next();
		}
		
		return p+"\n";
	}

}
