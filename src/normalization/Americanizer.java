package normalization;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Takes a HasWord or String and returns an Americanized version of it.
 * Optionally, it does some month/day name normalization to capitalized. This is
 * deterministic spelling coversion, and so cannot deal with certain cases
 * involving complex ambiguities, but it can do most of the simple case of
 * English to American conversion.
 * <p>
 * <i>This list is still quite incomplete, but does some of the commenest cases
 * found when running our parser or doing biomedical processing. to expand this
 * list, we should probably look at:</i>
 * <code>http://wordlist.sourceforge.net/</code> or
 * <code>http://home.comcast.net/~helenajole/Harry.html</code>.
 * 
 * @author Christopher Manning
 */
public class Americanizer /* implements Function */ {

	/** No word shorter in length than this is changed by Americanize */
	private static final int MINIMUM_LENGTH_CHANGED = 4;
	/** No word shorter in length than this can match a Pattern */
	private static final int MINIMUM_LENGTH_PATTERN_MATCH = 6;

	public static String americanize(String str) {
		int length = str.length();
		if (length < MINIMUM_LENGTH_CHANGED) {
			return str;
		}
		String result = mapping.get(str);
		if (result != null) {
			return result;
		}

		if (length < MINIMUM_LENGTH_PATTERN_MATCH) {
			return str;
		}
		if (!disjunctivePattern.matcher(str).find()) {
			return str;
		}
		for (int i = 0; i < pats.length; i++) {
			Matcher m = pats[i].matcher(str);
			if (m.find()) {
				Pattern ex = excepts[i];
				if (ex != null) {
					Matcher me = ex.matcher(str);
					if (me.find()) {
						continue;
					}
				}
				return m.replaceAll(reps[i]);
			}
		}
		return str;
	}

	private static final String[] patStrings = { "haem(at)?o", "aemia$", "([lL])eukaem", "programme(s?)$",
			"^([a-z]{3,})our(s?)$",

	};

	private static final Pattern[] pats = new Pattern[patStrings.length];

	private static final Pattern disjunctivePattern;

	static {
		StringBuilder foo = new StringBuilder();
		for (int i = 0, len = pats.length; i < len; i++) {
			pats[i] = Pattern.compile(patStrings[i]);
			if (i > 0) {
				foo.append('|');
			}
			foo.append("(?:");
			// Remove groups from String before appending for speed
			foo.append(patStrings[i].replaceAll("[()]", ""));
			foo.append(')');
		}
		disjunctivePattern = Pattern.compile(foo.toString());
	}

	private static final String[] OUR_EXCEPTIONS = { "abatjour", "beflour", "bonjour", "calambour", "carrefour",
			"cornflour", "contour", "de[tv]our", "dortour", "dyvour", "downpour", "giaour", "glamour", "holour",
			"inpour", "outpour", "pandour", "paramour", "pompadour", "recontour", "repour", "ryeflour", "sompnour",
			"tambour", "troubadour", "tregetour", "velour" };

	private static final Pattern[] excepts = { null, null, null, null,
			Pattern.compile(StringUtils.join(OUR_EXCEPTIONS, "|")) };

	private static final String[] reps = { "hem$1o", "emia", "$1eukem", "program$1", "$1or$2" };
	private static final HashMap<String, String> mapping = new HashMap<String, String>();

	private static final String[] converters = { "anaesthetic", "analogue", "analogues", "analyse", "analysed",
			"analysing", /* not analyses NNS */
			"armoured", "cancelled", "cancelling", "candour", "capitalise", "capitalised", "capitalisation", "centre",
			"chimaeric", "clamour", "coloured", "colouring", "colourful", "defence", "Defence",
			/* "dialogue", "dialogues", */ "discolour", "discolours", "discoloured", "discolouring", "encyclopaedia",
			"endeavour", "endeavours", "endeavoured", "endeavouring", "fervour", "favour", "favours", "favoured",
			"favouring", "favourite", "favourites", "fibre", "fibres", "finalise", "finalised", "finalising", "flavour",
			"flavours", "flavoured", "flavouring", "glamour", "grey", "harbour", "harbours", "homologue", "homologues",
			"honour", "honours", "honoured", "honouring", "honourable", "humour", "humours", "humoured", "humouring",
			"kerb", "labelled", "labelling", "labour", "Labour", "labours", "laboured", "labouring", "leant", "learnt",
			"localise", "localised", "manoeuvre", "manoeuvres", "maximise", "maximised", "maximising", "meagre",
			"minimise", "minimised", "minimising", "modernise", "modernised", "modernising", "misdemeanour",
			"misdemeanours", "neighbour", "neighbours", "neighbourhood", "neighbourhoods", "oestrogen", "oestrogens",
			"organisation", "organisations", "penalise", "penalised", "popularise", "popularised", "popularises",
			"popularising", "practise", "practised", "pressurise", "pressurised", "pressurises", "pressurising",
			"realise", "realised", "realising", "realises", "recognise", "recognised", "recognising", "recognises",
			"rumoured", "rumouring", "savour", "savours", "savoured", "savouring", "splendour", "splendours", "theatre",
			"theatres", "titre", "titres", "travelled", "travelling" };

	private static final String[] converted = { "anesthetic", "analog", "analogs", "analyze", "analyzed", "analyzing",
			"armored", "canceled", "canceling", "candor", "capitalize", "capitalized", "capitalization", "center",
			"chimeric", "clamor", "colored", "coloring", "colorful", "defense", "Defense",
			/* "dialog", "dialogs", */ "discolor", "discolors", "discolored", "discoloring", "encyclopedia", "endeavor",
			"endeavors", "endeavored", "endeavoring", "fervor", "favor", "favors", "favored", "favoring", "favorite",
			"favorites", "fiber", "fibers", "finalize", "finalized", "finalizing", "flavor", "flavors", "flavored",
			"flavoring", "glamour", "gray", "harbor", "harbors", "homolog", "homologs", "honor", "honors", "honored",
			"honoring", "honorable", "humor", "humors", "humored", "humoring", "curb", "labeled", "labeling", "labor",
			"Labor", "labors", "labored", "laboring", "leaned", "learned", "localize", "localized", "maneuver",
			"maneuvers", "maximize", "maximized", "maximizing", "meager", "minimize", "minimized", "minimizing",
			"modernize", "modernized", "modernizing", "misdemeanor", "misdemeanors", "neighbor", "neighbors",
			"neighborhood", "neighborhoods", "estrogen", "estrogens", "organization", "organizations", "penalize",
			"penalized", "popularize", "popularized", "popularizes", "popularizing", "practice", "practiced",
			"pressurize", "pressurized", "pressurizes", "pressurizing", "realize", "realized", "realizing", "realizes",
			"recognize", "recognized", "recognizing", "recognizes", "rumored", "rumoring", "savor", "savors", "savored",
			"savoring", "splendor", "splendors", "theater", "theaters", "titer", "titers", "traveled", "traveling" };

	// static initialization block
	static {
		if (converters.length != converted.length || pats.length != reps.length || pats.length != excepts.length) {
			throw new RuntimeException("Americanize: Bad initialization data");
		}
		for (int i = 0; i < converters.length; i++) {
			mapping.put(converters[i], converted[i]);
		}

	}
}
