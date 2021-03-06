/**
 * InferenceMachine.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.machine;

import java.applet.Applet;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.tu_dresden.psy.inference.AssertionEquivalenceClasses;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.EquivalentAssertions;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.compiler.StringIds;
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;
import de.tu_dresden.psy.inference.regexp.xml.InferableAssertions;
import de.tu_dresden.psy.inference.regexp.xml.XmlHandler;
import de.tu_dresden.psy.inference.regexp.xml.XmlRootTag;
import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatcher;
import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatchers;

/**
 * 
 * implements an java applet interface for the provided inference facilities
 * 
 * @author immanuel
 * 
 */

public class InferenceMachine extends Applet {

	/**
	 * serialization
	 */
	private static final long serialVersionUID = 8101906904403439152L;

	/**
	 * stop inference process after ... seconds
	 */

	private static final float defaultExcessTimeLimit = 45;

	/**
	 * machine state variables
	 */
	private AssertionEquivalenceClasses implicit, expert, studentArguments,
	studentConclusions;
	private Map<String, InferenceMap> inferenceMaps;
	private Map<String, InferenceMap> trivialInferenceMaps;
	private Set<ConstrainedAssertionFilter> trivial, invalid, justified,
	isConclusion;
	private Map<String, ConstrainedAssertionFilter> lackQualities;
	private Map<ConstrainedAssertionFilter, String> solutionParts;

	/**
	 * contains the set of parsers given with the last parsed xml file
	 */

	private SubjectPredicateObjectMatchers lastGivenSetOfParsers;

	private float excessTimeLimit;

	/**
	 * keep inference states
	 */

	InferableAssertions expertValid, studentValid;

	/**
	 * reset the state of the machine
	 */

	public void resetState() {
		this.implicit = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.expert = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.studentArguments = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.studentConclusions = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.inferenceMaps = new HashMap<String, InferenceMap>();
		this.trivialInferenceMaps = new HashMap<String, InferenceMap>();
		this.trivial = new HashSet<ConstrainedAssertionFilter>();
		this.invalid = new HashSet<ConstrainedAssertionFilter>();
		this.justified = new HashSet<ConstrainedAssertionFilter>();
		this.isConclusion = new HashSet<ConstrainedAssertionFilter>();
		this.expertValid = new InferableAssertions(
				new HashSet<AssertionInterface>(),
				new HashSet<AssertionInterface>(), new HashSet<InferenceMap>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.studentValid = new InferableAssertions(
				new HashSet<AssertionInterface>(),
				new HashSet<AssertionInterface>(), new HashSet<InferenceMap>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.lackQualities = new HashMap<String, ConstrainedAssertionFilter>();
		this.solutionParts = new HashMap<ConstrainedAssertionFilter, String>();
		this.lastGivenSetOfParsers = new SubjectPredicateObjectMatchers(
				new HashSet<SubjectPredicateObjectMatcher>());
	}

	/**
	 * reset the student's state of the machine
	 */

	public void resetStudentsState() {

		this.studentArguments = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.studentConclusions = new AssertionEquivalenceClasses(
				new HashSet<ArrayList<Integer>>(), new StringIds());
		this.studentValid = new InferableAssertions(
				new HashSet<AssertionInterface>(),
				new HashSet<AssertionInterface>(), new HashSet<InferenceMap>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ArrayList<Integer>>(), new StringIds());
	}

	public InferenceMachine() {
		this.resetState();

		this.excessTimeLimit = defaultExcessTimeLimit;
	}

	/**
	 * add data from inference xml file to the current machine state
	 * 
	 * @param root
	 */
	private void addXml(XmlRootTag root) {

		System.out.println("Adding: " + root.toString());

		this.implicit.addNewAssertions(root.getImplicitAssertions());
		this.expert.addNewAssertions(root.getExpertAssertions());
		this.studentArguments.addNewAssertions(root.getGivenAssertions());
		this.studentConclusions.addNewAssertions(root.getGivenConclusions());

		Map<String, InferenceMap> updated_rules = root.getInferenceMapsByName();

		for (String key : updated_rules.keySet()) {
			this.inferenceMaps.put(key, updated_rules.get(key));
		}

		updated_rules = root.getTrivialInferenceMapsByName();

		for (String key : updated_rules.keySet()) {
			this.trivialInferenceMaps.put(key, updated_rules.get(key));
		}

		this.trivial.addAll(root.getTrivialityFilters());
		this.invalid.addAll(root.getInvalidityFilters());
		this.justified.addAll(root.getJustifiedFilters());

		this.isConclusion.addAll(root.getConclusionFilters());

		for (ConstrainedAssertionFilter key : root.getQualityFilters().keySet()) {
			this.lackQualities.put(root.getQualityFilters().get(key), key);
		}

		this.lastGivenSetOfParsers = root.getParsers();

		for (ConstrainedAssertionFilter key : root.getPartFilters().keySet()) {
			this.solutionParts.put(key, root.getPartFilters().get(key));
		}

	}

	/**
	 * feeds inference machine data from the website java script into the applet
	 * 
	 * @param food
	 * @return success-status
	 */

	public String feed(String food) {

		System.out.println("fed with: " + food);

		String success = "";

		this.resetState();

		System.out.println("addXmlString gives: " + this.addXmlString(food));

		success += "assertions: " + this.closeExpertAssertions();

		this.updateExpertJustification();

		success += " ancestors: " + this.calculateAncestors();

		success += " preimages: " + this.calculatePreimages();

		return success;
	}

	/**
	 * adds a point
	 * 
	 * @param point
	 */

	public void addPoint(String point) {
		this.studentArguments.addNewAssertions(this.lastGivenSetOfParsers.match(point));
	}

	/**
	 * adds a conclusion
	 * 
	 * @param conlusion
	 */

	public void addConclusion(String conclusion) {
		this.studentConclusions.addNewAssertions(this.lastGivenSetOfParsers
				.match(conclusion));
	}

	/**
	 * 
	 * @return feedback data for
	 */

	public String checkAnswerAndFeedback() {
		String status = "";

		/**
		 * prepare studentValid data structure
		 */

		this.prepareStudentAssertions(false);

		String trivial_closure_status = "close: "
				+ this.studentValid.closeValid(
						new ExcessLimit(this.excessTimeLimit)).name();

		/**
		 * update justifications
		 */

		this.updateStudentJustification();

		/**
		 * calculate feedback data
		 */

		String feedback = "";

		boolean invalid_points = false;
		boolean invalid_conclusions = false;

		/**
		 * check which points are correct
		 */

		int correct_points = 0;

		String correct_point_list = "";

		/**
		 * and check which points are plain wrong
		 */

		int wrong_points = 0;

		String wrong_point_list = "";

		/**
		 * and check which points are plain wrong
		 */

		int unjustified_points = 0;

		String unjustified_point_list = "";

		for (AssertionInterface assertion : this.studentArguments.getClasses()) {
			if (this.expertValid.isInferable(assertion)) {

				correct_point_list += assertion.getSubject().toString() + " "
						+ assertion.getPredicate().toString() + " "
						+ assertion.getObject().toString() + "\n";
				correct_points++;

				if (this.studentValid.justificationLevel(assertion) == EquivalentAssertions.notJustified) {
					unjustified_point_list += assertion.getSubject().toString()
							+ " " + assertion.getPredicate().toString() + " "
							+ assertion.getObject().toString() + "\n";
					unjustified_points++;
				}
			} else {
				invalid_points = true;
				wrong_point_list += assertion.getSubject().toString() + " "
						+ assertion.getPredicate().toString() + " "
						+ assertion.getObject().toString() + "\n";
				wrong_points++;
			}
		}

		feedback += correct_points + "\n" + correct_point_list;
		feedback += wrong_points + "\n" + wrong_point_list;
		feedback += unjustified_points + "\n" + unjustified_point_list;

		/**
		 * check which conclusions are correct
		 */

		int correct_conclusions = 0;

		String correct_conclusion_list = "";

		/**
		 * and check which given conclusions are correct and considered to be
		 * conclusions
		 */

		int good_conclusions = 0;

		String good_conclusion_list = "";

		/**
		 * and check which points are plain wrong
		 */

		int wrong_conclusions = 0;

		String wrong_conclusion_list = "";

		/**
		 * and check which conclusions are not justified
		 */

		int unjustified_conclusions = 0;

		String unjustified_conclusion_list = "";

		Set<AssertionInterface> singleton = new HashSet<AssertionInterface>();

		Set<String> solution_parts = new HashSet<String>();

		for (AssertionInterface assertion : this.studentConclusions.getClasses()) {
			if (this.expertValid.isInferable(assertion)) {

				correct_conclusion_list += assertion.getSubject().toString()
						+ " " + assertion.getPredicate().toString() + " "
						+ assertion.getObject().toString() + "\n";
				correct_conclusions++;

				/**
				 * check whether it is indeed considered to be a conclusion
				 */

				boolean good = false;

				singleton.clear();
				singleton.add(assertion);

				for (ConstrainedAssertionFilter filter : this.isConclusion) {
					if (filter.filter(singleton).isEmpty() == false) {
						good = true;
						break;
					}
				}

				if (good) {
					good_conclusion_list += assertion.getSubject().toString()
							+ " " + assertion.getPredicate().toString() + " "
							+ assertion.getObject().toString() + "\n";
					good_conclusions++;

					for (ConstrainedAssertionFilter filter : this.solutionParts
							.keySet()) {
						if (filter.filter(singleton).isEmpty() == false) {
							solution_parts.add(this.solutionParts.get(filter));
						}
					}

					if (this.studentValid.justificationLevel(assertion) == EquivalentAssertions.notJustified) {
						unjustified_conclusion_list += assertion.getSubject()
								.toString()
								+ " "
								+ assertion.getPredicate().toString()
								+ " "
								+ assertion.getObject().toString() + "\n";
						unjustified_conclusions++;
					}
				}

			} else {
				invalid_conclusions = true;
				wrong_conclusion_list += assertion.getSubject().toString()
						+ " " + assertion.getPredicate().toString() + " "
						+ assertion.getObject().toString() + "\n";
				wrong_conclusions++;
			}
		}

		feedback += correct_conclusions + "\n" + correct_conclusion_list;

		feedback += good_conclusions + "\n" + good_conclusion_list;

		feedback += wrong_conclusions + "\n" + wrong_conclusion_list;
		feedback += unjustified_conclusions + "\n"
				+ unjustified_conclusion_list;

		Set<AssertionInterface> need_more_justification = new HashSet<AssertionInterface>();

		for (EquivalentAssertions ea : this.studentValid.getGiven()
				.getEquivalencyClasses()) {
			if (this.studentValid.justificationLevel(ea) == EquivalentAssertions.notJustified) {
				need_more_justification.add(ea);
			}
		}

		/**
		 * give some qualitative feedback information
		 */

		if (invalid_points) {
			status += "invalid points,";
		} else {
			status += ",";
		}

		if (invalid_conclusions) {
			status += "invalid conclusions,";
		} else {
			status += ",";
		}

		status += "lacks:"
				+ this.expertValid.getJustificationTips(need_more_justification,
						this.studentValid.getGiven().getEquivalencyClasses(),
						this.lackQualities, true) + ",";

		status += "parts:";
		boolean first = true;
		for (String name : solution_parts) {
			if (first == false) {
				status += "&";
			}
			status += name;
			first = false;
		}

		status += "," + trivial_closure_status;

		return status + "\n" + feedback;
	}

	/**
	 * 
	 * @return excess time limit in seconds as string
	 */

	public String getExcessTimeLimit() {
		return "" + this.excessTimeLimit;
	}

	/**
	 * add inference xml data from a local file
	 * 
	 * @param location
	 * @return Error Message
	 */
	public String addXmlFile(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			handler.readStream(new FileInputStream(location));

			this.addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * add inference xml data from an url
	 * 
	 * @param location
	 * @return Error message
	 */
	public String addXmlUrl(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			URL url = new URL(location);
			handler.readStream(url.openStream());

			this.addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * add inference xml data given as string without surrounding &lt;root>-tag
	 * 
	 * @param rootContents
	 * @return Error message
	 */

	public String addXmlString(String rootContents) {

		try {
			XmlHandler handler = new XmlHandler();

			handler.readString(rootContents);

			this.addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding xml string \n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * close expert assertions under given rules
	 * 
	 * @return "closed", "invalid" or "excess"
	 */

	public String closeExpertAssertions() {
		this.expertValid = new InferableAssertions(this.implicit.getClasses(),
				this.expert.getClasses(), this.inferenceMaps.values(),
				this.invalid, this.trivial, new HashSet<ArrayList<Integer>>(),
				new StringIds());
		return this.expertValid.closeValid(new ExcessLimit(this.excessTimeLimit)).name();
	}

	/**
	 * join the inter representations of points and conclusions in the field
	 * studentValid to be able to process them
	 */

	public void prepareStudentAssertions(boolean allowNonTrivial) {

		Set<AssertionInterface> given = new HashSet<AssertionInterface>();

		given.addAll(this.studentArguments.getClasses());

		given.addAll(this.studentConclusions.getClasses());

		if (allowNonTrivial) {
			this.studentValid = new InferableAssertions(this.implicit.getClasses(),
					given,
					this.inferenceMaps.values(), this.invalid, this.trivial,
					new HashSet<ArrayList<Integer>>(), new StringIds());
		} else {
			this.studentValid = new InferableAssertions(this.implicit.getClasses(),
					given,
					this.trivialInferenceMaps.values(), this.invalid,
					this.trivial, new HashSet<ArrayList<Integer>>(),
					new StringIds());
		}

	}

	/**
	 * close student assertions under given rules
	 * 
	 * @return "closed", "invalid" or "excess"
	 */

	public String closeStudentAssertions() {
		this.prepareStudentAssertions(true);
		return this.studentValid.closeValid(new ExcessLimit(this.excessTimeLimit)).name();
	}

	/**
	 * 
	 * @return correct conclusions separated by '\n'
	 */
	public String getCorrectStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : this.studentConclusions.getClasses()) {
			if (this.expertValid.isInferable(conclusion)) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return incorrect conclusions separated by '\n'
	 */
	public String getIncorrectStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : this.studentConclusions.getClasses()) {
			if (this.expertValid.isInferable(conclusion) == false) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return inferable conclusions separated by '\n'
	 */
	public String getInferableStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : this.studentConclusions.getClasses()) {
			if (this.studentValid.isInferable(conclusion)) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return non-inferable conclusions separated by '\n'
	 */
	public String getNonInferableStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : this.studentConclusions.getClasses()) {
			if (this.studentValid.isInferable(conclusion) == false) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * calculate justification levels
	 */

	public void updateExpertJustification() {
		this.expertValid
		.updateJustification(this.justified, this.expertValid.getValid(), true);
	}

	/**
	 * calculate justification levels
	 */

	public void updateStudentJustification() {

		this.studentValid.relativeJustification(this.expertValid);

	}

	/**
	 * 
	 * @return report on the inference of the expert assertions
	 */
	public String getExpertReport() {
		return this.expertValid.getReport();
	}

	/**
	 * 
	 * @return report on the inference of the student assertions
	 */
	public String getStudentReport() {
		return this.studentValid.getReport();
	}

	public static String orderedAssertionSetString(String pre,
			Collection<? extends AssertionInterface> set, String post,
			String heading) {

		if (set.isEmpty()) {
			return "";
		}

		String result = heading;

		Set<String> s = new TreeSet<String>();

		for (AssertionInterface a : set) {
			s.add(a.getSubject().toString() + "·" + a.getPredicate().toString()
					+ "·" + a.getObject().toString());
		}

		for (String x : s) {
			result += pre + x + post;
		}

		return result;
	}

	/**
	 * 
	 * @return report on the student assertions, conclusions etc. relative to
	 *         the expert solution
	 */
	public String getReport() {
		String report = "";

		Set<AssertionInterface> correct_arguments = new HashSet<AssertionInterface>();
		Set<AssertionInterface> incorrect_arguments = new HashSet<AssertionInterface>();
		Set<AssertionInterface> correct_arguments_yet_unjustified = new HashSet<AssertionInterface>();

		for (AssertionInterface a : this.studentArguments.getClasses()) {
			if (this.expertValid.isInferable(a)) {
				correct_arguments.add(a);
				if (this.studentValid.justificationLevel(a) == EquivalentAssertions.notJustified) {
					correct_arguments_yet_unjustified.add(a);
				}
			} else {
				incorrect_arguments.add(a);
			}

		}

		Set<AssertionInterface> correct_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> incorrect_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> inferable_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> correct_conclusions_yet_unjustified = new HashSet<AssertionInterface>();

		for (AssertionInterface a : this.studentConclusions.getClasses()) {
			if (this.expertValid.isInferable(a)) {
				correct_conclusions.add(a);
				if (this.studentValid.justificationLevel(a) == EquivalentAssertions.notJustified) {
					correct_conclusions_yet_unjustified.add(a);
				}
			} else {
				incorrect_conclusions.add(a);
			}
			if (this.studentValid.isInferable(a)) {
				inferable_conclusions.add(a);

			}
		}

		report += orderedAssertionSetString("     ", correct_arguments, "\n",
				"\nThe following arguments are correct:\n");
		report += orderedAssertionSetString("     ",
				correct_arguments_yet_unjustified, "\n",
				" where the following arguments need further justification:\n");

		report += orderedAssertionSetString("     ", incorrect_arguments, "\n",
				"\nThe following arguments are incorrect:\n");

		report += orderedAssertionSetString("     ", correct_conclusions, "\n",
				"\nThe following conclusions are correct:\n");

		report += orderedAssertionSetString("     ",
				correct_conclusions_yet_unjustified, "\n",
				" where the following conclusions need further justification:\n");

		report += orderedAssertionSetString("     ", incorrect_conclusions,
				"\n", "\nThe following conclusions are incorrect:\n");

		report += orderedAssertionSetString("     ", inferable_conclusions,
				"\n",
				"\nThe following conclusions can be infered from the arguments:\n");

		Set<AssertionInterface> need_more_justification = new HashSet<AssertionInterface>();
		need_more_justification.addAll(correct_conclusions_yet_unjustified);
		need_more_justification.addAll(correct_arguments_yet_unjustified);

		report += "\nTips\n";
		report += this.expertValid.getJustificationTips(need_more_justification,
				this.studentValid.getGiven().getEquivalencyClasses(), this.lackQualities);

		return report;
	}

	/**
	 * calculate all ancestor sets for the expert valid assertions
	 * 
	 * @return "closed" or "excess"
	 */

	public String calculateAncestors() {
		ExcessLimit result = new ExcessLimit(this.excessTimeLimit);
		this.expertValid.calculateAncestors(result);
		if (result.exceeded()) {
			return "excess";
		}
		return "closed";
	}

	/**
	 * calculate all preimage sets for the expert valid assertions
	 * 
	 * @return "closed" or "excess"
	 */

	public String calculatePreimages() {
		ExcessLimit result = new ExcessLimit(this.excessTimeLimit);
		this.expertValid.calculateRuleAncestors(result);
		if (result.exceeded()) {
			return "excess";
		}
		return "closed";
	}

	/**
	 * count the number of possible justifications
	 * 
	 * @return report
	 */

	public String countJustifications() {
		ExcessLimit result = new ExcessLimit(this.excessTimeLimit);
		this.expertValid.calculateRuleAncestors(result);
		if (result.exceeded()) {
			return "Cannot calculate ancestors due to time excess!";
		}

		String report = "";

		for (AssertionInterface a : this.studentConclusions.getClasses()) {
			if (report.isEmpty() == false) {
				report += "\n";
			}
			report += a + ": " + this.expertValid.countPossibleJustifications(a);
		}

		return report;
	}

	/**
	 * 
	 * @return a formatted list of rule names
	 */

	public String getRuleNames() {
		String result = "Rules: ";
		for (String name : this.inferenceMaps.keySet()) {
			result += name + " ";
		}

		result += "\nTrivial: ";

		for (String name : this.trivialInferenceMaps.keySet()) {
			result += name + " ";
		}

		return result;
	}

}
