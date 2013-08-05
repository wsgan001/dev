/**
 * hypergraphs.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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

/**
 * This file contains routines that are used for abstract inference.
 */

/**
 * creates an abstract inference graph
 */

function InferenceGraph() {

	/**
	 * array that maps inference ids to their premise and conclusion
	 * representation
	 */
	this.inferences = [];

	/**
	 * this array contains a sorted list of ids of trivial inferences, i.e.
	 * inferences that are marked trivial
	 */

	this.trivial_inference_ids = [];

	/**
	 * maps assertion ids to an array of inference ids that have the given
	 * assertion in the premises
	 */
	this.premise_based = {};

	/**
	 * map assertion ids to an array of inference ids that have the given
	 * assertion in the conclusions
	 */
	this.conclusion_based = {};

	/**
	 * store the correct assertions
	 */
	this.correct = {};

	/**
	 * store the correct and justified assertions
	 */
	this.justified = {};

	/**
	 * store the trivially correct assertions
	 */
	this.trivial = {};

	/**
	 * store the concluding assertions
	 */
	this.concluding = {};
	
	/**
	 * store the solution part corresponding to some assertion
	 */
	
	this.solution_parts = {};

	/**
	 * adds a list of justified assertion ids
	 * 
	 * @returns this
	 */

	this.AddJustified = function(ids) {
		for ( var int = 0; int < ids.length; int++) {
			var assertion = ids[int];
			this.justified[assertion] = true;
		}

		return this;
	};

	/**
	 * adds a list of trivial assertion ids
	 * 
	 * @returns this
	 */

	this.AddTrivial = function(ids) {
		for ( var int = 0; int < ids.length; int++) {
			var assertion = ids[int];
			this.trivial[assertion] = true;
		}

		return this;
	};

	/**
	 * adds a list of solution part assertion ids, for the given part
	 * 
	 * @returns this
	 */

	this.AddSolutionPart = function(part,ids) {
		
		for ( var int = 0; int < ids.length; int++) {
			var assertion = ids[int];
			if (this.solution_parts.hasOwnProperty(assertion)) {
				if (this.solution_parts[assertion].lastIndexOf(part) < 0) {
					this.solution_parts[assertion].push(part);
				}
			} else {
				this.solution_parts[assertion] = [part];
			}
			
		}

		return this;
	};

	
	/**
	 * adds a list of correct assertion ids
	 * 
	 * @returns this
	 */

	this.AddCorrect = function(ids) {
		for ( var int = 0; int < ids.length; int++) {
			var assertion = ids[int];
			this.correct[assertion] = true;
		}

		return this;
	};

	/**
	 * adds a list of concluding assertion ids
	 * 
	 * @returns this
	 */

	this.AddConcluding = function(ids) {
		for ( var int = 0; int < ids.length; int++) {
			var assertion = ids[int];
			this.concluding[assertion] = true;
		}

		return this;
	};

	/**
	 * id assertion
	 * 
	 * @returns true, if the assertion corresponding to the id is correct
	 */

	this.IsCorrect = function(id) {
		return this.correct.hasOwnProperty(id);
	};


	/**
	 * id assertion
	 * 
	 * @returns an array of solution parts associated with the assertion
	 */

	this.SolvesWhichParts = function(id) {
		if (this.solution_parts.hasOwnProperty(id) == false) {
			/**
			 * the given assertion doesn't solve anything
			 */
			return [];
		}
		
		return this.solution_parts[id];
	};

	/**
	 * id assertion
	 * 
	 * @returns true, if the assertion corresponding to the id is concluding
	 */

	this.IsConcluding = function(id) {
		return this.concluding.hasOwnProperty(id);
	};

	/**
	 * id assertion
	 * 
	 * @returns true, if the assertion corresponding to the id is trivial
	 */

	this.IsTrivial = function(id) {
		return this.trivial.hasOwnProperty(id);
	};

	/**
	 * id assertion
	 * 
	 * @returns true, if the assertion corresponding to the id is justified by
	 *          the problem itself
	 */

	this.IsJustified = function(id) {
		return this.justified.hasOwnProperty(id);
	};

	/**
	 * adds an inference hyper-arrow from the premises to the conclusions
	 * 
	 * @returns this
	 */
	this.AddInference = function(premises, conclusions, isTrivial) {
		var sorted_premises = [];
		var sorted_conclusions = [];
		for ( var int = 0; int < premises.length; int++) {
			var x = premises[int];
			if ((x in sorted_premises) == false)
				sorted_premises.push(x);
		}
		for ( var int2 = 0; int2 < conclusions.length; int2++) {
			var x = conclusions[int2];
			if ((x in sorted_conclusions) == false)
				sorted_conclusions.push(x);
		}

		sorted_premises.sort();
		sorted_conclusions.sort();

		var inference_id = this.inferences.length;

		this.inferences.push({
			"p" : sorted_premises,
			"c" : sorted_conclusions
		});

		for ( var int3 = 0; int3 < sorted_premises.length; int3++) {
			var premise_id = sorted_premises[int3];

			if (premise_id in this.premise_based == false) {
				this.premise_based[premise_id] = [];
			}

			this.premise_based[premise_id].push(inference_id);
		}

		for ( var int4 = 0; int4 < sorted_conclusions.length; int4++) {
			var conclusion_id = sorted_conclusions[int4];

			if (conclusion_id in this.conclusion_based == false) {
				this.conclusion_based[conclusion_id] = [];
			}

			this.conclusion_based[conclusion_id].push(inference_id);
		}

		/**
		 * if this is a trivial inference, add it to the list
		 */

		if (isTrivial) {
			this.trivial_inference_ids.push(inference_id);
		}

		return this;

	};

	/**
	 * returns all inference rule ids that have the given premises or less
	 */

	this.GetConcludibleInferences = function(plain_premises) {

		var premises = [];

		/**
		 * we have to take care of the trivial inference rules first, so we
		 * close the premises under the trivial rules, and then take that closed
		 * premise set and look for possible conclusions
		 */

		for ( var int_p = 0; int_p < plain_premises.length; int_p++) {
			premises.push(plain_premises[int_p]);
		}

		var last_premise_length = 0;
		var used_trivial_inferences = {};

		while (premises.length > last_premise_length) {
			/**
			 * continue as long as we have new premises
			 */
			last_premise_length = premises.length;
			
			for ( var int_t = 0; int_t < this.trivial_inference_ids.length; int_t++) {
				var inference_id = this.trivial_inference_ids[int_t];
				if (inference_id in used_trivial_inferences) {
					/**
					 * skip this inference
					 */
				} else {
					var got_all_premises = true;
					for ( var int_pr = 0; int_pr < this.inferences[inference_id].p.length; int_pr++) {
						var premise_needed = this.inferences[inference_id].p[int_pr];
						if (premises.indexOf(premise_needed) < 0) {
							got_all_premises = false;
							break;
						}
					}
					if (got_all_premises) {
						/**
						 * keep track of which inference ids we used
						 */
						used_trivial_inferences[inference_id] = true;
						for ( var int_con = 0; int_con < this.inferences[inference_id].c.length; int_con++) {
							var conclusion_id = this.inferences[inference_id].c[int_con];
							if (premises.indexOf(conclusion_id)<0) {
								premises.push(conclusion_id);
							}
						}
					}
				}

			}
		}
		
		
		//console.log("given premises:");
		//console.log(plain_premises);
		//console.log("trivially inferred premises:");
		//console.log(premises);

		/**
		 * check for conclusions
		 */

		var possible_keys = [];

		for ( var int = 0; int < premises.length; int++) {
			var premise_id = parseInt(premises[int]);

			if (premise_id in this.premise_based) {
				var inferences = this.premise_based[premise_id];
				for ( var int2 = 0; int2 < inferences.length; int2++) {
					var inference_id = inferences[int2];

					if (possible_keys.indexOf(inference_id) < 0) {

						possible_keys.push(inference_id);
					}
				}
			}
		}

		var good = [];

		for ( var int3 = 0; int3 < possible_keys.length; int3++) {
			var inference_id = possible_keys[int3];

			var inference = this.inferences[inference_id];

			var all_premises_there = true;

			for ( var int4 = 0; int4 < inference.p.length; int4++) {
				var premise_id = inference.p[int4];

				if (premises.indexOf(premise_id) < 0) {
					all_premises_there = false;
					break;
				}

			}

			if (all_premises_there)
				good.push(inference_id);
		}

		return good;
	};

	/**
	 * @param points
	 *            an array of the points given (as ids)
	 * 
	 * @returns an object that has the properties "justified" and "unjustified",
	 *          that contain the respective ids
	 */

	this.CloseJustification = function(points) {
		
		//console.log("close just: "+points);
		
		var result = {};
		result.justified = [];
		result.unjustified = [];

		/**
		 * initially add the points to justified if they are justified wrt to
		 * the problem; all other points are added to unjustified
		 */

		for ( var int = 0; int < points.length; int++) {
			var id = parseInt(points[int]);
			if (this.IsJustified(id))
				result.justified.push(id);
			else
				result.unjustified.push(id);
		}
		
		//console.log(result);

		var last_nbr_of_justified = -1;

		/**
		 * keep track which inferences have been used already, so we may skip
		 * them
		 */
		var inference_used = {};

		console.log("closing justification");

		while ((result.justified.length != last_nbr_of_justified)
				&& ((result.unjustified.length > 0))) {
			/**
			 * we have some new justified assertions /and some unjustified
			 * assertions left
			 */

			 console.log("justified "+result.justified.length);
			 console.log(": "+result.justified);
			 console.log("unjustified "+result.unjustified.length);
			 console.log(": "+result.unjustified);
			last_nbr_of_justified = result.justified.length;

			var inference_ids = this.GetConcludibleInferences(result.justified);
			
			

			 console.log("ids");
			 console.log(":-->> "+inference_ids);

			/**
			 * add the newly justified points from the new inference_ids
			 */

			for ( var int2 = 0; int2 < inference_ids.length; int2++) {
				var infer_id = inference_ids[int2];

				if (inference_used.hasOwnProperty(infer_id) == false) {
					var conclusions = this.inferences[infer_id].c;

					for ( var int3 = 0; int3 < conclusions.length; int3++) {
						var conclusion_id = conclusions[int3];
						var idx = result.unjustified.indexOf(conclusion_id);

						if (idx >= 0) {
							console.log("FOUND " + conclusion_id);
							result.justified.push(conclusion_id);
							/** remove the concludible assertion from unjustified */
							result.unjustified.splice(idx, 1);
							console.log(result.unjustified);
						}
					}

					inference_used[infer_id] = true;
				}
			}
		}
		
		 console.log("FINAL justified "+result.justified.length);
		 console.log(": "+result.justified);
		 console.log("FINAL unjustified "+result.unjustified.length);
		 console.log(": "+result.unjustified);

		return result;
	};

	/**
	 * 
	 * @param closed_results
	 *            an object whose .justified property is an array of justified
	 *            assertion ids and whose .unjustified property is an array of
	 *            unjustified assertion ids
	 * 
	 * @returns a set of point ids that needs to be justified in order to
	 *          justify all given points
	 */

	this.WhichPointsNeedJustification = function(closed_results) {
		var would_be_justified = [];
		var unjustified_left = [];
		var premise = [];

		/** copy justified to premise buffer */
		for ( var int = 0; int < closed_results.justified.length; int++) {
			var id = closed_results.justified[int];
			premise.push(id);
		}

		/** copy unjustified to working buffer */
		for ( var int = 0; int < closed_results.unjustified.length; int++) {
			var id = closed_results.unjustified[int];
			
			if (this.IsCorrect(id) && (!this.IsTrivial(id))) {
				
				/**
				 * there is no point in trying to justify an incorrect point,
				 * or a trivial one
				 */
				
				unjustified_left.push(id);
			}
				
		}

		/** determinize order */
		premise.sort();
		unjustified_left.sort();

		while (unjustified_left.length > 0) {
			var new_unjustifieds = [];
			var count_unjustifieds = unjustified_left.length;
			var added_id = -1;
			for ( var int2 = 0; int2 < unjustified_left.length; int2++) {
				var id = unjustified_left[int2];
				premise.push(id);

				var inference_ids = this.GetConcludibleInferences(premise);
				var still_unjustified = [];
				for ( var int3 = 0; int3 < unjustified_left.length; int3++) {
					var just_id = unjustified_left[int3];
					if (just_id == id)
						still_unjustified.push(false);
					else
						still_unjustified.push(true);
				}

				for ( var int4 = 0; int4 < inference_ids.length; int4++) {
					var infer_id = inference_ids[int4];
					var conclusions = this.inferences[infer_id].c;
					for ( var int5 = 0; int5 < conclusions.length; int5++) {
						var c_id = conclusions[int5];
						var idx = unjustified_left.indexOf(c_id);
						if (idx >= 0) {
							still_unjustified[idx] = false;
						}
					}
				}
				
				//console.log("premises "+premise);

				premise.pop(id);
				var count_this_unjustifieds = 0;
				for ( var int6 = 0; int6 < still_unjustified.length; int6++) {
					var unj = still_unjustified[int6];
					if (unj)
						count_this_unjustifieds += 1;
				}
				
				//console.log(id+": "+count_this_unjustifieds+" unjust'ds");
				//console.log("which are: "+still_unjustified);

				if (count_this_unjustifieds < count_unjustifieds) {
					new_unjustifieds = still_unjustified;
					count_unjustifieds = count_this_unjustifieds;
					added_id = id;
				}
			}
			var new_left = [];
			for ( var int7 = 0; int7 < new_unjustifieds.length; int7++) {
				var unj = new_unjustifieds[int7];
				if (unj) {
					new_left.push(unjustified_left[int7]);
				}
			}

			//console.log("Adding premise: "+added_id);
			
			unjustified_left = new_left;
			premise.push(added_id);
			would_be_justified.push(added_id);
		}

		return would_be_justified;
	};

	/**
	 * returns all inference rule ids that have any of the given conclusions
	 */

	this.GetJustifyingInferences = function(conclusions) {
		var inferences = [];

		for ( var int = 0; int < conclusions.length; int++) {
			var conclusion_id = parseInt(conclusions[int]);

			if (conclusion_id in this.conclusion_based) {
				for ( var int2 = 0; int2 < this.conclusion_based[conclusion_id].length; int2++) {
					var inference_id = this.conclusion_based[conclusion_id][int2];

					if (inferences.indexOf(inference_id) < 0) {
						inferences.push(inference_id);
					}

				}
			}
		}

		return inferences;
	};

	return this;
};
