package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import de.tu_dresden.psy.inference.compiler.EmbeddedInferenceXmlTag;
import de.tu_dresden.psy.inference.compiler.InferenceCompiler;

/**
 * implements the &lt;inference>-tag
 * 
 * @author albrecht
 * 
 */

public class InferenceTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	private static boolean useInferenceApplet = false;



	/**
	 * contains the inference xml data that is embedded with the inference tag
	 */
	private ArrayList<EmbeddedInferenceXmlTag> xmlMachineData;

	private ArrayList<FeedbackTag> feedbackData;

	public InferenceTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		this.xmlMachineData = new ArrayList<EmbeddedInferenceXmlTag>();
		this.feedbackData = new ArrayList<FeedbackTag>();


	}

	@Override
	public void open(Writer writer) throws IOException {

		/****
		 * NEW CODE: use generated hypergraphs and javascript instead of feeding
		 * xml code into an applet
		 */
		if (useInferenceApplet == false) {
			/**
			 * compiler object that calculates the inference graphs etc.
			 */
			InferenceCompiler compiler = new InferenceCompiler();

			/**
			 * actually compile
			 */

			String compiler_errors = compiler
					.processXmlData(this.xmlMachineData);

			/**
			 * write the compiler errors in the html document :)
			 */

			writer.write(compiler_errors);

		}

		/**** OLD & OBSOLETE CODE */
		if (useInferenceApplet) {

			/**
			 * get inner data string
			 */

			StringWriter xmlData = new StringWriter();

			for (EmbeddedInferenceXmlTag tag : this.xmlMachineData) {
				InferenceXmlTag backconvert = (InferenceXmlTag) tag;
				backconvert.open(xmlData);
				backconvert.close(xmlData);
			}

			/**
			 * write script code
			 */

			writer.write("<script type=\"text/javascript\">");

			writer.write("new InferenceButton(");

			/**
			 * write tags
			 */

			writer.write(this.attributes.getAcceptTags() + ", ");
			writer.write(this.attributes.getRejectTags() + ", ");

			writer.write("\""
					+ StringEscape.escapeToJavaScript(this.attributes.getValueOrDefault(
							"pointstag", "points")) + "\", ");
			writer.write("\""
					+ StringEscape.escapeToJavaScript(this.attributes.getValueOrDefault(
							"conclusionstag", "conclusions")) + "\"");

			writer.write(").Feed(");

			/**
			 * write content data
			 */

			writer.write(StringEscape.escapeToDecodableInJavaScript(xmlData
					.toString()));

			writer.write(")");

			/**
			 * write feedback data
			 */

			for (FeedbackTag feedback : this.feedbackData) {
				if (feedback.getCorrect() != null) {
					writer.write(".SetCorrect(");
					writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
							.getCorrect().getFeedback())));
					writer.write(")");
				}

				if (feedback.getIncorrect() != null) {
					writer.write(".SetIncorrect(");
					writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
							.getIncorrect().getFeedback())));
					writer.write(")");
				}

				if (feedback.getIncomplete() != null) {
					writer.write(".SetIncomplete(");
					writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
							.getIncomplete().getFeedback())));
					writer.write(")");
				}

				if (feedback.getNeedjustification() != null) {
					writer.write(".SetInjustified(");
					writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
							.getNeedjustification().getFeedback())));
					writer.write(")");
				}

				for (HintTag hint : feedback.getHints()) {
					writer.write(".AddHint(");
					writer.write(StringEscape.escapeToDecodeInJavaScript(hint
							.getLack()));
					writer.write(",");
					writer.write(StringEscape.escapeToDecodableInJavaScript(hint
							.getHint()));
					writer.write(")");
				}

				for (RequiredTag require : feedback.getRequires()) {
					if (require.requiresCount()) {
						writer.write(".RequireCount(" + require.getCount() + ")");
					}
					if (require.requiresPart()) {
						writer.write(".RequirePart("
								+ StringEscape.escapeToDecodeInJavaScript(require
										.getPart()) + ")");
					}
				}
			}

			/**
			 * let java script create the html contents
			 */
			writer.write(".WriteHtml();");

			writer.write("</script>");

		}

	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			/**
			 * we just ignore PCDATA content (should only consist of whitespaces
			 * anyway)
			 */
		} else if (innerTag.getClass() == InferenceXmlTag.class) {
			this.xmlMachineData.add((InferenceXmlTag) innerTag);
		} else if (innerTag.getClass() == FeedbackTag.class) {
			this.feedbackData.add((FeedbackTag) innerTag);
		} else {
			throw new OperationNotSupportedException(
					"<inference> cannot enclose "
							+ innerTag.getClass().toString());
		}

	}
	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<inference");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		for (EmbeddedInferenceXmlTag t : this.xmlMachineData) {
			AnyTag backconversion = (AnyTag) t;
			representation.append(backconversion.getEfml());
		}
		for (AnyTag t: this.feedbackData) {
			representation.append(t.getEfml());
		}
		representation.append("</inference>");

		return representation.toString();
	}

}
