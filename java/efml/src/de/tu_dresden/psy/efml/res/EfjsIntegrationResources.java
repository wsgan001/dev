/**
 * EfjsIntegrationResources.java, (c) 2013, Immanuel Albrecht; Dresden
 * University of Technology, Professur für die Psychologie des Lernen und
 * Lehrens
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

package de.tu_dresden.psy.efml.res;

import java.lang.reflect.InvocationTargetException;

import de.tu_dresden.psy.efml.AirportTag;
import de.tu_dresden.psy.efml.AnswerTag;
import de.tu_dresden.psy.efml.AnyTag;
import de.tu_dresden.psy.efml.ArrowTag;
import de.tu_dresden.psy.efml.BodyTag;
import de.tu_dresden.psy.efml.BoxspaceTag;
import de.tu_dresden.psy.efml.CTag;
import de.tu_dresden.psy.efml.CheckTag;
import de.tu_dresden.psy.efml.CheckboxTag;
import de.tu_dresden.psy.efml.CorrectTag;
import de.tu_dresden.psy.efml.DocumentIdTag;
import de.tu_dresden.psy.efml.DropdownTag;
import de.tu_dresden.psy.efml.EfmlBoardTag;
import de.tu_dresden.psy.efml.EfmlPreviewButtonTag;
import de.tu_dresden.psy.efml.EfmlQuoteTag;
import de.tu_dresden.psy.efml.EfmlTagsAttribute;
import de.tu_dresden.psy.efml.FeedbackTag;
import de.tu_dresden.psy.efml.FloatboxTag;
import de.tu_dresden.psy.efml.FreetextTag;
import de.tu_dresden.psy.efml.HintTag;
import de.tu_dresden.psy.efml.IncludeAddendumTag;
import de.tu_dresden.psy.efml.IncludePreambleTag;
import de.tu_dresden.psy.efml.IncompleteTag;
import de.tu_dresden.psy.efml.IncorrectTag;
import de.tu_dresden.psy.efml.InferenceTag;
import de.tu_dresden.psy.efml.InferenceXmlTag;
import de.tu_dresden.psy.efml.InstructionsTag;
import de.tu_dresden.psy.efml.JsUrlTag;
import de.tu_dresden.psy.efml.LabelTag;
import de.tu_dresden.psy.efml.MultilineTag;
import de.tu_dresden.psy.efml.NeedJustificationTag;
import de.tu_dresden.psy.efml.OptionTag;
import de.tu_dresden.psy.efml.PhpUrlTag;
import de.tu_dresden.psy.efml.PlainTag;
import de.tu_dresden.psy.efml.PopupHelpTag;
import de.tu_dresden.psy.efml.RTag;
import de.tu_dresden.psy.efml.RadiobuttonTag;
import de.tu_dresden.psy.efml.RequiredTag;
import de.tu_dresden.psy.efml.RunwayTag;
import de.tu_dresden.psy.efml.SniffyTag;
import de.tu_dresden.psy.efml.StudyIdTag;
import de.tu_dresden.psy.efml.SubjectChangeTag;
import de.tu_dresden.psy.efml.SubjectInfoTag;
import de.tu_dresden.psy.efml.SubjectPromptTag;
import de.tu_dresden.psy.efml.TablesTag;
import de.tu_dresden.psy.efml.TagsTag;
import de.tu_dresden.psy.efml.TemplateTag;
import de.tu_dresden.psy.efml.TieTag;
import de.tu_dresden.psy.efml.TitleTag;
import de.tu_dresden.psy.efml.TrashcanTag;
import de.tu_dresden.psy.efml.UnreadTag;
import de.tu_dresden.psy.efml.UnusedTag;
import de.tu_dresden.psy.efml.WaitForTag;

/**
 * This class is the common code point for the integration of new efjs elements,
 * so that there is a common place for adding .js files and the corresponding
 * classes
 * 
 * @author immo
 * 
 */

public class EfjsIntegrationResources {

	/**
	 * the list of all efjs files that are needed for generated web pages
	 */

	public static String[] javascript_includes = {
	/**
	 * cssSandpaper
	 */
	"EventHelpers.js", "cssQuery-p.js", "jcoglan.com.sylvester.js",
			"cssSandpaper.js",
			/**
			 * jQuery
			 */
			"jquery-1.8.0.min.js",
			/**
			 * prototypejs
			 */
			"prototype.js",
			/**
			 * efjs scripts
			 */
			"crosscompat.js", "mouse.js", "cssgraphics.js", "loglet.js",
			"storage.js", "logger.js", "tags.js", "logic.js", "hover.js",
			"endecoder.js", "runway.js", "answer.js", "sniffybutton.js",
			"dropdown.js", "checkbox.js", "radiobutton.js", "popupbutton.js",
			"freetext.js", "boxspace.js", "trashcan.js", "sentencepattern.js",
			"airport.js", "inference.js", "efml.js", "efmlbuttons.js",
			"efmlboard.js", "efmlcheckbox.js", "efmltag.js", "efmlquote.js",
			"efmlfactory.js" };

	/**
	 * interface for constructing other classes
	 */

	public static interface TagObjectConstructor {
		/**
		 * 
		 * 
		 * @return a new tag-handling object
		 */
		AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body);
	};

	/**
	 * X() constructor
	 */

	static class SimpleObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		public SimpleObjectConstructor(Class t) {
			this.t = t;
		}

		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.newInstance();
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			}
			return null;
		}

	};

	/**
	 * X(EfmlTagsAttribute attributes) constructor
	 */

	static class AttributeObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { EfmlTagsAttribute.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public AttributeObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();

			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(tags);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(EfmlTagsAttribute attributes, BodyTag body) constructor
	 */

	static class AttributeBodyObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { EfmlTagsAttribute.class,
				BodyTag.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public AttributeBodyObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();

			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(tags,
						body);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(AnyTag parent) constructor
	 */

	static class ParentObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings({ "rawtypes" })
		private static Class[] parameters = { AnyTag.class };

		@SuppressWarnings("rawtypes")
		public ParentObjectConstructor(Class t) {
			this.t = t;

			try {
				@SuppressWarnings({ "unchecked", "unused" })
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();

			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters)
						.newInstance(parent);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(BodyTag body) constructor
	 */

	static class BodyObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { BodyTag.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public BodyObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();

			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(body);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * tag name/handler class list
	 * 
	 */

	public static Object[] tagClasses = { "title",
			new AttributeObjectConstructor(TitleTag.class), "tags",
			new AttributeObjectConstructor(TagsTag.class), "efml",
			new AttributeObjectConstructor(TagsTag.class), "tie",
			new AttributeObjectConstructor(TieTag.class), "tables",
			new AttributeObjectConstructor(TablesTag.class), "r",
			new SimpleObjectConstructor(RTag.class), "c",
			new ParentObjectConstructor(CTag.class), "runway",
			new AttributeObjectConstructor(RunwayTag.class), "answer",
			new AttributeObjectConstructor(AnswerTag.class), "hint",
			new AttributeObjectConstructor(HintTag.class), "correct",
			new SimpleObjectConstructor(CorrectTag.class), "needjustification",
			new SimpleObjectConstructor(NeedJustificationTag.class),
			"incomplete", new SimpleObjectConstructor(IncompleteTag.class),
			"incorrect", new SimpleObjectConstructor(IncorrectTag.class),
			"check", new AttributeObjectConstructor(CheckTag.class),
			"includepreamble",
			new SimpleObjectConstructor(IncludePreambleTag.class),
			"includeaddendum",
			new SimpleObjectConstructor(IncludeAddendumTag.class), "label",
			new SimpleObjectConstructor(LabelTag.class), "unread",
			new SimpleObjectConstructor(UnreadTag.class), "unused",
			new SimpleObjectConstructor(UnusedTag.class), "instructions",
			new SimpleObjectConstructor(InstructionsTag.class), "sniffy",
			new SimpleObjectConstructor(SniffyTag.class), "waitfor",
			new SimpleObjectConstructor(WaitForTag.class), "dropdown",
			new AttributeObjectConstructor(DropdownTag.class), "radiobutton",
			new AttributeObjectConstructor(RadiobuttonTag.class), "checkbox",
			new AttributeObjectConstructor(CheckboxTag.class), "popuphelp",
			new AttributeObjectConstructor(PopupHelpTag.class), "freetext",
			new AttributeObjectConstructor(FreetextTag.class), "multiline",
			new AttributeObjectConstructor(MultilineTag.class), "option",
			new AttributeObjectConstructor(OptionTag.class), "studyid",
			new BodyObjectConstructor(StudyIdTag.class), "documentid",
			new BodyObjectConstructor(DocumentIdTag.class), "jsurl",
			new BodyObjectConstructor(JsUrlTag.class), "phpurl",
			new BodyObjectConstructor(PhpUrlTag.class), "subjectinfo",
			new BodyObjectConstructor(SubjectInfoTag.class), "subjectprompt",
			new BodyObjectConstructor(SubjectPromptTag.class), "subjectchange",
			new BodyObjectConstructor(SubjectChangeTag.class), "plain",
			new SimpleObjectConstructor(PlainTag.class), "template",
			new AttributeObjectConstructor(TemplateTag.class), "airport",
			new AttributeObjectConstructor(AirportTag.class), "feedback",
			new SimpleObjectConstructor(FeedbackTag.class), "required",
			new AttributeObjectConstructor(RequiredTag.class), "parse",
			new AttributeObjectConstructor(InferenceXmlTag.class), "subject",
			new AttributeObjectConstructor(InferenceXmlTag.class), "object",
			new AttributeObjectConstructor(InferenceXmlTag.class), "predicate",
			new AttributeObjectConstructor(InferenceXmlTag.class), "expert",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"conclusion",
			new AttributeObjectConstructor(InferenceXmlTag.class), "premise",
			new AttributeObjectConstructor(InferenceXmlTag.class), "infer",
			new AttributeObjectConstructor(InferenceXmlTag.class), "in",
			new AttributeObjectConstructor(InferenceXmlTag.class), "assert",
			new AttributeObjectConstructor(InferenceXmlTag.class), "implicit",
			new AttributeObjectConstructor(InferenceXmlTag.class), "rule",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"constraint",
			new AttributeObjectConstructor(InferenceXmlTag.class), "rho",
			new AttributeObjectConstructor(InferenceXmlTag.class), "out",
			new AttributeObjectConstructor(InferenceXmlTag.class), "justified",
			new AttributeObjectConstructor(InferenceXmlTag.class), "trivial",
			new AttributeObjectConstructor(InferenceXmlTag.class), "invalid",
			new AttributeObjectConstructor(InferenceXmlTag.class), "quality",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"conclusions",
			new AttributeObjectConstructor(InferenceXmlTag.class), "solves",
			new AttributeObjectConstructor(InferenceXmlTag.class), "inference",
			new AttributeObjectConstructor(InferenceTag.class), "trashcan",
			new AttributeObjectConstructor(TrashcanTag.class), "boxspace",
			new AttributeObjectConstructor(BoxspaceTag.class), "floatbox",
			new AttributeObjectConstructor(FloatboxTag.class), "arrow",
			new AttributeObjectConstructor(ArrowTag.class), "efmlboard",
			new AttributeObjectConstructor(EfmlBoardTag.class), "efmlpreview",
			new AttributeBodyObjectConstructor(EfmlPreviewButtonTag.class),
			"efmlquote", new AttributeObjectConstructor(EfmlQuoteTag.class) };

}