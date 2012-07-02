/**
 * checkbox.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var checkboxIdCounter = 0;
var checkboxArray = [];

/**
 * creates a check box object, that represents a check box list
 */
function Checkbox(name, tags, label, tokenChecked, tokenUnchecked) {
	this.id = checkboxIdCounter++;

	/**
	 * Provide automatic name generation: use provided tags
	 */

	if ((name === undefined) || (name === "") || (name === false)) {
		this.name = "";
		for (var i = 0; i < tags.length; ++i) {
			this.name += tags[i];
		}
	} else {
		this.name = name;
	}

	this.tags = tags;
	if (tokenChecked === undefined) {
		this.tokenChecked = "X";
	} else {
		this.tokenChecked = tokenChecked;
	}
	
	if (tokenUnchecked === undefined) {
		this.tokenUnchecked = "";
	} else {
		this.tokenUnchecked = tokenUnchecked;
	}
	
	this.token = this.tokenUnchecked;
	
	if (label === undefined) {
		this.label = "";
	} else {
		this.label = label;
	}
	
	this.width = "";
	this.height = "";
	this.colorEmpty = "#CCCCAA";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";
	

	
	/**
	 * this function sets the bounding parameters
	 * 
	 * @returns this
	 */
	this.Size = function(width, height) {
		this.width = width;
		this.height = height;

		return this;
	};



	/**
	 * this function sets the color parameters
	 * 
	 * @returns this
	 */
	this.Color = function(colorEmpty, colorFilled) {
		this.colorEmpty = colorEmpty;
		this.colorFilled = colorFilled;

		return this;
	};

	/**
	 * write the HTML code that will be used for displaying the check box
	 */
	this.WriteHtml = function() {
		var idstring = "checkbox"+this.id;
		
		document.write("<form id=\"checkboxForm"+this.id+"\" onsubmit=\"return false;\" style=\"display: inline-block;"+
				"background-color: "+this.colorFilled +";"+
				"\" onclick=\"checkboxArray["+this.id+"].OnClick();\">");
		
		document.write("<input type=\"checkbox\" name=\""+idstring+ "\" id=\""+idstring+"\" " 
				+ "style=\"display: inline-block; ");
		if (this.width) {
			document.write("width: "+this.width+"; ");
		}
		if (this.height){
			document.write("height: "+this.height+"; ");
		}
		document.write( "background-color: "+this.colorFilled +";\" "		
				+ "onclick=\"checkboxArray["+this.id+"].OnClick();\" "
				+ " />");
		
		document.write(this.label);
		
		document.write("</form>");
	};
	
	/**
	 * this function is called when the check box box changes its contents
	 */
	this.OnChange = function() {
		var element = document.getElementById("checkbox"+this.id);
		var checked = element.checked;
		
		if (checked) {
			this.token = this.tokenChecked;
		} else {
			this.token = this.tokenUnchecked;
		}
				
		this.MarkNeutral();
		myLogger.Log(this.name + " <- " + this.token);
	};
	
	/**
	 * this function is called when the check box is clicked
	 */
	this.OnClick = function() {
		var element = document.getElementById("checkbox"+this.id);
		element.checked = !element.checked;
		this.OnChange();
	};

	/**
	 * this function marks the current check box green
	 */
	this.MarkAsGood = function() {
		var html_object = document.getElementById("checkboxForm" + this.id);
		html_object.style.backgroundColor = this.colorGood;
	};

	/**
	 * this function demarks the current check box
	 */
	this.MarkNeutral = function() {
		var element = document.getElementById("checkboxForm" + this.id);
		element.style.backgroundColor = this.colorFilled;
		
	};
	
	/**
	 * return the current contents of the check box as string
	 */
	this.GetValue = function() {
		var element = document.getElementById("checkbox"+this.id);
		if (element.checked)
			return "X";
		return "";
	};
	
	/**
	 * restore the check box state from string
	 */
	
	this.SetValue = function(contents) {
		var element = document.getElementById("checkbox"+this.id);
		if (contents == "") {
			element.checked = false;
			this.token = this.tokenUnchecked;
		} else {
			element.checked = true;
			this.token = this.tokenChecked;
		}
			
	
	};

	checkboxArray[this.id] = this;
	myTags.Add(this, this.tags);
	
	myStorage.RegisterField(this,"checkboxArray["+this.id+"]");
}

