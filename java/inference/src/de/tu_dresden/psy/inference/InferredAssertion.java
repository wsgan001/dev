/**
 * InferredAssertation.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


/**
 * implements an assertion interface that provides information about the
 * inference map and premise assertions used to obtain the assertion
 * 
 * @author immanuel
 * 
 */

public class InferredAssertion implements AssertionInterface {

	private Vector<? extends AssertionInterface> premises;
	private int thisHash;
	private AssertionInterface assertion;
	private InferenceMap rule;

	private boolean old;
	private String representation;

	/**
	 * store, if the rules used for inference was marked trivial
	 */
	private boolean usedTrivialRule;

	@Override
	public boolean isOld() {
		return this.old;
	}

	@Override
	public void markAsOld() {
		this.old = true;
	}

	/**
	 * unified initialization method
	 * 
	 * @param newAssertion
	 * @param usedPremises
	 */
	@SuppressWarnings("unchecked")
	private void initialize(InferenceMap usedRule,
			AssertionInterface newAssertion,
			Collection<? extends AssertionInterface> usedPremises,
			boolean byTrivialRule) {
		this.usedTrivialRule = byTrivialRule;

		this.assertion = newAssertion;
		this.rule = usedRule;
		if (usedPremises instanceof Vector<?>) {
			this.premises = (Vector<? extends AssertionInterface>) ((Vector<?>)usedPremises).clone();
		} else {
			Vector<AssertionInterface> premise_vector= new Vector<AssertionInterface>(usedPremises.size());
			this.premises = premise_vector;
			premise_vector.addAll(usedPremises);
		}

		this.old = false;

		this.representation = "  +--- "+ this.rule.ruleName();
		for (AssertionInterface premise : this.premises) {
			this.representation += "\n  | " + premise.getSubject()+"·"+premise.getPredicate()+"·"+premise.getObject();
		}
		this.representation += "\n  +---";

		{
			final int prime = 31;
			int result = 1;
			result = (prime * result)
					+ ((this.assertion == null) ? 0 : this.assertion.hashCode());
			result = (prime * result)
					+ (this.representation.hashCode());
			result = (prime * result) + ((this.rule == null) ? 0 : this.rule.hashCode());
			this.thisHash = result;
		}


	}



	/**
	 * inferred assertion from a set of premises
	 * 
	 * @param usedRule
	 * @param newAssertion
	 * @param usedPremises
	 */
	public InferredAssertion(InferenceMap usedRule,
			AssertionInterface newAssertion,
			Collection<? extends AssertionInterface> usedPremises) {
		this.initialize(usedRule, newAssertion, usedPremises,
				usedRule.isTrivial());
	}

	/**
	 * inferred assertion from a single premise
	 * 
	 * @param usedRule
	 * @param newAssertion
	 * @param singlePremise
	 */
	public InferredAssertion(InferenceMap usedRule,
			AssertionInterface newAssertion, AssertionInterface singlePremise) {
		HashSet<AssertionInterface> premises = new HashSet<AssertionInterface>();
		premises.add(singlePremise);
		this.initialize(usedRule, newAssertion, premises, usedRule.isTrivial());
	}

	/**
	 * inferred assertion from two premises
	 * 
	 * @param usedRule
	 * @param newAssertion
	 * @param firstPremise
	 * @param secondPremise
	 */
	public InferredAssertion(InferenceMap usedRule,
			AssertionInterface newAssertion, AssertionInterface firstPremise,
			AssertionInterface secondPremise) {
		HashSet<AssertionInterface> premises = new HashSet<AssertionInterface>();
		premises.add(firstPremise);
		premises.add(secondPremise);
		this.initialize(usedRule, newAssertion, premises, usedRule.isTrivial());
	}

	/**
	 * inferred assertion from three premises
	 * 
	 * @param usedRule
	 * @param newAssertion
	 * @param firstPremise
	 * @param secondPremise
	 * @param thirdPremise
	 */
	public InferredAssertion(InferenceMap usedRule,
			AssertionInterface newAssertion, AssertionInterface firstPremise,
			AssertionInterface secondPremise, AssertionInterface thirdPremise) {
		HashSet<AssertionInterface> premises = new HashSet<AssertionInterface>();
		premises.add(firstPremise);
		premises.add(secondPremise);
		premises.add(thirdPremise);
		this.initialize(usedRule, newAssertion, premises, usedRule.isTrivial());
	}

	/**
	 * 
	 * @return true, if the inferred assertion is already within the premises
	 */
	public boolean isTrivial() {
		return this.isPremise(this.assertion);
	}

	@Override
	public Object getSubject() {
		return this.assertion.getSubject();

	}

	@Override
	public Object getObject() {
		return this.assertion.getObject();
	}

	@Override
	public Object getPredicate() {
		return this.assertion.getPredicate();
	}

	@Override
	public boolean isEqualTo(AssertionInterface assertion) {

		return this.assertion.isEqualTo(assertion);
	}

	@Override
	public boolean isPremise(AssertionInterface assertion) {
		for (AssertionInterface premise : this.premises) {
			if (premise.isEqualTo(assertion)) {
				return true;
			}
		}
		for (AssertionInterface premise : this.premises) {
			if (premise.isPremise(assertion)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {

		return this.representation;
	}

	@Override
	public int hashCode() {

		return this.thisHash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}


		if (this.getClass() != obj.getClass()) {
			return false;
		}
		InferredAssertion other = (InferredAssertion) obj;

		if (this.thisHash != other.thisHash) {
			return false;
		}

		if (!this.assertion.equals(other.assertion)) {
			return false;
		}

		if (this.rule == null) {
			if (other.rule != null) {
				return false;
			}
		} else if (!this.rule.equals(other.rule)) {
			return false;
		}

		if (!this.representation.equals(other.representation)) {
			return false;
		}

		return true;
	}

	/**
	 * return a subset of assertions that are either not InferredAssertions or non-trivial InferredAssertions
	 * @param assertions
	 * @return non trivial assertions
	 */
	static public Set<AssertionInterface> nonTrivial(Set<AssertionInterface> assertions) {
		Set<AssertionInterface> non_trivial = new HashSet<AssertionInterface>();

		Iterator<AssertionInterface> it;
		for (it=assertions.iterator();it.hasNext();){
			AssertionInterface a = it.next();

			if (a instanceof InferredAssertion) {
				InferredAssertion inferred = (InferredAssertion) a;
				if (inferred.isTrivial() == false) {
					non_trivial.add(inferred);
				}
			} else {
				non_trivial.add(a);
			}
		}

		return non_trivial;
	}

	/**
	 * 
	 * @return the premise vector of this inferred assertion
	 */

	public Vector<? extends AssertionInterface> getPremises() {
		return this.premises;
	}


	/**
	 * 
	 * @return true, if the rule used for the inference of this assertion has
	 *         been marked trivial
	 */
	public boolean isInferredByTrivialRule() {
		return this.usedTrivialRule;
	}

}
