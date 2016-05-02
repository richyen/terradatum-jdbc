package com.terradatum.jdbc;

import com.google.common.base.Strings;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rbellamy@terradatum.com
 * @date 5/1/16
 */
public class EdbUtils {

  public static List parse(String txt, boolean standardConformingStrings) throws SQLException {
    return parse(txt, 1, standardConformingStrings);
  }

  public static List parse(String txt, int level, boolean standardConformingStrings) {
    ArrayList list = new ArrayList();
    String quotesChars = getQuotesChars(level);
    String outerQuotes = getQuotesChars(level - 1);
    if (!txt.startsWith(outerQuotes + "(") && !txt.endsWith(outerQuotes + ")")) {
      throw new RuntimeException("Not a valid construct for a Row");
    } else {
      txt = txt.substring(outerQuotes.length() + 1);
      txt = txt.substring(0, txt.length() - outerQuotes.length() - 1);
      boolean inQuotes = false;
      String currentToken = "";

      for (int i = 0; i < txt.length(); ++i) {
        if (txt.startsWith(quotesChars, i)) {
          inQuotes = !inQuotes;
          currentToken = currentToken + quotesChars;
          i += quotesChars.length() - 1;
        } else if (txt.charAt(i) == 44) {
          if (inQuotes) {
            currentToken = currentToken + ",";
          } else {
            if (currentToken.length() > 0 && currentToken.startsWith(quotesChars + "(") && currentToken.endsWith(")" + quotesChars)) {
              list.add(parse(currentToken, level + 1, standardConformingStrings));
            } else {
              list.add(adjustLevel(currentToken, Types.ARRAY));
            }

            currentToken = "";
          }
        } else {
          currentToken = currentToken + txt.charAt(i);
        }
      }

      if (currentToken.length() > 0 && currentToken.startsWith(quotesChars + "(") && currentToken.endsWith(")" + quotesChars)) {
        list.add(parse(currentToken, level + 1, standardConformingStrings));
      } else {
        list.add(adjustLevel(currentToken, Types.ARRAY));
      }

      return list;
    }
  }

  private static String adjustLevel(String attribute, int type) {
    return adjustLevel(attribute, type, "\"", false);
  }

  private static String adjustLevel(String attribute, int type, String quote, boolean normalize) {
    StringBuffer buffer = new StringBuffer();
    char[] chars = attribute.toCharArray();
    ArrayList lastType = new ArrayList();
    lastType.add(new Integer(type));
    ArrayList lastQuote = new ArrayList();
    lastQuote.add(quote);
    int level = 0;
    int calculatedLevel = 0;
    int firstCalculatedLevel = -2;

    for (int i = 0; i < chars.length; ++i) {
      StringBuffer tempBuffer = null;
      boolean quoteFound = false;

      while (chars[i] == 92 || chars[i] == 34) {
        if (tempBuffer == null) {
          tempBuffer = new StringBuffer();
        }

        tempBuffer.append(chars[i]);
        ++i;
        if (firstCalculatedLevel >= 0 && (double) tempBuffer.length() == Math.pow(2.0D,
            (double) (level + firstCalculatedLevel)) || i == chars.length) {
          break;
        }
      }

      String replacedQuote;
      if (tempBuffer != null) {
        --i;
        int length = tempBuffer.length();
        calculatedLevel = log(length, 2);
        if (firstCalculatedLevel == -2) {
          firstCalculatedLevel = calculatedLevel;
          if (level == 0) {
            firstCalculatedLevel = calculatedLevel + 1;
          }
        }

        if (!normalize) {
          if (calculatedLevel - firstCalculatedLevel >= 0) {
            replacedQuote = (String) lastQuote.get(calculatedLevel - firstCalculatedLevel);
          } else {
            replacedQuote = "";
          }

          while (calculatedLevel - firstCalculatedLevel < level - 1) {
            lastQuote.remove(level);
            lastType.remove(level);
            --level;
          }
        } else {
          if (level == 0) {
            replacedQuote = "";
          } else {
            replacedQuote = "\"";
          }

          while (calculatedLevel - firstCalculatedLevel < level - 1) {
            --level;
          }
        }

        buffer.append(replacedQuote);
        quoteFound = true;
      }

      if (!quoteFound) {
        if (level <= calculatedLevel + 1) {
          if (123 == chars[i]) {
            if (!normalize) {
              lastType.add(Types.ARRAY);
              replacedQuote = getNextQuotes((String) lastQuote.get(level), Types.ARRAY);
              lastQuote.add(replacedQuote);
            }

            ++level;
          } else if (40 == chars[i]) {
            if (!normalize) {
              lastType.add(Types.STRUCT);
              replacedQuote = getNextQuotes((String) lastQuote.get(level), Types.STRUCT);
              lastQuote.add(replacedQuote);
            }

            ++level;
          }
        }

        buffer.append(chars[i]);
      }
    }

    return buffer.toString();
  }

  private static String getQuotesChars(int level) {
    if (level <= 0) {
      return "";
    } else {
      String src = "";
      int iterations = (int) Math.pow(2.0D, (double) (level - 1));

      for (int i = 1; i <= iterations; ++i) {
        src = src + "\"";
      }

      return src;
    }
  }

  private static String getNextQuotes(String lastQuotes, int lastType) {
    StringBuffer quotes = new StringBuffer();
    if (lastQuotes.length() == 0) {
      return "\"";
    } else {
      if (lastType == Types.ARRAY) {
        int length = lastQuotes.length();

        for (int i = 0; i < length; ++i) {
          quotes.append('\\');
        }

        quotes.append(lastQuotes);
      } else {
        quotes.append(lastQuotes).append(lastQuotes);
      }

      return quotes.toString();
    }
  }

  public static List buildArrayList(String arrayAsString, char delim) throws SQLException {
    return new EdbUtils().buildArrayListInternal(arrayAsString, delim);
  }

  private static int log(int x, int base) {
    return (int) (Math.log((double) x) / Math.log((double) base));
  }

  /**
   * This method is plucked directly from the PostgreSQL JDBC code from the private, synchronized, {@link com.edb.jdbc.PgArray}
   * {@code buildArrayList} method. The reason for this is that when returning Composite Structs with attributes that are {@link Array}'s
   * the PostgreSQL and EDB JDBC drivers fail to create those member attributes as even weakly referenced {@link Array}s. Instead
   * they are <b>{@link String}s</b>! In other words, you cannot do the following:
   * <code>
   * <pre>(Array) someStruct.getAttributes[6]</pre>
   * </code>
   * You will get a {@link ClassCastException} saying you cannot cast from {@link String} to {@link Array}.
   *
   * @param arrayAsString the {@link Array} in string form
   * @return the {@link List} of elements.
   * @throws SQLException
   */
  private List buildArrayListInternal(String arrayAsString, char delim) throws SQLException {

    EdbUtils.DimensionalArrayList arrayList = new EdbUtils.DimensionalArrayList();

    if (!Strings.isNullOrEmpty(arrayAsString)) {

      char[] chars = arrayAsString.toCharArray();
      StringBuilder buffer = null;
      boolean insideString = false;
      boolean wasInsideString = false; // needed for checking if NULL
      // value occurred
      List<EdbUtils.DimensionalArrayList> dims = new ArrayList<>(); // array dimension arrays
      EdbUtils.DimensionalArrayList curArray = arrayList; // currently processed array

      // Starting with 8.0 non-standard (beginning index
      // isn't 1) bounds the dimensions are returned in the
      // data formatted like so "[0:3]={0,1,2,3,4}".
      // Older versions simply do not return the bounds.
      //
      // Right now we ignore these bounds, but we could
      // consider allowing these index values to be used
      // even though the JDBC spec says 1 is the first
      // index. I'm not sure what a client would like
      // to see, so we just retain the old behavior.
      int startOffset = 0;
      {
        if (chars[0] == '[') {
          while (chars[startOffset] != '=') {
            startOffset++;
          }
          startOffset++; // skip =
        }
      }

      for (int i = startOffset; i < chars.length; i++) {

        // escape character that we need to skip
        if (chars[i] == '\\') {
          i++;
        } else if (!insideString && chars[i] == '{') {
          // subarray start
          if (dims.size() == 0) {
            dims.add(arrayList);
          } else {
            EdbUtils.DimensionalArrayList a = new EdbUtils.DimensionalArrayList();
            EdbUtils.DimensionalArrayList p = dims.get(dims.size() - 1);
            p.add(a);
            dims.add(a);
          }
          curArray = dims.get(dims.size() - 1);

          // number of dimensions
          {
            for (int t = i + 1; t < chars.length; t++) {
              if (chars[t] == '{') {
                curArray.dimensionsCount++;
              } else {
                break;
              }
            }
          }

          buffer = new StringBuilder();
          continue;
        } else if (chars[i] == '"') {
          // quoted element
          insideString = !insideString;
          wasInsideString = true;
          continue;
        } else if (!insideString && Character.isWhitespace(chars[i])) {
          // white space
          continue;
        } else if ((!insideString && (chars[i] == delim || chars[i] == '}'))
            || i == chars.length - 1) {
          // array end or element end
          // when character that is a part of array element
          if (chars[i] != '"' && chars[i] != '}' && chars[i] != delim && buffer != null) {
            buffer.append(chars[i]);
          }

          String b = buffer == null ? null : buffer.toString();

          // add element to current array
          if (b != null && (b.length() > 0 || wasInsideString)) {
            curArray.add(!wasInsideString ? null : b);
          }

          wasInsideString = false;
          buffer = new StringBuilder();

          // when end of an array
          if (chars[i] == '}') {
            dims.remove(dims.size() - 1);

            // when multi-dimension
            if (dims.size() > 0) {
              curArray = dims.get(dims.size() - 1);
            }

            buffer = null;
          }

          continue;
        }

        if (buffer != null) {
          buffer.append(chars[i]);
        }
      }
    }

    return arrayList;
  }

  /**
   * Array list implementation specific for storing PG array elements.
   */
  private class DimensionalArrayList extends ArrayList<Object> {
    /**
     * How many dimensions.
     */
    int dimensionsCount = 1;
  }
}
