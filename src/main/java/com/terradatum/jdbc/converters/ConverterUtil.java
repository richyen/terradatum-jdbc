package com.terradatum.jdbc.converters;

import com.google.common.base.Converter;

import java.util.HashSet;
import java.util.Set;

/**
 * Conversion utility for strings. Uses the Guava {@link Converter} library to create forward and backward conversion functors.
 *
 * @author rbellamy@terradatum.com
 * @date 1/31/16
 */
public class ConverterUtil {

  private static Set<AbstractConverter<?, ?>> converterSet = new HashSet<>();

  static {
    converterSet.add(new StringBigDecimalConverter());
    converterSet.add(new StringBigIntegerConverter());
    converterSet.add(new StringDoubleConverter());
    converterSet.add(new StringIntegerConverter());
    converterSet.add(new StringTimestampConverter());
  }

  /**
   * Registers a new converter for converting from from {@code a} to {@code b}.
   *
   * @param converter
   *          The converter
   * @param <A>
   *          the type of the forward conversion (from)
   * @param <B>
   *          the type of the backward conversion (to)
   */
  public static <A, B> void register(AbstractConverter<A, B> converter) {
    converterSet.add(converter);
  }

  public static <A, B> B convert(A value, Class<B> type) {
    B ret = null;
    if (value != null && !value.equals("")) {
      if (Object.class == type || value.getClass() == type
          || (value instanceof String && type.getName().equals("java.lang.String"))) {
        // noinspection unchecked
        ret = (B) value;
      } else {
        for (AbstractConverter<?, ?> converter : converterSet) {
          if (converter.getForwardType().isAssignableFrom(value.getClass()) && converter.getBackwardType() == type) {

            // noinspection unchecked
            ret = ((AbstractConverter<A, B>) converter).convert(value);
          }
          /**
           * FIXME: Error:(69, 87) java: incompatible types: A cannot be converted to B When A == backwardType and B ==
           * forwardType, converter.reverse().convert(A a) should work. Instead seeing the above compiler error. IntelliJ quick
           * information shows the expected signature so why the error occurs is unclear.
           * --------------------------------------------------------------------------------------- else if
           * (converter.getForwardType().isAssignableFrom(type) && converter.getBackwardType() == value.getClass()) { ret =
           * ((AbstractConverter<A, B>) converter).reverse().convert(value); }
           * --------------------------------------------------------------------------------------- 2016-02-04 - Further testing
           * using in-place expression evaluation during debugging proves that while
           * "((AbstractConverter<A, B>) converter).reverse().convert(value)" produces a compile error, it does actually execute
           * without exception during runtime. A guess is that it has to do with the type capture
           * "AbstractConverter<? capture, ? capture>" confusing the compiler.
           */
        }
        if (ret == null) {
          throw new ClassCastException(
              "No converters available to coerce [" + value + "] from " + value.getClass() + " into a " + type.getName() + ".");
        }
      }
    }

    return ret;
  }
}
