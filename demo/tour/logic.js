/**
 * logic.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * checks, whether in set is a value v, that gives test(v) implicit true
 */
function Exists(set, test) {
	for (var int=0;int < set.length; ++int) {
		if (test(set[int])) {
			return set[int];
		}
	}
	return null;
}

/**
 * checks, whether in set is a value v, that gives test(v) implicit true
 */
function ExistsTrack(set, quantified, test) {
	for (var int=0;int < set.length; ++int) {
		var f = set[int];
		if (quantified.indexOf(f) <= 0)
			quantified.push(f);
	}
	for (var int=0;int < set.length; ++int) {
		if (test(set[int])) {
			return set[int];
		}
	}
	return null;
}